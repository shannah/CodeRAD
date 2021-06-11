package com.codename1.rad.annotations.processors;

import com.codename1.rad.annotations.RAD;

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

            //indent(sb, indent).append("<xs:include schemaLocation=\"file://").append(includeFile.getAbsolutePath()).append("\"/>\n");

            indent(sb, indent).append(content.substring(content.indexOf("?>")+2)).append("\n");

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
                parentMembers.addAll(processingEnvironment.getElementUtils().getAllMembers(superType));
            }

            //indent(sb, indent).append("<xs:element  name=\"").append(tagName).append("\">\n");
            //indent += 2;
            String complexTypeName = javaClass.getQualifiedName().toString().replace('.', '_');


            Set<String> attributeNames = new HashSet<>();
            Set<TypeElement> enumTypes = new HashSet<>();
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
                            ExecutableType methodType = (ExecutableType) processingEnvironment.getTypeUtils().asMemberOf((DeclaredType) clazz.asType(), methodEl);
                            String propertyName = toCamelCase(methodEl.getSimpleName().toString().substring(3));
                            if (env.isA(methodType.getReturnType(), "com.codename1.ui.plaf.Style") || env.isA(methodType.getReturnType(), "com.codename1.rad.nodes.ActionNode.Builder")) {

                                TypeElement retType = (TypeElement)((DeclaredType)methodType.getReturnType()).asElement();//processingEnvironment.getElementUtils().getTypeElement("com.codename1.ui.plaf.Style");

                                for (Element subMember : processingEnvironment.getElementUtils().getAllMembers(retType)) {
                                    String subMethodName = subMember.getSimpleName().toString();
                                    if (subMember.getKind() != ElementKind.METHOD) continue;
                                    if (!subMethodName.startsWith("set")) continue;
                                    if (((ExecutableElement)subMember).getParameters().size() != 1) continue;


                                    List<String> enumValues = null;
                                    TypeElement parameterType = null;
                                    TypeMirror parameterTypeMirror = ((ExecutableElement)subMember).getParameters().get(0).asType();
                                    if (parameterTypeMirror.getKind() == TypeKind.DECLARED) {
                                        parameterType = (TypeElement) ((DeclaredType)parameterTypeMirror).asElement();
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
        }
        return sb;

    }

    private StringBuilder indent(StringBuilder sb, int indent) {
        for (int i=0; i<indent; i++) sb.append(' ');
        return sb;
    }


}
