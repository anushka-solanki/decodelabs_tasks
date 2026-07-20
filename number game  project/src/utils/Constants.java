package utils;

/**
 * Application-wide constants used throughout the Smart Number Guessing Game Pro.
 * Centralizes configuration values to avoid magic numbers and strings.
 */
public final class Constants {

    // Prevent instantiation
    private Constants() {}

    // ── App Info ──────────────────────────────────────────────────────────────
    public static final String APP_NAME    = "Smart Number Guessing Game Pro";
    public static final String APP_VERSION = "1.0.0";
    public static final String DEVELOPER   = "Smart Games Inc.";
    public static final String DEVELOPER_EMAIL = "support@smartgames.dev";
    public static final int    APP_YEAR    = 2026;

    // ── Data / File Paths ─────────────────────────────────────────────────────
    public static final String DATA_DIR        = "data/";
    public static final String HIGHSCORES_FILE = DATA_DIR + "highscores.csv";
    public static final String HISTORY_FILE    = DATA_DIR + "history.csv";
    public static final String PROFILE_FILE    = DATA_DIR + "profile.dat";
    public static final String LOG_FILE        = DATA_DIR + "game.log";

    // ── UI Dimensions ─────────────────────────────────────────────────────────
    public static final int WINDOW_WIDTH    = 1150;
    public static final int WINDOW_HEIGHT   = 760;
    public static final int SPLASH_WIDTH    = 520;
    public static final int SPLASH_HEIGHT   = 360;
    public static final int SPLASH_DURATION = 3200;  // ms

    // ── Game Defaults ─────────────────────────────────────────────────────────
    public static final int HINT_AFTER_ATTEMPTS  = 3;   // show hint after N wrong guesses
    public static final int MAX_LEADERBOARD_ROWS = 20;
    public static final int TIMER_TICK_MS        = 1000; // 1 second

    // ── Scoring ───────────────────────────────────────────────────────────────
    public static final int SCORE_BASE_PER_ATTEMPT_SAVED = 15;
    public static final int SCORE_TIME_BONUS_MAX_SEC     = 60;
    public static final int SCORE_TIME_BONUS_PER_SEC     = 4;
    public static final int SCORE_FIRST_GUESS_BONUS      = 200;

    // ── Fonts ─────────────────────────────────────────────────────────────────
    public static final String FONT_PRIMARY = "Segoe UI";
    public static final String FONT_MONO    = "Consolas";

    // ── Confetti ──────────────────────────────────────────────────────────────
    public static final int CONFETTI_COUNT      = 120;
    public static final int CONFETTI_DURATION   = 4000; // ms
    public static final int CONFETTI_TICK_MS    = 25;

    // ── CSV Headers ───────────────────────────────────────────────────────────
    public static final String HISTORY_CSV_HEADER =
        "Date,Player,Difficulty,SecretNumber,Attempts,MaxAttempts,Score,Won,TimeSec";
    public static final String SCORES_CSV_HEADER =
        "Player,Score,Difficulty,Attempts,Date";
}
