package com.codename1.rad.annotations.processors;

import com.codename1.rad.annotations.RAD;
import com.codename1.rad.annotations.RADDoc;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class XMLSchemaGenerator {
    private ViewProcessor.JavaEnvironment env;
    private ProcessingEnvironment processingEnvironment;
    private File rootDirectory;
    private TypeElement javaClass;
    private TypeElement builderClass;
    private int indent = 0;
    private String checksum;
    private Map<String,TypeElement> allTags = new HashMap<>();
    private boolean writeElements;
    private boolean partialSchema;

    public void setPartialSchema(boolean partial) {
        this.partialSchema = partial;
    }


    private List<File> includes = new ArrayList<>();
    private List<XMLSchemaGenerator> subGenerators = new ArrayList<>();

    public XMLSchemaGenerator(ProcessingEnvironment processingEnvironment, ViewProcessor.JavaEnvironment env, File rootDirectory, TypeElement javaClass, TypeElement builderClass) {
        this.processingEnvironment = processingEnvironment;
        this.env = env;
        this.rootDirectory = rootDirectory;
        this.javaClass = javaClass;
        this.builderClass = builderClass;
    }

    public void setAllTags(Map<String,TypeElement> tags) {
        this.allTags.clear();
        this.allTags.putAll(tags);
    }

    /**
     * Sets a checksum to embed in the file.  This can be any checksum, not necessarily related to the file contents, which is used
     * to determine if the file needs to be updated.  Currently the checksum is based on the imports in the file.
     * @param checksum
     */
    public void setChecksum(String checksum) {
        this.checksum = checksum;

    }

    public void addInclude(File includeFile) {
        includes.add(includeFile);
    }

    public void addSubGenerator(XMLSchemaGenerator generator) {
        subGenerators.add(generator);
    }

    /**
     * Writes to file. Will not overwrite existing file at location.  Will create parent directory if necessary.
     * @throws IOException
     */
    public void writeToFile() throws IOException {
        if (getSchemaFile().exists()) return;
        getSchemaFile().getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(getSchemaFile())) {
            fos.write(writeSchema(new StringBuilder()).toString().getBytes("utf-8"));
        }
    }

    public File getCommonDir() {
        File dir = rootDirectory;
        File cn1PropertiesFile = new File("codenameone_settings.properties");

        while (dir != null) {

            cn1PropertiesFile = new File(dir, cn1PropertiesFile.getName());
            if (cn1PropertiesFile.exists()) {
                return cn1PropertiesFile.getParentFile();
            }
            dir = dir.getParentFile();
        }
        return null;
    }

    public File getSchemaFile() throws IOException {
        if (writeElements) {
            // IF we are writing the elements, then we are going to be working with the xsd file in the same directory
            // as the original view's xml file.
            File commonDir = getCommonDir();
            if (commonDir == null) {
                throw new IOException("Cannot locate schema file for view "+javaClass);
            }
            File radViews = new File(commonDir, "src" + File.separator + "main" + File.separator + "rad" + File.separator + "views");
            return new File(radViews, javaClass.getQualifiedName().toString().replace('.', File.separatorChar) + ".xsd");

        }
        String path = javaClass.getQualifiedName().toString().replace('.', File.separatorChar);
        File out = new File(rootDirectory, path + ".xsd");
        if (partialSchema && !out.exists()) {
            return new File(rootDirectory, path + "-partial.xsd");
        }
        return out;
    }

    private String toCamelCase(String str) {
        int lowerCaseIndex = -1;
        int len = str.length();
        for (int i=0; i<len; i++) {
            if (Character.isLowerCase(str.charAt(i))) {
                lowerCaseIndex = i;
                break;
            }
        }
        if(lowerCaseIndex < 0) {
            // No lowercase found.  Change full string to lowercase.
            return str.toLowerCase();
        } else if (lowerCaseIndex == 0) {
            // First character is lower case.  We're good.
            return str;
        } else {
            // First character was capital but next was lowercase.  Just lc the first char.
            return str.substring(0, lowerCaseIndex).toLowerCase() + str.substring(lowerCaseIndex);
        }
    }


    public void setWriteElements(boolean writeElements) {
        this.writeElements = writeElements;
    }
    public StringBuilder writeSchema(StringBuilder sb) throws IOException {
        return writeSchema(sb, true);
    }

    private File getAttributeGroupFile(AttributeGroup group) {
        return new File(getCommonDir(), "target" + File.separator + "generated-sources" + File.separator + "rad" + File.separator + "xmlSchemas" + File.separator + group.prefix + "-" + group.type + "-" + group.depth + ".attgroup.xml");
    }

    private Set<TypeElement> enumTypes = new HashSet<>();
    public StringBuilder writeSchema(StringBuilder sb, boolean writeHeader) throws IOException {
        if (writeHeader) {
            sb.append("<?xml version=\"1.0\"?>\n");

        }
        if (writeElements) {
            sb.append("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n");
        }
        if (checksum != null) sb.append("<!-- ").append(checksum).append(" -->\n");
        indent += 2;
        for (File includeFile : includes) {
            //String id = includeFile.getAbsolutePath().replace(".", "_").replace(File.separatorChar, '_').replace("/", "_");
            String content;
            try (FileInputStream fis = new FileInputStream(includeFile)) {
                byte[] bytes = new byte[(int)includeFile.length()];
                fis.read(bytes);
                content = new String(bytes, "UTF-8");


            }
            content = content.trim();
            if (content.startsWith("====\n")) {
                // There is head matter
                int headMatterEndPos = content.indexOf("====\n", 5);
                if (headMatterEndPos < 0) {
                    throw new IOException("Invalid content found in file "+includeFile+".  Found head matter with no end separator.");
                }
                String headMatter = content.substring(5, headMatterEndPos+5).trim();
                content = content.substring(headMatterEndPos+5).trim();
                if (content.startsWith("<?xml")) {
                    content = content.substring(content.indexOf("?>")+2).trim();
                }

                StringTokenizer strtok = new StringTokenizer(headMatter, "\n");
                while (strtok.hasMoreTokens()) {
                    String nextTok = strtok.nextToken().trim();
                    if (nextTok.startsWith("requireAttributeGroup ")) {
                        String attGroupCoords = nextTok.substring(nextTok.indexOf(" ")+1);
                        String prefix = attGroupCoords.substring(0, attGroupCoords.indexOf(":"));
                        String type = attGroupCoords.substring(prefix.length()+1, attGroupCoords.indexOf(":", prefix.length()+1));
                        String depth = attGroupCoords.substring(attGroupCoords.lastIndexOf(":")+1);

                        File attgroupFile = getAttributeGroupFile(new AttributeGroup(prefix, type, Integer.parseInt(depth)));
                        if (!attgroupFile.exists()) {
                            throw new IOException("Cannot find attribute group file at "+attgroupFile+" required by "+includeFile+" while processing schema for "+javaClass);

                        }
                        try (FileInputStream fis = new FileInputStream(attgroupFile)) {
                            byte[] bytes = new byte[(int)attgroupFile.length()];
                            fis.read(bytes);
                            String tmp = new String(bytes, "UTF-8").trim();
                            if (tmp.startsWith("<?xml")) {
                                tmp = tmp.substring(tmp.indexOf("?>")+2).trim();
                            }
                            content = tmp + "\n" + content;
                        }
                    } else if (nextTok.startsWith("require ")) {
                        TypeElement requiredType = processingEnvironment.getElementUtils().getTypeElement(nextTok.substring(nextTok.indexOf(" ")+1).trim());
                        if (requiredType != null) {
                            File requiredTypeFile = getClassSchemaFile(requiredType);
                            if (!requiredTypeFile.exists()) {
                                throw new IOException("Cannot find type schema "+requiredTypeFile+" required by "+includeFile+" while processing schema for "+javaClass);
                            }
                            try (FileInputStream fis = new FileInputStream(requiredTypeFile)) {
                                byte[] bytes = new byte[(int)requiredTypeFile.length()];
                                fis.read(bytes);
                                String tmp = new String(bytes, "UTF-8").trim();
                                if (tmp.startsWith("<?xml")) {
                                    tmp = tmp.substring(tmp.indexOf("?>")+2).trim();
                                }
                                content =  tmp + "\n" + content;
                            }

                        }

                    }
                }

            }
            indent(sb, indent).append(content).append("\n");


        }

        if (!writeElements) {
            //Set<String> tagNames = new HashSet<String>();

            //String tagName0 = toCamelCase(javaClass.getSimpleName().toString());

            //tagNames.add(tagName0);
            Set<Element> parentMembers = new HashSet<>();

            String extensionBase = null;
            TypeElement superType = null;
            {
                TypeMirror superclass = javaClass.getSuperclass();
                if (superclass != null && superclass.getKind() == TypeKind.DECLARED) {
                    superType = (TypeElement) ((DeclaredType) superclass).asElement();
                }
            }
            if (superType != null) {
                extensionBase = superType.getQualifiedName().toString().replace('.', '_');
                final TypeElement fSuperType = superType;
                final DeclaredType fDeclaredSuperType = (DeclaredType)fSuperType.asType();
                processingEnvironment.getElementUtils().getAllMembers(superType).forEach(e -> {
                    if (e.getKind() == ElementKind.METHOD && e.getSimpleName().toString().startsWith("get")) {
                        TypeMirror tm = processingEnvironment.getTypeUtils().asMemberOf(fDeclaredSuperType, e);
                        if (tm.getKind() == TypeKind.EXECUTABLE) {
                            ExecutableType methodMirror = (ExecutableType)tm;
                            TypeMirror methodReturnType = methodMirror.getReturnType();
                            if (methodReturnType.getKind() == TypeKind.TYPEVAR || methodReturnType.getKind() == TypeKind.WILDCARD) {
                                return;
                            }
                        }
                    }
                    parentMembers.add(e);
                });
                //parentMembers.addAll(processingEnvironment.getElementUtils().getAllMembers(superType));
            }

            //indent(sb, indent).append("<xs:element  name=\"").append(tagName).append("\">\n");
            //indent += 2;
            String complexTypeName = javaClass.getQualifiedName().toString().replace('.', '_');


            Set<String> attributeNames = new HashSet<>();

            for (TypeElement clazz : new TypeElement[]{javaClass, builderClass}) {
                if (clazz == null) {
                    // The builder class is null
                    indent(sb, indent).append("<xs:complexType name=\"").append(complexTypeName).append("-impl\">\n");
                    indent(sb, indent).append("  <xs:complexContent>\n");
                    indent(sb, indent).append("    <xs:extension base=\"").append(complexTypeName).append("\"/>\n");
                    indent(sb, indent).append("  </xs:complexContent>\n");
                    indent(sb, indent).append("</xs:complexType>\n");
                    continue;

                }
                if (clazz == builderClass) {
                    indent(sb, indent).append("<xs:complexType name=\"").append(complexTypeName).append("-impl\">\n");
                    indent += 2;
                    indent(sb, indent).append("<xs:complexContent>\n");
                    indent += 2;
                    indent(sb, indent).append("<xs:extension base=\"").append(complexTypeName).append("\">\n");
                    indent += 2;

                } else {
                    String mixed = extensionBase != null ? "" : " mixed=\"true\"";
                    indent(sb, indent).append("<xs:complexType name=\"").append(complexTypeName).append("\"").append(mixed).append(">\n");
                    indent += 2;

                    if (extensionBase != null) {
                        indent(sb, indent).append("<xs:complexContent>\n");
                        indent += 2;
                        indent(sb, indent).append("<xs:extension base=\"").append(extensionBase).append("\">\n");
                        indent += 2;
                    } else {
                        indent(sb, indent).append("<xs:sequence><xs:any minOccurs=\"0\" maxOccurs=\"unbounded\" processContents=\"lax\"/></xs:sequence>");
                        indent(sb, indent).append("<xs:attribute name=\"layout-constraint\" type=\"xs:string\"/>\n");
                        indent(sb, indent).append("<xs:attribute name=\"rad-transition\" type=\"xs:string\"/>\n");
                        indent(sb, indent).append("<xs:attribute name=\"rad-implements\" type=\"xs:string\"/>\n");
                        indent(sb, indent).append("<xs:attribute name=\"rad-href\" type=\"xs:string\"/>\n");
                        indent(sb, indent).append("<xs:attribute name=\"rad-href-trigger\" type=\"xs:string\"/>\n");
                        indent(sb, indent).append("<xs:attribute name=\"view-model\" type=\"xs:string\"/>\n");
                        indent(sb, indent).append("<xs:attribute name=\"rad-extends\" type=\"xs:string\"/>\n");
                        indent(sb, indent).append("<xs:attribute name=\"rad-model\" type=\"xs:string\"/>\n");
                        indent(sb, indent).append("<xs:attribute name=\"rad-var\" type=\"xs:string\"/>\n");
                        indent(sb, indent).append("<xs:attribute name=\"rad-property\" type=\"xs:string\"/>\n");
                        indent(sb, indent).append("<xs:attribute name=\"rad-condition\" type=\"xs:string\"/>\n");

                    }
                }
                for (Element member : processingEnvironment.getElementUtils().getAllMembers(clazz)) {
                    if (extensionBase != null && parentMembers.contains(member)) continue;
                    if (member.getKind() == ElementKind.METHOD) {
                        ExecutableElement methodEl = (ExecutableElement) member;
                        if (methodEl.getParameters().size() == 1) {
                            // Could be a setter
                            String methodName = methodEl.getSimpleName().toString();
                            String propertyName = methodName;



                            if (clazz == javaClass && !methodName.startsWith("set")) {
                                continue;
                            }
                            if (clazz == builderClass && !methodName.startsWith("set")) {
                                ExecutableType methodType = (ExecutableType) processingEnvironment.getTypeUtils().asMemberOf((DeclaredType) builderClass.asType(), methodEl);
                                if (!env.isA(methodType.getReturnType(), "com.codename1.rad.ui.ComponentBuilder")) {
                                    // We'll allow methods that don't start with set in teh builder class if it is
                                    // a builder style method that returns the builder for chaining.
                                    continue;
                                }
                            }
                            if (methodName.startsWith("set")) {
                                propertyName = propertyName.substring(3);
                            }
                            if (propertyName.isEmpty()) continue;
                            propertyName = toCamelCase(propertyName);
                            if (attributeNames.contains(propertyName.toLowerCase())) continue;
                            attributeNames.add(propertyName.toLowerCase());
                            TypeMirror paramTypeMirror = methodEl.getParameters().get(0).asType();
                            List<String> enumValues = null;
                            TypeElement parameterType = null;
                            if (paramTypeMirror.getKind() == TypeKind.DECLARED) {
                                parameterType = (TypeElement)((DeclaredType)paramTypeMirror).asElement();
                                enumValues =
                                        parameterType.getEnclosedElements().stream()
                                                .filter(element -> element.getKind().equals(ElementKind.ENUM_CONSTANT))
                                                .map(Object::toString)
                                                .collect(Collectors.toList());
                            }

                            String type = "xs:string";
                            if (enumValues != null && !enumValues.isEmpty()) {
                                type = parameterType.getQualifiedName().toString().replace('.', '_');
                                enumTypes.add(parameterType);
                            }
                            indent(sb, indent).append("<xs:attribute name=\"").append(propertyName).append("\" type=\"").append(type).append("\"/>\n");
                            if (clazz == javaClass) {
                                indent(sb, indent).append("<xs:attribute name=\"").append("bind-" + propertyName).append("\" type=\"xs:string\"/>\n");
                            }

                        } else if (clazz == javaClass && methodEl.getParameters().size() == 0 && methodEl.getSimpleName().toString().startsWith("get")) {
                            boolean useAttributeGroups = true;



                            ExecutableType methodType = (ExecutableType) processingEnvironment.getTypeUtils().asMemberOf((DeclaredType) clazz.asType(), methodEl);
                            String propertyName = toCamelCase(methodEl.getSimpleName().toString().substring(3));

                            if (methodType != null && methodType.getReturnType() != null) {
                                if (env.isA(methodType.getReturnType(), "com.codename1.ui.plaf.Style")
                                        || env.isA(methodType.getReturnType(), "com.codename1.rad.nodes.ActionNode.Builder")
                                        || (methodEl.getAnnotation(RADDoc.class) != null && methodEl.getAnnotation(RADDoc.class).generateSubattributeHints() && methodType.getReturnType().getKind() == TypeKind.DECLARED)
                                ) {
                                    TypeMirror retTypeMirror = methodType.getReturnType();

                                    if (retTypeMirror.getKind() == TypeKind.DECLARED) {
                                        TypeElement retType = (TypeElement) ((DeclaredType) retTypeMirror).asElement();//processingEnvironment.getElementUtils().getTypeElement("com.codename1.ui.plaf.Style");

                                        if (useAttributeGroups) {
                                            indent(sb, indent).append("<xs:attributeGroup ref=\"").append(getAttributeGroupName((DeclaredType)retTypeMirror, propertyName+".", 3)).append("\" />\n");
                                            addRequiredAttributeGroup(new AttributeGroup(propertyName+".", retType.getQualifiedName().toString(), 3));

                                        } else {

                                            for (Element subMember : processingEnvironment.getElementUtils().getAllMembers(retType)) {
                                                String subMethodName = subMember.getSimpleName().toString();
                                                if (subMember.getKind() != ElementKind.METHOD) continue;
                                                if (!subMethodName.startsWith("set")) continue;
                                                if (((ExecutableElement) subMember).getParameters().size() != 1)
                                                    continue;


                                                List<String> enumValues = null;
                                                TypeElement parameterType = null;
                                                TypeMirror parameterTypeMirror = ((ExecutableElement) subMember).getParameters().get(0).asType();
                                                if (parameterTypeMirror.getKind() == TypeKind.DECLARED) {
                                                    parameterType = (TypeElement) ((DeclaredType) parameterTypeMirror).asElement();
                                                    enumValues =
                                                            parameterType.getEnclosedElements().stream()
                                                                    .filter(element -> element.getKind().equals(ElementKind.ENUM_CONSTANT))
                                                                    .map(Object::toString)
                                                                    .collect(Collectors.toList());
                                                }
                                                String type = "xs:string";
                                                if (enumValues != null && !enumValues.isEmpty()) {
                                                    type = parameterType.toString().replace('.', '_');
                                                    enumTypes.add(parameterType);
                                                }

                                                indent(sb, indent).append("<xs:attribute name=\"").append(propertyName).append(".").append(toCamelCase(subMethodName.toString().substring(3))).append("\" type=\"").append(type).append("\"/>\n");
                                                indent(sb, indent).append("<xs:attribute name=\"bind-").append(propertyName).append(".").append(toCamelCase(subMethodName.toString().substring(3))).append("\" type=\"xs:string\"/>\n");
                                            }
                                        }
                                    }
                                }
                            }


                        }
                    }
                }

                if (clazz == builderClass || extensionBase != null) {
                    indent -= 2;
                    indent(sb, indent).append("</xs:extension>\n");
                    indent -= 2;
                    indent(sb, indent).append("</xs:complexContent>\n");

                }


                indent -= 2;
                indent(sb, indent).append("</xs:complexType>\n");


            }
            for (TypeElement enumType : enumTypes) {
                indent(sb, indent).append("<xs:simpleType name=\"").append(enumType.getQualifiedName().toString().replace('.', '_')).append("\">\n");
                indent(sb, indent).append("  <xs:restriction base=\"xs:string\">\n");

                List<String> enumValues =
                        enumType.getEnclosedElements().stream()
                                .filter(element -> element.getKind().equals(ElementKind.ENUM_CONSTANT))
                                .map(Object::toString)
                                .collect(Collectors.toList());
                for (String enumVal : enumValues) {
                    indent(sb, indent).append("    <xs:enumeration value=\"").append(enumVal).append("\" />\n");
                }
                indent(sb, indent).append("  </xs:restriction>\n");
                indent(sb, indent).append("</xs:simpleType>\n");
            }

            for (XMLSchemaGenerator subGenerator : subGenerators) {
                subGenerator.writeSchema(sb, false);
            }
        }


        if (writeElements) {

            indent(sb, indent).append("<xs:element name=\"script\" type=\"xs:string\"/>\n");
            indent(sb, indent).append("<xs:element name=\"import\" type=\"xs:string\"/>\n");
            indent(sb, indent).append("<xs:element name=\"view-model\">\n");
            indent(sb, indent).append("  <xs:complexType>\n");
            indent(sb, indent).append("    <xs:sequence>\n");
            indent(sb, indent).append("      <xs:element ref=\"define-property\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n");
            indent(sb, indent).append("    </xs:sequence>\n");
            indent(sb, indent).append("    <xs:attribute name=\"extends\" type=\"xs:string\"/>\n");
            indent(sb, indent).append("    <xs:attribute name=\"implements\" type=\"xs:string\"/>\n");
            indent(sb, indent).append("  </xs:complexType>\n");
            indent(sb, indent).append("</xs:element>\n");
            indent(sb, indent).append("<xs:element name=\"form-controller\">\n");
            indent(sb, indent).append("  <xs:complexType>\n");
            indent(sb, indent).append("    <xs:attribute name=\"extends\" type=\"xs:string\"/>\n");
            indent(sb, indent).append("    <xs:attribute name=\"implements\" type=\"xs:string\"/>\n");
            indent(sb, indent).append("  </xs:complexType>\n");
            indent(sb, indent).append("</xs:element>\n");
            indent(sb, indent).append("<xs:element name=\"view-controller\">\n");
            indent(sb, indent).append("  <xs:complexType>\n");
            indent(sb, indent).append("    <xs:attribute name=\"extends\" type=\"xs:string\"/>\n");
            indent(sb, indent).append("  </xs:complexType>\n");
            indent(sb, indent).append("</xs:element>\n");
            indent(sb, indent).append("<xs:element name=\"bind-action\">\n");
            indent(sb, indent).append("  <xs:complexType>\n");
            indent(sb, indent).append("    <xs:attribute name=\"category\" type=\"xs:string\"/>\n");
            indent(sb, indent).append("    <xs:attribute name=\"inherit\" type=\"xs:boolean\"/>\n");
            indent(sb, indent).append("    <xs:attribute name=\"on\" type=\"xs:string\"/>\n");
            indent(sb, indent).append("  </xs:complexType>\n");
            indent(sb, indent).append("</xs:element>\n");
            indent(sb, indent).append("<xs:element name=\"define-tag\">\n");
            indent(sb, indent).append("  <xs:complexType>\n");
            indent(sb, indent).append("    <xs:attribute name=\"name\" type=\"xs:string\"/>\n");
            indent(sb, indent).append("    <xs:attribute name=\"value\" type=\"xs:string\"/>\n");
            indent(sb, indent).append("    <xs:attribute name=\"type\" type=\"xs:string\"/>\n");
            indent(sb, indent).append("  </xs:complexType>\n");
            indent(sb, indent).append("</xs:element>\n");
            indent(sb, indent).append("<xs:element name=\"use-taglib\">\n");
            indent(sb, indent).append("  <xs:complexType>\n");
            indent(sb, indent).append("    <xs:attribute name=\"package\" type=\"xs:string\"/>\n");
            indent(sb, indent).append("    <xs:attribute name=\"class\" type=\"xs:string\"/>\n");
            indent(sb, indent).append("  </xs:complexType>\n");
            indent(sb, indent).append("</xs:element>\n");
            indent(sb, indent).append("<xs:element name=\"define-property\">\n");
            indent(sb, indent).append("  <xs:complexType>\n");
            indent(sb, indent).append("    <xs:attribute name=\"name\" type=\"xs:string\"/>\n");
            indent(sb, indent).append("    <xs:attribute name=\"type\" type=\"xs:string\"/>\n");
            indent(sb, indent).append("  </xs:complexType>\n");
            indent(sb, indent).append("</xs:element>\n");
            indent(sb, indent).append("<xs:element name=\"define-category\">\n");
            indent(sb, indent).append("  <xs:complexType>\n");
            indent(sb, indent).append("    <xs:attribute name=\"name\" type=\"xs:string\"/>\n");
            indent(sb, indent).append("    <xs:attribute name=\"value\" type=\"xs:string\"/>\n");
            indent(sb, indent).append("  </xs:complexType>\n");
            indent(sb, indent).append("</xs:element>\n");
            indent(sb, indent).append("<xs:element name=\"var\">\n");
            indent(sb, indent).append("  <xs:complexType>\n");
            indent(sb, indent).append("    <xs:attribute name=\"value\" type=\"xs:string\"/>\n");
            indent(sb, indent).append("    <xs:attribute name=\"lookup\" type=\"xs:string\"/>\n");
            indent(sb, indent).append("    <xs:attribute name=\"name\" type=\"xs:string\"/>\n");
            indent(sb, indent).append("    <xs:attribute name=\"type\" type=\"xs:string\"/>\n");
            indent(sb, indent).append("  </xs:complexType>\n");
            indent(sb, indent).append("</xs:element>\n");
            indent(sb, indent).append("<xs:element name=\"define-slot\">\n");
            indent(sb, indent).append("  <xs:complexType>\n");
            indent(sb, indent).append("    <xs:sequence>\n");
            indent(sb, indent).append("      <xs:any minOccurs=\"0\" maxOccurs=\"1\" />\n");
            indent(sb, indent).append("    </xs:sequence>\n");
            indent(sb, indent).append("    <xs:attribute name=\"id\" type=\"xs:string\"/>\n");
            indent(sb, indent).append("  </xs:complexType>\n");
            indent(sb, indent).append("</xs:element>\n");
            indent(sb, indent).append("<xs:element name=\"fill-slot\">\n");
            indent(sb, indent).append("  <xs:complexType>\n");
            indent(sb, indent).append("    <xs:sequence>\n");
            indent(sb, indent).append("      <xs:any minOccurs=\"0\" maxOccurs=\"1\"/>\n");
            indent(sb, indent).append("    </xs:sequence>\n");
            indent(sb, indent).append("    <xs:attribute name=\"id\" type=\"xs:string\"/>\n");
            indent(sb, indent).append("  </xs:complexType>\n");
            indent(sb, indent).append("</xs:element>\n");
            indent(sb, indent).append("<xs:element name=\"row-template\">\n");
            indent(sb, indent).append("  <xs:complexType>\n");
            indent(sb, indent).append("    <xs:sequence>\n");
            indent(sb, indent).append("      <xs:any minOccurs=\"0\" maxOccurs=\"1\"/>\n");
            indent(sb, indent).append("    </xs:sequence>\n");
            indent(sb, indent).append("    <xs:attribute name=\"case\" type=\"xs:string\"/>\n");
            indent(sb, indent).append("  </xs:complexType>\n");
            indent(sb, indent).append("</xs:element>\n");

            for (Map.Entry<String,TypeElement> e : allTags.entrySet()) {
                if (e.getValue().getModifiers().contains(Modifier.PUBLIC) && !e.getValue().getModifiers().contains(Modifier.ABSTRACT)) {
                    indent(sb, indent).append("<xs:element name=\"").append(e.getKey()).append("\" type=\"").append(e.getValue().getQualifiedName().toString().replace('.', '_')).append("-impl\"/>\n");
                }
            }
        }



        if (writeElements) {
            indent -= 2;


            indent(sb, indent).append("</xs:schema>\n");
        } else {
            boolean includeHeadmatter = !requiredAttributeGroups.isEmpty() || !enumTypes.isEmpty();
            StringBuilder headMatter = includeHeadmatter ? new StringBuilder() : null;
            if (includeHeadmatter) {

                headMatter.append("====\n");
            }
            if (!requiredAttributeGroups.isEmpty()) {

                HashSet<AttributeGroup> currentRound = new HashSet<>(requiredAttributeGroups);
                while (!currentRound.isEmpty()) {
                    for (AttributeGroup group : currentRound) {
                        headMatter.append("requireAttributeGroup ").append(group.prefix).append(":").append(group.type).append(":").append(group.depth).append("\n");
                        File attGroupFile = getAttributeGroupFile(group);
                        if (!attGroupFile.exists()) {
                            StringBuilder attGroupContent = new StringBuilder();
                            writeAttributeGroup(attGroupContent, (DeclaredType) env.lookupClass(group.type).asType(), group.prefix, group.depth);
                            attGroupFile.getParentFile().mkdirs();
                            try (FileOutputStream fos = new FileOutputStream(attGroupFile)) {
                                fos.write(attGroupContent.toString().getBytes("UTF-8"));
                            }
                        }
                        writtenAttributeGroups.add(group);
                    }
                    currentRound.clear();
                    currentRound.addAll(requiredAttributeGroups);
                    currentRound.removeAll(writtenAttributeGroups);
                }

            }

            if (!enumTypes.isEmpty()) {
                for (TypeElement enumType : enumTypes) {
                    headMatter.append("require ").append(enumType.getQualifiedName()).append("\n");
                    File enumSchemaFile = getClassSchemaFile(enumType);
                    if (!enumSchemaFile.exists()) {
                        enumSchemaFile.getParentFile().mkdirs();
                        StringBuilder enumSchemaContent = new StringBuilder();
                        writeEnumType(enumSchemaContent, enumType);
                        try (FileOutputStream fos = new FileOutputStream(enumSchemaFile)) {
                            fos.write(enumSchemaContent.toString().getBytes("UTF-8"));
                        }

                    }
                }
            }

            if (includeHeadmatter) {
                headMatter.append("====\n");
                sb.insert(0, headMatter);
            }

        }
        return sb;

    }

    private File getClassSchemaFile(TypeElement enumType) {
        return new File(getCommonDir(), "target" + File.separator + "generated-sources" + File.separator + "rad" + File.separator + "xmlSchemas" + File.separator + enumType.getQualifiedName().toString().replace('.', File.separatorChar) + ".xsd");
    }
    private void writeEnumType(StringBuilder sb, TypeElement enumType) {
        indent(sb, indent).append("<xs:simpleType name=\"").append(enumType.getQualifiedName().toString().replace('.', '_')).append("\">\n");
        indent(sb, indent).append("  <xs:restriction base=\"xs:string\">\n");

        List<String> enumValues =
                enumType.getEnclosedElements().stream()
                        .filter(element -> element.getKind().equals(ElementKind.ENUM_CONSTANT))
                        .map(Object::toString)
                        .collect(Collectors.toList());
        for (String enumVal : enumValues) {
            indent(sb, indent).append("    <xs:enumeration value=\"").append(enumVal).append("\" />\n");
        }
        indent(sb, indent).append("  </xs:restriction>\n");
        indent(sb, indent).append("</xs:simpleType>\n");
    }

    private class AttributeGroup {
        private String prefix;
        private String type;
        private int depth;

        public AttributeGroup(String prefix, String type, int depth) {
            this.prefix = prefix;
            this.type = type;
            this.depth = depth;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AttributeGroup that = (AttributeGroup) o;
            return depth == that.depth &&
                    prefix.equals(that.prefix) &&
                    type.equals(that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(prefix, type, depth);
        }
    }


    private String getAttributeGroupName(DeclaredType type, String prefix, int depth) {
        TypeElement typeEl = (TypeElement)type.asElement();
        return prefix.replace('.', '_') + "-" + typeEl.getQualifiedName().toString().replace('.', '_') + "-" + depth;
    }

    private void writeAttributeGroup(StringBuilder sb, DeclaredType type, String prefix, int depth) {
        TypeElement typeEl = (TypeElement)type.asElement();
        String groupName = getAttributeGroupName(type, prefix, depth);
        indent(sb, indent).append("<xs:attributeGroup name=\"").append(groupName).append("\">\n");
        indent += 2;

        processingEnvironment.getElementUtils().getAllMembers(typeEl).forEach(el -> {
            if (el.getKind() != ElementKind.METHOD) return;
            ExecutableElement methodEl = (ExecutableElement)el;
            TypeMirror mirror = processingEnvironment.getTypeUtils().asMemberOf(type, methodEl);
            if (mirror.getKind() != TypeKind.EXECUTABLE) return;

            ExecutableType methodType = (ExecutableType)mirror;



            if (methodEl.getSimpleName().toString().startsWith("set") && methodEl.getParameters().size() == 1 && methodEl.getReturnType().getKind() == TypeKind.VOID && ((ExecutableElement) el).getEnclosingElement().equals(typeEl)) {
                // This is a setter
                String propertyName = methodEl.getSimpleName().toString().substring(3);

                if (propertyName.isEmpty()) return;
                propertyName = toCamelCase(propertyName);
                TypeMirror paramTypeMirror = methodType.getParameterTypes().get(0);
                List<String> enumValues = null;
                TypeElement parameterType = null;
                if (paramTypeMirror.getKind() == TypeKind.DECLARED) {
                    parameterType = (TypeElement)((DeclaredType)paramTypeMirror).asElement();
                    enumValues =
                            parameterType.getEnclosedElements().stream()
                                    .filter(element -> element.getKind().equals(ElementKind.ENUM_CONSTANT))
                                    .map(Object::toString)
                                    .collect(Collectors.toList());
                }

                String typeAttStr = "xs:string";
                if (enumValues != null && !enumValues.isEmpty()) {
                    typeAttStr = parameterType.getQualifiedName().toString().replace('.', '_');
                    enumTypes.add(parameterType);
                }
                indent(sb, indent).append("<xs:attribute name=\"").append(prefix).append(propertyName).append("\" type=\"").append(typeAttStr).append("\"/>\n");
                indent(sb, indent).append("<xs:attribute name=\"bind-").append(prefix).append(propertyName).append("\" type=\"").append("xs:string").append("\"/>\n");
                return;
            }

            if (depth > 0 && methodEl.getSimpleName().toString().startsWith("get") && methodEl.getParameters().size() == 0 && methodType.getReturnType().getKind() == TypeKind.DECLARED && ((ExecutableElement) el).getEnclosingElement().equals(typeEl)) {
                String propertyName = methodEl.getSimpleName().toString().substring(3);

                if (propertyName.isEmpty()) return;
                propertyName = toCamelCase(propertyName);
                DeclaredType returnType = (DeclaredType)methodType.getReturnType();
                RADDoc radDoc = methodEl.getAnnotation(RADDoc.class);
                TypeElement returnTypeEl = (TypeElement)((returnType.asElement().getKind() == ElementKind.CLASS || returnType.asElement().getKind() == ElementKind.INTERFACE) ? returnType.asElement() : null);
                if (returnTypeEl != null && ((radDoc != null && radDoc.generateSubattributeHints()) || returnTypeEl.getQualifiedName().contentEquals("com.codename1.ui.plaf.Style") || env.isA(returnType, "com.codename1.rad.nodes.ActionNode.Builder"))) {
                    indent(sb, indent).append("<xs:attributeGroup ref=\"").append(getAttributeGroupName(returnType, prefix + propertyName + ".", depth-1)).append("\"/>\n");
                    addRequiredAttributeGroup(new AttributeGroup(prefix + propertyName + ".", ((TypeElement)returnTypeEl).getQualifiedName().toString(), depth-1));
                }
            }

        });

        List<TypeMirror> superTypes = new ArrayList<>();
        if (typeEl.getSuperclass() != null) superTypes.add(typeEl.getSuperclass());
        superTypes.forEach(superMirror -> {
            if (superMirror.getKind() == TypeKind.DECLARED) {
                DeclaredType superType = (DeclaredType)superMirror;
                Element superTypeEl = superType.asElement();
                if (superTypeEl == null) return;
                if (superTypeEl.getKind() == ElementKind.CLASS || superTypeEl.getKind() == ElementKind.INTERFACE) {
                    indent(sb, indent).append("<xs:attributeGroup ref=\"").append(getAttributeGroupName(superType, prefix, depth)).append("\"/>\n");
                    addRequiredAttributeGroup(new AttributeGroup(prefix, ((TypeElement)superTypeEl).getQualifiedName().toString(), depth));
                }
            }
        });

        indent -= 2;
        indent(sb, indent).append("</xs:attributeGroup>\n");
    }

    private StringBuilder indent(StringBuilder sb, int indent) {
        for (int i=0; i<indent; i++) sb.append(' ');
        return sb;
    }


    private void addRequiredAttributeGroup(AttributeGroup group) {
        if (writtenAttributeGroups.contains(group)) return;
        requiredAttributeGroups.add(group);
    }




    private Set<AttributeGroup> requiredAttributeGroups = new HashSet<>();
    private Set<AttributeGroup> writtenAttributeGroups = new HashSet<>();

}
