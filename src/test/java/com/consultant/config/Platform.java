package com.consultant.config;

/**
 * Определение платформы на основе системных свойств.
 */
public enum Platform {
    MAC,
    WINDOWS,
    LINUX,
    UNKNOWN;

    public static Platform current() {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("mac") || os.contains("darwin")) {
            return MAC;
        }
        if (os.contains("win")) {
            return WINDOWS;
        }
        if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            return LINUX;
        }
        return UNKNOWN;
    }
}
