package com.codename1.rad.annotations.processors;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.codename1.rad.annotations.processors.HelperFunctions.fileContainsString;

class StaleXMLSchemaGarbageCollector implements Runnable {
    private final File javaSourcesDirectory;
    private final File radViewsDirectory;

    StaleXMLSchemaGarbageCollector(File javaSourcesDirectory, File radViewsDirectory) {
        this.javaSourcesDirectory = javaSourcesDirectory;
        this.radViewsDirectory = radViewsDirectory;
    }

    private Collection<File> getAllJavaSourceFiles() {
        return getAllJavaSourceFiles(javaSourcesDirectory, new LinkedHashSet<File>());
    }

    private Collection<File> getAllJavaSourceFiles(File directory, Collection<File> out) {
        for (File child : directory.listFiles()) {
            if (child.isFile() && child.getName().endsWith(".java")) {
                out.add(child);
            } else if (child.isDirectory()) {
                getAllJavaSourceFiles(child, out);
            }
        }
        return out;
    }

    private Collection<File> getAllSchemaFiles() {
        return getAllSchemaFilesInDirectory(radViewsDirectory, new LinkedHashSet<File>());
    }

    private Collection<File> getAllSchemaFilesInDirectory(File directory, Collection<File> out) {
        for (File child : directory.listFiles()) {
            if (child.isFile() && child.getName().endsWith(".xsd")) {
                out.add(child);
            } else if (child.isDirectory()) {
                getAllSchemaFilesInDirectory(child, out);
            }
        }
        return out;
    }

    private String getRelativePath(File javaSourceFile) {
        String javaSourcesPath = javaSourcesDirectory.getAbsolutePath().replace("\\", "/");
        String sourceFilePath = javaSourceFile.getAbsolutePath().replace("\\", "/");
        if (sourceFilePath.startsWith(javaSourcesPath)) {
            String relativePath = sourceFilePath.substring(javaSourcesPath.length());
            if (relativePath.startsWith("/")) {
                relativePath = relativePath.substring(1);
            }
            return relativePath;
        }
        throw new IllegalArgumentException("Source file "+javaSourceFile+" is not in the java sources directory. "+javaSourcesDirectory);

    }

    private String getNormalizedClassName(File javaSourceFile) {
        String name = getRelativePath(javaSourceFile).replace("/", "_");
        if (name.endsWith(".java") || name.endsWith(".kt")) {
            name = name.substring(0, name.lastIndexOf("."));
        }
        return name;
    }

    private boolean containsString(File haystack, String needle) {
        try {
            boolean out = fileContainsString(needle, haystack);
            return out;
        } catch (IOException ex) {
            return false;
        }
    }

    private Collection<File> findSchemasUsingJavaClass(File javaSourceFile) {
        return findSchemasUsingJavaClass(javaSourceFile, getAllSchemaFiles());
    }

    private Collection<File> findSchemasUsingJavaClass(File javaSourceFile, Collection<File> schemas) {
        ArrayList<File> out = new ArrayList<File>();
        String normalizedClassName = getNormalizedClassName(javaSourceFile);
        return schemas.stream().filter(schemaFile -> containsString(schemaFile, normalizedClassName))
                .collect(Collectors.toList());
    }

    private Collection<File> findDirtySchemas() {
        Set<File> cleanSchemas = new LinkedHashSet<>(getAllSchemaFiles());
        Set<File> dirtySchemas = new LinkedHashSet<>();
        for (File javaSourceFile : getAllJavaSourceFiles()) {
            if (cleanSchemas.isEmpty()) {
                return dirtySchemas;
            }
            long lastModified = javaSourceFile.lastModified();
            Collection<File> schemasUsingJavaClass = findSchemasUsingJavaClass(javaSourceFile, cleanSchemas)
                    .stream().filter(schemaFile -> {
                        return schemaFile.lastModified() < lastModified;

                    }).collect(Collectors.toList());
            cleanSchemas.removeAll(schemasUsingJavaClass);
            dirtySchemas.addAll(schemasUsingJavaClass);
        }
        return dirtySchemas;
    }




    @Override
    public void run() {
        findDirtySchemas().forEach(schemaFile -> {
            schemaFile.delete();
        });
    }
}
