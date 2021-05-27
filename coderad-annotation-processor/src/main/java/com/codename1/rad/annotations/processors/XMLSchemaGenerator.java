package com.codename1.rad.annotations.processors;

import com.codename1.rad.annotations.RAD;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class XMLSchemaGenerator {
    private ViewProcessor.JavaEnvironment env;
    private ProcessingEnvironment processingEnvironment;
    private File rootDirectory;
    private TypeElement javaClass;
    private TypeElement builderClass;
    private int indent = 0;
    private String checksum;


    private List<File> includes = new ArrayList<>();

    public XMLSchemaGenerator(ProcessingEnvironment processingEnvironment, ViewProcessor.JavaEnvironment env, File rootDirectory, TypeElement javaClass, TypeElement builderClass) {
        this.processingEnvironment = processingEnvironment;
        this.env = env;
        this.rootDirectory = rootDirectory;
        this.javaClass = javaClass;
        this.builderClass = builderClass;
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

    public File getSchemaFile() {
        String path = javaClass.getQualifiedName().toString().replace('.', File.separatorChar);
        return new File(rootDirectory, path + ".xsd");
    }

    public StringBuilder writeSchema(StringBuilder sb) {
        sb.append("<?xml version=\"1.0\"?>\n");
        sb.append("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n");
        if (checksum != null) sb.append("<!-- ").append(checksum).append(" -->\n");
        indent += 2;

        Set<String> tagNames = new HashSet<String>();

        String tagName0 = javaClass.getSimpleName().toString();
        tagName0 = tagName0.substring(0, 1).toLowerCase() + ((tagName0.length() > 1) ? tagName0.substring(1) : "");
        tagNames.add(tagName0);
        if (builderClass != null) {
            RAD radAnnotation = builderClass.getAnnotation(RAD.class);
            for (String radTag : radAnnotation.tag()) {
                tagNames.add(radTag);
            }
        }
        for (String tagName : tagNames) {


            indent(sb, indent).append("<xs:element  name=\"").append(tagName).append("\">\n");
            indent += 2;
            indent(sb, indent).append("<xs:complexType>\n");
            indent += 2;
            indent(sb, indent).append("<xs:sequence><xs:any minOccurs=\"0\"/></xs:sequence>");
            indent(sb, indent).append("<xs:attribute name=\"layout-constraint\" type=\"xs:string\"/>\n");
            indent(sb, indent).append("<xs:attribute name=\"rad-implements\" type=\"xs:string\"/>\n");
            indent(sb, indent).append("<xs:attribute name=\"rad-href\" type=\"xs:string\"/>\n");
            indent(sb, indent).append("<xs:attribute name=\"rad-extends\" type=\"xs:string\"/>\n");
            indent(sb, indent).append("<xs:attribute name=\"rad-model\" type=\"xs:string\"/>\n");

            Set<String> attributeNames = new HashSet<>();
            for (TypeElement clazz : new TypeElement[]{builderClass, javaClass}) {
                if (clazz == null) continue;
                for (Element member : processingEnvironment.getElementUtils().getAllMembers(clazz)) {
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
                                ExecutableType methodType = (ExecutableType)processingEnvironment.getTypeUtils().asMemberOf((DeclaredType)builderClass.asType(), methodEl);
                                if (!env.isA(methodType.getReturnType(),"com.codename1.rad.ui.ComponentBuilder")) {
                                    // We'll allow methods that don't start with set in teh builder class if it is
                                    // a builder style method that returns the builder for chaining.
                                    continue;
                                }
                            }
                            if (methodName.startsWith("set")) {
                                propertyName = propertyName.substring(3);
                            }
                            if (propertyName.isEmpty()) continue;
                            propertyName = propertyName.substring(0, 1).toLowerCase() + (propertyName.length() > 1 ? propertyName.substring(1) : "");
                            if (attributeNames.contains(propertyName.toLowerCase())) continue;
                            attributeNames.add(propertyName.toLowerCase());
                            indent(sb, indent).append("<xs:attribute name=\"").append(propertyName).append("\" type=\"xs:string\"/>\n");
                            if (clazz == javaClass) {
                                indent(sb, indent).append("<xs:attribute name=\"").append("bind-" + propertyName).append("\" type=\"xs:string\"/>\n");
                            }

                        }
                    }
                }
            }

            //
            indent -= 2;
            indent(sb, indent).append("</xs:complexType>\n");
            indent -= 2;
            indent(sb, indent).append("</xs:element>\n");
        }
        for (File includeFile : includes) {
            indent(sb, indent).append("<xs:include schemaLocation=\"file://").append(includeFile.getAbsolutePath()).append("\"/>\n");
        }
        indent -= 2;
        indent(sb, indent).append("</xs:schema>\n");
        return sb;

    }

    private StringBuilder indent(StringBuilder sb, int indent) {
        for (int i=0; i<indent; i++) sb.append(' ');
        return sb;
    }


}
