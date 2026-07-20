package utils;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple file-based logger for the Smart Number Guessing Game Pro.
 * Writes timestamped log entries to a log file in the data directory.
 * Thread-safe via synchronized write method.
 */
public class Logger {

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Path logPath;
    private static Logger instance;

    /** Severity levels. */
    public enum Level { INFO, WARN, ERROR, DEBUG }

    private Logger() {
        logPath = Paths.get(Constants.LOG_FILE);
        try {
            Files.createDirectories(logPath.getParent());
        } catch (IOException e) {
            System.err.println("Logger: could not create data directory — " + e.getMessage());
        }
    }

    /** Singleton accessor. */
    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    // ── Public API ────────────────────────────────────────────────────────────

    public void info (String msg) { log(Level.INFO,  msg); }
    public void warn (String msg) { log(Level.WARN,  msg); }
    public void error(String msg) { log(Level.ERROR, msg); }
    public void debug(String msg) { log(Level.DEBUG, msg); }

    public void error(String msg, Throwable t) {
        log(Level.ERROR, msg + " — " + t.getMessage());
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private synchronized void log(Level level, String message) {
        String entry = String.format("[%s] [%-5s] %s%n",
            LocalDateTime.now().format(FMT), level.name(), message);

        // Always echo to console
        if (level == Level.ERROR || level == Level.WARN) {
            System.err.print(entry);
        } else {
            System.out.print(entry);
        }

        // Write to file (append mode)
        try (BufferedWriter bw = Files.newBufferedWriter(
                logPath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            bw.write(entry);
        } catch (IOException e) {
            System.err.println("Logger: failed to write log — " + e.getMessage());
        }
    }
}
