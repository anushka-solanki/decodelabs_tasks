package model;

import java.io.*;

/**
 * Represents a player's profile including their name, avatar, and statistics.
 * Serializable for persistence via {@link controller.ProfileController}.
 */
public class Player implements Serializable {

    private static final long serialVersionUID = 2L;

    // ── Identity ──────────────────────────────────────────────────────────────
    private String name;
    private String avatarEmoji;   // e.g. "🎮", "🦊", "🧠"

    // ── Cumulative Statistics ─────────────────────────────────────────────────
    private int  totalGamesPlayed;
    private int  totalWins;
    private int  totalLosses;
    private int  bestScore;
    private long fastestWinSeconds;  // in seconds (Long.MAX_VALUE = never won)
    private long totalAttemptsUsed;
    private int  totalAttemptsPossible;

    // ── Available Avatars ─────────────────────────────────────────────────────
    public static final String[] AVATARS = {
        "🎮", "🧠", "🦊", "🐉", "🚀", "⚡", "🔥", "💎", "🦁", "🐺"
    };

    // ── Constructor ───────────────────────────────────────────────────────────

    public Player(String name) {
        this.name               = name;
        this.avatarEmoji        = AVATARS[0];
        this.totalGamesPlayed   = 0;
        this.totalWins          = 0;
        this.totalLosses        = 0;
        this.bestScore          = 0;
        this.fastestWinSeconds  = Long.MAX_VALUE;
        this.totalAttemptsUsed  = 0;
        this.totalAttemptsPossible = 0;
    }

    // ── Stat Update ───────────────────────────────────────────────────────────

    /**
     * Records the results of a completed game into this player's statistics.
     */
    public void recordGame(GameRecord record) {
        totalGamesPlayed++;
        totalAttemptsUsed      += record.getAttempts();
        totalAttemptsPossible  += record.getMaxAttempts();

        if (record.isWon()) {
            totalWins++;
            if (record.getScore() > bestScore) {
                bestScore = record.getScore();
            }
            if (record.getElapsedSeconds() < fastestWinSeconds) {
                fastestWinSeconds = record.getElapsedSeconds();
            }
        } else {
            totalLosses++;
        }
    }

    // ── Derived Stats ─────────────────────────────────────────────────────────

    /** Win percentage (0–100). Returns 0 if no games played. */
    public double getWinPercentage() {
        if (totalGamesPlayed == 0) return 0.0;
        return (totalWins * 100.0) / totalGamesPlayed;
    }

    /** Average attempts per game. */
    public double getAverageAttempts() {
        if (totalGamesPlayed == 0) return 0.0;
        return (double) totalAttemptsUsed / totalGamesPlayed;
    }

    /** Accuracy: ratio of attempts used vs. possible. */
    public double getAccuracyPercent() {
        if (totalAttemptsPossible == 0) return 0.0;
        // Fewer attempts used = higher accuracy
        double used = totalAttemptsUsed;
        double possible = totalAttemptsPossible;
        return Math.max(0, (1.0 - (used / possible)) * 100.0);
    }

    /** Fastest win as formatted string, or "N/A". */
    public String getFastestWinFormatted() {
        if (fastestWinSeconds == Long.MAX_VALUE) return "N/A";
        long minutes = fastestWinSeconds / 60;
        long secs    = fastestWinSeconds % 60;
        if (minutes > 0) return String.format("%dm %02ds", minutes, secs);
        return fastestWinSeconds + "s";
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public String getName()              { return name; }
    public void   setName(String n)      { this.name = n; }
    public String getAvatarEmoji()       { return avatarEmoji; }
    public void   setAvatarEmoji(String a){ this.avatarEmoji = a; }
    public int    getTotalGamesPlayed()  { return totalGamesPlayed; }
    public int    getTotalWins()         { return totalWins; }
    public int    getTotalLosses()       { return totalLosses; }
    public int    getBestScore()         { return bestScore; }
    public long   getFastestWinSeconds() { return fastestWinSeconds; }
    public long   getTotalAttemptsUsed() { return totalAttemptsUsed; }

    @Override
    public String toString() {
        return avatarEmoji + " " + name;
    }
}
