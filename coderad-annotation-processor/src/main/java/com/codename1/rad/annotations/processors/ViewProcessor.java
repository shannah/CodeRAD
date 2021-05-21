package com.codename1.rad.annotations.processors;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.annotations.RAD;

import org.apache.commons.text.StringEscapeUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ViewProcessor extends BaseProcessor {
    private RoundEnvironment roundEnv;


    ViewProcessor(ProcessingEnvironment processingEnvironment) {
        super.processingEnv = processingEnvironment;
    }

    @Override
    void installTypes(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> annotatedElements = (Set<? extends TypeElement>)roundEnv.getElementsAnnotatedWith(RAD.class);



        // First we process all views with the RAD annoation
        // This will create Stubs
        for (Element el : annotatedElements) {
            if (!(el instanceof TypeElement)) continue;
            TypeElement typeEl = (TypeElement)el;
            if (!isA(typeEl, "com.codename1.rad.ui.EntityView")) {
                continue;
            }

            installTypes(typeEl);

        }

    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        this.roundEnv = roundEnv;
        Set<? extends Element> annotatedElements = (Set<? extends TypeElement>)roundEnv.getElementsAnnotatedWith(RAD.class);



        // First we process all views with the RAD annoation
        // This will create Stubs
        for (Element el : annotatedElements) {
            if (!(el instanceof TypeElement)) continue;
            TypeElement typeEl = (TypeElement)el;
            if (!isA(typeEl, "com.codename1.rad.ui.EntityView")) {
                continue;
            }

            processFragment(typeEl);

        }


        return true;
    }



    private void installTypes(TypeElement typeEl) {
        try {
            EntityViewBuilder builder = new EntityViewBuilder(typeEl);
            builder.installTypes((ProcessingEnvironmentWrapper) processingEnv);
        } catch (XMLParseException ex) {
            env().getMessager().printMessage(Diagnostic.Kind.ERROR, ex.getMessage(), typeEl);
            ex.printStackTrace();
        }

    }



    private void processFragment(TypeElement typeEl) {
        try {
            EntityViewBuilder builder = new EntityViewBuilder(typeEl);
            builder.createSchemaSourceFile();
            builder.createControllerMarkerInterfaceSourceFile();
            builder.createModelSourceFile();
            builder.createControllerSourceFile();
            builder.createViewSourceFile();
        } catch (XMLParseException ex) {
            env().getMessager().printMessage(Diagnostic.Kind.ERROR, ex.getMessage(), typeEl);
            ex.printStackTrace();
        } catch (IOException io) {
            env().getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to write view for "+typeEl+":"+io.getMessage(), typeEl);
            io.printStackTrace();

        }
    }




    /**
     * Encapsulates the Java environment for the View class.
     */
    private class JavaEnvironment {

        final Map<String,TypeElement> tagCache = new HashMap<>();
        final Map<String,TypeElement> lookupClassCache = new HashMap<>();
        final Map<String,TypeElement> simpleNameClassCache = new HashMap<>();

        final org.w3c.dom.Element rootElement;

        /**
         * The type used for the view model of this view
         */
        private JavaClassProxy viewModelType;

        private EntityViewBuilder rootBuilder;

        String getRootViewQualifiedName() {
            return rootBuilder.packageName + "." + rootBuilder.className;
        }

        JavaEnvironment(org.w3c.dom.Element rootElement) {
            this.rootElement = rootElement;
        }

        /**
         * List of import statements.
         */
        private List<String> imports = new ArrayList<String>();




        void setViewModelType(String type) {
            TypeElement typeEl = lookupClass(type);
            if (typeEl == null) {
                // THis might happen on first pass if we try to use the generated model as the view model type
                //throw new IllegalArgumentException("Cannot find view type "+type);
                typeEl = lookupClass("com.codename1.rad.models.Entity");
            }

            this.viewModelType = new JavaClassProxy(typeEl, this);
        }


        private List<org.w3c.dom.Element> getChildrenOfType(org.w3c.dom.Element root, String type) {
            List<org.w3c.dom.Element> out = new ArrayList<>();
            for (org.w3c.dom.Element child : getChildElements(root)) {
                TypeElement el = findClassThatTagCreates(child.getTagName());
                if (isA(el, type)) {
                    out.add(child);
                }
            }
            return out;
        }

        public DeclaredType createDeclaredType(String name, TypeMirror... typeArgs) {
            return new ProcessingEnvironmentWrapper.RDeclaredType() {
                private List<TypeMirror> typeArguments = new ArrayList<>(Arrays.asList(typeArgs));
                @Override
                public Element asElement() {
                    return lookupClass(name);
                }

                @Override
                public TypeMirror getEnclosingType() {
                    return elements().getPackageElement(rootBuilder.packageName).asType();
                }

                @Override
                public List<? extends TypeMirror> getTypeArguments() {
                    return typeArguments;
                }

                @Override
                public TypeKind getKind() {
                    return TypeKind.DECLARED;
                }

                @Override
                public <R, P> R accept(TypeVisitor<R, P> v, P p) {
                    return v.visit(this);
                }

                @Override
                public List<? extends AnnotationMirror> getAnnotationMirrors() {
                    return new ArrayList<>();
                }

                @Override
                public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
                    return null;
                }

                @Override
                public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
                    return null;
                }
            };
        };

        /**
         * Checks if the element is a RAD context.  When an elemnt is a RAD context,
         * XML tag attributes and child nodes are converted to RAD attributes, and Nodes.
         *
         * An element is considered to be a rad context if it, or a parent node references a builder method
         * or constructor that takes a Node or list of attributes as parameters.
         * @param el
         * @return
         */
        boolean isRADContext(org.w3c.dom.Element el) {
            if (el.hasAttribute("is-rad-context")) {
                return "true".equalsIgnoreCase(el.getAttribute("is-rad-context"));
            }
            Node parentNode = el.getParentNode();
            if (parentNode instanceof org.w3c.dom.Element) {
                org.w3c.dom.Element parentEl = (org.w3c.dom.Element)parentNode;
                if (isRADContext(parentEl)) {
                    el.setAttribute("is-rad-context", "true");
                    return true;
                }
            }

            TypeElement componentClass = findClassThatTagCreates(el.getTagName());
            if (componentClass != null && isA(componentClass, "com.codename1.rad.nodes.Node")) {
                el.setAttribute("is-rad-context", "true");
                return true;
            }

            el.setAttribute("is-rad-context", "false");
            return false;
        }



        private List<JavaClassProxy> findInstantiatableClassesAssignableTo(PackageElement _contextPackage, org.w3c.dom.Element xmlTag, String... types) {
            final PackageElement contextPackage = wrap(_contextPackage);
            List<TypeElement> candidates = new ArrayList<TypeElement>();
            for (String importPath : imports) {
                if (importPath.startsWith("import ")) {
                    importPath = importPath.substring(importPath.indexOf(" ")+1);
                }
                if (importPath.startsWith("static ")) {
                    importPath = importPath.substring(importPath.indexOf(" ")+1);
                }
                boolean glob = importPath.endsWith("*");

                boolean deep = importPath.endsWith("**");
                if (glob) {
                    importPath = importPath.substring(0, importPath.lastIndexOf("."));
                }

                TypeElement matchingType = elements().getTypeElement(importPath);


                if (matchingType != null) {
                    if (!glob) {
                        candidates.addAll(findClassesAssignableTo(new ArrayList<TypeElement>(), matchingType, types));
                    } else {
                        candidates.addAll(findInnerClassesAssignableTo(new ArrayList<TypeElement>(), matchingType, types));
                    }

                } else {
                    PackageElement pkg = elements().getPackageElement(importPath);
                    candidates.addAll(findClassesAssignableTo(new ArrayList<TypeElement>(), pkg, types));


                }
            }

            candidates = candidates.stream()
                    .filter(el -> el.getModifiers().contains(Modifier.PUBLIC) || elements().getPackageOf(el).equals(contextPackage))
                    .filter(el -> !el.getModifiers().contains(Modifier.PRIVATE) && !el.getModifiers().contains(Modifier.ABSTRACT))
                    .filter(el -> el.getKind() == ElementKind.CLASS)

                    .collect(Collectors.toList());

            List<JavaClassProxy> candidateProxies = new ArrayList<>();
            for (TypeElement el : candidates) {
                candidateProxies.add(new JavaClassProxy(el, this));
            }
            if (xmlTag != null) {
                candidateProxies = candidateProxies.stream().filter(p -> p.getBestConstructor(xmlTag) != null).collect(Collectors.toList());
            }

            return candidateProxies;


        }

        private List<TypeElement> findClassesAssignableTo(List<TypeElement> out, TypeElement root, String... types) {

            boolean isAssignable = true;
            for (String type : types) {

                if (!isA(root, type)) {

                    isAssignable = false;
                    break;
                }

            }
            if (isAssignable) {
                out.add(root);
            }

            return out;
        }

        private List<TypeElement> findClassesAssignableTo(List<TypeElement> out, PackageElement root, String... types) {
            root = wrap(root);
            for (TypeElement e : (List<TypeElement>)root.getEnclosedElements().stream().filter(e->e.getKind() == ElementKind.INTERFACE || e.getKind() == ElementKind.CLASS).collect(Collectors.toList())) {
                findClassesAssignableTo(out, e, types);
            }
            return out;
        }

        private List<TypeElement> findInnerClassesAssignableTo(List<TypeElement> out, TypeElement root, String... types) {

            for (TypeElement e : (List<TypeElement>)root.getEnclosedElements().stream().filter(e->e.getKind() == ElementKind.INTERFACE || e.getKind() == ElementKind.CLASS).collect(Collectors.toList())) {
                findClassesAssignableTo(out, e, types);
            }
            return out;
        }

        /**
         * Writes Java import statements to the string buffer.
         * @param sb
         */
        private void writeImports(StringBuilder sb) {
            for (String importPath : imports) {
                if (importPath.endsWith("**")) {

                    importPath = importPath.substring(0, importPath.length()-1);
                    if (imports.contains(importPath)) {
                        continue;
                    }
                }
                sb.append(importPath).append(";\n");
            }
        }

        void createRADPropertySelector(StringBuilder sb, String tagPath) {
            createRADPropertySelector(sb, tagPath, "context.getEntity()");
        }
        /**
         * Appends a new PropertySelector() instantiation to the string buffer, representing
         * the provided tag path.
         * @param sb The buffer to append to.
         * @param tagPath The tag path.  Chained tags separated by forward slash '/' character.
         * @return
         */

        void createRADPropertySelector(StringBuilder sb, String tagPath, String rootElement) {
            sb.append("new PropertySelector(").append(rootElement).append(", ");
            StringTokenizer stringTokenizer = new StringTokenizer(tagPath, "/");
            boolean first = true;
            while (stringTokenizer.hasMoreTokens()) {
                if (first) {
                    first = false;
                } else {
                    sb.append(".createChildSelector(");
                }
                String tok = stringTokenizer.nextToken();

                sb.append(tok).append(")");
            }
        }

        /**
         * Checks if tag will produce a java.ui.Component.
         * @param tagName The tag name to check
         * @return
         */
        boolean isComponentTag(String tagName) {
            TypeElement el = findClassThatTagCreates(tagName);
            return el != null && (isA(el, "com.codename1.ui.Component"));
        }

        boolean isNodeTag(String tagName, boolean inNodeContext) {
            TypeElement el = findClassThatTagCreates(tagName, inNodeContext ? "com.codename1.rad.nodes.Node" : null);
            return el != null && (isA(el, "com.codename1.rad.nodes.Node"));
        }



        /**
         * Checks if the given tag name will produce a Container component.
         * @param tagName The tag name to check.
         * @return True if the tag name will produce a java.ui.Container or subclass.
         */
        boolean isContainerTag(String tagName) {
            TypeElement el = findClassThatTagCreates(tagName);

            if (el != null && isA(el, "com.codename1.ui.Container")) return true;

            return false;
        }

        TypeElement findClassThatTagCreates(String tag) {
            return findClassThatTagCreates(tag, null);
        }

        /**
         * Given a tag name, this returns the TypeElement for the class that it would create.  It does this by
         * first looking for a builder.  If it doesn't find a builder, it will look (case insensitively) in the
         * import paths for a class with the same name as the tag.
         * @param tag
         * @return
         */
        TypeElement findClassThatTagCreates(String tag, String isa) {
            if (isa == null && tagCache.containsKey(tag)) {
                return tagCache.get(tag);
            }
            if (isa == null || isa.equals("com.codename1.ui.Component")) {
                JavaClassProxy builderClass = findComponentBuilderForTag(tag);
                if (builderClass != null) {
                    TypeElement el = builderClass.findMethodProxy("getComponent", 0).getReturnType();
                    if (el != null) {
                        if (isa == null) {
                            tagCache.put(tag, el);
                        }
                        return el;
                    }

                }
            }


            if (isa != null && isa.equals("com.codename1.rad.nodes.Node")) {
                JavaClassProxy builderClass = findNodeBuilderForTag(tag);
                if (builderClass != null) {
                    TypeElement el = builderClass.findMethodProxy("getNode", 0).getReturnType();
                    if (el != null) {
                        if (isa == null) {
                            tagCache.put(tag, el);
                        }
                        return el;
                    }

                }
            }

            TypeElement cls = findClassBySimpleName(tag, isa);
            if (cls != null) {
                if (isa == null) {
                    tagCache.put(tag, cls);
                }
                return cls;
            }

            return null;
        }

        /**
         * Looks up a class by the given simple name.  Unlike {@link #findClassBySimpleName(String)}, this
         * is case sensitive.
         * @param className The class name to search for.
         * @return Matching TypeElement or null.
         */

        TypeElement lookupClass(String className) {

            if (lookupClassCache.containsKey(className)) {
                return lookupClassCache.get(className);
            }

            TypeElement typeEl = elements().getTypeElement(className);
            if (typeEl != null) {
                lookupClassCache.put(className, typeEl);
                return typeEl;
            }
            for (String importPath : imports) {
                if (importPath.startsWith("import ")) {
                    importPath = importPath.substring(importPath.indexOf(" ")+1);
                }
                if (importPath.startsWith("static ")) {
                    importPath = importPath.substring(importPath.indexOf(" ")+1);
                }
                if (importPath.endsWith("*")) {
                    importPath = importPath.substring(0, importPath.lastIndexOf("."));
                }
                typeEl = elements().getTypeElement( importPath + "." + className);
                if (typeEl != null) {
                    lookupClassCache.put(className, typeEl);
                    return typeEl;
                }

            }

            return null;
        }

        /**
         * Searches a package for a class with the matching simple name.  Search is case insensitive.
         * @param searchRoot The package to search
         * @param className The class name to search for (case insensitive)
         * @param deep If true, then this will also recursively search inner classes of the class.
         * @return A matching TypeElement or null if none is found.
         */
        TypeElement findClassBySimpleName(PackageElement searchRoot, String className, boolean deep, String isa) {
            if (searchRoot == null) return null;

            searchRoot = wrap(searchRoot);
            List<TypeElement> classes = (List<TypeElement>)searchRoot.getEnclosedElements().stream().filter(e -> e.getKind() == ElementKind.CLASS || e.getKind() == ElementKind.INTERFACE).collect(Collectors.toList());

            for (TypeElement cls : classes) {
                if (className.equalsIgnoreCase(cls.getSimpleName().toString())) {
                    if (isa == null || isA(cls, isa)) {
                        return cls;
                    }
                }
                if (deep) {
                    TypeElement typeElement = findClassBySimpleName(cls, className, false, deep, isa);
                    if (typeElement != null) {
                        return typeElement;
                    }
                }
            }

            if (deep) {
                List<PackageElement> packages = (List<PackageElement>)searchRoot.getEnclosedElements().stream().filter(e -> e.getKind() == ElementKind.PACKAGE).collect(Collectors.toList());
                for (PackageElement pkg : packages) {
                    pkg = wrap(pkg);
                    TypeElement typeElement = findClassBySimpleName(pkg, className, deep, isa);
                    if (typeElement != null) {
                        return typeElement;
                    }
                }
            }
            return null;
        }


        /**
         * Finds type element for class with given simple name
         * @param searchRoot The starting point for the search.  If the searchRoot matches (case-insensitively) the className parameter.
         *                   then it, itself, will be returned.  If glob or deep flags are set, it will look at inner classes for a match.
         * @param className The className to search for.  Search is case insensitive.
         * @param glob If true, then this searches inner classes (and will not return the search root itself).
         * @param deep If true, then this searches inner classes, recursively.
         * @return The matching TypeElement or null if none is found.
         */
        TypeElement findClassBySimpleName(TypeElement searchRoot, String className, boolean glob, boolean deep, String isa) {

            if (!glob && searchRoot.getSimpleName().toString().equalsIgnoreCase(className) && (isa == null || isA(searchRoot, isa))) {
                return searchRoot;
            }
            if (glob || deep) {
                List<TypeElement> classes = (List<TypeElement>) searchRoot.getEnclosedElements().stream()
                        .filter(e -> e.getModifiers().contains(Modifier.STATIC) && (e.getKind() == ElementKind.CLASS || e.getKind() == ElementKind.INTERFACE))
                        .collect(Collectors.toList());
                for (TypeElement cls : classes) {
                    if (className.equalsIgnoreCase(cls.getSimpleName().toString())) {
                        if (isa == null || isA(cls, isa)) {
                            return cls;
                        }
                    }
                    if (deep) {
                        TypeElement typeElement = findClassBySimpleName(cls, className, false, deep, isa);
                        if (typeElement != null) {
                            return typeElement;
                        }
                    }
                }
            }
            return null;
        }

        TypeElement findClassBySimpleName(String name) {
            return findClassBySimpleName(name, null);
        }

        /**
         * Gets a TypeElement for a class with the given simple name (CASE SENSITIVE!!)
         * @param name
         * @return
         */
        TypeElement findClassBySimpleName(String name, String isa) {
            if (isa == null && simpleNameClassCache.containsKey(name)) {
                return simpleNameClassCache.get(name);
            }
            for (String importPath : imports) {
                if (importPath.startsWith("import ")) {
                    importPath = importPath.substring(importPath.indexOf(" ")+1);
                }
                if (importPath.startsWith("static ")) {
                    importPath = importPath.substring(importPath.indexOf(" ")+1);
                }
                boolean glob = importPath.endsWith("*");

                boolean deep = importPath.endsWith("**");
                if (glob) {
                    importPath = importPath.substring(0, importPath.lastIndexOf("."));
                }

                TypeElement matchingType = elements().getTypeElement(importPath);

                if (matchingType != null) {

                    TypeElement builder = findClassBySimpleName(matchingType, name, glob, deep, isa);
                    if (builder != null) {
                        if (isa == null) {
                            simpleNameClassCache.put(name, builder);
                        }
                        return builder;
                    }

                } else {
                    PackageElement pkg = elements().getPackageElement(importPath);
                    pkg = wrap(pkg);
                    TypeElement builder = findClassBySimpleName(pkg, name, deep, isa);
                    if (builder != null) {
                        if (isa == null) {
                            simpleNameClassCache.put(name, builder);
                        }
                        return builder;
                    }

                }
            }
            return null;
        }


        /**
         * Finds the builder class that is used to build components for the given tag.
         *
         * Builder classes are associated with tags via the RAD annotation, with the {@literal tag} attribute
         * specifying the tag name.
         * @param searchRoot The package to search.
         * @param tagName The tag name.
         * @param deep Whether to also look through inner classes.
         * @return The matching builder class, or null if none is found.
         */
        TypeElement findComponentBuilderClass(PackageElement searchRoot, String tagName, boolean deep) {
            searchRoot = wrap(searchRoot);
            List<TypeElement> innerClasses = (List<TypeElement>)searchRoot.getEnclosedElements().stream().filter(e -> e.getKind() == ElementKind.CLASS || e.getKind() == ElementKind.INTERFACE).collect(Collectors.toList());
            for (TypeElement el : innerClasses) {
                TypeElement found = findComponentBuilderClass(el, tagName, false, false);
                if (found != null) {
                    return found;
                }

            }
            if (deep) {
                for (TypeElement el : innerClasses) {
                    TypeElement found = findComponentBuilderClass(el, tagName, true, true);
                    if (found != null) {
                        return found;
                    }

                }
                List<PackageElement> subPackages = (List<PackageElement>)searchRoot.getEnclosedElements().stream().filter(e -> e.getKind() == ElementKind.PACKAGE).collect(Collectors.toList());
                for (PackageElement pkg : subPackages) {
                    pkg = wrap(pkg);
                    TypeElement found = findComponentBuilderClass(pkg, tagName, deep);
                    if (found != null) {
                        return found;
                    }
                }
            }
            return null;
        }

        TypeElement findNodeBuilderClass(PackageElement searchRoot, String tagName, boolean deep) {
            searchRoot = wrap(searchRoot);
            List<TypeElement> innerClasses = (List<TypeElement>)searchRoot.getEnclosedElements().stream().filter(e -> e.getKind() == ElementKind.CLASS || e.getKind() == ElementKind.INTERFACE).collect(Collectors.toList());
            for (TypeElement el : innerClasses) {
                TypeElement found = findNodeBuilderClass(el, tagName, false, false);
                if (found != null) {
                    return found;
                }

            }
            if (deep) {
                for (TypeElement el : innerClasses) {
                    TypeElement found = findNodeBuilderClass(el, tagName, true, true);
                    if (found != null) {
                        return found;
                    }

                }
                List<PackageElement> subPackages = (List<PackageElement>)searchRoot.getEnclosedElements().stream().filter(e -> e.getKind() == ElementKind.PACKAGE).collect(Collectors.toList());
                for (PackageElement pkg : subPackages) {
                    pkg = wrap(pkg);
                    TypeElement found = findNodeBuilderClass(pkg, tagName, deep);
                    if (found != null) {
                        return found;
                    }
                }
            }
            return null;
        }


        JavaMethodProxy findBuilderMethod(org.w3c.dom.Element domElement, String requiredType) {
            for (String importPath : imports) {
                if (importPath.startsWith("import ")) {
                    importPath = importPath.substring(importPath.indexOf(" ")+1);
                }
                if (importPath.startsWith("static ")) {
                    importPath = importPath.substring(importPath.indexOf(" ")+1);
                }
                boolean glob = importPath.endsWith("*");

                boolean deep = importPath.endsWith("**");
                if (glob) {
                    importPath = importPath.substring(0, importPath.lastIndexOf("."));
                }

                TypeElement matchingType = elements().getTypeElement(importPath);

                if (matchingType != null) {

                    JavaMethodProxy match =  findBuilderMethod(matchingType, domElement, requiredType);
                    if (match != null) return match;


                } else {
                    PackageElement pkg = elements().getPackageElement(importPath);
                    pkg = wrap(pkg);
                    JavaMethodProxy match = findBuilderMethod(pkg, domElement, requiredType);
                    if (match != null) {
                        return match;
                    }

                }
            }
            return null;

        }

        JavaMethodProxy findBuilderMethod(PackageElement searchRoot, org.w3c.dom.Element domElement, String requiredType) {
            if (searchRoot == null) return null;
            searchRoot = wrap(searchRoot);
            String tagName = domElement.getTagName();
            List<TypeElement> classes;
            if (tagName.contains(".")) {
                String className = tagName.substring(0, tagName.indexOf("."));
                classes = (List<TypeElement>)searchRoot.getEnclosedElements().stream().filter(e -> e.getKind() == ElementKind.CLASS && className.equalsIgnoreCase(e.getSimpleName().toString()))
                        .collect(Collectors.toList());
            } else {
                classes = (List<TypeElement>)searchRoot.getEnclosedElements().stream().filter(e -> e.getKind() == ElementKind.CLASS)
                        .collect(Collectors.toList());
            }
            for (TypeElement cls : classes) {
                JavaMethodProxy candidate = findBuilderMethod(cls, domElement, requiredType);
                if (candidate != null) {
                    return candidate;
                }
            }
            return null;
        }

        /**
         * Finds a static builder method matching the given dom element.  To match the dom element, the method name
         * must match the tag name, and the parameter names of the method must have corresponding parameters in the
         * attributes.
         *
         * @param searchRoot
         * @param domElement
         * @param requiredType
         * @return
         */
        JavaMethodProxy findBuilderMethod(TypeElement searchRoot, org.w3c.dom.Element domElement, String requiredType) {
            String tagName = domElement.getTagName();
            String methodName = tagName;
            if (methodName.contains(".")) methodName = methodName.substring(methodName.indexOf(".")+1);
            String className = null;
            if (tagName.contains(".")) {
                className = tagName.substring(0, tagName.indexOf("."));
            }
            if (className != null && !className.equalsIgnoreCase(searchRoot.getSimpleName().toString())) {
                return null;
            }
            final String fMethodName = methodName;
            List<ExecutableElement> candidates = (List<ExecutableElement>)searchRoot.getEnclosedElements().stream().filter(e -> e.getKind() == ElementKind.METHOD && e.getModifiers().contains(Modifier.STATIC) && e.getSimpleName().toString().equalsIgnoreCase(fMethodName))
                    .collect(Collectors.toList());

            if (candidates.isEmpty()) return null;

            ExecutableElement bestCandidate = null;
            NamedNodeMap attributes = domElement.getAttributes();
            outer: for (ExecutableElement candidate : candidates) {
                List<String> paramNames = new ArrayList<String>();
                List<? extends VariableElement> params = candidate.getParameters();
                ExecutableType candidateType = (ExecutableType)types().asMemberOf((DeclaredType)searchRoot.asType(), candidate);
                List<? extends TypeMirror> paramTypes = candidateType.getParameterTypes();
                int paramIndex = -1;
                for (VariableElement p : params) {
                    paramIndex++;
                    String attName = p.getSimpleName().toString();
                    TypeMirror paramType = paramTypes.get(paramIndex);
                    if (isA(paramType, "com.codename1.rad.models.Entity")) {

                        if (domElement.hasAttribute("view-model")) {
                            // The element specifies an explicit view-model attribute
                            // that we can use for this parameter.
                            continue;
                        }

                        if  (!isA(paramType, "com.codename1.rad.models.EntityList")) {

                            // For entity parameters we may be able to just derive the parameter
                            // from the entity for the current view.

                            // First the obvious case where the view's model type could be passed directly.
                            if (isA(viewModelType.typeEl, paramTypes.get(paramIndex).toString())) {
                                continue;
                            }

                            // Next, inquire if there is a wrapper for the specified type that we can use.
                            TypeElement wrapperType = lookupClass(paramType.toString() + "Wrapper");
                            if (wrapperType != null) {
                                continue;
                            }
                        }


                    }

                    if (paramType.toString().equalsIgnoreCase("com.codename1.rad.nodes.Node") || isA(paramType, "com.codename1.rad.nodes.ViewNode")) {
                        // For Node and ViewNode subclasses, we should be able to just create a child node of the current node.
                        // and use that.
                        continue;
                    }

                    if (isA(paramType, "com.codename1.rad.nodes.ListNode")) {
                        // List nodes we would need an explicit node parameter
                        boolean foundMatch = false;
                        for (org.w3c.dom.Element childEl : getChildElements(domElement)) {
                            TypeElement childElClass = findClassThatTagCreates(childEl.getTagName());
                            if (childElClass != null && isA(childElClass, "com.codename1.rad.nodes.ListNode")) {
                                foundMatch = true;
                                break;
                            }
                        }
                        if (foundMatch) {
                            continue;
                        }
                    }


                    // For other attributes we look for an explicit xml attribute
                    // The _attname_ notation is used to explicitly specify that this attribute should
                    // be used as a parameter rather than as a property.
                    if (!domElement.hasAttribute(attName) && !domElement.hasAttribute("_"+attName+"_")) {
                        continue outer;
                    }



                }

                // If we are this far, then this candidate has at least the correct named parameters
                TypeMirror returnTypeMirror = candidate.getReturnType();

                if (returnTypeMirror.getKind() != TypeKind.DECLARED) {
                    continue;
                }
                if (requiredType != null && !isA(returnTypeMirror, requiredType)) {
                    continue;
                }
                if (bestCandidate == null || bestCandidate.getParameters().size() < candidate.getParameters().size()) {
                    bestCandidate = candidate;
                }

            }

            if (bestCandidate != null) {
                JavaClassProxy proxy = new JavaClassProxy(searchRoot, this);
                JavaMethodProxy methodProxy = new JavaMethodProxy(proxy, bestCandidate);
                return methodProxy;
            }
            return null;

        }

        /**
         * Finds a builder class that is registered to handle the given tag name.
         * @param searchRoot The class to search in.  If glob is null, and this class is a match for the query, then it, itself,
         *                   will be returned.
         * @param tagName The tag name to search for.
         * @param glob If true, then inner classes will also be searched, but the searchRoot will not be considered as a candidate.
         * @param deep If true, then inner classes will also be search, and their inner classes, recursively.
         * @return Matching TypeElement or null if none is found
         */
        TypeElement findComponentBuilderClass(TypeElement searchRoot, String tagName, boolean glob, boolean deep) {
            if (!glob) {

                RAD annotation = searchRoot.getAnnotation(RAD.class);

                if (annotation != null && isA(searchRoot, "com.codename1.rad.ui.ComponentBuilder")) {

                    for (String annoTag : annotation.tag()) {
                        if (annoTag.equalsIgnoreCase(tagName)) {
                            return searchRoot;
                        }
                    }
                }
            }
            if (glob || deep) {
                List<TypeElement> innerClasses = (List<TypeElement>)searchRoot.getEnclosedElements().stream().filter(e -> e.getKind() == ElementKind.CLASS && e.getModifiers().contains(Modifier.STATIC)).collect(Collectors.toList());
                for (TypeElement child : innerClasses) {
                    //  We first do a shallow search
                    TypeElement found = findComponentBuilderClass(child, tagName, false, false);
                    if (found != null) {
                        return found;
                    }
                    // Then we do a deep search.
                    // This make sure that a shallow match takes a higher priority than a deep one.
                    if (deep) {
                        found = findComponentBuilderClass(child, tagName, true, true);
                        if (found != null) {
                            return found;
                        }
                    }
                }
            }
            return null;
        }

        TypeElement findNodeBuilderClass(TypeElement searchRoot, String tagName, boolean glob, boolean deep) {
            if (!glob) {

                RAD annotation = searchRoot.getAnnotation(RAD.class);
                if (annotation != null && isA(searchRoot, "com.codename1.rad.nodes.NodeBuilder")) {
                    for (String annoTag : annotation.tag()) {
                        if (annoTag.equalsIgnoreCase(tagName)) {
                            return searchRoot;
                        }
                    }
                }
            }
            if (glob || deep) {
                List<TypeElement> innerClasses = (List<TypeElement>)searchRoot.getEnclosedElements().stream().filter(e -> e.getKind() == ElementKind.CLASS && e.getModifiers().contains(Modifier.STATIC)).collect(Collectors.toList());
                for (TypeElement child : innerClasses) {
                    //  We first do a shallow search
                    TypeElement found = findNodeBuilderClass(child, tagName, false, false);
                    if (found != null) {
                        return found;
                    }
                    // Then we do a deep search.
                    // This make sure that a shallow match takes a higher priority than a deep one.
                    if (deep) {
                        found = findNodeBuilderClass(child, tagName, true, true);
                        if (found != null) {
                            return found;
                        }
                    }
                }
            }
            return null;
        }

        /**
         * Finds the builder class that is registered to handle the given tag name.
         * @param tagName The tag name,
         * @return A TypeElement for a builder class, or null if none is found.
         */
        TypeElement findComponentBuilderClass(String tagName) {
            List<String> deepSearches = new ArrayList<String>(0);
            for (String importPath : imports) {
                if (importPath.startsWith("import ")) {
                    importPath = importPath.substring(importPath.indexOf(" ")+1);
                }
                if (importPath.startsWith("static ")) {
                    importPath = importPath.substring(importPath.indexOf(" ")+1);
                }
                boolean glob = importPath.endsWith("*");

                boolean deep = importPath.endsWith("**");
                if (glob) {
                    importPath = importPath.substring(0, importPath.lastIndexOf("."));
                }

                TypeElement matchingType = elements().getTypeElement(importPath);

                if (matchingType != null) {
                    TypeElement builder = findComponentBuilderClass(matchingType, tagName, glob, false);
                    if (builder != null) {
                        return builder;
                    } else if (deep){
                        deepSearches.add(importPath);
                    }

                } else {
                    PackageElement pkg = elements().getPackageElement(importPath);
                    if (pkg != null) {
                        TypeElement builder = findComponentBuilderClass(pkg, tagName, false);
                        if (builder != null) {
                            return builder;
                        } else if (deep) {
                            deepSearches.add(importPath);
                        }
                    }

                }
            }

            for (String importPath : deepSearches) {
                TypeElement matchingType = elements().getTypeElement(importPath);

                if (matchingType != null) {

                    TypeElement builder = findComponentBuilderClass(matchingType, tagName, true, true);
                    if (builder != null) {
                        return builder;
                    } else {
                        deepSearches.add(importPath);
                    }

                } else {
                    PackageElement pkg = elements().getPackageElement(importPath);
                    TypeElement builder = findComponentBuilderClass(pkg, tagName, true);
                    if (builder != null) {
                        return builder;
                    }

                }
            }
            return null;

        }
        /**
         * Finds the builder class that is registered to handle the given tag name.
         * @param tagName The tag name,
         * @return A TypeElement for a builder class, or null if none is found.
         */
        TypeElement findNodeBuilderClass(String tagName) {
            List<String> deepSearches = new ArrayList<String>(0);
            for (String importPath : imports) {
                if (importPath.startsWith("import ")) {
                    importPath = importPath.substring(importPath.indexOf(" ")+1);
                }
                if (importPath.startsWith("static ")) {
                    importPath = importPath.substring(importPath.indexOf(" ")+1);
                }
                boolean glob = importPath.endsWith("*");

                boolean deep = importPath.endsWith("**");
                if (glob) {
                    importPath = importPath.substring(0, importPath.lastIndexOf("."));
                }

                TypeElement matchingType = elements().getTypeElement(importPath);

                if (matchingType != null) {

                    TypeElement builder = findNodeBuilderClass(matchingType, tagName, glob, false);
                    if (builder != null) {
                        return builder;
                    } else if (deep){
                        deepSearches.add(importPath);
                    }

                } else {
                    PackageElement pkg = elements().getPackageElement(importPath);
                    if (pkg != null) {
                        TypeElement builder = findNodeBuilderClass(pkg, tagName, false);
                        if (builder != null) {
                            return builder;
                        } else if (deep) {
                            deepSearches.add(importPath);
                        }
                    }

                }
            }

            for (String importPath : deepSearches) {
                TypeElement matchingType = elements().getTypeElement(importPath);

                if (matchingType != null) {

                    TypeElement builder = findNodeBuilderClass(matchingType, tagName, true, true);
                    if (builder != null) {
                        return builder;
                    } else {
                        deepSearches.add(importPath);
                    }

                } else {
                    PackageElement pkg = elements().getPackageElement(importPath);
                    TypeElement builder = findNodeBuilderClass(pkg, tagName, true);
                    if (builder != null) {
                        return builder;
                    }

                }
            }
            return null;

        }


        /**
         * Adds imports to the environment.
         * @param imports A string of import statements.
         */
        void addImports(String imports) {
            StringTokenizer strtok = new StringTokenizer(imports, ";\n");
            while (strtok.hasMoreTokens()) {
                String nextTok = strtok.nextToken().trim();
                if (nextTok.isEmpty()) continue;
                this.imports.add(nextTok);
            }
        }


        /**
         * Finds a bulider for the given tag.
         * @param tag
         * @return
         */
        JavaClassProxy findComponentBuilderForTag(String tag) {
            TypeElement builderEl = findComponentBuilderClass(tag);
            if (builderEl == null) return null;
            return new JavaClassProxy(builderEl, this);

        }
        JavaClassProxy findNodeBuilderForTag(String tag) {
            TypeElement builderEl = findNodeBuilderClass(tag);
            if (builderEl == null) return null;
            return new JavaClassProxy(builderEl, this);

        }




    }


    /**
     * Creates a property selector to select the given property (possibly chained with dot notation).
     * @param classProxy The starting class from which the property chain begins.
     * @param selector The selector, in the form a.b.c.d.... etc..
     * @return A JavaPropertySelector
     */
    private JavaPropertySelector createPropertySelector(JavaClassProxy classProxy, String selector) {
        return createPropertySelector(classProxy, selector, "java.lang.String");
    }

    /**
     * Creates a property selector to select the given property.
     * @param classProxy The class that is the starting point of the property chain.
     * @param selector The selector, in the form a.b.c.d.... etc..
     * @param propType The property type.  E.g. java.lang.String.
     * @return
     */
    private JavaPropertySelector createPropertySelector(JavaClassProxy classProxy, String selector, String propType) {
        JavaPropertySelector out = null;
        StringTokenizer strtok = new StringTokenizer(selector, ".");
        while (strtok.hasMoreTokens()) {
            String tok = strtok.nextToken();
            ExecutableElement getterMethod = classProxy.findGetter(tok);

            if (strtok.hasMoreTokens() && getterMethod == null) {
                throw new IllegalArgumentException("Cannot create property selector for "+classProxy.className+" with selector "+selector+" becuase the class has no appropriate getter method.");
            }

            String _propType = propType;
            if (strtok.hasMoreTokens()) {
                _propType = getterMethod.getReturnType().toString();
            }

            out = (out == null) ? new JavaPropertySelector(classProxy, tok, _propType) : new JavaPropertySelector(out, tok, _propType);
        }
        return out;
    }

    /**
     * A class that encapsulates a property, or property chain.
     */
    private class JavaPropertySelector {
        /**
         * The starting point of the property chain.
         */
        private JavaClassProxy classProxy;

        /**
         * The selector, in the form a.b.c.d.... etc.
         */
        private String selector;

        /**
         * The parent property selector.
         */
        private JavaPropertySelector parent;

        /**
         * The getter method.
         */
        private JavaMethodProxy getter;


        private boolean getterLoaded, setterLoaded;

        /**
         * The setter method.
         */
        private JavaMethodProxy setter;

        /**
         * The property type.
         */
        private String propType = "java.lang.String";


        /**
         * Creates a new property selector.  Use {@link #createPropertySelector(JavaClassProxy, String)} instead.
         * @param parent The parent selector.
         * @param selector The selector string.  Format a.b.c.d
         */
        JavaPropertySelector(JavaPropertySelector parent, String selector) {
            this(parent, selector, "java.lang.String");
        }

        /**
         * Creates a new property selector.  Use {@link #createPropertySelector(JavaClassProxy, String, String)}
         * @param parent THe parent selector
         * @param selector The selector string.  Format a.b.c.d
         * @param propType The property format.  E.g. java.lang.String.
         */
        JavaPropertySelector(JavaPropertySelector parent, String selector, String propType) {
            this.propType = propType;
            this.parent = parent;
            this.selector = selector;
            if (selector.contains(".")) {
                throw new IllegalArgumentException("JavaPropertySelector constructor cannot take multipart selectors.  Use createPropertySelector() instead");
            }
            TypeElement classProxyType = elements().getTypeElement(parent.getPropertyType(false));
            if (classProxyType == null) {
                throw new IllegalStateException("Cannot determine class proxy type for property selector from the parent.  Looking for "+parent.getPropertyType(false));
            }
            this.classProxy = new JavaClassProxy(classProxyType, parent.classProxy.env);
        }
        JavaPropertySelector(JavaClassProxy classProxy, String selector) {
            this(classProxy, selector, "java.lang.String");
        }
        JavaPropertySelector(JavaClassProxy classProxy, String selector, String propType) {
            this.propType = propType;
            this.classProxy = classProxy;
            this.selector = selector;
            if (selector.contains(".")) {
                throw new IllegalArgumentException("JavaPropertySelector constructor cannot take multipart selectors.  Use createPropertySelector() instead");
            }
        }

        /**
         * Gets the getter method for the propery.
         * @return
         */
        JavaMethodProxy getter(){
            if (!getterLoaded) {
                getterLoaded = true;
                getter = classProxy.findMethodProxy("get"+selector, 0);
                if (getter == null) {
                    getter = classProxy.findMethodProxy("is"+selector, 0);
                }
            }
            return  getter;


        }

        /**
         * Gets the setter method for the property.
         * @return
         */
        JavaMethodProxy setter() {
            if (!setterLoaded) {
                setterLoaded = true;
                ExecutableElement setterEl = classProxy.findSetter(selector, propType);
                if (setterEl != null) {
                    setter = new JavaMethodProxy(classProxy, setterEl);
                }
            }
            return  setter;

        }

        /**
         * Gets the property type.
         * @param forWriting True for the property type used for setting the property.
         * @return The property type.  E.g. java.lang.String
         */
        public String getPropertyType(boolean forWriting) {
            if (forWriting) {
                if (!isWritable()) {
                    throw new IllegalStateException("Cannot get property type for reading on property "+this.selector+" of class "+classProxy.typeEl+" because the property is not writable");
                }
                return setter().getParameterType(0).getQualifiedName().toString();
            } else {
                if (!isReadable()) {
                    throw new IllegalStateException("Cannot get property type for reading on property "+this.selector+" of class "+classProxy.typeEl+" because the property is not readable");
                }
                return getter().getReturnType().getQualifiedName().toString();
            }
        }

        /**
         * Gets the null value equivalent for the property type.  E.g. For integers, it would be 0.  For boolean, it
         * would be false.  Etc..
         * @param forWriting
         * @return The null value as a string.
         */
        public String getPropertyNullValue(boolean forWriting) {
            String propertyType = getPropertyType(forWriting);
            TypeElement typeEl = elements().getTypeElement(propertyType);

            switch (typeEl.asType().getKind()) {
                case INT:
                case SHORT:
                case LONG:
                case FLOAT:
                case DOUBLE:
                case BYTE:
                case CHAR:
                    return "0";
                case BOOLEAN:
                    return "false";

            }
            return null;
        }

        /**
         * Checks if property is writable.
         * @return
         */
        boolean isWritable() {
            return setter() != null;
        }


        /**
         * Checks if property is readable.
         * @return
         */
        boolean isReadable() {
            return getter() != null;
        }

        /**
         * Gets the class of the owner of the propert.
         * @return
         */
        JavaClassProxy getClassProxy() {
            return classProxy;
        }

        /**
         *
         * Gets the parent selector.
         * @return
         */
        JavaPropertySelector getParent() {
            return parent;
        }

        /**
         * Checks if this is a root selector (has no parent).
         * @return
         */
        boolean isRootSelector() {
            return parent == null;
        }

        JavaPropertySelector getRoot() {
            if (isRootSelector()) return this;
            return parent.getRoot();
        }


        /**
         * Writes to a stringbuilder an assignment operation which assigns the result of this property selector to the targetVar.
         * If the result of the selector would be null, then defaulValue is assigned instead.
         * @param appendTo A StringBuilder to append the expression to
         * @param receiverVar The variable name of the object on which the property selector is evaluated. (I.e. the starting point of the property selection.
         * @param targetVar The target variable name - the variable being assigned to.
         * @param defaultValue The default value in case the selector comes up null.
         */
        void assignVar(StringBuilder appendTo, String receiverVar, String targetVar, String defaultValue) {
            if (!isReadable()) {
                throw new IllegalStateException("Cannot appendGetter on property selector "+this+" because the property is not readable (i.e. has no suitable getter method).");
            }
            if (parent != null) {
                // If this is a chain of selectors, then we need to take care to deal with a null
                // value anywhere along the line.
                // So we process each stage of the selector, check for null, and if a null is found, it returns the default value.
                ArrayList<JavaPropertySelector> chain = new ArrayList<>();
                JavaPropertySelector currSelector = this;
                while (currSelector != null) {
                    chain.add(currSelector);
                    currSelector = currSelector.getParent();
                }
                String varName = "__tmp_" + targetVar; // Temporary placeholder for us to iterate and update the final value
                String indexVarName = "__tmp_" + targetVar + "Counter"; // Name for the counter variable.
                appendTo.append("{Object ").append(varName).append("= null;"); // initialize placeholder to null
                // We loop through the chain from the beginning.
                appendTo.append("for (int ").append(indexVarName).append("=0; ").append(indexVarName).append("<").append(chain.size()).append(";").append(indexVarName).append("++){");

                // Check if the last link in the chain returned null
                // If it did, we just set the targetVar to the default value.
                appendTo.append("if (").append(indexVarName).append(">0 && ").append(varName).append("==null){").append(targetVar).append("=").append(defaultValue).append(";break;}");


                appendTo.append("switch (").append(indexVarName).append(") {");
                int len = chain.size();
                for (int i = 0; i < len; i++) {
                    int selectorIndex = len - i - 1;
                    JavaPropertySelector selector = chain.get(selectorIndex);

                    appendTo.append("case ").append(i).append(": ");
                    String thisVarName = varName;

                    if (i == 0) {
                        thisVarName = receiverVar;

                    }
                    appendTo.append(varName).append("=((").append(selector.getClassProxy().className).append(")").append(thisVarName).append(").").append(selector.getter().methodEl.getSimpleName()).append("();break;");


                }
                appendTo.append("}");
                appendTo.append("if (").append(indexVarName).append("==").append(chain.size() - 1).append(") ")
                        .append(targetVar).append("=(").append(getter().getReturnType().toString()).append(")((").append(varName).append("==null)?").append(defaultValue).append(":").append(varName).append(");}");
                appendTo.append("}");
            } else {
                String varName = "__tmp_" + targetVar;
                appendTo.append("{").append("Object ").append(varName).append("=").append(receiverVar).append(".").append(getter().methodEl.getSimpleName().toString()).append("();");
                appendTo.append(targetVar).append("=(").append(varName).append("==null)?").append(defaultValue).append(":(").append(getter().getReturnType().toString()).append(")").append(varName).append(";}");
            }
        }


        /**
         * Appends expression to stringbuilder which sets a property with the given value.
         * @param appendTo Buffer to append expression to.
         * @param receiverVar The name of the variable containing the receiver (the class that is the starting point).
         * @param value The value to set the property to.
         */
        void setProperty(StringBuilder appendTo, String receiverVar, String value) {
            if (parent != null) {
                String varName = "__tmpReceiver_"+receiverVar;
                appendTo.append("{").append(classProxy.typeEl.getQualifiedName()).append(" ").append(varName).append(" = null;");
                parent.assignVar(appendTo, receiverVar, varName, "null");
                appendTo.append("if (").append(varName).append("!=null)").append(varName).append(".").append(setter().methodEl.getSimpleName()).append("(").append(value).append(");}");
            } else {
                appendTo.append(receiverVar).append(".").append(setter().methodEl.getSimpleName()).append("(").append(value).append(");");
            }
        }


    }


    private enum JavaClassType {
        ENTITY,
        COMPONENT,
        NODE,
        OTHER
    }

    /**
     * Encapsulates a Java Class with some convenience methods.  Wraps a TypeElement, but adds the context of a {@link JavaEnvironment}.
     */
    private class JavaClassProxy {
        private String className;
        private TypeElement typeEl;
        private JavaEnvironment env;
        private JavaClassType classType;


        String getQualifiedName() {
            return typeEl.getQualifiedName().toString();
        }

        JavaClassType getClassType() {
            if (classType == null) {
                if (isA(typeEl, "com.codename1.ui.Component")) classType = JavaClassType.COMPONENT;
                else if (isA(typeEl, "com.codename1.rad.models.Entity")) classType = JavaClassType.ENTITY;
                else if (isA(typeEl, "com.codename1.rad.models.Node")) classType = JavaClassType.NODE;
                else classType = JavaClassType.OTHER;
            }
            return classType;
        }

        @Override
        public String toString() {
            return typeEl.toString();
        }

        boolean isEntity() {
            return getClassType() == JavaClassType.ENTITY;
        }

        boolean isComponent() {
            return getClassType() == JavaClassType.COMPONENT;
        }

        boolean isNode() {
            return getClassType() == JavaClassType.NODE;
        }

        String getSimpleName() {
            return typeEl.getSimpleName().toString();
        }

        /**
         * Checks if this is a subclass of com.codename1.rad.ui.ComponentBuilder
         * @return
         */
        boolean isComponentBuilder() {
            return isA(typeEl, "com.codename1.rad.ui.ComponentBuilder");
        }

        /**
         * Creates a new classproxy
         * @param className The name of hte class.
         * @param env The environment used to lookup the class.
         * @throws ClassNotFoundException If class could not be found.
         */
        JavaClassProxy(String className, JavaEnvironment env) throws ClassNotFoundException {
            this.className = className;
            this.env = env;
            if (env == null) {
                throw new IllegalArgumentException("JavaClassProxy constructor requires non-null JavaEnvironment");
            }
            this.typeEl = env.findClassBySimpleName(className);
            if (this.typeEl == null) {
                throw new ClassNotFoundException();
            }
        }

        JavaClassProxy(TypeElement typeEl, JavaEnvironment jenv) {
            this.className = typeEl.getSimpleName().toString();
            this.env = jenv;
            if (env == null) {
                throw new IllegalArgumentException("JavaClassProxy constructor requires non-null JavaEnvironment");
            }
            this.typeEl = typeEl;
        }

        /**
         * Finds setter methods for the given property.
         * @param propertyName
         * @return
         */
        List<ExecutableElement> findSetters(String propertyName) {
            return  (List<ExecutableElement>)elements().getAllMembers(typeEl).stream()
                    .filter(e -> e.getKind() == ElementKind.METHOD &&
                            (e.getSimpleName().toString().equalsIgnoreCase(propertyName) || e.getSimpleName().toString().equalsIgnoreCase("set"+propertyName)) &&
                            ((ExecutableElement)e).getParameters().size() == 1).collect(Collectors.toList());
        }

        List<ExecutableElement> findInjectableSettersForType(TypeElement type) {
            return (List<ExecutableElement>)elements().getAllMembers(typeEl).stream()
                    .filter(e -> e.getKind() == ElementKind.METHOD &&
                            ((ExecutableElement)e).getParameters().size() == 1 &&
                            ((ExecutableElement)e).getParameters().get(0).getAnnotation(Inject.class) != null &&
                            isA(type, ((ExecutableType)types().asMemberOf((DeclaredType)typeEl.asType(), e)).getParameterTypes().get(0).toString())).collect(Collectors.toList());
        }

        List<ExecutableElement> findGetters(String propertyName) {
            return  (List<ExecutableElement>)elements().getAllMembers(typeEl).stream()
                    .filter(e -> e.getKind() == ElementKind.METHOD &&
                            (e.getSimpleName().toString().equalsIgnoreCase(propertyName) || e.getSimpleName().toString().equalsIgnoreCase("get"+propertyName) || e.getSimpleName().toString().equalsIgnoreCase("is"+propertyName)) &&
                            (((ExecutableElement)e).getParameters().size() == 0) &&
                            ((ExecutableElement)e).getReturnType().getKind() != TypeKind.VOID).collect(Collectors.toList());
        }

        /**
         * Finds setter method for the given property
         * @param propertyName
         * @param type
         * @return
         */
        ExecutableElement findSetter(String propertyName, String... type) {
            for (String t : type) {
                ExecutableElement out = findSetters(propertyName).stream()
                        .filter(e -> e.getParameters().get(0).asType().toString().equalsIgnoreCase(t) || isA(e.getParameters().get(0).asType(), t)).findAny().orElse(null);
                if (out != null) {
                    return out;
                }
            }
            return findSetters(propertyName).stream().findAny().orElse(null);

        }

        ExecutableElement findGetter(String propertyName, String... type) {
            for (String t : type) {
                ExecutableElement out = findGetters(propertyName).stream()
                        .filter(e -> e.getParameters().get(0).asType().toString().equalsIgnoreCase(t) || isA(e.getParameters().get(0).asType(), t)).findAny().orElse(null);
                if (out != null) {
                    return out;
                }
            }
            return findGetters(propertyName).stream().findAny().orElse(null);

        }

        /**
         * Finds method matching query.
         * @param methodName The method name.
         * @param numParams Number of parameters in the method.
         * @return ExecutableElement for matching method, or null.
         */
        ExecutableElement findMethod(String methodName, int numParams) {
            return (ExecutableElement) elements().getAllMembers(typeEl).stream()
                    .filter(e -> e.getKind() == ElementKind.METHOD &&
                            methodName.equalsIgnoreCase(e.getSimpleName().toString()) &&
                            ((ExecutableElement)e).getParameters().size() == numParams)

                    .findAny().orElse(null);
        }

        JavaMethodProxy findMethodProxy(String methodName, int numParams) {
            ExecutableElement el = findMethod(methodName, numParams);
            if (el == null) {
                return null;
            }
            return new JavaMethodProxy(this, el);
        }


        org.w3c.dom.Element createXmlTag() {
            return env.rootElement.getOwnerDocument().createElement(typeEl.getSimpleName().toString());
        }

        JavaMethodProxy getBestConstructor(org.w3c.dom.Element xmlTag) {
            Comparator<JavaMethodProxy> comparator = (c1, c2) -> {
                return c2.methodEl.getParameters().size() - c1.methodEl.getParameters().size();
            };
            List<JavaMethodProxy> constructors = getEligibleConstructors(xmlTag);
            constructors.sort(comparator);
            if (constructors.isEmpty()) {
                return null;

            }
            return constructors.get(0);

        }

        List<JavaMethodProxy> getPublicConstructors() {
            List<JavaMethodProxy> out = new ArrayList<JavaMethodProxy>();
            List<ExecutableElement> constructors = (List<ExecutableElement>)elements().getAllMembers(typeEl).stream()
                    .filter(e -> e.getKind() == ElementKind.CONSTRUCTOR && e.getModifiers().contains(Modifier.PUBLIC)).collect(Collectors.toList());
            for (ExecutableElement el : constructors) {
                out.add(new JavaMethodProxy(this, el));
            }
            return out;
        }

        List<JavaMethodProxy> getEligibleConstructors(org.w3c.dom.Element xmlTag) {
            List<JavaMethodProxy> out = new ArrayList<JavaMethodProxy>();
            Set<String> namedParams = extractNamedParameters(xmlTag).stream().map(String::toLowerCase).collect(Collectors.toSet());
            Set<Integer> indexedParams = extractIndexedParameters(xmlTag);
            List<String> injectableTypes = new ArrayList<String>();
            injectableTypes.add("com.codename1.rad.ui.EntityView");
            injectableTypes.add("com.codename1.rad.ui.ViewContext");
            injectableTypes.add("com.codename1.rad.models.Entity");
            injectableTypes.add("com.codename1.rad.models.BaseEntity");
            injectableTypes.add("com.codename1.rad.controllers.ApplicationController");
            injectableTypes.add("com.codename1.rad.controllers.Controller");
            injectableTypes.add("com.codename1.rad.controllers.FormController");
            injectableTypes.add("com.codename1.rad.controllers.AppSectionController");
            injectableTypes.add("com.codename1.rad.controllers.ViewController");



            for (JavaMethodProxy constructor : getPublicConstructors()) {
                boolean eligible = true;
                int index = -1;
                for (String paramName : constructor.getParameterNames()) {
                    index++;
                    TypeElement paramType = constructor.getParameterType(index);
                    if (namedParams.contains(paramName.toLowerCase())) {
                        continue;
                    }
                    if (indexedParams.contains(index)) {
                        continue;
                    }
                    if (!constructor.isParameterInjectable(index)) {
                        // Parameter hasn't been explicitly supplied, nor is it
                        // injectable so this constructor is not eligible.
                        eligible = false;
                        break;
                    }
                    if (constructor.isArrayParameter(index)) {
                        // Array types are always injectable because we can just inject an empty array.
                        continue;
                    }


                    String paramTypeStr = paramType.toString();



                    if (injectableTypes.contains(paramType.toString())) {
                        // This parameter can be injected directly as it is one of the directly injectable types.
                        continue;
                    }
                    if (isA(env.rootBuilder.parentClass, paramType.toString())) {
                        // This parameter could be filled by this entity view because it is a subclass of the parameter type
                        continue;
                    }
                    boolean matchingInterface = false;
                    for (String iface : env.rootBuilder.viewImplements.split(",")) {
                        iface = iface.trim();
                        TypeElement ifaceType = env.lookupClass(iface);
                        if (ifaceType != null) {
                            if (isA(ifaceType, paramType.toString())) {
                                matchingInterface = true;
                                break;
                            }
                        }
                    }
                    if (matchingInterface) {
                        // The root view implements an interface that will allow it to be used for this parameter.
                        continue;
                    }

                    TypeElement viewModelType = env.lookupClass(env.rootBuilder.viewModelType);

                    if (isA(viewModelType, paramType.getQualifiedName().toString())) {
                        // We can use the view model for this parameter
                        continue;
                    }

                    if (isA(paramType, "com.codename1.rad.models.Entity")) {
                        // The parameter type is a subclass of Entity
                        // but the root view's model class isn't assignable to the parameter
                        // (We already checked for direct match, and whether it is assignable)
                        // We may still be able to assign it if there is a Wrapper class available
                        // for the parameter type, which we can use to wrap our model.
                        TypeElement wrapperType = env.lookupClass(env.rootBuilder.viewModelType+"Wrapper");
                        if (wrapperType != null) {
                            // There IS a wrapper class available so we can use our own view model here.
                            continue;
                        }
                    }

                    boolean foundInjectableType = false;
                    for (JavaClassProxy injectableType : env.rootBuilder.injectableTypes.values()) {
                        if (isA(injectableType.typeEl, paramType.getQualifiedName().toString())) {
                            foundInjectableType = true;
                            break;
                        }
                    }
                    if (foundInjectableType) {
                        continue;
                    }


                    eligible = false;
                    break;
                    //


                }
                if (eligible) {
                    out.add(constructor);
                }
            }
            return out;
        }




        /**
         * Appends to the buffer the java code to set all of the properties in the given element.
         * @param appendTo
         * @param tag
         * @param receiverVar
         */
        void setProperties(StringBuilder appendTo, org.w3c.dom.Element tag, String receiverVar) {
            NamedNodeMap attributes = tag.getAttributes();
            int len = attributes.getLength();
            for (int i=0; i<len; i++) {
                Attr attr = (Attr)attributes.item(i);
                if (!attr.getName().contains("-")) {
                    appendTo.append("        ");
                    setProperty(appendTo, attr, receiverVar);
                    appendTo.append("\n");
                }
            }
        }


        void setProperty(StringBuilder appendTo, Attr attribute, String receiverVar) {
            setProperty(appendTo, attribute, receiverVar, "java.lang.String");
        }
        void setProperty(StringBuilder appendTo, Attr attribute, String receiverVar, String propType) {
            setProperty(appendTo, attribute.getName(), attribute.getValue(), receiverVar, propType);
        }


        void setProperty(StringBuilder appendTo, String attName, String attValue, String receiverVar) {
            setProperty(appendTo, attName, attValue, receiverVar,"java.lang.String");
        }
        void setProperty(StringBuilder appendTo, String attName, String attValue, String receiverVar, String propType) {
            String propertyName = attName;

            JavaPropertySelector propertySelector = createPropertySelector(this, propertyName, propType);

            if (!propertySelector.isWritable() && isComponentBuilder()) {
                propertySelector = createPropertySelector(this, "component."+propertyName, propType);
            }




            String value = attValue;
            if (!propertySelector.isWritable()) {

                if (isEntity()) {
                    // If we are dealing with an entity, then we may be able to interpret this property as a tag path.
                    appendTo.append("{");
                    if (value.startsWith("java:")) {
                        appendTo.append("Object _newVal = ").append(value.substring(value.indexOf(":")+1)).append(";");
                        appendTo.append("ContentType _contentType = ").append("_newVal == null ? ContentType.Text : ContentType.createObjectType(_newVal);");
                    }
                    env.createRADPropertySelector(appendTo, attName.replace('.', '/'), receiverVar);
                    if (value.startsWith("string:")) {
                        appendTo.append(".setText(\"").append(StringEscapeUtils.escapeJava(value.substring(value.indexOf(":")+1))).append("\")");
                    } else if (value.startsWith("java:")) {
                        appendTo.append(".set(_contentType, ").append(value.substring(value.indexOf(":")+1)).append(")");
                    } else {
                        appendTo.append(".setText(\"").append(StringEscapeUtils.escapeJava(value)).append("\")");
                    }
                    appendTo.append("}");

                }

                throw new IllegalArgumentException("Cannot set property "+propertyName+" on class "+typeEl.getQualifiedName()+" because it has no suitable setter method.");
            }

            JavaMethodProxy setter = propertySelector.setter();
            if (setter == null) {
                throw new IllegalStateException("Cannot find setter method for property selector while working on setting property for attribute "+attName+" with value "+attValue);
            }
            TypeElement paramType = setter.getParameterType(0);
            if (paramType == null) {
                throw new IllegalStateException("No parameter type found for first parameter of setter method "+setter);
            }

            boolean treatAsString = paramType.getQualifiedName().contentEquals("java.lang.String");


            if (value.startsWith("java:")) {
                treatAsString = false;
                value = value.substring(value.indexOf(":")+1);
            } else if (value.startsWith("string:")) {
                treatAsString = true;
                value = value.substring(value.indexOf(":")+1);
            }

            // Special cases:
            // Dates
            // Numbers rgb(...), rgba(....), #FFFFFF, 0xFFFFFF, 0FFFFFF
            // Tags
            // Action categories
            // Enums
            //
            if (!treatAsString) {
                TypeElement parameterType = propertySelector.setter().getParameterType(0);
                List<String> enumValues =
                        parameterType.getEnclosedElements().stream()
                                .filter(element -> element.getKind().equals(ElementKind.ENUM_CONSTANT))
                                .map(Object::toString)
                                .collect(Collectors.toList());
                String fValue = value;
                String enumConstant = enumValues.stream().filter(s -> s.equalsIgnoreCase(fValue)).findFirst().orElse(null);
                if (enumConstant != null) {
                    value = parameterType.getQualifiedName() + "." + enumConstant;
                }


            }
            if (treatAsString) {
                value = "\"" + StringEscapeUtils.escapeJava(value) + "\"";
            }


            propertySelector.setProperty(appendTo, receiverVar, value);





        }

        public boolean isContainer() {
            return isComponent() && isA(typeEl, "com.codename1.ui.Container");
        }


    }

    private class JavaMethodProxy {
        private JavaClassProxy classProxy;
        private String methodName;
        private ExecutableElement methodEl;
        private ExecutableType executableType;

        JavaMethodProxy(JavaClassProxy classProxy, String methodName, int numParams) throws NoSuchMethodException {
            if (classProxy == null) {
                throw new IllegalArgumentException("JavaMethodProxy constructor requires non-null classProxy");
            }
            this.classProxy = classProxy;
            this.methodName = methodName;
            this.methodEl = classProxy.findMethod(methodName, numParams);
            if (this.methodEl == null) {
                throw new NoSuchMethodException(methodName);
            }

        }

        JavaMethodProxy(JavaClassProxy classProxy, ExecutableElement methodEl) {
            this.classProxy = classProxy;
            if (this.classProxy == null) {
                throw new IllegalArgumentException("JavaMethodProxy constructor requires non-null classProxy");
            }
            this.methodName = methodEl.getSimpleName().toString();
            this.methodEl = methodEl;
        }

        @Override
        public String toString() {
            return "JavaMethodProxy{" +
                    "classProxy=" + classProxy +
                    ", methodName='" + methodName + '\'' +
                    ", methodEl=" + methodEl +
                    ", executableType=" + executableType +
                    '}';
        }

        ExecutableType executableType() {
            if (executableType == null) {
                executableType = (ExecutableType)types().asMemberOf((DeclaredType)classProxy.typeEl.asType(), methodEl);
            }
            return executableType;
        }

        TypeElement getReturnType() {
            switch (methodEl.getReturnType().getKind()) {
                case VOID: return null;

            }

            ExecutableType et = executableType();

            return toTypeElement(executableType().getReturnType());
        }

        ExecutableType getExecutableType() {
            ExecutableType et =   (ExecutableType)types().asMemberOf((DeclaredType)classProxy.typeEl.asType(), methodEl);

            return et;
        }

        TypeElement getParameterType(int paramIndex) {
            if (methodEl.getParameters().size() <= paramIndex) {
                throw new IllegalArgumentException("Cannot get parameter "+paramIndex+" from "+methodEl+" because it only has "+methodEl.getParameters().size()+" parameters");
            }

            return getParameterTypes().get(0);

        }

        boolean isParameterInjectable(int index) {
            return (methodEl.getParameters().get(index).getAnnotation(Inject.class) != null);
        }

        boolean isArrayParameter(int index) {
            List<? extends TypeMirror> mirrors = getExecutableType().getParameterTypes();
            return mirrors.get(index).getKind() == TypeKind.ARRAY;
        }

        List<TypeElement> getParameterTypes() {
            List<? extends TypeMirror> mirrors = getExecutableType().getParameterTypes();
            List<TypeElement> out = new ArrayList<TypeElement>(mirrors.size());
            for (TypeMirror m : mirrors) {
                if (m.getKind() == TypeKind.TYPEVAR) {
                    TypeVariable typeVar = (TypeVariable)m;
                    m = typeVar.getUpperBound();
                } else if (m.getKind() == TypeKind.ARRAY) {
                    ArrayType arrayType = (ArrayType)m;
                    m = arrayType.getComponentType();
                }
                if (m.getKind() == TypeKind.TYPEVAR) {
                    TypeVariable typeVar = (TypeVariable) m;
                    m = typeVar.getUpperBound();
                }
                TypeElement el = toTypeElement(m);
                if (el == null) {
                    throw new IllegalStateException("Failed to find type element for TypeMirror "+m+" kind="+m.getKind()+", method="+methodEl);
                }
                out.add(toTypeElement(m));
            }
            return out;
        }

        int getNumParams() {
            return methodEl.getParameters().size();
        }

        List<String> getParameterNames() {
            List<VariableElement> params = (List<VariableElement>)methodEl.getParameters();
            if (params == null) {
                throw new IllegalStateException("method getParameters() returned null for method "+methodEl+" of class "+classProxy.typeEl);
            }
            List<String> out = new ArrayList<String>(params.size());
            for (VariableElement e : params) {
                out.add(e.getSimpleName().toString());
            }
            return out;

        }


        void callAsConstructor(StringBuilder appendTo, org.w3c.dom.Element xmlTag, boolean allowNullParams) {
            appendTo.append("new ").append(classProxy.getQualifiedName()).append("(");

            writeCallParams(appendTo, xmlTag, allowNullParams);
            appendTo.append(")");
        }

        void callStatic(StringBuilder appendTo, org.w3c.dom.Element xmlTag, boolean allowNullParams) {
            appendTo.append(classProxy.getQualifiedName()).append(".").append(methodEl.getSimpleName()).append("(");

            writeCallParams(appendTo, xmlTag, allowNullParams);
            appendTo.append(")");
        }

        void writeCallParams(StringBuilder appendTo, org.w3c.dom.Element xmlTag) {
            writeCallParams(appendTo, xmlTag, false);
        }


        void writeCallParams(StringBuilder appendTo, org.w3c.dom.Element xmlTag, boolean allowNull) {
            List<String> paramNames = getParameterNames();
            List<TypeElement> paramTypes = getParameterTypes();
            int numParams = paramNames.size();
            paramloop: for (int i=0; i<numParams; i++) {
                String arrayParamPrefix = "";
                String arrayParamSuffix = "";

                if (i > 0) {
                    appendTo.append(", ");
                }

                // Order of precedence:
                // 1. _attname_=attval
                // 2. attname=attval
                // 3. if entity, view-model=attval
                // 4. if entity and not entity list, getEntity() (use the view's entity)
                // 5. if viewcontext, construct a view context from the current context.
                // 6. if entityview, use this
                // 7. if ...
                String paramName = paramNames.get(i);
                TypeElement paramType = paramTypes.get(i);

                if (paramType == null) {
                    throw new IllegalArgumentException("Cannot find parameter type for "+paramName+" in tag "+xmlTag);
                }
                if (isArrayParameter(i)) {
                    arrayParamPrefix = "new "+paramType+"[]{";
                    arrayParamSuffix = "}";
                }
                boolean isEntity = isA(paramType, "com.codename1.rad.models.Entity");
                TypeElement entityWrapperClass = null;
                if (isEntity && !paramType.getQualifiedName().contentEquals("com.codename1.rad.models.Entity") && !paramType.getQualifiedName().contentEquals("com.codename1.rad.models.EntityList")) {
                    entityWrapperClass = classProxy.env.lookupClass(paramType.getQualifiedName()+"Wrapper");

                }


                org.w3c.dom.Element childElementParam = null;
                for (org.w3c.dom.Element child : getChildElements(xmlTag)) {
                    String childParam = child.getAttribute("rad-param");
                    if (childParam.equalsIgnoreCase(paramName) || (!childParam.isEmpty() && Character.isDigit(childParam.charAt(0)))) {
                        try {
                            if (Integer.parseInt(childParam) == i) {
                                childElementParam = child;
                                break;
                            }
                        } catch (NumberFormatException nfe){}
                    }

                }

                if (childElementParam != null) {
                    if (isA(paramType, "com.codename1.ui.Component")) {
                        appendTo.append(arrayParamPrefix).append("createComponent").append(childElementParam.getAttribute("rad-id")).append("()").append(arrayParamSuffix);
                    } else if (isA(paramType, "com.codename1.ui.Node")) {

                        appendTo.append(arrayParamPrefix).append("_setParent(").append(paramType.getQualifiedName()).append(".class, ").append("createNode").append(childElementParam.getAttribute("rad-id")).append("())").append(arrayParamSuffix);
                    } else {
                        appendTo.append(arrayParamPrefix).append("createBean").append(childElementParam.getAttribute("rad-id")).append("()").append(arrayParamSuffix);
                    }
                    continue;
                }
                if (xmlTag.hasAttribute("_"+paramName+"_")) {
                    appendTo.append(formatParamValue(i, xmlTag.getAttribute("_" + paramName + "_")));
                    continue;
                } else if (xmlTag.hasAttribute("_"+i+"_")) {
                    appendTo.append(formatParamValue(i, xmlTag.getAttribute("_" + i + "_")));
                    continue;
                }


                if (!isParameterInjectable(i)) {
                    throw new IllegalArgumentException("Cannot generate call parameters for tag "+xmlTag.getTagName()+" because the parameter "+paramName+" is not injectable and there is no suitable XML attribute or child element that can be used to fill it.");
                }



                if (isEntity) {

                    if (xmlTag.hasAttribute("view-model")) {
                        appendTo.append(arrayParamPrefix).append(formatParamValue(i, xmlTag.getAttribute("view-model"))).append(arrayParamSuffix);
                        continue;
                    }


                    if (paramType.getQualifiedName() == null) {
                        throw new IllegalStateException("Parameter "+paramName+" of tag "+xmlTag+" does not seem to have a qualified name: "+paramType);
                    }
                    List<org.w3c.dom.Element> entityChildren = classProxy.env.getChildrenOfType(xmlTag, paramType.getQualifiedName().toString());
                    if (!entityChildren.isEmpty()) {
                        for (org.w3c.dom.Element entityChild : entityChildren) {
                            if (entityChild.hasAttribute("rad-param") || entityChild.hasAttribute("rad-property")) {
                                // Don't use entities that were explicitly assigned to parameters and properties for injection
                                continue;
                            }
                            // With entities, we usually use an interface, but the convention is to generate an implementation
                            // class for it with a no-arg constructor.
                            appendTo.append(arrayParamPrefix).append("createBean").append(entityChild.getAttribute("rad-id")).append("()").append(arrayParamSuffix);
                            entityChild.setAttribute("rad-param", paramName); // So we don't double dip
                            continue paramloop;
                        }
                    }


                    if (entityWrapperClass != null) {

                        // If there is an entity wrapper class then we are more flexible on the types of entities we can
                        // use here.  Look through the child tags again to see if there are any entities that are unassigned
                        // that can be used.
                        entityChildren = classProxy.env.getChildrenOfType(xmlTag, "com.codename1.rad.models.Entity");
                        if (!entityChildren.isEmpty()) {
                            for (org.w3c.dom.Element entityChild : entityChildren) {
                                if (entityChild.hasAttribute("rad-param") || entityChild.hasAttribute("rad-property")) {
                                    // Don't use entities that were explicitly assigned to parameters and properties for injection
                                    continue;
                                }
                                // With entities, we usually use an interface, but the convention is to generate an implementation
                                // class for it with a no-arg constructor.
                                appendTo.append(arrayParamPrefix).append(entityWrapperClass.getQualifiedName()).append(".wrap(createBean").append(entityChild.getAttribute("rad-id")).append("())").append(arrayParamSuffix);
                                entityChild.setAttribute("rad-param", paramName); // So we don't double dip
                                continue paramloop;
                            }
                        }

                        if (isArrayParameter(i)) {
                            appendTo.append(entityWrapperClass.getQualifiedName()).append(".wrap(").append("_getInjectedArrayParameter(").append(paramType.getQualifiedName()).append(".class))");
                        } else {
                            appendTo.append(entityWrapperClass.getQualifiedName()).append(".wrap(").append("_getInjectedParameter(").append(paramType.getQualifiedName()).append(".class))");
                        }
                        continue;
                    }
                }

                if (isA(paramType, "com.codename1.rad.nodes.Node")) {
                    // For Node types, we need to create a child node.
                    List<org.w3c.dom.Element> candidateChildren = classProxy.env.getChildrenOfType(xmlTag, paramType.getQualifiedName().toString());
                    for (org.w3c.dom.Element candidateChild : candidateChildren) {
                        if (candidateChild.hasAttribute("rad-param") || candidateChild.hasAttribute("rad-property")) {
                            // IF this has a rad-param or rad-property attribute, then it is not for injection
                            // it is assigned to some explicit purpose.
                            continue;
                        }

                        appendTo.append(arrayParamPrefix).append("_setParent(").append(paramType.getQualifiedName()).append(".class, ").append("createNode").append(candidateChild.getAttribute("rad-id")).append("())").append(arrayParamSuffix);
                        continue paramloop;
                    }

                    JavaClassProxy paramTypeProxy = new JavaClassProxy(paramType, classProxy.env);
                    if (paramTypeProxy.typeEl.getModifiers().contains(Modifier.ABSTRACT)) {
                        paramTypeProxy = new JavaClassProxy(classProxy.env.lookupClass("com.codename1.rad.nodes.ViewNode"), classProxy.env);

                    }
                    JavaMethodProxy nodeConstructor = paramTypeProxy.getBestConstructor(xmlTag.getOwnerDocument().createElement(paramTypeProxy.getSimpleName()));
                    if (nodeConstructor == null) {
                        throw new IllegalArgumentException("Cannot find suitable constructor for class "+paramTypeProxy.getQualifiedName()+" in order to inject argument "+paramName+" into tag "+xmlTag.getTagName());
                    }

                    appendTo.append(arrayParamPrefix).append("_setParent(").append(paramTypeProxy.getQualifiedName()).append(".class, ");
                    nodeConstructor.callAsConstructor(appendTo, xmlTag.getOwnerDocument().createElement(paramTypeProxy.getSimpleName()), false);
                    appendTo.append(")").append(arrayParamSuffix);
                    continue;
                }

                if (isA(paramType, "com.codename1.rad.ui.ViewContext")) {
                    JavaClassProxy paramClass = new JavaClassProxy(paramType, classProxy.env);
                    // Look for a child xml tag that matches this type to use as our injected context
                    List<org.w3c.dom.Element> vcTags = classProxy.env.getChildrenOfType(xmlTag, paramType.getQualifiedName().toString());
                    if (vcTags.isEmpty()) {
                        // there were no tags for this view context, so we'll create one
                        org.w3c.dom.Element vcTag = xmlTag.getOwnerDocument().createElement(paramType.getQualifiedName().toString());
                        List<org.w3c.dom.Element> nodeChildren = classProxy.env.getChildrenOfType(xmlTag, "com.codename1.rad.nodes.Node");
                        nodeChildren.addAll(classProxy.env.getChildrenOfType(xmlTag, "com.codename1.rad.models.Entity"));
                        nodeChildren.addAll(classProxy.env.getChildrenOfType(xmlTag, "com.codename1.rad.controllers.Controller"));
                        for (org.w3c.dom.Element nodeChild : nodeChildren) {
                            if (nodeChild.hasAttribute("rad-param") || nodeChild.hasAttribute("rad-property")) {
                                // Don't copy nodes that are explicit parameters and arguments
                                // We only want nodes that might be used for injection
                                continue;
                            }
                            vcTag.appendChild(nodeChild.cloneNode(true));
                        }
                        xmlTag.appendChild(vcTag);

                        if (xmlTag.hasAttribute("view-model")) {
                            vcTag.setAttribute("view-model", xmlTag.getAttribute("view-model"));
                        }
                        vcTags.add(vcTag);

                    }
                    JavaMethodProxy contextConstructor = paramClass.getBestConstructor(vcTags.get(0));
                    if (contextConstructor == null) {
                        StringBuilder err = new StringBuilder();
                        err.append("Type variables of "+paramClass.typeEl+": "+paramClass.typeEl.getTypeParameters()).append("\n");
                        for (TypeParameterElement tpe : paramClass.typeEl.getTypeParameters()) {
                            err.append("Bounds:").append(tpe.getBounds()).append("\n");
                        }
                        err.append("Found constructors:\n");

                        for (JavaMethodProxy cnst : paramClass.getPublicConstructors()) {

                            err.append(cnst.methodEl).append("\n");
                            err.append("Type variables of ").append(cnst.methodEl).append(":").append(cnst.executableType().getTypeVariables()).append("\n");
                            err.append("Parameter types: ").append(cnst.getParameterTypes()).append("\n");

                        }
                        err.append("Type Variables of : "+methodEl+":" + executableType().getTypeVariables()).append("\n");
                        throw new IllegalArgumentException("Failed to find constructor for parameter "+paramClass.getQualifiedName()+" using tag "+vcTags.get(0)
                                +".\n  The parameter was  "+methodEl.getParameters().get(i)+" and type was "+methodEl.getParameters().get(i).asType() + ".\n"
                                +"The method was "+methodEl+"\n"
                                +err
                        );
                    }
                    appendTo.append(arrayParamPrefix);
                    contextConstructor.callAsConstructor(appendTo, vcTags.get(0), false);
                    appendTo.append(arrayParamSuffix);
                    continue;


                }

                if (isArrayParameter(i)) {
                    appendTo.append(arrayParamPrefix).append("_getInjectedArrayParameter(").append(paramType.getQualifiedName()).append(".class)").append(arrayParamSuffix);
                } else {
                    appendTo.append(arrayParamPrefix).append("_getInjectedParameter(").append(paramType.getQualifiedName()).append(".class)").append(arrayParamSuffix);
                }
                continue;

            }


        }

        private String formatParamValue(int index, String value) {
            String paramName = getParameterNames().get(index);
            TypeElement paramType = getParameterType(index);
            boolean isEntity = isA(paramType, "com.codename1.rad.models.Entity");
            boolean treatAsString = paramType.getQualifiedName().contentEquals("java.lang.String");
            if (value.startsWith("java:")) {
                treatAsString = false;
                value = value.substring(value.indexOf(":")+1);
            } else if (value.startsWith("string:")) {
                treatAsString = true;
                value = value.substring(value.indexOf(":")+1);
            }
            if (treatAsString) value = '"' + StringEscapeUtils.escapeJava(value) + '"';
            TypeElement entityWrapperClass = null;
            if (isEntity && !paramType.getQualifiedName().contentEquals("com.codename1.rad.models.Entity") && !paramType.getQualifiedName().contentEquals("com.codename1.rad.models.EntityList")) {
                entityWrapperClass = classProxy.env.lookupClass(paramType.getQualifiedName()+"Wrapper");


            }
            if (isEntity) {
                if (entityWrapperClass != null) {
                    return entityWrapperClass.getQualifiedName()+".wrap("+value+")";
                } else {
                    return value;
                }
            }
            return value;
        }
    }

    private class JavaBuilder {
        protected JavaEnvironment jenv;
        protected int indent = 0;
        protected org.w3c.dom.Element xmlTag;

        void writeVariables(StringBuilder sb) {
            writeVariables(sb, xmlTag);
        }

        void writeVariables(StringBuilder sb, org.w3c.dom.Element el) {
            NodeList childNodes = el.getChildNodes();
            int numChildNodes = childNodes.getLength();
            indent(sb, indent).append("// ").append(numChildNodes).append(" child nodes\n");
            for (int i=0; i<numChildNodes; i++) {
                Node n = childNodes.item(i);
                if (!(n instanceof org.w3c.dom.Element)) {
                    continue;
                }
                org.w3c.dom.Element childEl = (org.w3c.dom.Element)childNodes.item(i);

                if (childEl.getTagName().equalsIgnoreCase("private") || childEl.getTagName().equalsIgnoreCase("public") || childEl.getTagName().equalsIgnoreCase("protected")) {
                    writeVariables(sb, childEl);
                    continue;
                }

                if (childEl.getTagName().equalsIgnoreCase("var")) {
                    if (childEl.hasAttribute("lookup") && childEl.hasAttribute("name")) {
                        indent(sb, indent).append(childEl.getAttribute("name")).append("=").append("getContext().getController().lookup(").append(childEl.getAttribute("lookup")).append(".class);\n");
                    } else if (childEl.hasAttribute("value") && childEl.hasAttribute("name")) {
                        String value = childEl.getAttribute("value");
                        if (value.startsWith("string:")) {
                            value = "\"" + StringEscapeUtils.escapeJava(value.substring(value.indexOf(":")+1))+"\"";
                        } else if (value.startsWith("java:")) {
                            value = value.substring(value.indexOf(":")+1);
                        }
                        indent(sb, indent).append(childEl.getAttribute("name")).append("=").append(value).append(";\n");
                    } else {
                        indent(sb, indent).append("// <var name='").append(childEl.getAttribute("name")).append("'> skipped because no value or lookup key specified\n");
                    }
                    continue;
                }


            }
        }

    }

    private class JavaBeanBuilder extends JavaBuilder {

        private JavaClassProxy beanClass;


        private List<JavaNodeBuilder> children = new ArrayList<JavaNodeBuilder>();
        JavaBeanBuilder(org.w3c.dom.Element xmlTag, JavaEnvironment jenv, JavaClassProxy beanClass) throws ClassNotFoundException{
            this.xmlTag = xmlTag;
            this.jenv = jenv;
            this.beanClass = beanClass == null ? new JavaClassProxy(jenv.findClassThatTagCreates(xmlTag.getTagName()), jenv) : beanClass;
            if (beanClass.typeEl.getKind() == ElementKind.INTERFACE) {
                // Check if there is an implementation that we can use instead
                if (beanClass.getQualifiedName().equals("com.codename1.rad.models.Entity")) {
                    // Translate <entity> to baseEntity
                    TypeElement implementationClass = jenv.lookupClass("com.codename1.rad.models.BaseEntity");
                    if (implementationClass != null && isA(implementationClass, beanClass.typeEl.getQualifiedName().toString())) {
                        beanClass = new JavaClassProxy(implementationClass, jenv);
                    }
                } else {

                    TypeElement implementationClass = jenv.lookupClass(beanClass.typeEl.getQualifiedName() + "Impl");
                    if (implementationClass != null && isA(implementationClass, beanClass.typeEl.getQualifiedName().toString())) {
                        beanClass = new JavaClassProxy(implementationClass, jenv);
                    }
                }
            }

        }

        void writeBuilderMethod(StringBuilder sb) throws XMLParseException {

            indent(sb, indent).append("private ").append(beanClass.getQualifiedName()).append(" createBean").append(xmlTag.getAttribute("rad-id")).append("() {\n");
            indent += 4;


            indent(sb, indent).append(beanClass.typeEl.getQualifiedName()).append(" _bean = ");
            JavaMethodProxy constructor = beanClass.getBestConstructor(xmlTag);

            if (constructor == null) {
                throw new XMLParseException("Cannot find suitable constructor to build tag "+xmlTag.getTagName()+" with class "+beanClass.typeEl.getQualifiedName(), xmlTag, null);
            }
            constructor.callAsConstructor(sb, xmlTag, false);
            sb.append(";\n");

            writeProperties(sb);

            writeVariables(sb);

            indent(sb, indent).append("// Create child nodes" +
                    "\n");
            writeChildren(sb);


            indent(sb, indent).append("return _bean;\n");
            indent -= 4;
            indent(sb, indent).append("}\n");


        }

        void writeProperties(StringBuilder sb) {

            String textContent = xmlTag.getTextContent();
            if (textContent != null && !textContent.isEmpty()) {
                ExecutableElement setText = beanClass != null ? beanClass.findSetter("text", "java.lang.String") : null;



                if (setText != null) {
                    indent(sb, indent);
                    beanClass.setProperty(sb, "text", textContent, "_bean");
                    sb.append("\n");
                }
            }

            NamedNodeMap attributes = xmlTag.getAttributes();
            int len = attributes.getLength();
            for (int i=0; i<len; i++) {
                Attr attr = (Attr)attributes.item(i);
                String name = attr.getName();
                if (name.startsWith("_") && name.endsWith("_")) {
                    // this is a parameter for the constructor or static builder method.  Do not treat is as a property.
                    continue;
                }

                if (name.contains("-")) {
                    continue;
                }
                String value = attr.getValue();
                indent(sb, indent);
                sb.append("// ").append(name).append("=").append(value).append("\n");
                indent(sb, indent);
                beanClass.setProperty(sb, attr, "_bean");

                sb.append("\n");
            }
        }


        void writeChildren(StringBuilder sb) {
            NodeList childNodes = xmlTag.getChildNodes();
            int numChildNodes = childNodes.getLength();
            indent(sb, indent).append("// ").append(numChildNodes).append(" child nodes\n");
            for (int i=0; i<numChildNodes; i++) {
                Node child = childNodes.item(i);
                if (!(child instanceof org.w3c.dom.Element)) {
                    continue;
                }
                org.w3c.dom.Element childEl = (org.w3c.dom.Element)child;
                String propertyName = childEl.getAttribute("rad-property");
                String childId = childEl.getAttribute("rad-id");

                // Some sub components are added to the parent component as a property.  E.g. setHintLabel().
                // Others are added as children.
                String createBeanCall = "createBean"+childId+"()";
                if (propertyName != null && !propertyName.isEmpty()) {
                    indent(sb, indent).append("// Set property ").append(propertyName).append(" with child tag ").append(childEl.getTagName()).append("\n");
                    indent(sb, indent);
                    if (jenv.isComponentTag(childEl.getTagName())) {
                        createBeanCall = "createComponent"+childId+"()";
                    } else if (jenv.isNodeTag(childEl.getTagName(), false)) {
                        createBeanCall = "createNode"+childId+"()";
                    }
                    beanClass.setProperty(sb, propertyName, "java:"+createBeanCall, "_bean", null);

                    sb.append("\n");
                    continue;
                }

                if (childEl.getTagName().equalsIgnoreCase("var")) {
                    continue;
                }

                if (childEl.getTagName().equalsIgnoreCase("script")) {
                    indent(sb, indent).append("// <script> tag\n");
                    indent(sb, indent).append("script").append(childEl.getAttribute("rad-id")).append("(_cmp);\n");
                    continue;
                }

                TypeElement type = jenv.findClassThatTagCreates(childEl.getTagName());
                if (type != null && jenv.rootBuilder.containsBeanBuilderFor(type.getQualifiedName())) {
                    indent(sb, indent).append("// Create bean ").append(type.getQualifiedName()).append("\n");
                    indent(sb, indent).append("createBean").append(childEl.getAttribute("rad-id")).append("();\n");
                    continue;
                }






            }

        }

    }

    private class JavaNodeBuilder extends JavaBuilder {

        private JavaMethodProxy builderMethod;
        private JavaClassProxy builderClass;
        private JavaClassProxy nodeClass;


        private List<JavaNodeBuilder> children = new ArrayList<JavaNodeBuilder>();
        JavaNodeBuilder(org.w3c.dom.Element xmlTag, JavaEnvironment jenv, JavaClassProxy builderClass, JavaClassProxy nodeClass) throws ClassNotFoundException{
            this.xmlTag = xmlTag;
            this.jenv = jenv;
            this.builderMethod = jenv.findBuilderMethod(xmlTag, "com.codename1.rad.nodes.Node");

            this.builderClass = builderClass;
            if (builderMethod != null) {
                this.nodeClass = new JavaClassProxy(builderMethod.getReturnType(), jenv);
            } else {
                this.nodeClass = nodeClass == null ? new JavaClassProxy(builderClass.findMethodProxy("getNode", 0).getReturnType(), jenv) : nodeClass;
            }
        }

        void writeBuilderMethod(StringBuilder sb) throws XMLParseException {

            indent(sb, indent).append("private ").append(nodeClass.getQualifiedName()).append(" createNode").append(xmlTag.getAttribute("rad-id")).append("() {\n");
            indent += 4;
            indent(sb, indent).append("java.util.Map<String,String> attributes = new java.util.HashMap<String,String>();\n");
            NamedNodeMap attributes = xmlTag.getAttributes();
            int numAtts = attributes.getLength();
            for (int i=0; i<numAtts; i++) {
                org.w3c.dom.Attr attribute = (org.w3c.dom.Attr)attributes.item(i);
                indent(sb, indent).append(" attributes.put(\"").append(StringEscapeUtils.escapeJava(attribute.getName())).append("\", \"")
                        .append(StringEscapeUtils.escapeJava(attribute.getValue())).append("\");\n");
            }

            if (builderClass != null) {
                indent(sb, indent).append(builderClass.getQualifiedName()).append(" _builder = ").append("new ")
                        .append(builderClass.getQualifiedName()).append("(context, \"").append(StringEscapeUtils.escapeJava(xmlTag.getTagName())).append("\", attributes);\n");
            } else {
                //indent(sb, indent).append(builderClass.getQualifiedName()).append(" _builder = null;\n");
            }
            indent(sb, indent).append(nodeClass.typeEl.getQualifiedName()).append(" _node = ");
            if (builderMethod != null) {
                //sb.append(builderMethod.classProxy.getQualifiedName()).append(".").append(builderMethod.methodEl.getSimpleName()).append("(");
                builderMethod.callStatic(sb, xmlTag, false);
                //List<? extends VariableElement> params = builderMethod.methodEl.getParameters();
                sb.append(";\n");

                indent(sb, indent).append("com.codename1.rad.ui.builders.SimpleNodeBuilder<").append(nodeClass.getQualifiedName()).append("> _builder = new com.codename1.rad.ui.builders.SimpleNodeBuilder<").append(nodeClass.getQualifiedName()).append(">(_node, context, ");

                sb.append("\"").append(StringEscapeUtils.escapeJava(xmlTag.getTagName())).append("\", attributes);\n");

            } else if (builderClass != null) {
                sb.append("_builder.getNode();\n");
            } else {
                // Node classes need to have a no arg constructor - or single var-arg constructor.
                sb.append("new ").append(nodeClass.getQualifiedName()).append("();\n");


                indent(sb, indent).append("com.codename1.rad.ui.builders.SimpleNodeBuilder<").append(nodeClass.getQualifiedName()).append("> _builder = new com.codename1.rad.ui.builders.SimpleNodeBuilder<").append(nodeClass.getQualifiedName()).append(">(_node, context, ");

                sb.append("\"").append(StringEscapeUtils.escapeJava(xmlTag.getTagName())).append("\", attributes);\n");
            }

            writeProperties(sb);

            writeVariables(sb);

            indent(sb, indent).append("// Create child nodes" +
                    "\n");
            writeChildren(sb);


            indent(sb, indent).append("return _node;\n");
            indent -= 4;
            indent(sb, indent).append("}\n");


        }

        void writeProperties(StringBuilder sb) {
            NamedNodeMap attributes = xmlTag.getAttributes();
            int len = attributes.getLength();
            for (int i=0; i<len; i++) {
                Attr attr = (Attr)attributes.item(i);
                String name = attr.getName();
                String value = attr.getValue();
                if (name.contains("-")) {
                    continue;
                }
                if (name.startsWith("_") && name.endsWith("_")) {
                    // this is a parameter for the constructor or static builder method.  Do not treat is as a property.
                    continue;
                }
                indent(sb, indent);
                sb.append("// ").append(name).append("=").append(value).append("\n");
                indent(sb, indent);
                if (name.contains(".") || builderClass == null) {

                    nodeClass.setProperty(sb, attr, "_node");

                } else {

                    builderClass.setProperty(sb, attr, "_builder");

                }
                sb.append("\n");
            }
        }



        void writeChildren(StringBuilder sb) {
            NodeList childNodes = xmlTag.getChildNodes();
            int numChildNodes = childNodes.getLength();
            indent(sb, indent).append("// ").append(numChildNodes).append(" child nodes\n");
            for (int i=0; i<numChildNodes; i++) {
                Node child = childNodes.item(i);
                if (!(child instanceof org.w3c.dom.Element)) {
                    continue;
                }
                org.w3c.dom.Element childEl = (org.w3c.dom.Element)child;
                String propertyName = childEl.getAttribute("rad-property");
                String childId = childEl.getAttribute("rad-id");

                // Some sub components are added to the parent component as a property.  E.g. setHintLabel().
                // Others are added as children.
                String createNodeCall = "createNode"+childId+"()";
                if (propertyName != null && !propertyName.isEmpty()) {
                    indent(sb, indent).append("// Set property ").append(propertyName).append(" with child tag ").append(childEl.getTagName()).append("\n");
                    indent(sb, indent);
                    if (propertyName.contains(".") || builderClass == null) {
                        nodeClass.setProperty(sb, propertyName, "java:"+createNodeCall, "_node", "com.codename1.rad.nodes.Node");
                    } else {
                        builderClass.setProperty(sb, propertyName, "java:"+createNodeCall, "_node", "com.codename1.rad.nodes.Node");
                    }
                    sb.append("\n");
                    continue;
                }

                if (childEl.getTagName().equalsIgnoreCase("var")) {
                    continue;
                }

                if (childEl.getTagName().equalsIgnoreCase("script")) {
                    indent(sb, indent).append("// <script> tag\n");
                    indent(sb, indent).append("script").append(childEl.getAttribute("rad-id")).append("(_cmp);\n");
                    continue;
                }



                indent(sb, indent).append("// Child tag ").append(childEl.getTagName()).append(" is type ").append(jenv.findClassThatTagCreates(childEl.getTagName())).append("\n");

                if (jenv.isNodeTag(childEl.getTagName(), true)) {
                    indent(sb, indent).append("// Add child node ").append(" with child tag ").append(childEl.getTagName()).append("\n");
                    indent(sb, indent).append("_node.setAttributes(createNode").append(childEl.getAttribute("rad-id")).append("());\n");
                    continue;
                }






            }

        }

    }



    private class JavaComponentBuilder extends JavaBuilder {

        private JavaMethodProxy builderMethod;
        private JavaClassProxy builderClass;
        private JavaClassProxy componentClass;


        private List<JavaComponentBuilder> children = new ArrayList<JavaComponentBuilder>();


        JavaComponentBuilder(org.w3c.dom.Element xmlTag, JavaEnvironment jenv, JavaClassProxy builderClass, JavaClassProxy componentClass) throws ClassNotFoundException{
            this.xmlTag = xmlTag;
            this.jenv = jenv;
            this.builderMethod = jenv.findBuilderMethod(xmlTag, "com.codename1.ui.Component");

            this.builderClass = builderClass;
            if (builderMethod != null) {
                this.componentClass = new JavaClassProxy(builderMethod.getReturnType(), jenv);
            } else {
                this.componentClass = componentClass == null ? new JavaClassProxy(builderClass.findMethodProxy("getComponent", 0).getReturnType(), jenv) : componentClass;
            }
        }


        void writeBuilderProperties(StringBuilder sb) throws XMLParseException {
            if (builderClass == null) return;
            String textContent = xmlTag.getTextContent();
            if (textContent != null && !textContent.isEmpty()) {
                ExecutableElement setText = builderClass.findSetter("text", "java.lang.String");
                if (setText != null) {
                    indent(sb, indent);
                    builderClass.setProperty(sb, "text", textContent, "_builder");
                }
            }
            NamedNodeMap attributes = xmlTag.getAttributes();
            int len = attributes.getLength();
            for (int i=0; i<len; i++) {
                Attr attr = (Attr)attributes.item(i);
                String name = attr.getName();
                String value = attr.getValue();
                if (name.contains("-")) {
                    continue;
                }
                if (name.startsWith("_") && name.endsWith("_")) {
                    // this is a parameter for the constructor or static builder method.  Do not treat is as a property.
                    continue;
                }
                indent(sb, indent);
                sb.append("// ").append(name).append("=").append(value).append("\n");
                indent(sb, indent);
                ExecutableElement setter = builderClass.findSetter(name);
                if (setter != null) {
                    builderClass.setProperty(sb, attr, "_builder");
                }

                sb.append("\n");
            }

            if (isA(componentClass.typeEl, "com.codename1.rad.ui.entityviews.EntityListView")) {
                List<org.w3c.dom.Element> rowTemplates = getChildElementsByTagName(xmlTag, "row-template");
                if (!rowTemplates.isEmpty()) {
                    // This is an EntityListView, and there are some row templates defined.
                    indent(sb, indent).append("// ").append(rowTemplates.size()).append(" row templates defined for list view\n");
                    indent(sb, indent).append("{\n"); // open block
                    indent += 4;
                    indent(sb, indent).append("class Renderer").append(xmlTag.getAttribute("rad-id")).append(" implements com.codename1.rad.ui.EntityListCellRenderer {\n");
                    indent += 4;
                    indent(sb, indent).append("public EntityView getListCellRendererComponent(EntityListView list, Entity value, int index, boolean isSelected, boolean isFocused) {\n");
                    indent += 4;
                    indent(sb, indent).append(jenv.rootBuilder.className).append(".this.").append("rowModel = value;\n");
                    indent(sb, indent).append(jenv.rootBuilder.className).append(".this.").append("rowIndex = index;\n");
                    indent(sb, indent).append(jenv.rootBuilder.className).append(".this.").append("rowSelected = isSelected;\n");
                    indent(sb, indent).append(jenv.rootBuilder.className).append(".this.").append("rowFocused = isFocused;\n");
                    indent(sb, indent).append(jenv.rootBuilder.className).append(".this.").append("rowList = list;\n");
                    indent(sb, indent).append("try {\n");
                    indent += 4;
                    for (org.w3c.dom.Element rowTemplate : rowTemplates) {
                        String condition = rowTemplate.getAttribute("case");
                        if (condition.isEmpty()) {
                            condition = "true";
                        }

                        indent(sb, indent).append("if (").append(condition).append(") {\n");
                        indent += 4;

                        for (org.w3c.dom.Element rowView : getChildElements(rowTemplate)) {
                            if (rowView.getTagName().contains("-")) continue;
                            TypeElement rowViewTypeEl = jenv.findClassThatTagCreates(rowView.getTagName(), "com.codename1.rad.ui.EntityView");
                            if (rowViewTypeEl == null) {
                                // This tag doesn't resolve to an EntityView
                                TypeElement rowComponentTypeEl = jenv.findClassThatTagCreates(rowView.getTagName(), "com.codename1.ui.Component");
                                if (rowComponentTypeEl == null) {
                                    continue;
                                }
                                indent(sb, indent).append("com.codename1.rad.nodes.ViewNode _viewNode = ").append("new com.codename1.rad.nodes.ViewNode();\n");
                                indent(sb, indent).append("_viewNode.setParent(context.getNode());\n");
                                indent(sb, indent).append("return new com.codename1.rad.ui.entityviews.WrapperEntityView(createComponent").append(rowView.getAttribute("rad-id")).append("(), ").append(jenv.rootBuilder.className).append(".this.").append("rowModel, _viewNode);\n");
                                break;
                            }
                            if (!rowView.hasAttribute("view-model")) {
                                rowView.setAttribute("view-model", "java:rowModel");
                            }

                            indent(sb, indent).append("return (EntityView) createComponent").append(rowView.getAttribute("rad-id")).append("();\n");
                            break;

                        }

                        indent -= 4;
                        indent(sb, indent).append("}\n"); // if (condition)
                    }
                    indent(sb, indent).append("throw new RuntimeException(\"No row view matches the provided condition.\");\n");
                    indent -= 4;
                    indent(sb, indent).append("} finally {\n");
                    indent += 4;
                    indent(sb, indent).append(jenv.rootBuilder.className).append(".this.").append("rowModel = null;\n");
                    indent(sb, indent).append(jenv.rootBuilder.className).append(".this.").append("rowIndex = -1;\n");
                    indent(sb, indent).append(jenv.rootBuilder.className).append(".this.").append("rowSelected = false;\n");
                    indent(sb, indent).append(jenv.rootBuilder.className).append(".this.").append("rowFocused = false;\n");
                    indent(sb, indent).append(jenv.rootBuilder.className).append(".this.").append("rowList = null;\n");
                    indent -= 4;
                    indent(sb, indent).append("}\n"); // finally

                    indent -= 4;
                    indent(sb, indent).append("}\n"); // getListCellRendererComponent()

                    indent -= 4;
                    indent(sb, indent).append("}\n"); // class Renderer
                    indent(sb, indent).append("_builder.renderer(new Renderer").append(xmlTag.getAttribute("rad-id")).append("());\n");

                    indent -= 4;
                    indent(sb, indent).append("}\n"); // close block
                }
            }

            for (org.w3c.dom.Element childEl : getChildElements(xmlTag)) {

                String propertyName = childEl.getAttribute("rad-property");
                String childId = childEl.getAttribute("rad-id");
                TypeElement type = jenv.findClassThatTagCreates(childEl.getTagName());
                String createCall = "createBean";
                if (isA(type, "com.codename1.ui.Component")) {
                    createCall = "createComponent";
                } else if (isA(type, "com.codename1.rad.nodes.Node")) {
                    createCall = "createNode";
                }
                createCall += childId + "()";




                if (childEl.getTagName().contains("-") || childEl.hasAttribute("rad-param")) {
                    continue;
                }

                if (propertyName != null && !propertyName.isEmpty()) {
                    ExecutableElement setter = builderClass.findSetter(propertyName);
                    ExecutableType setterType = (ExecutableType)types().asMemberOf((DeclaredType)builderClass.typeEl.asType(), setter);
                    TypeMirror propertyType = setterType.getParameterTypes().get(0);
                    if (propertyName.contains(".") || setter == null) {
                        // The builder class would have already handled this when setting its properties.
                        continue;
                    }

                    indent(sb, indent).append("// Set property ").append(propertyName).append(" with child tag ").append(childEl.getTagName()).append("\n");
                    indent(sb, indent);
                    builderClass.setProperty(sb, propertyName, "java:"+createCall, "_builder", propertyType.toString());
                    sb.append("\n");
                    continue;
                }

                boolean first = true;

                for (ExecutableElement injectableSetter : builderClass.findInjectableSettersForType(type)) {
                    if (createCall.startsWith("createBean") && !jenv.rootBuilder.containsBeanBuilderFor(type.getQualifiedName())) {
                        // Yes it's a bean, but we have no bean builder for it.
                        continue;
                    }
                    ExecutableType setterType = (ExecutableType)types().asMemberOf((DeclaredType)builderClass.typeEl.asType(), injectableSetter);
                    TypeMirror propertyType = setterType.getParameterTypes().get(0);
                    indent(sb, indent).append("// Injectable property ").append(injectableSetter.getSimpleName()).append(" being set with tag ").append(childEl.getTagName()).append("\n");
                    indent(sb, indent);
                    if (first) {
                        first = false;
                        sb.append("{\n");
                        indent += 4;

                        indent(sb, indent).append(type.getQualifiedName()).append(" _tmpProperty = ").append(createCall).append(";\n");

                    }
                    childEl.setAttribute("rad-property", injectableSetter.getSimpleName().toString());
                    builderClass.setProperty(sb, injectableSetter.getSimpleName().toString(), "java:_tmpProperty", "_builder", setterType.toString());

                }
                if (!first) {
                    // We must have injected this child in at least one property.
                    // Close the block and skip to next child element
                    indent -= 4;
                    indent(sb, indent).append("}\n");

                    continue;
                }

            }
        }


        void writeProperties(StringBuilder sb) {
            String textContent = xmlTag.getTextContent();
            if (textContent != null && !textContent.isEmpty()) {
                ExecutableElement  setText = componentClass.findSetter("text", "java.lang.String");



                if (setText != null) {
                    indent(sb, indent);
                    componentClass.setProperty(sb, "text", textContent, "_cmp");
                }
            }
            NamedNodeMap attributes = xmlTag.getAttributes();
            int len = attributes.getLength();
            for (int i=0; i<len; i++) {
                Attr attr = (Attr)attributes.item(i);
                String name = attr.getName();
                String value = attr.getValue();
                if (name.contains("-")) {
                    continue;
                }
                if (name.startsWith("_") && name.endsWith("_")) {
                    // this is a parameter for the constructor or static builder method.  Do not treat is as a property.
                    continue;
                }
                if (builderClass != null && !name.contains(".") && builderClass.findSetter(name) != null) {
                    // this attribute was processed by the builder already
                    continue;
                }
                indent(sb, indent);
                sb.append("// ").append(name).append("=").append(value).append("\n");
                indent(sb, indent);
                ExecutableElement setter = componentClass.findSetter(name);
                if (name.contains(".") || setter != null) {

                    componentClass.setProperty(sb, attr, "_cmp");

                }
                sb.append("\n");
            }

            for (org.w3c.dom.Element childEl : getChildElements(xmlTag)) {
                if (childEl.getTagName().contains("-") || childEl.hasAttribute("rad-param")) {
                    continue;
                }
                String propertyName = childEl.getAttribute("rad-property");
                String childId = childEl.getAttribute("rad-id");
                TypeElement type = jenv.findClassThatTagCreates(childEl.getTagName());
                String createCall = "createBean";
                if (isA(type, "com.codename1.ui.Component")) {
                    createCall = "createComponent";
                } else if (isA(type, "com.codename1.rad.nodes.Node")) {
                    createCall = "createNode";
                }
                createCall += childId + "()";
                if (propertyName != null && !propertyName.isEmpty()) {
                    if (!propertyName.contains(".") && builderClass.findSetter(propertyName) != null) {
                        // The builder class would have already handled this when setting its properties.
                        continue;
                    }

                    indent(sb, indent).append("// Set property ").append(propertyName).append(" with child tag ").append(childEl.getTagName()).append("\n");
                    indent(sb, indent);
                    componentClass.setProperty(sb, propertyName, "java:"+createCall, "_cmp");
                    sb.append("\n");
                    continue;
                }

                boolean first = true;

                for (ExecutableElement injectableSetter : componentClass.findInjectableSettersForType(type)) {
                    if (createCall.startsWith("createBean") && !jenv.rootBuilder.containsBeanBuilderFor(type.getQualifiedName())) {
                        // Yes it's a bean, but we have no bean builder for it.
                        continue;
                    }
                    indent(sb, indent).append("// Injectable property ").append(injectableSetter.getSimpleName()).append(" being set with tag ").append(childEl.getTagName()).append("\n");
                    indent(sb, indent);
                    if (first) {
                        first = false;
                        sb.append("{\n");
                        indent += 4;

                        indent(sb, indent).append(type.getQualifiedName()).append(" _tmpProperty = ").append(createCall).append(";\n");

                    }
                    childEl.setAttribute("rad-property", injectableSetter.getSimpleName().toString());
                    componentClass.setProperty(sb, injectableSetter.getSimpleName().toString(), "java:_tmpProperty", "_cmp");

                }
                if (!first) {
                    // We must have injected this child in at least one property.
                    // Close the block and skip to next child element
                    indent -= 4;
                    indent(sb, indent).append("}\n");

                    continue;
                }

            }
        }


        /**
         * The rad-href attribute is "magic".  You can specify a URL to link to in the form #ControllerName, and it will
         * first try to create a controller of that type using the Controller.createObjectWithFactory() method, and if that
         * fails, it will create a controller of that type using compile-time inference to find a suitable class.
         * @param sb
         */
        void writeHrefLink(StringBuilder sb) throws XMLParseException {
            String href = xmlTag.getAttribute("rad-href");
            if (href.isEmpty()) return;
            org.w3c.dom.Element bindAction = getChildElements(xmlTag).stream().filter(e->e.getTagName().equals("bind-action")).findFirst().orElse(null);
            StringBuilder bindScript = new StringBuilder();
            if (bindAction != null) {
                bindScript.append(bindAction.getTextContent());
            }
            String explicitParams = null;
            if (href.contains("(") && href.endsWith(")")) {
                explicitParams = href.substring(href.indexOf("("), href.length()-1);
                href = href.substring(0, href.indexOf("("));
            }
            String rel = "child";
            if (href.contains(" ")) {
                rel = href.substring(href.indexOf(" ")+1);
                href = href.substring(0, href.indexOf(" "));
            }


            if (!href.startsWith("#")) {
                throw new IllegalArgumentException("Unsupported rad-href value.  Only FormController addresses are allowed.  Of the form #FormControllerClassName");
            }
            String formControllerClass = href.substring(1);
            TypeElement formControllerTypeEl = jenv.lookupClass(formControllerClass);
            if (formControllerTypeEl == null) {
                String fqn = this.jenv.rootBuilder.packageName+"."+formControllerClass;
                formControllerTypeEl = jenv.lookupClass(fqn);
                if (formControllerTypeEl == null) {
                    PackageElement pkg = elements().getPackageElement(this.jenv.rootBuilder.packageName);
                }
            }

            if (formControllerTypeEl != null && isA(formControllerTypeEl, "com.codename1.rad.ui.EntityView")) {
                // This is a view class
                // let's try to look up the default controller for it.
                TypeElement markerControllerInterface = jenv.lookupClass("I"+formControllerTypeEl.getSimpleName()+"Controller");
                if (markerControllerInterface != null) {
                    formControllerTypeEl = markerControllerInterface;
                } else {
                    markerControllerInterface = jenv.lookupClass(formControllerTypeEl.getSimpleName()+"FormController");
                    if (markerControllerInterface != null) {
                        formControllerTypeEl = markerControllerInterface;
                    } else {
                        markerControllerInterface = jenv.lookupClass(formControllerTypeEl.getSimpleName()+"Controller");
                        if (markerControllerInterface != null) {
                            formControllerTypeEl = markerControllerInterface;
                        }
                    }

                }

            }

            String paramsString = explicitParams == null ?
                    (rel.equals("child") ? "{formController}" :
                            rel.equals("sibling") ? "{parentFormController}" :
                                    rel.equals("top") ? "{applicationController}" :
                                            rel.equals("section") ? "{sectionController}" : "{formController}") : "{" + explicitParams.substring(1, explicitParams.length()-1) + "}";
            paramsString = "new Object[]" + paramsString;

            indent(bindScript, indent).append("com.codename1.rad.controllers.FormController _rad_href_controller = (com.codename1.rad.controllers.FormController)getContext().getController().createObjectWithFactory(")
                    .append(formControllerTypeEl.getQualifiedName()).append(".class, ").append(paramsString).append(");\n");
            indent(bindScript, indent).append("if (_rad_href_controller == null) {\n");

            /*
            if (component has bind-action already) {
                piggy-back onto the bind-action
            } else {
                fire a "link" action
                if the "link action" was not consumed
                add an action listener
            }

            // Actual action handling:
            Try to create controller using Controller.createObjectWithFactory
            if (controller was not created) {
                find a suitable class to create the controller
                controller = new FoundControllerClass
            }
            controller.show()
             */
            PackageElement packageElement = elements().getPackageOf(jenv.rootBuilder.parentClass);
            org.w3c.dom.Element dummyTag = null;
            if (explicitParams == null) {
                dummyTag = xmlTag.getOwnerDocument().createElement(formControllerClass);
                if (rel.equals("child")) {
                    dummyTag.setAttribute("_0_", "formController");
                } else if (rel.equals("sibling")) {
                    dummyTag.setAttribute("_0_", "parentFormController");

                } else if (rel.equals("top")) {
                    dummyTag.setAttribute("_0_", "applicationController");
                } else if (rel.equals("section")) {
                    dummyTag.setAttribute("_0_", "sectionController");
                } else {
                    dummyTag.setAttribute("_0_", "formController");
                }
            }

            if (formControllerTypeEl != null) {
                List<JavaClassProxy> candidateControllerClasses = jenv.findInstantiatableClassesAssignableTo(packageElement, dummyTag, "com.codename1.rad.controllers.FormController", formControllerTypeEl.getQualifiedName().toString());
                if (candidateControllerClasses.isEmpty()) {
                   throw new XMLParseException("Cannot find any instantiatable FormController classes that can be assigned to " + formControllerTypeEl.getQualifiedName() + ".  Referenced in rad-href attribute of " + xmlTag, xmlTag, null);

                } else {
                    JavaClassProxy controllerClass = candidateControllerClasses.get(0);
                    indent(bindScript, indent).append("    _rad_href_controller = ");

                    if (explicitParams == null) {
                        JavaMethodProxy constructor = controllerClass.getBestConstructor(dummyTag);
                        controllerClass.getBestConstructor(dummyTag).callAsConstructor(bindScript, dummyTag, false);
                    } else {
                        bindScript.append("new ").append(controllerClass.getQualifiedName()).append(explicitParams);
                    }
                    bindScript.append(";\n");
                }
            }

            indent(bindScript, indent).append("}\n");
            indent(bindScript, indent).append("_rad_href_controller.show();\n");

            if (bindAction == null) {
                JavaMethodProxy addActionListenerMethod = componentClass.findMethodProxy("addActionListener", 1);
                indent(sb, indent).append("_cmp.addActionListener(event -> {\n");
                indent(sb, indent).append("    if (event.isConsumed()) return;\n");
                sb.append(bindScript);
                indent(sb, indent).append("});\n");
            } else {
                bindAction.setTextContent(bindScript.toString());
            }
        }


        void writeChildren(StringBuilder sb) {
            NodeList childNodes = xmlTag.getChildNodes();
            int numChildNodes = childNodes.getLength();
            indent(sb, indent).append("// ").append(numChildNodes).append(" child nodes\n");
            for (int i=0; i<numChildNodes; i++) {
                Node child = childNodes.item(i);
                if (!(child instanceof org.w3c.dom.Element)) {
                    continue;
                }
                org.w3c.dom.Element childEl = (org.w3c.dom.Element)child;
                //String propertyName = childEl.getAttribute("rad-property");
                if (childEl.hasAttribute("rad-property") || childEl.hasAttribute("rad-param")) {
                    continue;
                }

                String childId = childEl.getAttribute("rad-id");

                if (componentClass.isContainer() && childEl.getTagName().equalsIgnoreCase("define-slot")) {
                    // Handle slot definitions specially.
                    indent(sb, indent).append("// Define slot ").append(childEl.getAttribute("id")).append("\n");
                    indent(sb, indent).append("{\n");
                    indent += 4;
                    indent(sb, indent).append("com.codename1.rad.ui.Slot _slot = new com.codename1.rad.ui.Slot(");
                    String slotId = childEl.getAttribute("id");
                    sb.append(slotId).append(", this);\n");
                    for (org.w3c.dom.Element slotChild : getChildElements(childEl)) {
                        if (jenv.isComponentTag(slotChild.getTagName())) {
                            sb.append("_slot.setContent(").append("createComponent").append(slotChild.getAttribute("rad-id")).append("());\n");
                            break;
                        }
                    }

                    indent(sb, indent);
                    String constraint = childEl.getAttribute("layout-constraint");
                    if (constraint == null || constraint.isEmpty()) {
                        indent(sb, indent).append("_cmp.addComponent(_slot);\n");
                    } else {
                        indent(sb, indent).append("_cmp.addComponent(_builder.parseConstraint(\"").append(StringEscapeUtils.escapeJava(constraint)).append("\"), _slot);\n");
                    }
                    indent -= 4;
                    indent(sb, indent).append("}\n");
                    continue;
                }

                if (componentClass.isContainer() && childEl.getTagName().equalsIgnoreCase("fill-slot")) {
                    // This is a directive to fill a slot that was defined in the parent container.
                    indent(sb, indent).append("// fill slot ").append(childEl.getAttribute("id")).append("\n");
                    indent(sb, indent).append("{\n");
                    indent += 4;
                    String slotId = childEl.getAttribute("id");
                    if (slotId.startsWith("java:")) {
                        slotId = slotId.substring(slotId.indexOf(":")+1);
                    } else if (!slotId.contains(".")){
                        slotId = componentClass.getQualifiedName() + "." +slotId;
                    }
                    for (org.w3c.dom.Element slotChild : getChildElements(childEl)) {
                        if (jenv.isComponentTag(slotChild.getTagName())) {
                            indent(sb, indent).append("viewController.fillSlot(").append(slotId).append(", evt -> {\n");
                            indent(sb, indent).append("    evt.getSlot().setContent(createComponent").append(slotChild.getAttribute("rad-id")).append("());\n");
                            indent(sb, indent).append("});\n");
                            break;
                        }
                    }

                    indent -= 4;
                    indent(sb, indent).append("}\n");
                    continue;
                }



                if (childEl.getTagName().equalsIgnoreCase("var")) {
                    continue;
                }

                if (childEl.getTagName().equalsIgnoreCase("script")) {
                    indent(sb, indent).append("// <script> tag\n");
                    indent(sb, indent).append("script").append(childEl.getAttribute("rad-id")).append("(_cmp);\n");
                    continue;
                }

                TypeElement type = jenv.findClassThatTagCreates(childEl.getTagName());

                // See if there are any injected properties that this would fit into
                String createCall = "createBean";
                if (isA(type, "com.codename1.ui.Component")) {
                    createCall = "createComponent";
                } else if (isA(type, "com.codename1.rad.nodes.Node")) {
                    createCall = "createNode";
                }
                createCall += childId + "()";







                indent(sb, indent).append("// Child tag ").append(childEl.getTagName()).append(" is type ").append(jenv.findClassThatTagCreates(childEl.getTagName())).append("\n");
                if (componentClass.isContainer() && jenv.isComponentTag(childEl.getTagName())) {
                    // This is a component tag.  and the parent is a container.  Just add it as a child.
                    indent(sb, indent).append("// Add child component ").append(" with child tag ").append(childEl.getTagName()).append("\n");
                    indent(sb, indent);
                    String constraint = childEl.getAttribute("layout-constraint");
                    if (constraint == null || constraint.isEmpty()) {
                        indent(sb, indent).append("_cmp.addComponent(").append(createCall).append(");\n");
                    } else {
                        indent(sb, indent).append("_cmp.addComponent(_builder.parseConstraint(\"").append(StringEscapeUtils.escapeJava(constraint)).append("\"), ").append(createCall).append(");\n");
                    }
                    continue;
                }

                if (type != null && jenv.rootBuilder.containsBeanBuilderFor(type.getQualifiedName())) {
                    // This is a bean tag, and it hasn't been assigned as a property or parameter.  Just create
                    // the bean and don't assign it to anything.  Some beans, such as Title, modify the environment
                    // inside either its constructor or property setter methods, so this may not be futile.
                    indent(sb, indent).append("// Create bean ").append(type.getQualifiedName()).append("\n");
                    indent(sb, indent).append("createBean").append(childEl.getAttribute("rad-id")).append("();\n");
                }









            }

        }

        boolean isInsideRowTemplate() {
            return isElementInsideRowTemplate(xmlTag);
        }

        void writeBinding(StringBuilder sb, String attName, String attValue) throws XMLParseException {
            if (!attName.startsWith("bind-")) {
                throw new IllegalArgumentException("AttName must start with bind-");
            }
            String propName = attName.substring(attName.indexOf("-")+1);

            JavaPropertySelector propertySelector = createPropertySelector(componentClass, propName);
            indent(sb, indent).append("{\n");
            indent += 4;
            indent(sb, indent).append("// Binding for ").append(attName).append("=").append(attValue).append("\n");
            indent(sb, indent).append("final ").append(componentClass.getQualifiedName()).append(" _fcmp = _cmp;\n");
            boolean parseAsJava = false;
            if (attValue.startsWith("java:")) {
                attValue = attValue.substring(attValue.indexOf(":")+1);
                parseAsJava = true;
            }
            indent(sb, indent).append("PropertySelector _propertySelector = ");
            if (parseAsJava) {
                sb.append("null");
            } else {
                jenv.createRADPropertySelector(sb, attValue);
            }
            sb.append(";\n");



            indent(sb, indent).append("Runnable _onUpdate = ()-> {\n");
            indent += 4;
            String paramType = propertySelector.getPropertyType(true);
            if (propertySelector.isReadable()) {
                indent(sb, indent).append(propertySelector.getPropertyType(false)).append(" _oldVal = ").append(propertySelector.getPropertyNullValue(true)).append(";\n");
                indent(sb, indent);
                propertySelector.assignVar(sb, "_fcmp", "_oldVal", propertySelector.getPropertyNullValue(true));
                sb.append("\n");
            } else {
                indent(sb, indent);
                propertySelector.assignVar(sb, "_fcmp", "_oldVal", propertySelector.getPropertyNullValue(true));
                sb.append("\n");
            }
            TypeElement param = propertySelector.setter().getParameterType(0);
            if (parseAsJava) {
                indent(sb, indent).append(paramType).append(" _newVal = ").append(attValue).append(";\n");
                //indent(sb, indent);
                //propertySelector.assignVar(sb, "_fcmp", attValue);
                //sb.append("\n");
            } else {
                indent(sb, indent).append(paramType).append(" _newVal = ");
                if (param.asType().getKind() == TypeKind.BOOLEAN || isA(toTypeElement(param.asType()), "java.lang.Boolean")) {
                    sb.append("!_propertySelector.isFalsey()");
                } else if (param.asType().getKind() == TypeKind.FLOAT || isA(toTypeElement(param.asType()), "java.lang.Float")) {
                    sb.append("_propertySelector.getFloat(0f)");
                } else if (param.asType().getKind() == TypeKind.INT || isA(toTypeElement(param.asType()), "java.lang.Integer")) {
                    sb.append("_propertySelector.getInt(0)");
                } else if (param.asType().getKind() == TypeKind.DOUBLE || isA(toTypeElement(param.asType()), "java.lang.Double")) {
                    sb.append("_propertySelector.getDouble(0.0)");
                } else if (paramType.equals("java.lang.String")) {
                    sb.append("_propertySelector.getText(\"\")");
                } else if (paramType.equals("java.util.Date")) {
                    sb.append("_propertySelector.getDate(null)");
                } else {

                    sb.append("_propertySelector.exists() ? (").append(paramType).append(")").append("_propertySelector.getLeafEntity().get(_propertySelector.getLeafProperty()) : null");
                }
                sb.append(";\n");
            }

            //sb.append(";\n");
            indent(sb, indent).append("if (!java.util.Objects.equals(_newVal, _oldVal)) {\n");
            indent += 4;
            indent(sb, indent);
            propertySelector.setProperty(sb, "_fcmp", "_newVal");
            sb.append("\n");
            indent -= 4;
            indent(sb, indent).append("}\n");
            indent -= 4;
            indent(sb, indent).append("};\n");

            indent(sb, indent).append("_onUpdate(_onUpdate);\n");
            if (propertySelector.isReadable()  && !parseAsJava) {
                indent(sb, indent).append("Runnable onCommit = () -> {\n");
                indent += 4;
                indent(sb, indent).append(paramType).append(" _newVal = ").append(propertySelector.getPropertyNullValue(false)).append(";\n");
                indent(sb, indent);
                propertySelector.assignVar(sb, "_fcmp", "_newVal", propertySelector.getPropertyNullValue(false));
                sb.append("\n");

                indent(sb, indent).append(paramType).append(" _oldVal = ");
                if (param.asType().getKind() == TypeKind.BOOLEAN || isA(toTypeElement(param.asType()), "java.lang.Boolean")) {
                    sb.append("!_propertySelector.isFalsey()");
                } else if (param.asType().getKind() == TypeKind.FLOAT || isA(toTypeElement(param.asType()), "java.lang.Float")) {
                    sb.append("_propertySelector.getFloat(0f)");
                } else if (param.asType().getKind() == TypeKind.INT || isA(toTypeElement(param.asType()), "java.lang.Integer")) {
                    sb.append("_propertySelector.getInt(0)");
                } else if (param.asType().getKind() == TypeKind.DOUBLE || isA(toTypeElement(param.asType()), "java.lang.Double")) {
                    sb.append("_propertySelector.getDouble(0.0)");
                } else if (paramType.equals("java.lang.String")) {
                    sb.append("_propertySelector.getText(\"\")");
                } else if (paramType.equals("java.util.Date")) {
                    sb.append("_propertySelector.getDate(null)");
                } else {

                    sb.append("_propertySelector.exists() ? (").append(paramType).append(")").append("_propertySelector.getLeafEntity().get(_propertySelector.getLeafProperty()) : null");
                }
                sb.append(";\n");
                indent(sb, indent).append("if (!java.util.Objects.equals(_newVal, _oldVal)) {\n");
                indent += 4;
                indent(sb, indent).append("_propertySelector.");
                if (param.asType().getKind() == TypeKind.BOOLEAN || isA(toTypeElement(param.asType()), "java.lang.Boolean")) {
                    sb.append("setBoolean");
                } else if (param.asType().getKind() == TypeKind.FLOAT || isA(toTypeElement(param.asType()), "java.lang.Float")) {
                    sb.append("setFloat");
                } else if (param.asType().getKind() == TypeKind.INT || isA(toTypeElement(param.asType()), "java.lang.Integer")) {
                    sb.append("setInt");
                } else if (param.asType().getKind() == TypeKind.DOUBLE || isA(toTypeElement(param.asType()), "java.lang.Double")) {
                    sb.append("setDouble");
                } else if (paramType.equals("java.lang.String")) {
                    sb.append("setText");
                } else if (paramType.equals("java.util.Date")) {
                    sb.append("setDate");
                } else {

                    XMLParseException ex = new XMLParseException("Failed to create binding for attribute "+attName+" in tag "+xmlTag+" because the property type "+param.asType()+" is not supported for bindings.", xmlTag, null);
                    ex.attributeName = attName;
                    ex.attributeValue = attValue;
                    throw ex;

                }
                sb.append("(_newVal);\n");
                indent -= 4;
                indent(sb, indent).append("}\n");
                indent -= 4;
                indent(sb, indent).append("};\n");



                // For the commit side of the binding, we'll use heuristics.  Looking for
                // methods like addDataChangedListener, addChangeListener, addActionListener, etc...

                JavaMethodProxy addDataChangedListenerMethod = componentClass.findMethodProxy("addDataChangedListener", 1);


                if (addDataChangedListenerMethod != null) {
                    ExecutableElement addDataChangedListener = (ExecutableElement) addDataChangedListenerMethod.methodEl;
                    int numParams = addDataChangedListener.getParameters().size();
                    indent(sb, indent).append("_fcmp.").append(addDataChangedListener.getSimpleName().toString()).append("((");
                    sb.append("type, index");
                    sb.append(") -> onCommit.run());\n");

                }
                JavaMethodProxy addActionListenerMethod = componentClass.findMethodProxy("addActionListener", 1);

                if (addActionListenerMethod != null) {
                    ExecutableElement addActionListener = (ExecutableElement) addActionListenerMethod.methodEl;
                    int numParams = addActionListener.getParameters().size();
                    indent(sb, indent).append("_fcmp.").append(addActionListener.getSimpleName().toString()).append("((evt");

                    sb.append(") -> onCommit.run());\n");

                }

            }

            indent -= 4;
            indent(sb, indent).append("} // END Binding for property ").append(attName).append("\n");




        }

        void writeBindings(StringBuilder sb) throws XMLParseException {
            XMLParseException[] errors = new XMLParseException[1];
            forEachAttribute(xmlTag, attr -> {
                if (errors[0] != null) {
                    return null;
                }
                if (attr.getName().startsWith("bind-")) {
                    try {
                        writeBinding(sb, attr.getName(), attr.getValue());
                    } catch (XMLParseException ex) {
                        errors[0] = ex;
                    }
                }
                return null;
            });
            if (errors[0] != null) {
                throw errors[0];
            }

        }

        void writeActionBindings(StringBuilder sb) throws XMLParseException {
            XMLParseException[] errors = new XMLParseException[1];
            // We need to write the href link before the bind-action procesing because it will add content to the bind-action
            // if present.
            writeHrefLink(sb);
            forEachChild(xmlTag, el -> {
                String tagName = el.getTagName();
                if (!tagName.equalsIgnoreCase("bind-action")) {
                    return null;
                }
                if (errors[0] != null) return null;
                String category = el.getAttribute("category");
                boolean inherit = "true".equalsIgnoreCase(el.getAttribute("inherit"));
                if (category.isEmpty()) {
                    errors[0] = new XMLParseException("bind-action tag missing category attribute.  Tag: "+el, el, null);
                    return null;
                }
                String trigger = "action";
                if (el.hasAttribute("on")) {
                    trigger = el.getAttribute("on");
                }


                JavaMethodProxy addListenerMethod = componentClass.findMethodProxy("add" + trigger + "listener", 1);
                indent(sb, indent).append("{\n");
                indent += 4;
                String defaultHandler = el.getTextContent();

                String inheritedStr = inherit ? "Inherited" : "";
                indent(sb, indent).append("ActionNode __action = getViewNode().get").append(inheritedStr).append("Action(").append(category).append(");\n");

                indent(sb, indent).append("if (__action == null) _cmp.setVisible(false);\n");
                indent(sb, indent).append("else {\n");
                indent(sb, indent).append("    ").append(componentClass.getQualifiedName()).append(" _fcmp = _cmp;\n");
                if (!defaultHandler.trim().isEmpty()) {
                    // A default handler was supplied
                    indent(sb, indent).append("    // Creating proxy copy of action because a default handler was provided\n");
                    indent(sb, indent).append("    // and we don't want to modify the original action.\n");
                    indent(sb, indent).append("    __action = (ActionNode)__action.createProxy(__action.getParent());\n");
                    indent(sb, indent).append("    class AfterActionCallback_ implements ActionNode.AfterActionCallback {\n");
                    indent(sb, indent).append("        public void onSucess(com.codename1.ui.events.ActionEvent evt) {\n");
                    //indent(sb, indent).append("    __action.addAfterActionCallback(evt -> {\n");
                    indent(sb, indent).append("            ActionNode.ActionNodeEvent event = (ActionNode.ActionNodeEvent)evt;\n");
                    indent(sb, indent).append("            if (event.isConsumed()) return;\n");
                    indent(sb, indent).append("            ").append(componentClass.getQualifiedName()).append(" it = _fcmp;\n");
                    indent(sb, indent).append("            ").append(reformat(defaultHandler, indent + 12)).append("\n");
                    indent(sb, indent).append("        }\n");
                    indent(sb, indent).append("    }\n");
                    indent(sb, indent).append("    __action.addAfterActionCallback(new AfterActionCallback_());\n");

                }
                indent(sb, indent).append("    ActionNode _action = __action;\n");

                if (isA(componentClass.typeEl, "com.codename1.ui.Button")) {
                    indent(sb, indent).append("    com.codename1.rad.ui.DefaultActionViewFactory.initUI(_fcmp, context.getEntity(), _action);\n");
                    indent(sb, indent).append("    Runnable _onUpdate = () -> {\n");
                    indent(sb, indent).append("        com.codename1.rad.ui.DefaultActionViewFactory.update(_fcmp, context.getEntity(), _action);\n");
                    indent(sb, indent).append("    };\n");
                    indent(sb, indent).append("    _onUpdate(_onUpdate);\n");
                }




                indent(sb, indent).append("}\n");

                indent -=4;
                indent(sb, indent).append("}\n");
                return null;
            });
            if (errors[0] != null) {
                throw errors[0];
            }

        }

        private List<String> getConstructorParamNames() {
            List<String> out = new ArrayList<String>();
            forEachAttribute(xmlTag, attr -> {
                if (attr.getName().startsWith("_") && attr.getName().endsWith("_")) {
                    out.add(attr.getName().substring(1, attr.getName().length()-1));
                }
                return null;
            });
            for (org.w3c.dom.Element child : getChildElements(xmlTag)) {
                if (child.hasAttribute("rad-param")) {
                    out.add(child.getAttribute("rad-param"));
                }
            }
            return out;
        }
        private List<String> getConstructorNamedParams() {
            List<String> out = new ArrayList<String>();
            for (String pname : getConstructorParamNames()) {
                if (!Character.isDigit(pname.charAt(0))) {
                    out.add(pname);
                }
            }
            return out;
        }



        private org.w3c.dom.Element xmlTagParent() {
            Node parent = xmlTag.getParentNode();
            if (parent instanceof org.w3c.dom.Element) {
                return (org.w3c.dom.Element)parent;
            }
            return null;
        }


        private org.w3c.dom.Element getRowTemplateTag(org.w3c.dom.Element startingPoint) {
            if (startingPoint == null) startingPoint = xmlTag;
            if (startingPoint.getTagName().equalsIgnoreCase("row-template")) {
                return startingPoint;
            }
            org.w3c.dom.Element parent = startingPoint.getParentNode() instanceof org.w3c.dom.Element ? (org.w3c.dom.Element)startingPoint.getParentNode() : null;
            if (parent == null) return null;
            return getRowTemplateTag(parent);
        }

        private org.w3c.dom.Element getListViewTag() {
            org.w3c.dom.Element rowTemplate = getRowTemplateTag(null);
            if (rowTemplate == null) return null;
            return (org.w3c.dom.Element)rowTemplate.getParentNode();

        }

        void writeBuilderMethod(StringBuilder sb) throws XMLParseException {

            indent(sb, indent).append("private ").append(componentClass.getQualifiedName()).append(" createComponent").append(xmlTag.getAttribute("rad-id")).append("() {\n");
            indent += 4;

            String rowModelType = "Entity";
            String rowModelTypeWrapper = null;
            if (isInsideRowTemplate()) {
                org.w3c.dom.Element listViewTag = getListViewTag();
                if (listViewTag.hasAttribute("row-type")) {
                    rowModelType = listViewTag.getAttribute("row-type");
                    TypeElement rowModelTypeEl = jenv.lookupClass(rowModelType);
                    if (rowModelTypeEl == null) {
                        throw new XMLParseException("Cannot find class corresponding to "+rowModelType+" from tag "+listViewTag, listViewTag, null);
                    }
                    rowModelType = rowModelTypeEl.getQualifiedName().toString();
                    if (jenv.lookupClass(rowModelType+"Wrapper") != null) {
                        rowModelTypeWrapper = rowModelType+"Wrapper";
                    }

                }
                indent(sb, indent).append(rowModelType).append(" rowModel = ");
                if (rowModelTypeWrapper != null) {
                    sb.append(rowModelTypeWrapper).append(".wrap(this.rowModel);\n");
                } else {
                    sb.append("this.rowModel;\n");
                }
                indent(sb, indent).append("int rowIndex = this.rowIndex;\n");
                indent(sb, indent).append("boolean rowSelected = this.rowSelected;\n");
                indent(sb, indent).append("boolean rowFocused = this.rowFocused;\n");

                indent(sb, indent).append("EntityListView rowList = this.rowList;\n");
                indent(sb, indent).append("ViewNode _node = new ViewNode();\n");
                indent(sb, indent).append("_node.setParent(rowList.getViewNode());\n");
                indent(sb, indent).append("ViewContext<").append(rowModelType).append("> context = new ViewContext<>(viewController, rowModel, _node);\n");

            }



            indent(sb, indent).append("java.util.Map<String,String> attributes = new java.util.HashMap<String,String>();\n");
            NamedNodeMap attributes = xmlTag.getAttributes();
            int numAtts = attributes.getLength();
            for (int i=0; i<numAtts; i++) {
                org.w3c.dom.Attr attribute = (org.w3c.dom.Attr)attributes.item(i);
                indent(sb, indent).append(" attributes.put(\"").append(StringEscapeUtils.escapeJava(attribute.getName())).append("\", \"")
                        .append(StringEscapeUtils.escapeJava(attribute.getValue())).append("\");\n");
            }

            if (builderClass != null) {
                indent(sb, indent).append(builderClass.getQualifiedName()).append(" _builder = ").append("new ")
                        .append(builderClass.getQualifiedName()).append("(context, \"").append(StringEscapeUtils.escapeJava(xmlTag.getTagName())).append("\", attributes);\n");
                writeBuilderProperties(sb);
            } else {
                //indent(sb, indent).append(builderClass.getQualifiedName()).append(" _builder = null;\n");
            }
            indent(sb, indent).append(componentClass.typeEl.getQualifiedName()).append(" _cmp = ");
            if (builderMethod != null) {
                builderMethod.callStatic(sb, xmlTag, false);
                sb.append(";\n");
                indent(sb, indent);

                sb.append("com.codename1.rad.ui.builders.SimpleComponentDecorator<").append(componentClass.getQualifiedName()).append("> _builder = new com.codename1.rad.ui.builders.SimpleComponentDecorator<").append(componentClass.getQualifiedName()).append(">(_cmp, context, ");

                sb.append("\"").append(StringEscapeUtils.escapeJava(xmlTag.getTagName())).append("\", attributes);\n");

            } else if (builderClass != null) {
                sb.append("_builder.getComponent();\n");
            } else {
                // First look for constructor that takes EntityView as parameter
                JavaMethodProxy constructor = componentClass.getBestConstructor(xmlTag);
                if (constructor == null) {
                    throw new XMLParseException("No applicable constructor found for tag "+xmlTag.getTagName(), xmlTag, null);
                }
                constructor.callAsConstructor(sb, xmlTag, false);

                sb.append(";\n");
                indent(sb, indent);

                sb.append("com.codename1.rad.ui.builders.SimpleComponentDecorator<").append(componentClass.getQualifiedName()).append("> _builder = new com.codename1.rad.ui.builders.SimpleComponentDecorator<").append(componentClass.getQualifiedName()).append(">(_cmp, context, ");

                sb.append("\"").append(StringEscapeUtils.escapeJava(xmlTag.getTagName())).append("\", attributes);\n");
            }

            writeProperties(sb);

            writeVariables(sb);

            indent(sb, indent).append("// Create child components\n");
            writeChildren(sb);

            indent(sb, indent).append("// Set up bindings\n");
            writeBindings(sb);


            indent(sb, indent).append("// Set up action Bindings\n");
            writeActionBindings(sb);




            String varName = getVarName();
            if (varName != null) {
                indent(sb, indent).append(varName).append(" = ").append("_cmp;\n");
            }
            indent(sb, indent).append("return _cmp;\n");
            indent -= 4;
            indent(sb, indent).append("}\n");


        }


        private String getVarName() {
            if (xmlTag.hasAttribute("rad-var")) {
                String varName = xmlTag.getAttribute("rad-var");
                if (varName.isEmpty()) {
                    return null;
                }
                char firstChar = varName.charAt(0);
                switch (firstChar) {
                    case '-':
                    case '+':
                    case '#':
                        return varName.substring(1);
                }
                return varName;

            }

            if (xmlTag.hasAttribute("id")) {
                return xmlTag.getAttribute("id").replace(' ', '_').replace('-', '_');

            }
            if (xmlTag.hasAttribute("name")) {
                return xmlTag.getAttribute("name").replace(' ', '_').replace('-', '_');
            }
            return null;
        }


    }

    private static StringBuilder indent(StringBuilder sb, int num) {
        for (int i=0; i<num; i++) {
            sb.append(' ');
        }
        return sb;
    }


    private class XMLParseException extends Exception {
        private org.w3c.dom.Element element;
        private String attributeName;
        private String attributeValue;

        XMLParseException(String message, org.w3c.dom.Element element, Throwable cause) {
            super(message, cause);
            this.element = element;
        }
    }




    private static void forEach(org.w3c.dom.Element root, Function<org.w3c.dom.Element, Void> callback) {
        callback.apply(root);
        NodeList children = root.getChildNodes();
        int len = children.getLength();
        for (int i=0; i<len; i++) {
            Node n = (Node)children.item(i);
            if (n instanceof org.w3c.dom.Element) {
                org.w3c.dom.Element childEl = (org.w3c.dom.Element)n;
                forEach(childEl, callback);
            }
        }

    }


    private static void forEachChild(org.w3c.dom.Element root, Function<org.w3c.dom.Element, Void> callback) {
        NodeList children = root.getChildNodes();
        int len = children.getLength();
        for (int i=0; i<len; i++) {
            Node n = (Node)children.item(i);
            if (n instanceof org.w3c.dom.Element) {

                org.w3c.dom.Element childEl = (org.w3c.dom.Element)n;
                callback.apply(childEl);
            }
        }

    }

    private static List<org.w3c.dom.Element> getChildElements(org.w3c.dom.Element root) {
        List<org.w3c.dom.Element> out = new ArrayList<org.w3c.dom.Element>();
        NodeList children = root.getChildNodes();
        int len = children.getLength();
        for (int i=0; i<len; i++) {
            Node n = (Node)children.item(i);
            if (n instanceof org.w3c.dom.Element) {

                org.w3c.dom.Element childEl = (org.w3c.dom.Element)n;
                out.add(childEl);
            }
        }
        return out;
    }

    private static List<org.w3c.dom.Element> getDescendantElements(List<org.w3c.dom.Element> out, org.w3c.dom.Element root) {
        NodeList children = root.getChildNodes();
        int len = children.getLength();
        for (int i=0; i<len; i++) {
            Node n = (Node)children.item(i);
            if (n instanceof org.w3c.dom.Element) {

                org.w3c.dom.Element childEl = (org.w3c.dom.Element)n;
                out.add(childEl);
                getDescendantElements(out, childEl);
            }
        }
        return out;
    }



    private static List<org.w3c.dom.Element> getChildElementsByTagName(org.w3c.dom.Element root, String tagName) {
        return getChildElements(root).stream().filter(e -> e.getTagName().equalsIgnoreCase(tagName)).collect(Collectors.toList());
    }

    private static List<org.w3c.dom.Element> getDescendantElementsByTagName(org.w3c.dom.Element root, String tagName) {
        return getDescendantElements(new ArrayList<>(), root).stream().filter(e -> e.getTagName().equalsIgnoreCase(tagName)).collect(Collectors.toList());
    }


    private static org.w3c.dom.Element getChildElementByTagName(org.w3c.dom.Element root, String tagName) {
        for (org.w3c.dom.Element child : getChildElements(root)) {
            if (child.getTagName().equals(tagName)) return child;
        }
        return null;
    }

    private static void forEachAttribute(org.w3c.dom.Element el, Function<org.w3c.dom.Attr, Void> callback) {
        NamedNodeMap attributes = el.getAttributes();
        int len = attributes.getLength();
        for (int i=0; i<len; i++) {
            Attr attr = (Attr)attributes.item(i);
            callback.apply(attr);
        }
    }



    private class EntityViewBuilder {
        private JavaEnvironment jenv;

        private int nextRadId=0;
        private int indent = 0;
        private org.w3c.dom.Element rootEl;
        private ArrayList<JavaComponentBuilder> componentBuilders = new ArrayList<>();
        private ArrayList<JavaNodeBuilder> nodeBuilders = new ArrayList<>();
        private ArrayList<JavaBeanBuilder> beanBuilders = new ArrayList<>();
        private Map<String,JavaClassProxy> injectableTypes = new HashMap<>();
        private String viewImplements;
        private String viewExtends;
        private String viewModelType = "Entity";
        final private String packageName;
        private String className;
        private TypeElement parentClass;




        EntityViewBuilder(TypeElement parentClass) {
            this.parentClass = parentClass;
            packageName = elements()
                    .getPackageOf(parentClass)
                    .getQualifiedName()
                    .toString();
            viewExtends = parentClass.getSimpleName().toString();

            className = parentClass.getSimpleName().toString();

            if (className.startsWith("Abstract")) {
                className = className.substring("Abstract".length());
            }

            if (parentClass.getSimpleName().contentEquals(className)) {
                className += "Impl";
            }


        }









        private <T> Set<T> setOf(T... elements) {
            Set<T> out = new HashSet<T>();
            for (T el : elements) {
                out.add(el);
            }
            return out;
        }


        /**
         * The AST won't have the types that we are generating yet, so we need to generate "mock" types and add them
         * to the processing environment.  THis is used for introspection (e.g. finding getters and setters).
         * @param env
         * @throws XMLParseException
         */
        private void installTypes(ProcessingEnvironmentWrapper env) throws XMLParseException {
            parse();
            ProcessingEnvironmentWrapper.SchemaBuilder schemaBuilder = env.new SchemaBuilder(packageName + "." + className + "Schema");
            ProcessingEnvironmentWrapper.EntityBuilder entityBuilder = env.new EntityBuilder(packageName + "." + className + "Model");
            ProcessingEnvironmentWrapper.EntityImplBuilder entityImplBuilder = env.new EntityImplBuilder(packageName + "." + className + "ModelImpl");
            ProcessingEnvironmentWrapper.EntityControllerBuilder controllerBuilder = env.new EntityControllerBuilder(packageName + "." + className+"Controller");
            ProcessingEnvironmentWrapper.EntityControllerMarkerBuilder controllerMarkerBuilder = env.new EntityControllerMarkerBuilder(packageName + ".I" + className + "Controller");
            ProcessingEnvironmentWrapper.EntityViewBuilder entityViewBuilder = env.new EntityViewBuilder(packageName + "." + className);
            for (org.w3c.dom.Element defineTag : getChildElementsByTagName(rootEl, "define-tag")) {
                if (defineTag.hasAttribute("name")) {
                    schemaBuilder.addTag(defineTag.getAttribute("name"));

                }

                String type = defineTag.hasAttribute("type") ? defineTag.getAttribute("type") : "String";
                DeclaredType declaredType = jenv.createDeclaredType(type);

                entityBuilder.addProperty(defineTag.getAttribute("name"), declaredType);
                entityImplBuilder.addProperty(defineTag.getAttribute("name"), declaredType);
            }
            for (org.w3c.dom.Element defineTag : getDescendantElementsByTagName(rootEl, "define-slot")) {
                if (defineTag.hasAttribute("id")) {
                    schemaBuilder.addTag(defineTag.getAttribute("id"));

                }
            }
            for (org.w3c.dom.Element defineCategory : getChildElementsByTagName(rootEl, "define-category")) {
                if (defineCategory.hasAttribute("name")) {
                    schemaBuilder.addCategory(defineCategory.getAttribute("name"));
                }
            }
            org.w3c.dom.Element viewModelTag = getChildElementByTagName(rootEl, "view-model");
            if (viewModelTag != null) {
                for (org.w3c.dom.Element defineProperty : getChildElementsByTagName(viewModelTag, "define-property")) {
                    if (!defineProperty.hasAttribute("name") || !defineProperty.hasAttribute("type")) {
                        continue;
                    }

                    String type = defineProperty.getAttribute("type");
                    DeclaredType declaredType = jenv.createDeclaredType(type);
                    entityBuilder.addProperty(defineProperty.getAttribute("name"), declaredType);
                    entityImplBuilder.addProperty(defineProperty.getAttribute("name"), declaredType);
                }
                if (viewModelTag.hasAttribute("implements")) {
                    StringTokenizer strtok = new StringTokenizer(viewModelTag.getAttribute("implements"), ",");
                    while (strtok.hasMoreTokens()) {
                        String tok = strtok.nextToken().trim();
                        if (tok.isEmpty()) continue;
                        TypeElement supertypeEl = jenv.lookupClass(tok);
                        if (supertypeEl == null) {
                            if (!tok.contains(".")) {
                                tok = packageName + "." + tok;
                            }
                            entityBuilder.addInterface(env.createDeclaredType(tok));
                        } else {
                            entityBuilder.addInterface(supertypeEl.asType());
                        }
                    }
                }
                if (viewModelTag.hasAttribute("extends")) {
                    String superclass = viewModelTag.getAttribute("extends");
                    TypeElement supertypeEl = jenv.lookupClass(superclass);
                    if (supertypeEl == null) {
                        if (!superclass.contains(".")) {
                            superclass = packageName + "." + superclass;
                        }
                        entityImplBuilder.superclass(env.createDeclaredType(superclass));
                    } else {
                        entityImplBuilder.superclass(supertypeEl.asType());
                    }
                }
            }
            org.w3c.dom.Element controllerTag = getChildElementByTagName(rootEl, "form-controller");
            if (controllerTag != null) {
                if (controllerTag.hasAttribute("extends")) {
                    String superclass = controllerTag.getAttribute("extends");
                    TypeElement supertypeEl = jenv.lookupClass(superclass);
                    if (supertypeEl == null) {
                        if (!superclass.contains(".")) {
                            superclass = packageName + "." + superclass;
                        }
                        controllerBuilder.superclass(env.createDeclaredType(superclass));
                    } else {
                        controllerBuilder.superclass(supertypeEl.asType());
                    }
                }
                if (controllerTag.hasAttribute("implements")) {
                    StringTokenizer strtok = new StringTokenizer(controllerTag.getAttribute("implements"), ",");
                    while (strtok.hasMoreTokens()) {
                        String tok = strtok.nextToken().trim();
                        if (tok.isEmpty()) continue;
                        TypeElement supertypeEl = jenv.lookupClass(tok);
                        if (supertypeEl == null) {
                            if (!tok.contains(".")) {
                                tok = packageName + "." + tok;
                            }
                            controllerBuilder.addInterface(env.createDeclaredType(tok));
                        } else {
                            controllerBuilder.addInterface(supertypeEl.asType());
                        }
                    }
                }
            }
            env.addTypes(
                    schemaBuilder.build(),
                    entityBuilder.build(),
                    entityImplBuilder.build(),
                    controllerBuilder.build(),
                    controllerMarkerBuilder.build(),
                    entityViewBuilder.build()
            );





        }

        private void loadImports(org.w3c.dom.Element root) {
            forEach(root, el -> {
                if (el.getTagName().equalsIgnoreCase("import")) {
                    jenv.addImports(el.getTextContent());
                }
                return null;
            });

            jenv.addImports("import " + packageName+".*;");
            // Now add the default imports
            if (!root.hasAttribute("strict-imports")) {
                jenv.addImports("import com.codename1.rad.schemas.**;\n");
                jenv.addImports("import com.codename1.rad.ui.builders.**;\n");
                jenv.addImports("import com.codename1.rad.models.*;\n");
                jenv.addImports("import com.codename1.rad.nodes.*;\n");
                jenv.addImports("import com.codename1.rad.ui.entityviews.*;\n");
                jenv.addImports("import com.codename1.rad.ui.beans.*;\n");
                jenv.addImports("import com.codename1.rad.propertyviews.*;\n");
                jenv.addImports("import ca.weblite.shared.components.*;\n");
                jenv.addImports("import com.codename1.ui.*;\n");
                jenv.addImports("import com.codename1.ui.plaf.*;\n");
                jenv.addImports("import com.codename1.components.*;\n");
                jenv.addImports("import static com.codename1.ui.CN.*;\n");
                jenv.addImports("import com.codename1.ui.layouts.*;\n");
                jenv.addImports("import com.codename1.rad.ui.ViewContext;\n");
                jenv.addImports("import com.codename1.rad.ui.EntityView;\n");

            }

        }



        private boolean parsed;
        private void parse() throws XMLParseException {
            if (parsed) return;
            parsed = true;
            org.w3c.dom.Document doc;

            try {
                String xmlString = null;
                for (Element member : elements().getAllMembers(parentClass)) {
                    if (member.getKind() == ElementKind.FIELD && member.getSimpleName().contentEquals("FRAGMENT_XML")) {
                        if (!(member instanceof VariableElement)) {
                            continue;
                        }
                        VariableElement varEl = (VariableElement) member;
                        xmlString = (String) varEl.getConstantValue();
                        break;

                    }
                }

                if (xmlString == null) {
                    throw new XMLParseException("Parent class " + parentClass + " did not contain a FRAGMENT_XML property.", null, null);
                }

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                doc = builder.parse(new ByteArrayInputStream(xmlString.getBytes("utf-8")), "utf-8");

            } catch (Exception ex) {
                throw new XMLParseException("Failed to parse XML in view XML file for " + parentClass, null, ex);
            }

            parse(doc.getDocumentElement());
        }

        private void setViewModelType(String viewModelType) {
            this.viewModelType = viewModelType;
            jenv.setViewModelType(viewModelType);
        }

        private void parse(org.w3c.dom.Element root) throws XMLParseException {
            jenv = new JavaEnvironment(root);
            jenv.rootBuilder = this;
            jenv.addImports("import "+packageName+".*;");

            final XMLParseException[] errors = new XMLParseException[1];
            forEach(root, el -> {
                if (errors[0] != null) {
                    return null;
                }

                String tagName = el.getTagName();
                if (rootEl == null) {
                    rootEl = el;
                    loadImports(el);


                    if (el.hasAttribute("rad-model")) {
                        setViewModelType(el.getAttribute("rad-model"));
                    } else {
                        setViewModelType(className+"Model");

                    }
                    viewImplements = className+"Schema";
                    if (el.hasAttribute("rad-implements")) {
                        viewImplements += ", " + el.getAttribute("rad-implements");
                    }





                }
                if (!tagName.contains("-") && jenv.isComponentTag(tagName)) {
                    JavaClassProxy builderClass = jenv.findComponentBuilderForTag(tagName);
                    if (builderClass != null) {
                        try {
                            JavaComponentBuilder componentBuilder = new JavaComponentBuilder(el, jenv, builderClass, null);
                            componentBuilders.add(componentBuilder);
                        } catch (ClassNotFoundException cnfe) {
                            XMLParseException ex = new XMLParseException("Failed to find component class for builder "+builderClass.getQualifiedName()+" while processing tag "+tagName, el, cnfe);
                            errors[0] = ex;
                            return null;
                        }
                    } else {
                        TypeElement componentTypeEl = jenv.findClassThatTagCreates(tagName);
                        try {
                            if (componentTypeEl != null) {
                                JavaClassProxy componentClassProxy = new JavaClassProxy(componentTypeEl, jenv);
                                JavaComponentBuilder componentBuilder = new JavaComponentBuilder(el, jenv, null, componentClassProxy);
                                componentBuilders.add(componentBuilder);
                            } else {
                                throw new ClassNotFoundException();
                            }
                        } catch (ClassNotFoundException cnfe) {
                            XMLParseException ex = new XMLParseException("Failed to find component class for tag "+tagName, el, cnfe);
                            errors[0] = ex;
                            return null;
                        }
                    }

                } else if (!tagName.contains("-")){
                    TypeElement beanClass = jenv.findClassThatTagCreates(tagName);
                    if (beanClass != null) {
                        try {
                            beanBuilders.add(new JavaBeanBuilder(el, jenv, new JavaClassProxy(beanClass, jenv)));
                        } catch (ClassNotFoundException cnfe) {
                            XMLParseException ex = new XMLParseException("Failed to find bean class for tag "+tagName, el, cnfe);
                            errors[0] = ex;
                            return null;
                        }
                    }
                }
                if (!tagName.contains("-") && jenv.isNodeTag(tagName, true)) {
                    JavaClassProxy builderClass = jenv.findNodeBuilderForTag(tagName);
                    if (builderClass != null) {
                        try {
                            JavaNodeBuilder nodeBuilder = new JavaNodeBuilder(el, jenv, builderClass, null);
                            nodeBuilders.add(nodeBuilder);
                        } catch (ClassNotFoundException cnfe) {
                            XMLParseException ex = new XMLParseException("Failed to find node class for builder "+builderClass.getQualifiedName()+" while processing tag "+tagName, el, cnfe);
                            errors[0] = ex;
                            return null;
                        }
                    } else {
                        TypeElement nodeTypeEl = jenv.findClassThatTagCreates(tagName, "com.codename1.rad.nodes.Node");
                        try {
                            if (nodeTypeEl != null) {
                                JavaClassProxy nodeClassProxy = new JavaClassProxy(nodeTypeEl, jenv);
                                JavaNodeBuilder nodeBuilder = new JavaNodeBuilder(el, jenv, null, nodeClassProxy);
                                nodeBuilders.add(nodeBuilder);
                            } else {
                                throw new ClassNotFoundException();
                            }
                        } catch (ClassNotFoundException cnfe) {
                            XMLParseException ex = new XMLParseException("Failed to find node class for tag "+tagName, el, cnfe);
                            errors[0] = ex;
                            return null;
                        }
                    }
                }

                return null;
            });

            if (errors[0] != null) {
                throw errors[0];
            }

        }



        private void writeClassVariables(StringBuilder sb) throws XMLParseException {
            XMLParseException errors[] = new XMLParseException[1];
            Map<String,String> variables = new HashMap<String,String>();
            indent(sb, indent).append("// Placeholder for the row model when creating EntityListCellRenderer.\n");
            indent(sb, indent).append("// Can access inside <script> tags inside <row-template>\n");
            indent(sb, indent).append("com.codename1.rad.models.Entity rowModel;\n");
            indent(sb, indent).append("// Placeholder for the row index when creating EntityListCellRenderer.\n");
            indent(sb, indent).append("// Can access inside <script> tags inside <row-template>\n");
            indent(sb, indent).append("int rowIndex;\n");
            indent(sb, indent).append("// Placeholder for the row selected state when creating EntityListCellRenderer.\n");
            indent(sb, indent).append("// Can access inside <script> tags inside <row-template>\n");
            indent(sb, indent).append("boolean rowSelected;\n");
            indent(sb, indent).append("// Placeholder for the row focused state when creating EntityListCellRenderer.\n");
            indent(sb, indent).append("// Can access inside <script> tags inside <row-template>\n");
            indent(sb, indent).append("boolean rowFocused;\n");
            indent(sb, indent).append("// Placeholder for the EntityListView when creating EntityListCellRenderer.\n");
            indent(sb, indent).append("// Can access inside <script> tags inside <row-template>\n");
            indent(sb, indent).append("com.codename1.rad.ui.entityviews.EntityListView rowList;\n");
            forEach(this.rootEl, el -> {
                if (errors[0] != null) return null;
                if (el.getTagName().equalsIgnoreCase("var")) {
                    String name = el.getAttribute("name");
                    if (name.isEmpty()) {
                        errors[0] = new XMLParseException("var tag requires a name attribute.", el, null);
                        return null;
                    }
                    if (variables.containsKey(name)) {
                        return null;
                    }
                    org.w3c.dom.Element parentEl = (org.w3c.dom.Element)el.getParentNode();
                    String visibility = "";

                    if (parentEl != null) {
                        String parentTag = parentEl.getTagName();
                        if (parentTag.equalsIgnoreCase("public")) {
                            visibility = "public";
                        } else if (parentTag.equalsIgnoreCase("private")) {
                            visibility = "private";
                        } else if (parentTag.equalsIgnoreCase("protected")) {
                            visibility = "protected";
                        } else {
                            visibility = "";
                        }
                    }

                    String type = null;
                    String lookup = el.getAttribute("lookup");
                    if (!lookup.isEmpty()) {
                        type = lookup;


                    }

                    if (el.hasAttribute("type")) {
                        type = el.getAttribute("type");
                    }
                    if (type != null) {
                        if ("true".equals(el.getAttribute("inject"))) {
                            // If inject=true then this variable will be used to inject into properties
                            // and parameters that take this type.
                            TypeElement injectableType = jenv.lookupClass(type);
                            if (injectableType != null) {
                                injectableTypes.put(name, new JavaClassProxy(injectableType, jenv));
                            }
                        }
                        variables.put(name, type);
                        indent(sb, indent);
                        if (!visibility.isEmpty()) {
                            sb.append(visibility).append(" ");
                        }
                        sb.append(type).append(" ").append(name).append(";\n");
                        return null;
                    }



                    return null;


                }
                if (!el.getTagName().contains("-") && (el.hasAttribute("rad-var") || el.hasAttribute("id") || el.hasAttribute("name"))) {
                    String varName = el.getAttribute("rad-var");
                    if (varName == null || varName.isEmpty()) varName = el.getAttribute("id");
                    if (varName == null || varName.isEmpty()) varName = el.getAttribute("name");
                    if (varName == null || varName.isEmpty()) {
                        return null;
                    }
                    varName = varName.replace('-', '_').replace(' ', '_');
                    String modifiers = "";
                    if (varName.startsWith("-")) {
                        modifiers = "private";
                        varName = varName.substring(1);
                    } else if (varName.startsWith("#")) {
                        modifiers = "protected";
                        varName = varName.substring(1);
                    } else if (varName.startsWith("+")) {
                        modifiers = "public";
                        varName = varName.substring(1);
                    } else if (!el.hasAttribute("rad-var")) {
                        modifiers = "public";
                    }

                    TypeElement type = jenv.findClassThatTagCreates(el.getTagName());
                    if (type == null) {
                        //throw new IllegalStateException("Failed to create variable "+varName+" for tag "+el+" because we couldn't determine the type of the tag.  Check your imports to make sure that a class matching "+el.getTagName()+" is findable.");
                        return null;
                    }
                    indent(sb, indent);
                    if (!modifiers.isEmpty()) {
                        sb.append(modifiers).append(" ");
                    }
                    sb.append(type.getQualifiedName().toString()).append(" ").append(varName).append(";\n");

                }
                return null;
            });
            if (errors[0] != null) throw errors[0];
        }

        /**
         * Writes methods for script tags in the class.
         * @param sb
         * @throws XMLParseException
         */
        private void writeScriptMethods(StringBuilder sb) throws XMLParseException {
            XMLParseException[] errors = new XMLParseException[1];
            forEach(rootEl, el -> {
                if (el.getTagName().equalsIgnoreCase("script")) {
                    if (errors[0] != null) return null;
                    String id = el.getAttribute("rad-id");
                    org.w3c.dom.Element parentEl = (org.w3c.dom.Element)el.getParentNode();
                    TypeElement parentType = jenv.findClassThatTagCreates(parentEl.getTagName());
                    if (parentType == null) {
                        errors[0] = new XMLParseException("Cannot find class for tag "+parentEl.getTagName()+" while generating script element.", el, null);
                        return null;
                    }
                    indent(sb, indent).append("private void script").append(id).append("(").append(parentType.getQualifiedName()).append(" it) {\n");
                    indent += 4;
                    String scriptContent = el.getTextContent();
                    sb.append(reformat(scriptContent, indent));
                    sb.append("\n");
                    indent -= 4;
                    indent(sb, indent).append("}\n");


                }
                return null;
            });
            if (errors[0] != null) {
                throw errors[0];
            }
        }

        private void writeMarkerInterfaces(StringBuilder sb) {
            indent(sb, indent).append("public static interface I").append(className).append("Controller{}\n");

        }

        private void writeFormControllerMarkerInterface(StringBuilder sb) {
            sb.append("package ").append(packageName).append(";\n");
            sb.append("import com.codename1.rad.annotations.*;\n");
            sb.append("/**\n");
            sb.append(" * A marker interface that can be used to signify that a FormController is designed to work\n");
            sb.append(" * with ").append(className).append(".  \n");
            sb.append(" * Usage: appController.addObjectFactory(I").append(className).append("Controller.class, evt -> {...});\n");
            sb.append(" */\n");
            sb.append("@Autogenerated\n");
            sb.append("public interface I").append(className).append("Controller {}\n");
        }



        private void writeFormController(StringBuilder sb) {
            sb.append("package ").append(packageName).append(";\n");
            sb.append("import com.codename1.rad.annotations.*;\n");
            sb.append("import com.codename1.rad.controllers.FormController;\n");
            sb.append("import com.codename1.rad.controllers.Controller;\n");
            jenv.writeImports(sb);
            NodeList formControllerTags = rootEl.getElementsByTagName("form-controller");
            org.w3c.dom.Element formControllerTag = null;
            if (formControllerTags.getLength() > 0) formControllerTag = (org.w3c.dom.Element)formControllerTags.item(0);

            String parentController = formControllerTag == null || !formControllerTag.hasAttribute("extends") ? "FormController" : formControllerTag.getAttribute("extends");
            String ifaces = "I" + className + "Controller";
            if (formControllerTag != null && formControllerTag.hasAttribute("implements")) {
                ifaces += ", " + formControllerTag.getAttribute("implements");
            }


            indent(sb, indent).append("public class ").append(className).append("Controller extends ").append(parentController).append(" implements ").append(ifaces).append(" {\n");
            indent += 4;
            indent(sb, indent).append("private ").append(viewModelType).append(" viewModel;\n");
            org.w3c.dom.Element overrideConstructors = formControllerTag != null ? getChildElementByTagName(formControllerTag, "override-constructors") : null;
            org.w3c.dom.Element defineConstructors = formControllerTag != null ? getChildElementByTagName(formControllerTag, "define-constructors") : null;
            if (overrideConstructors != null) {
                sb.append(reformat(overrideConstructors.getTextContent(), indent));
                sb.append("\n");
            } else {
                indent(sb, indent).append("public ").append(className).append("Controller(@Inject Controller parent) {\n");
                indent += 4;
                indent(sb, indent).append("super(parent);\n");
                indent(sb, indent).append("this.viewModel = createViewModel();\n");
                indent -= 4;
                indent(sb, indent).append("}\n");
                indent(sb, indent).append("public ").append(className).append("Controller(@Inject Controller parent, @Inject ").append(viewModelType).append(" viewModel) {\n");
                indent += 4;
                indent(sb, indent).append("super(parent);\n");
                indent(sb, indent).append("this.viewModel = viewModel;\n");
                indent -= 4;
                indent(sb, indent).append("}\n");
                TypeElement viewModelTypeEl = jenv.lookupClass(viewModelType);
                TypeElement viewModelClassEl = viewModelTypeEl;
                if (viewModelTypeEl != null) {
                    if (viewModelTypeEl.getKind() == ElementKind.INTERFACE || viewModelTypeEl.getModifiers().contains(Modifier.ABSTRACT)) {
                        viewModelClassEl = jenv.lookupClass(viewModelTypeEl+"Impl");
                        if (viewModelClassEl == null) {
                            List<JavaClassProxy> candidates = jenv.findInstantiatableClassesAssignableTo(elements().getPackageOf(viewModelTypeEl),
                                    rootEl.getOwnerDocument().createElement(viewModelTypeEl.getSimpleName().toString()),
                                    viewModelTypeEl.getQualifiedName().toString());
                            if (!candidates.isEmpty()) {
                                viewModelClassEl = candidates.get(0).typeEl;
                            }
                        }

                    }
                }
                if (viewModelClassEl == null) {
                    throw new IllegalArgumentException("Cannot find view model for "+viewModelType+" while generating form controller for " +className);
                }
                indent(sb, indent).append("public ").append(viewModelType).append(" createViewModel() {\n");

                indent(sb, indent).append("    return new ").append(_(viewModelClassEl.getQualifiedName().toString())).append("();\n");


                indent(sb, indent).append("}\n");

            }
            if (defineConstructors != null) {
                sb.append(reformat(defineConstructors.getTextContent(), indent));
                sb.append("\n");
            }

            org.w3c.dom.Element overrideOnStartController = formControllerTag != null ? getChildElementByTagName(formControllerTag, "override-onStartController") : null;
            indent(sb, indent).append("@Override\n");
            indent(sb, indent).append("protected void onStartController() {\n");
            indent += 4;
            if (overrideOnStartController != null) {
                sb.append(reformat(overrideOnStartController.getTextContent(), indent));
            } else {
                indent(sb, indent).append("super.onStartController();\n");
                indent(sb, indent).append("setView(new ").append(className).append("(new ViewContext<").append(viewModelType).append(">(this, viewModel)));\n");
            }
            indent -= 4;
            indent(sb, indent).append("}\n");

            org.w3c.dom.Element overrideOnStopController = formControllerTag != null ? getChildElementByTagName(formControllerTag, "override-onStopController") : null;
            indent(sb, indent).append("@Override\n");
            indent(sb, indent).append("protected void onStopController() {\n");
            indent += 4;
            if (overrideOnStopController != null) {
                sb.append(reformat(overrideOnStopController.getTextContent(), indent));
            } else {
                indent(sb, indent).append("super.onStopController();\n");

            }
            indent -= 4;
            indent(sb, indent).append("}\n");

            org.w3c.dom.Element defineMethods = formControllerTag != null ? getChildElementByTagName(formControllerTag, "define-methods") : null;
            if (defineMethods != null) {
                sb.append(reformat(defineMethods.getTextContent(), indent));
            }


            indent -= 4;
            indent(sb, indent).append("}\n");


        }


        private void writeSchemaInterface(StringBuilder sb) throws XMLParseException {
            indent(sb, indent).append("package ").append(packageName).append(";\n");
            sb.append("import com.codename1.rad.annotations.*;\n");
            jenv.writeImports(sb);
            sb.append("import com.codename1.rad.schemas.*;\n");
            sb.append("@Autogenerated\n");
            sb.append("public interface ").append(className).append("Schema {\n");
            indent += 4;
            Set<String> usedNames = new HashSet<>();
            for (org.w3c.dom.Element defineTag : getChildElementsByTagName(rootEl, "define-tag")) {

                String name = defineTag.getAttribute("name");
                if (usedNames.contains(name)) {
                    continue;
                }
                String value = defineTag.getAttribute("value");
                if (value.isEmpty()) {
                    value = "new Tag(\"" + StringEscapeUtils.escapeJava(name) + "\")";
                }
                usedNames.add(name);
                indent(sb, indent).append("public static final Tag ").append(name).append(" = ").append(value).append(";\n");
            }
            for (org.w3c.dom.Element defineTag : getDescendantElementsByTagName(rootEl, "define-slot")) {
                String name = defineTag.getAttribute("id");
                if (usedNames.contains(name)) {
                    continue;
                }
                usedNames.add(name);
                String value = "";
                if (value.isEmpty()) {
                    value = "new Tag(\"" + StringEscapeUtils.escapeJava(name) + "\")";
                }
                indent(sb, indent).append("public static final Tag ").append(name).append(" = ").append(value).append(";\n");
            }
            for (org.w3c.dom.Element defineCategory : getChildElementsByTagName(rootEl, "define-category")) {
                String name = defineCategory.getAttribute("name");

                String value = defineCategory.getAttribute("value");
                if (value.isEmpty()) {
                    value = "new ActionNode.Category(\"" + StringEscapeUtils.escapeJava(name) + "\")";
                }
                indent(sb, indent).append("public static final ActionNode.Category ").append(name).append(" = ").append(value).append(";\n");
            }
            for (org.w3c.dom.Element defineProperty : getChildElementsByTagName(rootEl, "define-property")) {
                String name = defineProperty.getAttribute("name");
                String type = defineProperty.getAttribute("type");
                String value = null;
                if (type.equalsIgnoreCase("text") || type.equalsIgnoreCase("string")) {
                    value = "ViewProperty.stringProperty()";
                    type = "String";
                } else if (type.equalsIgnoreCase("boolean")) {
                    value = "ViewProperty.booleanProperty()";
                    type = "Boolean";
                } else if (type.equalsIgnoreCase("double")) {
                    value = "ViewProperty.doubleProperty()";
                    type = "Double";
                } else if (type.equalsIgnoreCase("float")) {
                    value = "ViewProperty.floatProperty()";
                    type = "Float";
                } else if (type.equalsIgnoreCase("int")) {
                    value = "ViewProperty.intProperty()";
                    type = "Integer";
                } else {
                    value = "new ViewProperty(ContentType.createObjectType(" + type + ".class))";
                }

                if (type.isEmpty()) {
                    throw new XMLParseException("define-property tag requires type attribute", defineProperty, null);
                }
                indent(sb, indent).append("public static final ViewProperty<").append(type).append("> ").append(name).append(" = ").append(value).append(";\n");
            }


            indent -= 4;
            sb.append("}\n");
        }


        private void writeModelInterface(StringBuilder sb) throws XMLParseException {
            indent(sb, indent).append("package ").append(packageName).append(";\n");
            sb.append("import com.codename1.rad.annotations.*;\n");
            jenv.writeImports(sb);

            org.w3c.dom.Element viewModelTag = getChildElementByTagName(rootEl, "view-model");
            String ifaces = "Entity, " + className + "Schema";
            if (viewModelTag != null && viewModelTag.hasAttribute("implements")) {
                ifaces += ", " + viewModelTag.getAttribute("implements");
            }

            sb.append("@RAD\n");
            sb.append("@Autogenerated\n");
            sb.append("public interface ").append(className).append("Model extends ").append(ifaces).append(" {\n");
            indent += 4;
            Set<String> usedPropertyNames = new HashSet<String>();
            if (viewModelTag != null) {
                for (org.w3c.dom.Element defineProp : getChildElementsByTagName(viewModelTag, "define-property")) {
                    String name = defineProp.getAttribute("name");
                    String tags = defineProp.getAttribute("tags");
                    StringBuilder tagsStr = new StringBuilder();
                    StringTokenizer strtok = new StringTokenizer(tags, ",");
                    while (strtok.hasMoreTokens()) {
                        String tok = strtok.nextToken().trim();
                        if (tagsStr.length() > 0) tagsStr.append(", ");
                        tagsStr.append("\"").append(StringEscapeUtils.escapeJava(tok)).append("\"");
                    }
                    String type = defineProp.getAttribute("type");
                    if (type.isEmpty()) {
                        type = "String";
                    }
                    if (tagsStr.length() > 0) {
                        tagsStr.insert(0, "tag=");
                    }

                    if (name.isEmpty()) {
                        throw new XMLParseException("define-property tag requires attribute name", defineProp, null);
                    }
                    if (usedPropertyNames.contains(name)) {
                        continue;
                    }
                    usedPropertyNames.add(name);
                    String ucName = name.substring(0, 1).toUpperCase() + name.substring(1);
                    indent(sb, indent).append("@RAD(").append(tagsStr).append(")\n");
                    indent(sb, indent).append(type).append(" get").append(ucName).append("();\n");
                    indent(sb, indent).append("@RAD\n");
                    indent(sb, indent).append("void set").append(ucName).append("(").append(type).append(" ").append(name).append(");\n");

                }
            }

            for (org.w3c.dom.Element defineTag : getChildElementsByTagName(rootEl, "define-tag")) {
                String tag = defineTag.getAttribute("name");
                String name = tag;

                String type = defineTag.getAttribute("type");
                if (type.isEmpty()) {
                    type = "String";
                }


                if (name.isEmpty()) {
                    throw new XMLParseException("define-tag tag requires attribute name", defineTag, null);
                }
                if (usedPropertyNames.contains(name)) {
                    continue;
                }
                usedPropertyNames.add(name);
                String ucName = name.substring(0, 1).toUpperCase() + name.substring(1);
                indent(sb, indent).append("@RAD(tag=\"").append(name).append("\")\n");
                indent(sb, indent).append(type).append(" get").append(ucName).append("();\n");
                indent(sb, indent).append("@RAD\n");
                indent(sb, indent).append("void set").append(ucName).append("(").append(type).append(" ").append(name).append(");\n");

            }
            indent -= 4;
            sb.append("}\n");

        }


        private void writeViewClass(StringBuilder sb) throws XMLParseException {
            indent(sb, indent).append("package ").append(packageName).append(";\n");
            sb.append("import com.codename1.rad.annotations.*;\n");
            sb.append("import com.codename1.rad.controllers.*;\n");
            jenv.writeImports(sb);
            sb.append("@Autogenerated\n");
            sb.append("public class ").append(className).append(" extends ").append(viewExtends);
            if (viewImplements != null) {
                sb.append(" implements ").append(viewImplements);
            }
            sb.append(" {\n");
            indent += 4;
            indent(sb, indent).append("private final ViewContext<").append(viewModelType).append("> context;\n");
            indent(sb, indent).append("private final FormController formController;\n");
            indent(sb, indent).append("private final ApplicationController applicationController;\n");
            indent(sb, indent).append("private final AppSectionController sectionController;\n");
            indent(sb, indent).append("private final ViewController viewController;\n");
            indent(sb, indent).append("private final FormController parentFormController;\n");
            writeClassVariables(sb);
            writeScriptMethods(sb);
            indent(sb, indent).append("public ").append(className).append("(ViewContext<").append(viewModelType).append("> context) {\n");
            indent += 4;
            indent(sb, indent).append("super(context);\n");
            indent(sb, indent).append("this.context = context;\n");
            indent(sb, indent).append("this.formController = context.getController().getFormController();\n");
            indent(sb, indent).append("this.viewController = context.getController();\n");
            indent(sb, indent).append("this.applicationController = context.getController().getApplicationController();\n");
            indent(sb, indent).append("this.sectionController = context.getController().getSectionController();\n");
            indent(sb, indent).append("this.parentFormController = (this.formController == null || this.formController.getParent() == null) ? null : this.formController.getParent().getFormController();\n");
            indent(sb, indent).append("getAllStyles().stripMarginAndPadding();\n");
            indent(sb, indent).append("setLayout(new BorderLayout());\n");
            indent(sb, indent).append("add(BorderLayout.CENTER, ").append("createComponent0());\n");
            indent -= 4;
            indent(sb, indent).append("}\n");

            for (JavaComponentBuilder component : componentBuilders) {
                component.indent = indent;
                component.writeBuilderMethod(sb);
            }

            for (JavaNodeBuilder nodeBuilder : nodeBuilders) {
                nodeBuilder.indent = indent;
                nodeBuilder.writeBuilderMethod(sb);
            }

            for (JavaBeanBuilder beanBuilder : beanBuilders) {
                beanBuilder.indent = indent;
                beanBuilder.writeBuilderMethod(sb);
            }

            sb.append("    @Override\n");
            sb.append("    public void commit() {}\n");
            sb.append("    private java.util.List<Runnable> _onUpdate;\n");
            sb.append("    private void _onUpdate(Runnable runnable) {\n");
            sb.append("        if (_onUpdate == null) _onUpdate = new java.util.ArrayList<Runnable>();\n");
            sb.append("        _onUpdate.add(runnable);\n");
            sb.append("    }\n");
            sb.append("    @Override\n");
            sb.append("    public void update() {\n");
            sb.append("        if (_onUpdate != null && !_onUpdate.isEmpty()) {\n");
            sb.append("            for (Runnable r : _onUpdate) {\n");
            sb.append("                r.run();\n");
            sb.append("            }\n");
            sb.append("        }\n");
            sb.append("    }\n");
            sb.append("    @Override\n");
            sb.append("    public void activate() {\n");
            sb.append("        super.activate();\n");
            sb.append("    }\n");
            sb.append("    private <T extends Node> T _setParent(Class<T> cls, T node) {\n");
            sb.append("        node.setParent(getViewNode());\n");
            sb.append("        return node;\n");
            sb.append("    }\n");
            sb.append("    private <T> T _getInjectedParameter(Class<T> type) {\n");
            sb.append("        if (type == Entity.class) return (T)context.getEntity();\n");
            sb.append("        if (type == EntityView.class) return (T)this;\n");
            sb.append("        T lookedUp = (T)getContext().getController().lookup(type);\n");
            sb.append("        if (lookedUp != null) return lookedUp;\n");
            sb.append("        return null;\n");
            sb.append("    }\n");

            indent -= 4;
            sb.append("}\n");


        }



        private String getQualifiedName() {
            String out = parentClass.getQualifiedName().toString();
            if (out.contains(".")) {
                out = out.substring(0, out.lastIndexOf(".") + 1);
            } else {
                out = "";
            }
            return out + className;
        }



        public void createViewSourceFile() throws XMLParseException, IOException {
            parse();
            String qualifiedName = getQualifiedName();

            StringBuilder sb = new StringBuilder();


            writeViewClass(sb);

            JavaFileObject sourceFile = createSourceFile(qualifiedName, new Element[]{parentClass});
            try (java.io.Writer w = sourceFile.openWriter()) {
                String content = sb.toString();

                w.write(content);
            }

        }



        public void createModelSourceFile() throws XMLParseException, IOException {
            parse();
            if (rootEl.hasAttribute("rad-model")) {
                // The view explicitly specifies a model type so we don't generate a model for it.

                return;
            }

            StringBuilder sb = new StringBuilder();
            String modelQualifiedName = getQualifiedName()+"Model";


            writeModelInterface(sb);


            JavaFileObject sourceFile = createSourceFile(modelQualifiedName, new Element[]{parentClass});
            try (java.io.Writer w = sourceFile.openWriter()) {
                String content = sb.toString();

                w.write(content);
            }

        }


        public void createSchemaSourceFile() throws XMLParseException, IOException {
            parse();
            StringBuilder sb = new StringBuilder();
            String schemaQualifiedName = getQualifiedName()+"Schema";

            // This is the second pass
            writeSchemaInterface(sb);


            JavaFileObject sourceFile = createSourceFile(schemaQualifiedName, new Element[]{parentClass});


            try (java.io.Writer w = sourceFile.openWriter()) {
                String content = sb.toString();

                w.write(content);
            }

        }


        JavaFileObject createSourceFile(String qualifiedName, Element[] dependentElements) throws IOException {
            return env().getFiler().createSourceFile(qualifiedName, dependentElements);


        }


        public void createControllerSourceFile() throws XMLParseException, IOException {
            parse();
            StringBuilder sb = new StringBuilder();
            String controllerQualifiedName = getQualifiedName()+"Controller";


            writeFormController(sb);


            JavaFileObject sourceFile = createSourceFile(controllerQualifiedName, new Element[]{parentClass});
            try (java.io.Writer w = sourceFile.openWriter()) {
                String content = sb.toString();

                w.write(content);
            }

        }


        public void createControllerMarkerInterfaceSourceFile() throws XMLParseException, IOException {
            parse();
            StringBuilder sb = new StringBuilder();
            String controllerQualifiedName = getQualifiedName().substring(0, getQualifiedName().lastIndexOf(".")) + ".I" + className + "Controller";


            writeFormControllerMarkerInterface(sb);


            JavaFileObject sourceFile = createSourceFile(controllerQualifiedName, new Element[]{parentClass});
            try (java.io.Writer w = sourceFile.openWriter()) {
                String content = sb.toString();

                w.write(content);
            }

        }


        public boolean containsBeanBuilderFor(Name qualifiedName) {
            for (JavaBeanBuilder builder : beanBuilders) {
                if (builder.beanClass.typeEl.getQualifiedName().contentEquals(qualifiedName)) {
                    return true;
                }
            }
            return false;
        }
    }

    private TypeElement toTypeElement(TypeMirror mirror) {
        switch (mirror.getKind()) {
            case FLOAT:
            case INT:
            case BOOLEAN:
            case DOUBLE:
            case LONG:
            case CHAR:
            case BYTE:
            case SHORT:
                return types().boxedClass((PrimitiveType)mirror);
            case DECLARED:
            case ERROR:
                DeclaredType declaredType = (DeclaredType)mirror;
                TypeElement out = (TypeElement)declaredType.asElement();
                if (out != null) {
                    return out;
                }
                out = elements().getTypeElement(mirror.toString());
                if (out == null) {
                    out = elements().getTypeElement(mirror.toString().substring(0, mirror.toString().indexOf("<")));
                }
                if (out == null) {
                    throw new IllegalArgumentException("Cannot find class "+mirror);
                }
                return out;



        }

        throw new IllegalArgumentException("Cannot convert type mirror "+mirror+" to type element");
    }



    private static String reformat(String content, int indentLevel) {
        int oldLevel = getIndentLevel(content);
        StringBuilder sb = new StringBuilder();
        Scanner scanner = new Scanner(content);
        int delta = indentLevel - oldLevel;
        String lineSeparator = getLineSeparator(content);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if (delta > 0) {
                for (int i=0; i<delta; i++) {
                    sb.append(' ');
                }
                sb.append(line);
                sb.append(lineSeparator);
            } else {
                int lineIndent = getIndentLevel(line);
                if (lineIndent + delta >= 0) {
                    sb.append(line.substring(-delta));
                } else {
                    sb.append(line.substring(lineIndent));
                }
                sb.append(lineSeparator);
            }
        }
        return sb.toString();
    }

    private static int getIndentLevel(String content) {
        Scanner scanner = new Scanner(content);
        outer: while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.trim().isEmpty()) continue;
            int count = 0;
            int len = line.length();
            loop: for (int i=0; i<len; i++) {
                char c = line.charAt(i);
                switch (c) {
                    case ' ': count++; break;
                    case '\t': count++; break;
                    case '\n': continue outer;
                    default: break loop;
                }
            }
            return count;
        }

        return 0;
    }

    private static String getLineSeparator(String content) {
        if (content.contains("\r\n")) {
            return "\r\n";
        } else if (content.contains("\n")) {
            return "\n";
        } else {
            return System.lineSeparator();
        }
    }

    /**
     * Extracts named parameters from the given element.  Named parameters can be specified both with
     * attributes of the form _paramName_="..."  (i.e. the attribute name starts and ends with an underscore),
     * and with child elements with attribute rad-param="paramName". In both cases paramName cannot begin with a digit.
     * @param element
     * @return
     */
    private static Set<String> extractNamedParameters(org.w3c.dom.Element element) {
        Set<String> out = new HashSet<String>();
        forEachAttribute(element, attr -> {
            String name = attr.getName();
            if (name.startsWith("_") && name.endsWith("_") && name.length() > 2 && !Character.isDigit(name.charAt(1))) {
                out.add(name.substring(1, name.length()-1));
            }
            return null;
        });
        forEachChild(element, child -> {
            if (child.hasAttribute("rad-param")) {
                String name = child.getAttribute("rad-param");
                if (name.length() > 0 && !Character.isDigit(name.charAt(0))) {
                    out.add(name);
                }
            }
            return null;
        });
        return out;
    }

    /**
     * Extracts the indexed parameters from element.  Indexed parameter are specified both by attributes
     * of the form _N_="..." where N is an integer, and via child elements with attribute rad-param="N" where N is an
     * integer.  These parameters are used when calling the constructor that is created by this tag.
     * @param element An element to check.
     * @return
     */
    private static Set<Integer> extractIndexedParameters(org.w3c.dom.Element element) {
        Set<Integer> out = new HashSet<Integer>();
        forEachAttribute(element, attr -> {
            String name = attr.getName();
            if (name.startsWith("_") && name.endsWith("_") && name.length() > 2 && Character.isDigit(name.charAt(1))) {
                try {
                    out.add(Integer.parseInt(name.substring(1, name.length() - 1)));
                } catch (NumberFormatException ex){}
            }
            return null;
        });
        forEachChild(element, child -> {
            if (child.hasAttribute("rad-param")) {
                String name = child.getAttribute("rad-param");
                if (name.length() > 0 && Character.isDigit(name.charAt(0))) {
                    try {
                        out.add(Integer.parseInt(name));
                    } catch (NumberFormatException ex){}
                }
            }
            return null;
        });
        return out;
    }


    private <T extends Element> T wrap(T element) {
        return env().wrap(element);
    }

    static boolean isElementInsideRowTemplate(org.w3c.dom.Element el) {
        if (el.getTagName().equalsIgnoreCase("row-template")) {
            return true;
        }
        Node parentNode = el.getParentNode();
        if (parentNode instanceof org.w3c.dom.Element) {
            org.w3c.dom.Element parentEl = (org.w3c.dom.Element)parentNode;
            return isElementInsideRowTemplate(parentEl);
        }
        return false;
    }

}
