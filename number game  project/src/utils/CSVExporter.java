package utils;

import model.GameRecord;
import java.io.*;
import java.nio.file.*;
import java.util.List;

/**
 * Exports game records to CSV files.
 * Handles file creation, header writing, and append-mode row writing.
 */
public final class CSVExporter {

    private CSVExporter() {}

    /**
     * Appends a single {@link GameRecord} to the history CSV file.
     * Creates the file (with header) if it does not yet exist.
     *
     * @param record the game record to append
     */
    public static void appendHistory(GameRecord record) {
        Path path = Paths.get(Constants.HISTORY_FILE);
        try {
            Files.createDirectories(path.getParent());
            boolean fileExists = Files.exists(path);

            try (BufferedWriter bw = Files.newBufferedWriter(path,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                if (!fileExists) {
                    bw.write(Constants.HISTORY_CSV_HEADER);
                    bw.newLine();
                }
                bw.write(record.toCsvRow());
                bw.newLine();
            }
        } catch (IOException e) {
            Logger.getInstance().error("CSVExporter: failed to write history", e);
        }
    }

    /**
     * Overwrites the high-scores CSV file with the provided list.
     * First row is always the header.
     *
     * @param rows list of CSV row strings (each already formatted)
     */
    public static void writeHighScores(List<String> rows) {
        Path path = Paths.get(Constants.HIGHSCORES_FILE);
        try {
            Files.createDirectories(path.getParent());
            try (BufferedWriter bw = Files.newBufferedWriter(path,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                bw.write(Constants.SCORES_CSV_HEADER);
                bw.newLine();
                for (String row : rows) {
                    bw.write(row);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            Logger.getInstance().error("CSVExporter: failed to write high scores", e);
        }
    }

    /**
     * Reads all lines from a CSV file, skipping the header.
     *
     * @param filePath path to the CSV file
     * @return list of data rows (may be empty if file not found)
     */
    public static List<String> readDataRows(String filePath) {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) return List.of();
        try {
            List<String> lines = Files.readAllLines(path);
            // Skip header row
            return lines.size() > 1 ? lines.subList(1, lines.size()) : List.of();
        } catch (IOException e) {
            Logger.getInstance().error("CSVExporter: failed to read " + filePath, e);
            return List.of();
        }
    }
}
