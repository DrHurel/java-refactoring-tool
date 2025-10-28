package fr.jeremyhurel.utils;

public final class StringFormatter {

    private StringFormatter() {

    }

    public static String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static String escapeDot(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static String sanitizeForDot(String name) {
        if (name == null) {
            return "";
        }
        return name.replaceAll("[^a-zA-Z0-9_.]", "_");
    }

    public static String getSimpleClassName(String fullClassName) {
        if (fullClassName == null || fullClassName.isEmpty()) {
            return "";
        }

        int lastDotIndex = fullClassName.lastIndexOf('.');
        if (lastDotIndex >= 0 && lastDotIndex < fullClassName.length() - 1) {
            return fullClassName.substring(lastDotIndex + 1);
        }
        return fullClassName;
    }

    public static String formatScientific(double value) {
        if (value == 0.0) {
            return "0";
        }

        int exponent = (int) Math.floor(Math.log10(Math.abs(value)));

        double mantissa = value / Math.pow(10, exponent);

        return String.format("%.2f×10^%d", mantissa, exponent);
    }

    public static String repeat(String str, int count) {
        if (str == null || count <= 0) {
            return "";
        }
        return str.repeat(Math.max(0, count));
    }
}
