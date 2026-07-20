package model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Immutable record of a single completed game session.
 * Used for CSV export and analytics dashboard.
 */
public class GameRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String     playerName;
    private final Difficulty difficulty;
    private final int        secretNumber;
    private final int        attempts;
    private final int        maxAttempts;
    private final int        score;
    private final boolean    won;
    private final long       elapsedSeconds;
    private final LocalDate  date;

    public GameRecord(String playerName, Difficulty difficulty, int secretNumber,
                      int attempts, int maxAttempts, int score,
                      boolean won, long elapsedSeconds) {
        this.playerName     = playerName;
        this.difficulty     = difficulty;
        this.secretNumber   = secretNumber;
        this.attempts       = attempts;
        this.maxAttempts    = maxAttempts;
        this.score          = score;
        this.won            = won;
        this.elapsedSeconds = elapsedSeconds;
        this.date           = LocalDate.now();
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public String     getPlayerName()     { return playerName; }
    public Difficulty getDifficulty()     { return difficulty; }
    public int        getSecretNumber()   { return secretNumber; }
    public int        getAttempts()       { return attempts; }
    public int        getMaxAttempts()    { return maxAttempts; }
    public int        getScore()          { return score; }
    public boolean    isWon()             { return won; }
    public long       getElapsedSeconds() { return elapsedSeconds; }
    public LocalDate  getDate()           { return date; }

    /**
     * Converts this record to a CSV row matching {@link utils.Constants#HISTORY_CSV_HEADER}.
     */
    public String toCsvRow() {
        return String.join(",",
            date.toString(),
            playerName,
            difficulty.getDisplayName(),
            String.valueOf(secretNumber),
            String.valueOf(attempts),
            String.valueOf(maxAttempts),
            String.valueOf(score),
            won ? "Yes" : "No",
            String.valueOf(elapsedSeconds)
        );
    }

    @Override
    public String toString() {
        return String.format("GameRecord[player=%s, diff=%s, won=%b, score=%d, attempts=%d/%d]",
            playerName, difficulty.getDisplayName(), won, score, attempts, maxAttempts);
    }
}
