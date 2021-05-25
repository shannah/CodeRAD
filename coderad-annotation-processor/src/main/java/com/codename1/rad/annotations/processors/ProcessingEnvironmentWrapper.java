package com.codename1.rad.annotations.processors;


import com.codename1.rad.annotations.Inject;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class ProcessingEnvironmentWrapper implements ProcessingEnvironment {

    public static interface RElement extends Element{}
    public static interface RTypeElement extends RElement, TypeElement {}
    public static interface RTypeMirror extends TypeMirror {}
    public static interface RDeclaredType extends RTypeMirror, DeclaredType {}
    public static interface RExecutableType extends RTypeMirror, ExecutableType {}
    public static interface RNoType extends RTypeMirror, NoType {}


    private ProcessingEnvironment wrapped;
    private TypesWrapper types;
    private ElementsWrapper elements;

    private Map<String,CustomTypeElement> typeMap = new HashMap<>();
    private Map<String,PackageWrapper> packageMap = new HashMap<>();




    public void addTypes(CustomTypeElement... types) {
        for (CustomTypeElement e : types) {
            typeMap.put(e.getQualifiedName().toString(), e);
            Element enclosing = e.getEnclosingElement();
            if (enclosing == null) {
                if (e.getNestingKind() == NestingKind.TOP_LEVEL) {
                    enclosing = elements.getPackageElement(extractParentQualifiedName(e.getQualifiedName().toString()));
                }
            }
            if (enclosing != null && enclosing.getKind() == ElementKind.PACKAGE) {
                ((PackageWrapper)wrap(enclosing)).enclosedElements.add(e);

            }

        }
    }


    public ProcessingEnvironmentWrapper(ProcessingEnvironment wrapped) {
        this.wrapped = wrapped;
        types = new TypesWrapper(wrapped.getTypeUtils());
        elements = new ElementsWrapper(wrapped.getElementUtils());
    }
    @Override
    public Map<String, String> getOptions() {
        return wrapped.getOptions();
    }

    @Override
    public Messager getMessager() {
        return wrapped.getMessager();
    }

    @Override
    public Filer getFiler() {
        return wrapped.getFiler();
    }

    @Override
    public Elements getElementUtils() {
        return elements;
    }

    @Override
    public Types getTypeUtils() {
        return types;
    }

    @Override
    public SourceVersion getSourceVersion() {
        return wrapped.getSourceVersion();
    }

    @Override
    public Locale getLocale() {
        return wrapped.getLocale();
    }


    public class TypesWrapper implements Types {
        private Types wrapped;

        private TypesWrapper(Types wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public Element asElement(TypeMirror t) {
            Element out = null;
            if (isNativeMirror(t)) {
                out = wrapped.asElement(t);

            }
            if (t instanceof DeclaredType) {
                if (t instanceof CustomDeclaredType) {
                    out = elements.getTypeElement(((CustomDeclaredType)t).stringValue);
                } else {
                    out = ((DeclaredType) t).asElement();
                }
            }
            if (out == null) {
                // couldn't find it
                out = typeMap.get(t.toString());
            }

            return out;
        }

        @Override
        public boolean isSameType(TypeMirror t1, TypeMirror t2) {
            if (isNativeMirror(t1) && isNativeMirror(t2)) {
                try {
                    return wrapped.isSameType(t1, t2);
                } catch (Exception ex) {
                    System.err.println("Exception raised while checking "+t1+" and "+t2 +" for same type");
                    ex.printStackTrace();
                }
            }

            if (t1.getKind() != t2.getKind()) {
                return false;
            }


            switch (t1.getKind()) {
                case DECLARED:
                    DeclaredType dt1 = (DeclaredType)t1;
                    DeclaredType dt2 = (DeclaredType)t2;
                    if (dt1.getTypeArguments().size() != dt2.getTypeArguments().size()) return false;
                    TypeElement te1 = (TypeElement)dt1.asElement();
                    TypeElement te2 = (TypeElement)dt2.asElement();
                    if (te1 == null || te2 == null) return false;
                    if (!te1.getQualifiedName().contentEquals(te2.getQualifiedName())) {
                        return false;
                    }
                    int len = dt1.getTypeArguments().size();
                    for (int i=0; i<len; i++) {
                        if (!isSameType(dt1.getTypeArguments().get(i), dt2.getTypeArguments().get(i))) {
                            return false;
                        }
                    }

                    return true;

            }
            return false;
        }

        @Override
        public boolean isSubtype(TypeMirror t1, TypeMirror t2) {

            if (isSameType(t1, t2)) return true;

            if (isNativeMirror(t1) && isNativeMirror(t2)) {

                if (wrapped.isSubtype(t1, t2)) return true;
            }

            for (TypeMirror supertype : directSupertypes(t1)) {
                if (isSubtype(supertype, t2)) {
                    return true;
                }
            }

            return false;

        }

        @Override
        public boolean isAssignable(TypeMirror t1, TypeMirror t2) {
            if (isSubtype(t1, t2)) {
                return true;
            }
            if (isNativeMirror(t1) && isNativeMirror(t2)) {
                if (wrapped.isAssignable(t1, t2)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean contains(TypeMirror t1, TypeMirror t2) {
            if (isNativeMirror(t1) && isNativeMirror(t2)) {
                return wrapped.contains(t1, t2);
            }
            return false;

        }

        @Override
        public boolean isSubsignature(ExecutableType m1, ExecutableType m2) {
            if (isNativeMirror(m1) && isNativeMirror(m2)) {
                return wrapped.isSubsignature(m1, m2);
            }
            return false;
        }

        @Override
        public List<? extends TypeMirror> directSupertypes(TypeMirror t) {
            if (t instanceof CustomDeclaredType) {
                CustomDeclaredType cdt = (CustomDeclaredType)t;
                List<TypeMirror> out = new ArrayList<>();
                if (cdt.getSuperclass() == null) {
                    out.add(elements.getTypeElement("java.lang.Object").asType());
                }
                out.addAll(cdt.getInterfaces());
                return out;


            }
            if (t instanceof RDeclaredType) {
                RDeclaredType dt = (RDeclaredType)t;
                TypeElement typeEl = (TypeElement)dt.asElement();
                List<TypeMirror> out = new ArrayList<>();
                if (typeEl == null) return out;
                if (typeEl.getSuperclass() != null) {
                    out.add(typeEl.getSuperclass());
                }
                for (TypeMirror iface : typeEl.getInterfaces()) {
                    out.add(iface);
                }
                return out;

            }
            if (isNativeMirror(t)) {
                return wrapped.directSupertypes(t);
            }
            return new ArrayList<>();
        }

        @Override
        public TypeMirror erasure(TypeMirror t) {
            if (isNativeMirror(t)) {
                return wrapped.erasure(t);
            }
            return t;
        }

        @Override
        public TypeElement boxedClass(PrimitiveType p) {
            return wrapped.boxedClass(p);
        }

        @Override
        public PrimitiveType unboxedType(TypeMirror t) {
            if (isNativeMirror(t)) {
                return wrapped.unboxedType(t);
            }
            return null;
        }

        @Override
        public TypeMirror capture(TypeMirror t) {
            if (isNativeMirror(t)) {
                return wrapped.capture(t);
            }
            return t;
        }

        @Override
        public PrimitiveType getPrimitiveType(TypeKind kind) {

            return wrapped.getPrimitiveType(kind);
        }

        @Override
        public NullType getNullType() {
            return wrapped.getNullType();
        }

        @Override
        public NoType getNoType(TypeKind kind) {
            return wrapped.getNoType(kind);
        }

        @Override
        public ArrayType getArrayType(TypeMirror componentType) {

            return wrapped.getArrayType(componentType);
        }

        @Override
        public WildcardType getWildcardType(TypeMirror extendsBound, TypeMirror superBound) {
            return wrapped.getWildcardType(extendsBound, superBound);
        }

        @Override
        public DeclaredType getDeclaredType(TypeElement typeElem, TypeMirror... typeArgs) {
            if (!isNativeElement(typeElem) || !isNativeMirrors(typeArgs)) {
                //CustomTypeElement cte = (CustomTypeElement)typeElem;
                if (typeArgs.length == 0) {
                    return (DeclaredType)typeElem.asType();
                }
                return new RDeclaredType() {
                    List<TypeMirror> typeArguments = new ArrayList<>(Arrays.asList(typeArgs));

                    @Override
                    public List<? extends AnnotationMirror> getAnnotationMirrors() {
                        return typeElem.getAnnotationMirrors();
                    }

                    @Override
                    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
                        return typeElem.getAnnotation(annotationType);
                    }

                    @Override
                    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
                        return typeElem.getAnnotationsByType(annotationType);
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
                    public Element asElement() {
                        return typeElem;
                    }

                    @Override
                    public TypeMirror getEnclosingType() {
                        return ((DeclaredType)typeElem.asType()).getEnclosingType();
                    }

                    @Override
                    public List<? extends TypeMirror> getTypeArguments() {
                        return typeArguments;
                    }
                };
            }

            if (isNativeElement(typeElem) && isNativeMirrors(typeArgs)) {
                try {
                    return wrapped.getDeclaredType(typeElem, typeArgs);
                } catch (Exception ex) {
                    System.err.println("Exception while trying to get declared type for "+typeElem+" with typeArgs="+Arrays.toString(typeArgs));
                    throw ex;
                }
            }

            throw new IllegalStateException("Should never get here");


        }

        @Override
        public DeclaredType getDeclaredType(DeclaredType containing, TypeElement typeElem, TypeMirror... typeArgs) {
            if (isNativeMirror(containing) && isNativeElement(typeElem) && isNativeMirrors(typeArgs)) {
                return wrapped.getDeclaredType(containing, typeElem, typeArgs);
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public TypeMirror asMemberOf(DeclaredType containing, Element element) {
            if (isNativeMirror(containing) && isNativeElement(element)) {
                return wrapped.asMemberOf(containing, element);
            }
            if (element.getKind() == ElementKind.METHOD || element.getKind() == ElementKind.CONSTRUCTOR) {
                ExecutableElement ee = (ExecutableElement)element;
                return new RExecutableType() {

                    @Override
                    public String toString() {
                        return ""+element.getSimpleName() + getParameterTypes();
                    }

                    List<TypeVariable> typeVariables = new ArrayList<>();
                    @Override
                    public List<? extends TypeVariable> getTypeVariables() {
                        return typeVariables;
                    }



                    @Override
                    public TypeMirror getReturnType() {
                        return getTypeParameter(containing, ee, ee.getReturnType());
                    }

                    @Override
                    public List<? extends TypeMirror> getParameterTypes() {
                        return ee.getParameters().stream().map(e->getTypeParameter(containing, ee, e.asType())).collect(Collectors.toList());
                    }

                    @Override
                    public TypeMirror getReceiverType() {
                        return getTypeParameter(containing, ee, ee.getReceiverType());
                    }

                    @Override
                    public List<? extends TypeMirror> getThrownTypes() {
                        return ee.getThrownTypes().stream().map(e->getTypeParameter(containing, ee, e)).collect(Collectors.toList());
                    }

                    @Override
                    public TypeKind getKind() {
                        return TypeKind.EXECUTABLE;
                    }

                    @Override
                    public <R, P> R accept(TypeVisitor<R, P> v, P p) {
                        return v.visit(this);
                    }

                    @Override
                    public List<? extends AnnotationMirror> getAnnotationMirrors() {
                        return ee.getAnnotationMirrors();
                    }

                    @Override
                    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
                        return ee.getAnnotation(annotationType);
                    }

                    @Override
                    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
                        return ee.getAnnotationsByType(annotationType);
                    }
                };
            }

            throw new UnsupportedOperationException("asMemberOf not implemented yet for type "+element.getKind()+" on element "+element+" in declared type "+containing);
        }
    }

    public class ElementsWrapper implements Elements {
        private Elements wrapped;

        private ElementsWrapper(Elements wrapped) {
            this.wrapped = wrapped;

        }

        @Override
        public PackageElement getPackageElement(CharSequence name) {
            String nameStr = name.toString();
            PackageElement pkg = packageMap.get(nameStr);
            if (pkg == null) {
                pkg = new PackageWrapper(getName(name));
                packageMap.put(nameStr, (PackageWrapper)pkg);
            }
            return pkg;
        }

        @Override
        public TypeElement getTypeElement(CharSequence name) {

            TypeElement out = wrapped.getTypeElement(name);
            if (out == null) {
                out = typeMap.get(name.toString());
            }
            return out;
        }

        @Override
        public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults(AnnotationMirror a) {
            return wrapped.getElementValuesWithDefaults(a);
        }

        @Override
        public String getDocComment(Element e) {
            return wrapped.getDocComment(e);
        }

        @Override
        public boolean isDeprecated(Element e) {
            return wrapped.isDeprecated(e);
        }

        @Override
        public Name getBinaryName(TypeElement type) {
            return wrapped.getBinaryName(type);
        }

        @Override
        public PackageElement getPackageOf(Element type) {
            PackageElement out = null;
            switch (type.getKind()) {
                case PACKAGE:
                    return wrap((PackageElement)type);
                case CLASS:
                case INTERFACE:
                    TypeElement typeEl = (TypeElement)type;
                    if (typeEl.getNestingKind() == NestingKind.TOP_LEVEL) {
                        return wrap((PackageElement)typeEl.getEnclosingElement());
                    } else {
                        return getPackageOf(typeEl.getEnclosingElement());
                    }
                default:
                    Element enclosing = type.getEnclosingElement();
                    return getPackageOf(enclosing);
            }

        }

        @Override
        public List<? extends Element> getAllMembers(TypeElement type) {


            Map<Name,List<ExecutableElement>> methodMap = new HashMap<>();
            Map<Name,List<VariableElement>> fieldMap = new HashMap<>();
            List<Element> out = new ArrayList<>();
            PackageElement typePackage = elements.getPackageOf(type);
            if (typePackage == null) {
                throw new IllegalStateException("Attempt to get members of type "+type+" which has a null package.");
            }

            out.addAll(type.getEnclosedElements());
            for (Element el : out) {
                Name name = el.getSimpleName();
                if (el.getKind() == ElementKind.METHOD) {
                    ExecutableElement method = (ExecutableElement)el;
                    List<ExecutableElement> methods = methodMap.get(name);
                    if (methods == null) {
                        methods = new ArrayList<>();
                        methodMap.put(name, methods);
                    }
                    methods.add(method);
                } else if (el.getKind() == ElementKind.FIELD) {
                    VariableElement field = (VariableElement)el;
                    List<VariableElement> fields = fieldMap.get(name);
                    if (fields == null) {
                        fields = new ArrayList<>();

                    }
                    fields.add(field);
                }
            }
            List<TypeMirror> supertypes = (List<TypeMirror>)types.directSupertypes(type.asType());
            for (TypeMirror supertype : supertypes) {
                if (supertype.getKind() != TypeKind.DECLARED) {
                    continue;
                }
                TypeElement supertypeEl = (TypeElement)types.asElement(supertype);
                PackageElement supertypePackage = elements.getPackageOf(supertypeEl);
                if (supertypePackage == null) {
                    throw new IllegalStateException("Supertype "+supertypeEl+" has null for package, while try ing to get members of subclass "+type);
                }

                if (supertypeEl == null) continue;
                for (Element superMember : getAllMembers(supertypeEl)) {
                    if (superMember.getModifiers().contains(Modifier.PRIVATE)) {
                        continue;
                    }

                    boolean samePackage = elements.getPackageOf(supertypeEl).getQualifiedName().contentEquals(elements.getPackageOf(type).getQualifiedName());
                    if (!samePackage && !superMember.getModifiers().contains(Modifier.PROTECTED) && !superMember.getModifiers().contains(Modifier.PUBLIC)) {
                        continue;
                    }
                    Name superName = superMember.getSimpleName();
                    if (superMember.getKind() == ElementKind.METHOD) {
                        List<ExecutableElement> existingMethods = methodMap.get(superName);
                        if (existingMethods != null && !existingMethods.isEmpty()) {
                            if (existingMethods.stream().anyMatch(m -> overrides(m, (ExecutableElement)superMember, type))) {
                                continue;
                            }
                        }
                        if (existingMethods == null) {
                            existingMethods = new ArrayList<>();
                            methodMap.put(superName, existingMethods);
                        }
                        existingMethods.add((ExecutableElement)superMember);
                        out.add(superMember);
                    } else if (superMember.getKind() == ElementKind.FIELD) {
                        List<VariableElement> existingFields = fieldMap.get(superName);
                        if (existingFields != null && !existingFields.isEmpty()) {
                            if (existingFields.stream().anyMatch(m->hides(m, (VariableElement)superMember))) {
                                continue;
                            }
                        }
                        if (existingFields == null) {
                            existingFields = new ArrayList<>();
                            fieldMap.put(superName, existingFields);
                        }
                        existingFields.add((VariableElement)superMember);
                        out.add(superMember);
                    } else {
                        if (superMember.getKind() != ElementKind.CONSTRUCTOR) {
                            out.add(superMember);
                        }
                    }
                }
            }


            return out;



        }

        @Override
        public List<? extends AnnotationMirror> getAllAnnotationMirrors(Element e) {
            return wrapped.getAllAnnotationMirrors(e);
        }

        @Override
        public boolean hides(Element hider, Element hidden) {
            return wrapped.hides(hider, hidden);
        }

        @Override
        public boolean overrides(ExecutableElement overrider, ExecutableElement overridden, TypeElement type) {
            if (isNativeElement(overrider) && isNativeElement(overridden) && isNativeElement(type)) {
                if (wrapped.overrides(overrider, overridden, type)) {
                    return true;
                }
            }
            if (!overridden.getSimpleName().contentEquals(overridden.getSimpleName())) {
                return false;
            }

            if (overridden.getParameters().size() != overrider.getParameters().size()) {
                return false;
            }

            int index = -1;
            for (VariableElement overriddenParam : overridden.getParameters()) {
                index++;
                VariableElement overridingParam = overrider.getParameters().get(index);
                if (!types.isSubtype(overridingParam.asType(), overriddenParam.asType())) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public String getConstantExpression(Object value) {
            return wrapped.getConstantExpression(value);
        }

        @Override
        public void printElements(Writer w, Element... elements) {
            wrapped.printElements(w, elements);
        }

        @Override
        public Name getName(CharSequence cs) {
            return wrapped.getName(cs);
        }

        @Override
        public boolean isFunctionalInterface(TypeElement type) {
            return wrapped.isFunctionalInterface(type);
        }
    }

    public class CustomElement implements RElement {
        protected TypeMirror type;
        protected ElementKind kind;
        protected Set<Modifier> modifiers = new HashSet<>();
        protected Name simpleName;
        protected Element enclosingElement;
        protected List<Element> enclosedElements = new ArrayList<>();
        protected List<AnnotationMirror> annotationMirrors = new ArrayList<>();

        @Override
        public String toString() {
            return "CustomElement{" +
                    "type=" + type +
                    ", kind=" + kind +
                    ", modifiers=" + modifiers +
                    ", simpleName=" + simpleName +
                    ", enclosingElement=" + enclosingElement +
                    ", enclosedElements=" + enclosedElements +
                    ", annotationMirrors=" + annotationMirrors +
                    '}';
        }

        @Override
        public TypeMirror asType() {
            return type;
        }

        @Override
        public ElementKind getKind() {
            return kind;
        }

        @Override
        public Set<Modifier> getModifiers() {
            return modifiers;
        }

        @Override
        public Name getSimpleName() {
            return simpleName;
        }

        @Override
        public Element getEnclosingElement() {
            Element out = enclosingElement;
            if (out != null && out.getKind() == ElementKind.PACKAGE && !(out instanceof PackageWrapper)) {
                out = elements.getPackageElement(((PackageElement)out).getQualifiedName());
            }
            return out;
        }

        @Override
        public List<? extends Element> getEnclosedElements() {
            return enclosedElements;
        }

        @Override
        public List<? extends AnnotationMirror> getAnnotationMirrors() {
            return annotationMirrors;
        }

        @Override
        public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
           return null;
        }

        @Override
        public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
            return (A[])Array.newInstance(annotationType, 0);
        }

        @Override
        public <R, P> R accept(ElementVisitor<R, P> v, P p) {
            return v.visit(this, p);
        }
    }

    public class CustomTypeElement extends CustomElement implements TypeElement {
        private NestingKind nestingKind;
        private Name qualifiedName;
        private TypeMirror superclass;
        private List<TypeMirror> interfaces = new ArrayList<>();
        private List<TypeParameterElement> typeParameters = new ArrayList<>();

        @Override
        public String toString() {
            return qualifiedName.toString();
        }

        @Override
        public NestingKind getNestingKind() {
            return nestingKind;
        }

        @Override
        public Name getQualifiedName() {
            return qualifiedName;
        }

        @Override
        public TypeMirror getSuperclass() {
            return superclass;
        }

        @Override
        public List<? extends TypeMirror> getInterfaces() {
            return interfaces;
        }

        @Override
        public List<? extends TypeParameterElement> getTypeParameters() {
            return typeParameters;
        }

        public void addMethod(CustomExecutableElement method) {
            if (!method.getModifiers().contains(Modifier.STATIC)) {
                method.receiverType = type;
            }
            method.enclosingElement = this;
            enclosedElements.add(method);
        }

        public void addField(CustomVariableElement field) {
            field.enclosingElement = this;
            enclosedElements.add(field);
        }


    }


    public class CustomExecutableElement extends CustomElement implements RElement, ExecutableElement {
        protected List<TypeParameterElement> typeParameters = new ArrayList<>();
        protected TypeMirror returnType;
        protected List<VariableElement> parameters = new ArrayList<>();
        protected TypeMirror receiverType;
        protected boolean varargs;
        protected boolean _default;
        protected List<TypeMirror> thrownTypes = new ArrayList<>();
        protected AnnotationValue defaultValue;

        @Override
        public String toString() {
            StringBuilder sb =  new StringBuilder().append(returnType).append(" ").append(simpleName).append("(");
            boolean first = true;
            for (VariableElement param : parameters) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(param);
            }
            sb.append(") ");
            first = true;
            if (!thrownTypes.isEmpty()) {
                sb.append(" throws ");

                for (TypeMirror type : thrownTypes) {
                    if (first) {
                        first = false;
                    } else {
                        sb.append(", ");
                    }
                    sb.append(type);
                }
            }
            return sb.toString();

        }

        @Override
        public List<? extends TypeParameterElement> getTypeParameters() {
            return typeParameters;
        }

        @Override
        public TypeMirror getReturnType() {
            return returnType;
        }

        @Override
        public List<? extends VariableElement> getParameters() {
            return parameters;
        }

        @Override
        public TypeMirror getReceiverType() {
            return receiverType;
        }

        @Override
        public boolean isVarArgs() {
            return varargs;
        }

        @Override
        public boolean isDefault() {
            return _default;
        }

        @Override
        public List<? extends TypeMirror> getThrownTypes() {
            return thrownTypes;
        }

        @Override
        public AnnotationValue getDefaultValue() {
            return defaultValue;
        }
    }

    public class CustomVariableElement extends CustomElement implements RElement, VariableElement {
        private Object constantValue;

        @Override
        public Object getConstantValue() {
            return constantValue;
        }
    }

    public abstract class CustomElementBuilder<T extends CustomElementBuilder, E extends CustomElement> {
        private TypeMirror type;
        private ElementKind kind;
        private Set<Modifier> modifiers;
        private Name simpleName;
        private Element enclosingElement;
        private List<Element> enclosedElements = new ArrayList<>();
        private List<AnnotationMirror> annotationMirrors;


        public T type(TypeMirror type) {
            this.type = type;
            return (T)this;
        }

        public T kind(ElementKind kind) {
            this.kind = kind;
            return (T)this;
        }

        public T modifiers(Modifier... modifiers) {
            if (this.modifiers == null) this.modifiers = new HashSet<>();
            this.modifiers.addAll(Arrays.asList(modifiers));
            return (T)this;
        }

        public T simpleName(Name simpleName) {
            this.simpleName = simpleName;
            return (T)this;
        }

        public T enclosingElement(Element e) {
            this.enclosingElement = e;
            return (T)this;

        }

        public T add(Element element) {
            if (enclosedElements == null) enclosedElements = new ArrayList<>();
            enclosedElements.add(element);
            return (T)this;
        }

        public T add(AnnotationMirror anno) {
            if (annotationMirrors == null) annotationMirrors = new ArrayList<>();
            annotationMirrors.add(anno);
            return (T) this;
        }

        public abstract E build();

        protected T decorate(E element) {
            element.kind = kind;
            element.type = type;
            element.enclosedElements = enclosedElements;
            element.enclosingElement = enclosingElement;
            element.annotationMirrors = annotationMirrors;
            element.modifiers = modifiers;
            element.simpleName = simpleName;
            return (T)this;

        }


    }

    public class CustomTypeElementBuilder extends CustomElementBuilder<CustomTypeElementBuilder, CustomTypeElement> {
        private NestingKind nestingKind = NestingKind.TOP_LEVEL;
        private Name qualifiedName;
        private TypeMirror superclass = elements.getTypeElement("java.lang.Object").asType();
        private List<TypeMirror> interfaces = new ArrayList<>();
        private List<TypeParameterElement> typeParameters = new ArrayList<>();

        public CustomTypeElementBuilder() {

        }

        public CustomTypeElementBuilder qualifiedName(Name qualifiedName) {
            this.qualifiedName = qualifiedName;
            return this;
        }

        public CustomTypeElementBuilder nestingKind(NestingKind kind) {
            this.nestingKind = kind;
            return this;
        }

        public CustomTypeElementBuilder superclass(TypeMirror superclass) {
            this.superclass = superclass;
            return this;
        }

        public CustomTypeElementBuilder addInterface(TypeMirror iface) {
            interfaces.add(iface);
            return this;
        }

        public CustomTypeElementBuilder addTypeParameter(TypeParameterElement el) {
            typeParameters.add(el);
            return this;
        }

        @Override
        protected CustomTypeElementBuilder decorate(CustomTypeElement element) {
            super.decorate(element);
            element.nestingKind = nestingKind;
            element.qualifiedName = qualifiedName;
            element.superclass = superclass;
            element.interfaces = interfaces;
            element.typeParameters = typeParameters;
            if (element.type == null) {
                element.type = createDeclaredType(qualifiedName.toString());
            }
            return this;
        }

        @Override
        public CustomTypeElement build() {
            CustomTypeElement out = new CustomTypeElement();
            decorate(out);
            return out;
        }


    }

    public class CustomExecutableElementBuilder extends CustomElementBuilder<CustomExecutableElementBuilder, CustomExecutableElement> {
        private List<TypeParameterElement> typeParameters = new ArrayList<>();
        private TypeMirror returnType;
        private List<VariableElement> parameters = new ArrayList<>();
        private TypeMirror receiverType;
        private boolean varargs;
        private boolean _default;
        private List<TypeMirror> thrownTypes = new ArrayList<>();
        private AnnotationValue defaultValue;

        public CustomExecutableElementBuilder addTypeParameter(TypeParameterElement el) {
            typeParameters.add(el);
            return this;
        }

        public CustomExecutableElementBuilder returnType(TypeMirror returnType) {
            this.returnType = returnType;
            return this;
        }

        public CustomExecutableElementBuilder addParameter(VariableElement param) {
            parameters.add(param);
            return this;
        }

        public CustomExecutableElementBuilder receiverType(TypeMirror receiverType) {
            this.receiverType = receiverType;
            return this;
        }

        public CustomExecutableElementBuilder varargs(boolean varargs) {
            this.varargs = varargs;
            return this;
        }

        public CustomExecutableElementBuilder setDefault(boolean def) {
            this._default = def;
            return this;
        }

        public CustomExecutableElementBuilder addThrownType(TypeMirror thrownType) {
            this.thrownTypes.add(thrownType);
            return this;
        }

        @Override
        protected CustomExecutableElementBuilder decorate(CustomExecutableElement element) {
            super.decorate(element);

            element._default = _default;
            element.defaultValue = defaultValue;
            element.typeParameters = typeParameters;
            element.returnType = returnType;
            element.receiverType = receiverType;
            element.varargs = varargs;
            element.thrownTypes = thrownTypes;
            element.parameters = parameters;
            return this;
        }

        @Override
        public CustomExecutableElement build() {
            CustomExecutableElement out = new CustomExecutableElement();
            decorate(out);
            return out;
        }
    }

    public class CustomVariableElementBuilder extends CustomElementBuilder<CustomVariableElementBuilder, CustomVariableElement> {
        private Object constantValue;
        private boolean injected;

        public CustomVariableElementBuilder constantValue(Object cv) {
            this.constantValue = cv;
            return this;
        }

        public CustomVariableElementBuilder injected(boolean injected) {
            this.injected = injected;
            return this;
        }

        @Override
        protected CustomVariableElementBuilder decorate(CustomVariableElement element) {
            super.decorate(element);
            element.constantValue = constantValue;
            return this;
        }

        @Override
        public CustomVariableElement build() {
            CustomVariableElement out = new CustomVariableElement() {
                @Override
                public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
                    if (injected && annotationType.equals(Inject.class)) {
                       return (A)getInjectAnnotation();
                    }
                    return super.getAnnotation(annotationType);
                }

            };
            decorate(out);
            return out;
        }
    }

    public class MethodBuilder extends CustomExecutableElementBuilder {
        public MethodBuilder() {
            kind(ElementKind.METHOD);
        }
    }

    public class FieldBuilder extends CustomVariableElementBuilder {
        public FieldBuilder() {
            kind(ElementKind.FIELD);

        }
    }



    public class CustomTypeMirror implements RTypeMirror {
        protected TypeKind kind;
        protected List<AnnotationMirror> annotationMirrors;
        protected String stringValue;

        @Override
        public String toString() {
            return stringValue;
        }

        @Override
        public TypeKind getKind() {
            return kind;
        }

        @Override
        public <R, P> R accept(TypeVisitor<R, P> v, P p) {
            return v.visit(this, p);
        }

        @Override
        public List<? extends AnnotationMirror> getAnnotationMirrors() {
            return annotationMirrors;
        }

        @Override
        public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
            return null;
        }

        @Override
        public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
            return (A[])Array.newInstance(annotationType, 0);
        }
    }

    public class CustomDeclaredType extends CustomTypeMirror implements RDeclaredType {

        protected TypeMirror enclosingType;
        protected List<TypeMirror> typeArguments = new ArrayList<>();

        protected TypeMirror superclass;
        protected List<TypeMirror> interfaces = new ArrayList<>();

        @Override
        public Element asElement() {
            return types.asElement(this);
        }

        @Override
        public TypeMirror getEnclosingType() {
            return enclosingType;
        }

        @Override
        public List<? extends TypeMirror> getTypeArguments() {
            return typeArguments;
        }

        public TypeMirror getSuperclass() {
            return superclass;
        }

        public List<? extends TypeMirror> getInterfaces() {
            return interfaces;
        }

        public void addTypeArgument(TypeMirror mirror) {
            typeArguments.add(mirror);
        }

    }

    public class CustomExecutableType extends CustomTypeMirror implements RTypeMirror, ExecutableType {
        protected List<TypeVariable> typeVariables = new ArrayList<>();
        protected TypeMirror returnType;
        protected List<TypeMirror> parameterTypes = new ArrayList<>();
        protected TypeMirror receiverType;
        protected List<TypeMirror> thrownTypes = new ArrayList<>();

        @Override
        public <R, P> R accept(TypeVisitor<R, P> v, P p) {

            return super.accept(v, p);
        }

        @Override
        public List<? extends TypeVariable> getTypeVariables() {
            return typeVariables;
        }

        @Override
        public TypeMirror getReturnType() {
            return returnType;
        }

        @Override
        public List<? extends TypeMirror> getParameterTypes() {
            return parameterTypes;
        }

        @Override
        public TypeMirror getReceiverType() {
            return receiverType;
        }

        @Override
        public List<? extends TypeMirror> getThrownTypes() {
            return thrownTypes;
        }
    }

    public abstract class CustomTypeMirrorBuilder<T extends CustomTypeMirrorBuilder, E extends CustomTypeMirror> {
        private TypeKind kind;
        private List<AnnotationMirror> annotationMirrors = new ArrayList<>();
        private String stringValue;

        public T kind(TypeKind kind) {
            this.kind = kind;
            return (T)this;
        }





        public T stringValue(String stringValue) {
            if (stringValue == null) {
                throw new IllegalArgumentException("stringValue must be non-null");
            }
            this.stringValue = stringValue;
            return (T)this;
        }

        protected void decorate(E element) {
            element.kind = kind;
            element.annotationMirrors = annotationMirrors;
            if (stringValue == null) {
                throw new IllegalStateException("Cannot build CustomTypeMirrorBuilder without stringValue");
            }
            element.stringValue = stringValue;
        }
        public abstract E build();
    }

    public class CustomDeclaredTypeBuilder extends CustomTypeMirrorBuilder<CustomDeclaredTypeBuilder, CustomDeclaredType> {
        protected TypeMirror enclosingType;
        protected List<TypeMirror> typeArguments = new ArrayList<>();
        protected TypeMirror superclass;
        protected List<TypeMirror> interfaces = new ArrayList<>();


        public CustomDeclaredTypeBuilder enclosingType(TypeMirror enclosingType) {
            this.enclosingType = enclosingType;
            return this;
        }

        public CustomDeclaredTypeBuilder addTypeArgument(TypeMirror typeMirror) {
            typeArguments.add(typeMirror);
            return this;
        }

        public CustomDeclaredTypeBuilder superclass(TypeMirror superclass) {
            this.superclass = superclass;
            return this;
        }

        public CustomDeclaredTypeBuilder addInterface(TypeMirror mirror) {
            this.interfaces.add(mirror);
            return this;
        }

        @Override
        protected void decorate(CustomDeclaredType element) {
            super.decorate(element);
            element.enclosingType = enclosingType;
            element.typeArguments = typeArguments;
            element.superclass = superclass;
            element.interfaces = interfaces;

        }

        @Override
        public CustomDeclaredType build() {
            CustomDeclaredType out = new CustomDeclaredType();
            decorate(out);
            if (out.stringValue == null) {
                throw new IllegalArgumentException("Attempt to build CustomDeclaredType without stringValue.");
            }
            return out;
        }
    }

    public class CustomExecutableTypeBuilder extends CustomTypeMirrorBuilder<CustomExecutableTypeBuilder, CustomExecutableType> {
        protected List<TypeVariable> typeVariables = new ArrayList<>();
        protected TypeMirror returnType;
        protected List<TypeMirror> parameterTypes = new ArrayList<>();
        protected TypeMirror receiverType;
        protected List<TypeMirror> thrownTypes = new ArrayList<>();

        public CustomExecutableTypeBuilder addTypeVariable(TypeVariable var) {
            typeVariables.add(var);
            return this;
        }

        public CustomExecutableTypeBuilder returnType(TypeMirror returnType) {
            this.returnType = returnType;
            return this;
        }

        public CustomExecutableTypeBuilder addParameterType(TypeMirror parameterType) {
            parameterTypes.add(parameterType);
            return this;
        }

        public CustomExecutableTypeBuilder receiverType(TypeMirror receiverType) {
            this.receiverType = receiverType;
            return this;
        }

        public CustomExecutableTypeBuilder addThrownType(TypeMirror mirror) {
            this.thrownTypes.add(mirror);
            return this;
        }

        @Override
        protected void decorate(CustomExecutableType element) {
            super.decorate(element);
            element.typeVariables = typeVariables;
            element.returnType = returnType;
            element.parameterTypes = parameterTypes;
            element.receiverType = receiverType;
            element.thrownTypes = thrownTypes;

        }

        @Override
        public CustomExecutableType build() {
            CustomExecutableType element = new CustomExecutableType();
            decorate(element);
            return element;
        }
    }


    private static class TagDescriptor {
        String name;
        TagDescriptor(String name) { this.name = name;}
    }

    private static class CategoryDescriptor {
        String name;
        CategoryDescriptor(String name){ this.name = name;}
    }

    public class SchemaBuilder extends CustomTypeElementBuilder {
        private List<TagDescriptor> tags = new ArrayList<>();
        private List<CategoryDescriptor> categories = new ArrayList<>();

        public SchemaBuilder(String qualifiedName) {
            if (qualifiedName == null) {
                throw new IllegalArgumentException("SchemaBuilder requires non-null qualified name");
            }
            simpleName(elements.getName(qualifiedName.substring(qualifiedName.lastIndexOf(".")+1)));
            this.qualifiedName(elements.getName(qualifiedName));
            kind(ElementKind.INTERFACE);
            nestingKind(NestingKind.TOP_LEVEL);
            modifiers(Modifier.PUBLIC);

            String packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
            PackageElement pkg = elements.getPackageElement(packageName);
            enclosingElement(pkg);
            type(new CustomDeclaredTypeBuilder()
                    .stringValue(qualifiedName)
                    .enclosingType(pkg.asType())
                    .build());


        }

        public SchemaBuilder addTag(String name) {
            tags.add(new TagDescriptor(name));
            return this;
        }

        public SchemaBuilder addCategory(String category) {
            categories.add(new CategoryDescriptor(category));
            return this;
        }

        @Override
        protected CustomTypeElementBuilder decorate(CustomTypeElement element) {
            super.decorate(element);
            for (TagDescriptor tag : tags) {
                element.addField(new FieldBuilder()
                    .simpleName(elements.getName(tag.name))
                        .modifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .type(elements.getTypeElement("com.codename1.rad.models.Tag").asType())
                    .build()
                );
            }
            for (CategoryDescriptor tag : categories) {
                element.addField(new FieldBuilder()
                        .simpleName(elements.getName(tag.name))
                        .modifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .type(elements.getTypeElement("com.codename1.rad.nodes.ActionNode.Category").asType())
                        .build()
                );
            }
            return this;
        }
    }

    private class PropertyDescriptor {
        String name;
        private String type;
        DeclaredType declaredType;

        public PropertyDescriptor(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public PropertyDescriptor(String name, DeclaredType type) {
            this.name = name;
            this.declaredType = type;
        }

        public DeclaredType type() {
            if (declaredType != null) return declaredType;
            return createDeclaredType(type);
        }


    }

    private TypeMirror getTypeParameter(DeclaredType type, ExecutableElement method, TypeMirror param) {
        if (param.getKind() == TypeKind.DECLARED || param.toString().length() != 1 || !Character.isUpperCase(param.toString().charAt(0))) {

            return param;
        }
        TypeElement typeEl = (TypeElement)type.asElement();
        int index = -1;
        for (TypeParameterElement typeParameterElement : typeEl.getTypeParameters()) {
            index++;
            if (typeParameterElement.asType().toString().equals(param.toString())) {
                if (type.getTypeArguments().size() > index) {
                    return type.getTypeArguments().get(index);
                } else {
                    return typeParameterElement.asType();
                }
            }
        }

        return null;

    }




    public RDeclaredType createDeclaredType(String type, TypeMirror... _typeArguments) {
        if (type == null) throw new IllegalArgumentException("createDeclaredType requires non-null type");
        String packageName = extractParentQualifiedName(type);
        PackageElement pkg = elements.getPackageElement(packageName);

        /*
        return new CustomDeclaredTypeBuilder()
                .stringValue(type)
                .enclosingType(pkg.asType())
                .build();

         */

        return new RDeclaredType() {
            ArrayList<AnnotationMirror> annotationMirrors = new ArrayList<>();
            List<TypeMirror> typeArguments = new ArrayList<>(Arrays.asList(_typeArguments));

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append(type);
                if (_typeArguments.length > 0) {
                    sb.append("<");
                    for (int i=0; i< _typeArguments.length; i++) {
                        if (i > 0) sb.append(", ");
                        sb.append(_typeArguments[i]);
                    }
                    sb.append(">");
                }
                return sb.toString();
            }

            @Override
            public List<? extends AnnotationMirror> getAnnotationMirrors() {
                return annotationMirrors;
            }

            @Override
            public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
                return null;
            }

            @Override
            public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
                return (A[])Array.newInstance(Annotation.class, 0);
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
            public Element asElement() {
                return elements.getTypeElement(type);
            }

            @Override
            public TypeMirror getEnclosingType() {
                return asElement().getEnclosingElement().asType();
            }

            @Override
            public List<? extends TypeMirror> getTypeArguments() {
                return typeArguments;
            }
        };
    }

    public class EntityBuilder extends CustomTypeElementBuilder {
        List<PropertyDescriptor> properties = new ArrayList<>();

        EntityBuilder(String qualifiedName) {
            simpleName(elements.getName(qualifiedName.substring(qualifiedName.lastIndexOf(".")+1)));
            String packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
            qualifiedName(elements.getName(qualifiedName));
            PackageElement pkg = elements.getPackageElement(packageName);

            if (qualifiedName.endsWith("Model")) {
                String schemaQualifiedName = qualifiedName.substring(0, qualifiedName.length() - "Model".length()) + "Schema";
                addInterface(createDeclaredType(schemaQualifiedName));
            }
            kind(ElementKind.INTERFACE);
            nestingKind(NestingKind.TOP_LEVEL);
            modifiers(Modifier.PUBLIC);
            addInterface(elements.getTypeElement("com.codename1.rad.models.Entity").asType());
            enclosingElement(pkg);
        }

        public EntityBuilder addProperty(String name, String type) {
            properties.add(new PropertyDescriptor(name, type));
            return this;
        }

        public EntityBuilder addProperty(String name, DeclaredType declaredType) {
            properties.add(new PropertyDescriptor(name, declaredType));
            return this;
        }

        @Override
        protected CustomTypeElementBuilder decorate(CustomTypeElement element) {
            super.decorate(element);
            for (PropertyDescriptor property : properties) {
                CustomExecutableElement setter = new CustomExecutableElementBuilder()
                        .receiverType(element.type)
                        .modifiers(Modifier.PUBLIC)
                        .returnType(property.type())
                        .simpleName(elements.getName("set" + property.name.substring(0,1).toUpperCase() + property.name.substring(1)))

                        .build();

                CustomExecutableType type = new CustomExecutableTypeBuilder()
                        .receiverType(element.type)
                        .returnType(property.type())
                        .stringValue(property.name)
                        .stringValue("public void " + setter.simpleName + "()")
                        .build();
                setter.type = type;
                element.addMethod(setter);

                String prefix = "get";
                if (property.type().toString().toLowerCase().contains("boolean")) {
                    prefix = "is";
                }
                CustomExecutableElement getter = new CustomExecutableElementBuilder()
                        .receiverType(element.type)
                        .modifiers(Modifier.PUBLIC)
                        .addParameter(new CustomVariableElementBuilder()
                                .simpleName(elements.getName(property.name))
                                .type(property.type())
                            .build()
                        )

                        .simpleName(elements.getName(prefix + property.name.substring(0,1).toUpperCase() + property.name.substring(1)))

                        .build();

                type = new CustomExecutableTypeBuilder()
                        .receiverType(element.type)
                        .returnType(property.type())
                        .stringValue(property.name)
                        .stringValue("public void " + setter.simpleName + "()")
                        .build();
                getter.type = type;
                element.addMethod(getter);
            }
            return this;
        }
    }

    public class EntityImplBuilder extends CustomTypeElementBuilder {
        List<PropertyDescriptor> properties = new ArrayList<>();
        public EntityImplBuilder(String qualifiedName) {
            simpleName(elements.getName(qualifiedName.substring(qualifiedName.lastIndexOf(".")+1)));
            String packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
            qualifiedName(elements.getName(qualifiedName));
            PackageElement pkg = elements.getPackageElement(packageName);

            if (qualifiedName.endsWith("ModelImpl")) {
                String modelQualifiedName = qualifiedName.substring(0, qualifiedName.length() - "ModelImpl".length()) + "Model";
                addInterface(createDeclaredType(modelQualifiedName));
            } else {
                addInterface(elements.getTypeElement("com.codename1.rad.models.Entity").asType());
            }
            kind(ElementKind.CLASS);
            nestingKind(NestingKind.TOP_LEVEL);
            modifiers(Modifier.PUBLIC);

            superclass(elements.getTypeElement("com.codename1.rad.models.BaseEntity").asType());
            enclosingElement(pkg);
        }


        public EntityImplBuilder addProperty(String name, String type) {
            properties.add(new PropertyDescriptor(name, type));
            return this;
        }

        public EntityImplBuilder addProperty(String name, DeclaredType type) {
            properties.add(new PropertyDescriptor(name, type));
            return this;
        }

        @Override
        protected CustomTypeElementBuilder decorate(CustomTypeElement element) {
            super.decorate(element);

            CustomExecutableElement constructor = new CustomExecutableElementBuilder()
                    .simpleName(elements.getName("<init>"))
                    .receiverType(element.asType())
                    .returnType(element.asType())
                    .kind(ElementKind.CONSTRUCTOR)
                    .modifiers(Modifier.PUBLIC)
                    .build();

            for (PropertyDescriptor property : properties) {

                CustomExecutableElement setter = new CustomExecutableElementBuilder()
                        .receiverType(element.type)
                        .modifiers(Modifier.PUBLIC)
                        .returnType(property.type())
                        .simpleName(elements.getName("set" + property.name.substring(0,1).toUpperCase() + property.name.substring(1)))

                        .build();

                CustomExecutableType type = new CustomExecutableTypeBuilder()
                        .receiverType(element.type)
                        .returnType(property.type())
                        .stringValue(property.name)
                        .stringValue("public void " + setter.simpleName + "()")
                        .build();
                setter.type = type;
                element.addMethod(setter);

                String prefix = "get";
                if (property.type().toString().toLowerCase().contains("boolean")) {
                    prefix = "is";
                }
                CustomExecutableElement getter = new CustomExecutableElementBuilder()
                        .receiverType(element.type)
                        .modifiers(Modifier.PUBLIC)
                        .addParameter(new CustomVariableElementBuilder()
                                .simpleName(elements.getName(property.name))
                                .type(property.type())
                                .build()
                        )

                        .simpleName(elements.getName(prefix + property.name.substring(0,1).toUpperCase() + property.name.substring(1)))

                        .build();

                type = new CustomExecutableTypeBuilder()
                        .receiverType(element.type)
                        .returnType(property.type())
                        .stringValue(property.name)
                        .stringValue("public void " + setter.simpleName + "()")
                        .build();
                getter.type = type;
                element.addMethod(getter);
            }
            return this;
        }
    }

    public class EntityViewBuilder extends CustomTypeElementBuilder {
        public EntityViewBuilder(String qualifiedName) {
            String packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
            PackageElement pkg = elements.getPackageElement(packageName);
            qualifiedName(elements.getName(qualifiedName));
            String simpleName = qualifiedName.substring(qualifiedName.lastIndexOf(".")+1);
            simpleName(elements.getName(simpleName));
            modifiers(Modifier.PUBLIC);
            superclass(elements.getTypeElement(packageName+".Abstract"+simpleName).asType());
            kind(ElementKind.CLASS);

            addInterface(createDeclaredType(packageName+"."+simpleName+"Schema"));
            DeclaredType type = createDeclaredType(qualifiedName, createDeclaredType(packageName+"."+simpleName+"Model"));


            type(type);
            enclosingElement(pkg);

        }

        @Override
        protected CustomTypeElementBuilder decorate(CustomTypeElement element) {
            super.decorate(element);
            TypeElement viewContext = elements.getTypeElement("com.codename1.rad.ui.ViewContext");
            if (viewContext == null) {
                throw new RuntimeException("Can't find ViewContext class.  It should be there!!!");
            }
            CustomExecutableElement constructor = new CustomExecutableElementBuilder()
                    .returnType(element.type)
                    .receiverType(element.type)
                    .kind(ElementKind.CONSTRUCTOR)
                    .simpleName(elements.getName("<init>"))
                    .modifiers(Modifier.PUBLIC)
                    .addParameter(new CustomVariableElementBuilder()
                            .injected(true)
                            .simpleName(elements.getName("context"))
                            .type(types.getDeclaredType(viewContext, createDeclaredType(element.getQualifiedName()+"Model")))
                            .build())
            .build();
            element.addMethod(constructor);
            return this;


        }
    }


    public class EntityControllerBuilder extends CustomTypeElementBuilder {

        public EntityControllerBuilder(String qualifiedName, String modelType) {
            packageName = extractParentQualifiedName(qualifiedName);

            PackageElement pkg = elements.getPackageElement(packageName);
            kind(ElementKind.CLASS);
            String simpleName = toSimpleName(qualifiedName);
            simpleName(elements.getName(simpleName));
            baseName = simpleName.substring(0, simpleName.length() - "Controller".length());
            if (modelType == null) modelType = packageName + "." +baseName + "Model";
            this.modelType = modelType;
            qualifiedName(elements.getName(qualifiedName));
            modifiers(Modifier.PUBLIC);
            type(createDeclaredType(qualifiedName.toString()));
            addInterface(createDeclaredType(packageName+".I"+simpleName));
            superclass(elements.getTypeElement("com.codename1.rad.controllers.FormController").asType());
            enclosingElement(pkg);


        }

        private String packageName, baseName, modelType;

        @Override
        protected CustomTypeElementBuilder decorate(CustomTypeElement element) {
            super.decorate(element);

            element.addMethod(new CustomExecutableElementBuilder()
                    .kind(ElementKind.CONSTRUCTOR)
                    .modifiers(Modifier.PUBLIC)
                    .simpleName(elements.getName("<init>"))
                    .receiverType(element.asType())
                    .returnType(element.asType())
                    .addParameter(new CustomVariableElementBuilder()
                            .simpleName(elements.getName("parent"))
                            .type(elements.getTypeElement("com.codename1.rad.controllers.Controller").asType())
                            .build())
                    .build());

            element.addMethod(new CustomExecutableElementBuilder()
                    .kind(ElementKind.CONSTRUCTOR)
                    .modifiers(Modifier.PUBLIC)
                    .simpleName(elements.getName("<init>"))
                    .receiverType(element.asType())
                    .returnType(element.asType())
                    .addParameter(new CustomVariableElementBuilder()
                            .injected(true)
                            .simpleName(elements.getName("parent"))
                            .type(elements.getTypeElement("com.codename1.rad.controllers.Controller").asType())
                            .build())
                    .addParameter(new CustomVariableElementBuilder()
                            .injected(true)
                            .simpleName(elements.getName("entity"))
                            .type(createDeclaredType(modelType))
                            .build())
                    .build());

            return this;
        }


    }
    public class EntityControllerMarkerBuilder extends CustomTypeElementBuilder {
        private String packageName;
        public EntityControllerMarkerBuilder(String qualifiedName) {
            packageName = extractParentQualifiedName(qualifiedName);
            PackageElement pkg = elements.getPackageElement(packageName);
            kind(ElementKind.INTERFACE);
            String simpleName = qualifiedName.substring(qualifiedName.lastIndexOf(".")+1);
            simpleName(elements.getName(simpleName));
            qualifiedName(elements.getName(qualifiedName));
            modifiers(Modifier.PUBLIC);
            type(createDeclaredType(qualifiedName.toString()));
            enclosingElement(pkg);


        }
    }

    public class PackageWrapper implements RElement, PackageElement {
        private final Name qualifiedName;
        private TypeMirror type;
        private List<Element> enclosedElements = new ArrayList<>();

        private PackageElement wrapped() {
            return ((ElementsWrapper)elements).wrapped.getPackageElement(qualifiedName);
        }



        public PackageWrapper(Name qualifiedName) {
            this.qualifiedName = qualifiedName;
        }


        @Override
        public String toString() {
            return qualifiedName.toString();
        }

        @Override
        public Name getQualifiedName() {
            return qualifiedName;
        }

        @Override
        public TypeMirror asType() {
            PackageElement wrapped = wrapped();
            if (wrapped != null) return wrapped.asType();
            if (type == null) {
                type = new NoType() {

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
                        return (A[])Array.newInstance(annotationType, 0);
                    }

                    @Override
                    public TypeKind getKind() {
                        return TypeKind.PACKAGE;
                    }

                    @Override
                    public <R, P> R accept(TypeVisitor<R, P> v, P p) {
                        return v.visit(this, p);
                    }
                };
            }
            return type;

        }

        @Override
        public ElementKind getKind() {
            return ElementKind.PACKAGE;
        }

        @Override
        public Set<Modifier> getModifiers() {
            return new HashSet<>();
        }

        @Override
        public Name getSimpleName() {
            PackageElement wrapped = wrapped();
            if (wrapped != null) return wrapped.getSimpleName();
            String qualifiedNameStr = qualifiedName.toString();
            if (qualifiedNameStr.contains(".")) {
                return elements.getName(qualifiedNameStr.substring(qualifiedNameStr.lastIndexOf(".") + 1));
            } else {
                return qualifiedName;
            }
        }

        @Override
        public List<? extends Element> getEnclosedElements() {
            List<Element> out = new ArrayList<>();
            PackageElement wrapped = wrapped();
            if (wrapped != null) {
                out.addAll(wrapped.getEnclosedElements());
            }
            out.addAll(enclosedElements);
            out = out.stream().map(e -> wrap(e)).collect(Collectors.toList());
            return out;

        }

        @Override
        public List<? extends AnnotationMirror> getAnnotationMirrors() {
            PackageElement wrapped = wrapped();
            if (wrapped != null) {
                return wrapped.getAnnotationMirrors();
            }
            return new ArrayList<>();
        }

        @Override
        public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
            PackageElement wrapped = wrapped();
            if (wrapped != null) {
                return wrapped.getAnnotation(annotationType);
            }
            return null;
        }

        @Override
        public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
            PackageElement wrapped = wrapped();
            if (wrapped != null) {
                return wrapped.getAnnotationsByType(annotationType);
            }
            return (A[])Array.newInstance(annotationType, 0);
        }

        @Override
        public <R, P> R accept(ElementVisitor<R, P> v, P p) {
            return v.visit(this, p);
        }

        @Override
        public boolean isUnnamed() {
            PackageElement wrapped = wrapped();
            if (wrapped != null) {
                return wrapped.isUnnamed();
            }
            return false;
        }

        @Override
        public Element getEnclosingElement() {

            PackageElement wrapped = wrapped();
            if (wrapped != null) {
                return wrap(wrapped.getEnclosingElement());
            }
            return null;
        }


    }


    public <T extends Element> T wrap(T element) {
        if (element == null) return null;
        if (element.getKind() == ElementKind.PACKAGE) {
            if (element instanceof PackageWrapper) {
                return element;
            } else {
                return (T)elements.getPackageElement(((PackageElement)element).getQualifiedName());
            }
        }
        return element;
    }

    public <T extends Element> T unwrap(T element) {
        if (element == null) {
            return null;
        }
        if (element.getKind() == ElementKind.PACKAGE && element instanceof PackageWrapper) {
            PackageElement pe =  (PackageElement)((PackageWrapper)element).wrapped();
            if (pe == null) {
                pe = elements.wrapped.getPackageElement(pe.getQualifiedName());
            }
            return (T)pe;
        }
        return element;
    }


    public String toSimpleName(String fqn) {
        if (!fqn.contains(".")) return fqn;
        return fqn.substring(fqn.lastIndexOf(".")+1);
    }

    public String toQualifiedName(String packageName, String name) {
        if (packageName == null || packageName.isEmpty() || name.startsWith(packageName+".")) return name;
        return packageName + "." + name;

    }

    public String extractParentQualifiedName(String name) {
        if (name.contains(".")) return name.substring(0, name.lastIndexOf("."));
        return "";
    }

    public boolean isNativeElement(Element el) {
        return !(el instanceof RElement);
    }

    public boolean isNativeElements(Collection<Element> elements) {
        for (Element e : elements) {
            if (!isNativeElement(e)) return false;
        }
        return true;
    }

    public boolean isNativeMirror(TypeMirror tm) {
        return !(tm instanceof RTypeMirror);
    }

    public boolean isNativeMirrors(Collection<TypeMirror> mirrors) {
        for (TypeMirror tm : mirrors) {
            if (!isNativeMirror(tm)) return false;
        }
        return true;
    }

    public boolean isNativeMirrors(TypeMirror... mirrors) {
        for (TypeMirror tm : mirrors) {
            if (!isNativeMirror(tm)) return false;
        }
        return true;
    }

    private Inject getInjectAnnotation() {
        TypeElement controller = elements.getTypeElement("com.codename1.rad.ui.ViewContext");
        return controller.getEnclosedElements().stream().filter(e->e.getKind() == ElementKind.CONSTRUCTOR && ((ExecutableElement)e).getParameters().size() > 0)
                .map(e->((ExecutableElement) e).getParameters().get(0).getAnnotation(Inject.class)).findFirst().orElse(null);
    }
}
