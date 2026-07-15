package com.consultant.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Автоопределение пути к бинарнику браузера под текущую ОС.
 * Проверяет несколько типовых путей и возвращает первый существующий.
 */
public final class BrowserBinaryResolver {

    private BrowserBinaryResolver() {
    }

    /**
     * Возвращает путь к бинарнику: явный из конфига или первый найденный кандидат.
     */
    public static Optional<String> resolve(Browser browser) {
        TestConfig config = ConfigHolder.config();
        String configured = config.browserBinaryPath();

        if (isExistingFile(configured)) {
            return Optional.of(normalize(configured));
        }

        if (!config.browserBinaryAutoDetect()) {
            return Optional.empty();
        }

        return candidatesFor(browser.toLocal()).stream()
                .filter(BrowserBinaryResolver::isExistingFile)
                .findFirst()
                .map(BrowserBinaryResolver::normalize);
    }

    public static String resolveDefaultChromePath() {
        return firstExisting(candidatesFor(Browser.CHROME)).orElse(null);
    }

    public static String resolveDefaultEdgePath() {
        return firstExisting(candidatesFor(Browser.EDGE)).orElse(null);
    }

    public static String resolveDefaultFirefoxPath() {
        return firstExisting(candidatesFor(Browser.FIREFOX)).orElse(null);
    }

    private static Optional<String> firstExisting(List<String> paths) {
        return paths.stream()
                .filter(BrowserBinaryResolver::isExistingFile)
                .findFirst()
                .map(BrowserBinaryResolver::normalize);
    }

    private static List<String> candidatesFor(Browser browser) {
        return switch (browser) {
            case CHROME, REMOTE_CHROME -> chromeCandidates();
            case EDGE, REMOTE_EDGE -> edgeCandidates();
            case FIREFOX, REMOTE_FIREFOX -> firefoxCandidates();
        };
    }

    private static List<String> chromeCandidates() {
        List<String> paths = new ArrayList<>();
        switch (Platform.current()) {
            case MAC -> paths.add("/Applications/Google Chrome.app/Contents/MacOS/Google Chrome");
            case WINDOWS -> {
                paths.add("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
                paths.add("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe");
                addEnvPath(paths, "LOCALAPPDATA", "Google", "Chrome", "Application", "chrome.exe");
            }
            case LINUX -> {
                paths.add("/usr/bin/google-chrome");
                paths.add("/usr/bin/google-chrome-stable");
                paths.add("/usr/bin/chromium-browser");
                paths.add("/usr/bin/chromium");
            }
            default -> { }
        }
        addPathEnv(paths, "chrome");
        return paths;
    }

    private static List<String> edgeCandidates() {
        List<String> paths = new ArrayList<>();
        switch (Platform.current()) {
            case MAC -> paths.add("/Applications/Microsoft Edge.app/Contents/MacOS/Microsoft Edge");
            case WINDOWS -> {
                paths.add("C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe");
                paths.add("C:\\Program Files\\Microsoft\\Edge\\Application\\msedge.exe");
                addEnvPath(paths, "LOCALAPPDATA", "Microsoft", "Edge", "Application", "msedge.exe");
            }
            case LINUX -> {
                paths.add("/usr/bin/microsoft-edge");
                paths.add("/usr/bin/microsoft-edge-stable");
            }
            default -> { }
        }
        addPathEnv(paths, "msedge");
        return paths;
    }

    private static List<String> firefoxCandidates() {
        List<String> paths = new ArrayList<>();
        switch (Platform.current()) {
            case MAC -> paths.add("/Applications/Firefox.app/Contents/MacOS/firefox");
            case WINDOWS -> {
                paths.add("C:\\Program Files\\Mozilla Firefox\\firefox.exe");
                paths.add("C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe");
            }
            case LINUX -> {
                paths.add("/usr/bin/firefox");
                paths.add("/usr/bin/firefox-esr");
            }
            default -> { }
        }
        addPathEnv(paths, "firefox");
        return paths;
    }

    /**
     * Ищет исполняемый файл в каталогах из переменной PATH.
     */
    private static void addPathEnv(List<String> paths, String executableName) {
        String pathEnv = System.getenv("PATH");
        if (pathEnv == null || pathEnv.isBlank()) {
            return;
        }
        String fileName = Platform.current() == Platform.WINDOWS ? executableName + ".exe" : executableName;
        String separator = Platform.current() == Platform.WINDOWS ? ";" : ":";

        for (String dir : pathEnv.split(separator)) {
            if (dir.isBlank()) {
                continue;
            }
            Path candidate = Path.of(dir.trim(), fileName);
            String normalized = candidate.toString();
            if (!paths.contains(normalized)) {
                paths.add(normalized);
            }
        }
    }

    private static void addEnvPath(List<String> paths, String envVar, String... parts) {
        String base = System.getenv(envVar);
        if (base == null || base.isBlank()) {
            return;
        }
        Path candidate = Path.of(base, parts);
        paths.add(candidate.toString());
    }

    private static boolean isExistingFile(String path) {
        if (path == null || path.isBlank()) {
            return false;
        }
        Path file = Path.of(path.trim());
        return Files.isRegularFile(file);
    }

    private static String normalize(String path) {
        return Path.of(path.trim()).toAbsolutePath().normalize().toString();
    }
}
