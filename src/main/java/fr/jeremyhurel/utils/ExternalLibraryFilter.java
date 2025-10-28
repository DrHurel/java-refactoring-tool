package fr.jeremyhurel.utils;

import java.util.Set;

public final class ExternalLibraryFilter {

    private ExternalLibraryFilter() {

    }

    private static final Set<String> PRIMITIVE_TYPES = Set.of(
        "int", "long", "double", "float",
        "boolean", "char", "byte", "short", "void"
    );

    private static final Set<String> JDK_PREFIXES = Set.of(
        "java.", "javax.", "jdk.", "sun.", "com.sun."
    );

    private static final Set<String> FRAMEWORK_PREFIXES = Set.of(
        "org.springframework.",
        "org.hibernate.",
        "org.apache.",
        "com.google.",
        "com.fasterxml.jackson.",
        "org.junit.",
        "org.mockito.",
        "org.slf4j.",
        "ch.qos.logback.",
        "org.eclipse.",
        "com.googlecode.lanterna.",
        "spoon."
    );

    public static boolean isExternalLibraryClass(String typeName, String rootPackage) {
        if (typeName == null) {
            return true;
        }

        if (isPrimitiveType(typeName)) {
            return true;
        }

        if (isJdkClass(typeName)) {
            return true;
        }

        if (isFrameworkClass(typeName)) {
            return true;
        }

        if (rootPackage != null && !typeName.startsWith(rootPackage)) {
            return true;
        }

        return false;
    }

    public static boolean isPrimitiveType(String typeName) {
        return PRIMITIVE_TYPES.contains(typeName);
    }

    public static boolean isJdkClass(String typeName) {
        return JDK_PREFIXES.stream().anyMatch(typeName::startsWith);
    }

    public static boolean isFrameworkClass(String typeName) {
        return FRAMEWORK_PREFIXES.stream().anyMatch(typeName::startsWith);
    }
}
