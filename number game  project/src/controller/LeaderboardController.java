package controller;

import model.GameRecord;
import utils.CSVExporter;
import utils.Constants;
import utils.Logger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages loading, saving, and querying the leaderboard.
 * Scores are persisted in a CSV file and kept sorted (descending) in memory.
 *
 * CSV row format: Player,Score,Difficulty,Attempts,Date
 */
public class LeaderboardController {

    // ── Singleton ─────────────────────────────────────────────────────────────

    private static LeaderboardController instance;
    public static LeaderboardController getInstance() {
        if (instance == null) instance = new LeaderboardController();
        return instance;
    }

    // ── Data ──────────────────────────────────────────────────────────────────

    /** In-memory sorted list of leaderboard entries. */
    private final List<LeaderboardEntry> entries = new ArrayList<>();

    private LeaderboardController() {
        load();
    }

    // ── Inner Record ──────────────────────────────────────────────────────────

    public record LeaderboardEntry(
        String player,
        int    score,
        String difficulty,
        int    attempts,
        String date
    ) implements Comparable<LeaderboardEntry> {

        @Override
        public int compareTo(LeaderboardEntry o) {
            return Integer.compare(o.score, this.score); // descending
        }

        public String toCsvRow() {
            return String.join(",", player, String.valueOf(score),
                difficulty, String.valueOf(attempts), date);
        }
    }

    // ── Load / Save ───────────────────────────────────────────────────────────

    private void load() {
        entries.clear();
        List<String> rows = CSVExporter.readDataRows(Constants.HIGHSCORES_FILE);
        for (String row : rows) {
            String[] cols = row.split(",", -1);
            if (cols.length < 5) continue;
            try {
                entries.add(new LeaderboardEntry(
                    cols[0], Integer.parseInt(cols[1]), cols[2],
                    Integer.parseInt(cols[3]), cols[4]));
            } catch (NumberFormatException e) {
                Logger.getInstance().warn("Skipping malformed leaderboard row: " + row);
            }
        }
        Collections.sort(entries);
        Logger.getInstance().info("Leaderboard loaded: " + entries.size() + " entries.");
    }

    private void save() {
        List<String> rows = entries.stream()
            .map(LeaderboardEntry::toCsvRow)
            .collect(Collectors.toList());
        CSVExporter.writeHighScores(rows);
    }

    // ── API ───────────────────────────────────────────────────────────────────

    /**
     * Records a completed game on the leaderboard (only if player won).
     */
    public void recordGame(GameRecord record) {
        if (!record.isWon()) return;
        LeaderboardEntry entry = new LeaderboardEntry(
            record.getPlayerName(),
            record.getScore(),
            record.getDifficulty().getDisplayName(),
            record.getAttempts(),
            record.getDate().toString()
        );
        entries.add(entry);
        Collections.sort(entries);

        // Keep only top N
        if (entries.size() > Constants.MAX_LEADERBOARD_ROWS) {
            entries.subList(Constants.MAX_LEADERBOARD_ROWS, entries.size()).clear();
        }

        save();
        Logger.getInstance().info("Leaderboard updated: " + entry);
    }

    /** Returns an unmodifiable view of all leaderboard entries (sorted descending). */
    public List<LeaderboardEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    /** Returns the current rank of a score (1-based), or -1 if not on board. */
    public int getRankFor(int score) {
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).score() == score) return i + 1;
        }
        return -1;
    }

    /** Returns the highest score on record, or 0 if empty. */
    public int getTopScore() {
        return entries.isEmpty() ? 0 : entries.get(0).score();
    }

    /** Refreshes from disk (call after an external file change). */
    public void reload() { load(); }
}
