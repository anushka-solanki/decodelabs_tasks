package model;

/**
 * Difficulty levels for the Smart Number Guessing Game Pro.
 * Each level defines the number range and maximum allowed attempts.
 */
public enum Difficulty {

    EASY   ("Easy",   1,    50,  15, "Great for beginners — small range, generous attempts!"),
    MEDIUM ("Medium", 1,   100,  10, "A balanced challenge for casual players."),
    HARD   ("Hard",   1,   500,  12, "Wider range — requires strategic guessing!"),
    EXPERT ("Expert", 1,  1000,  15, "For seasoned players only. Good luck!");

    private final String displayName;
    private final int    minValue;
    private final int    maxValue;
    private final int    maxAttempts;
    private final String description;

    Difficulty(String displayName, int minValue, int maxValue,
               int maxAttempts, String description) {
        this.displayName  = displayName;
        this.minValue     = minValue;
        this.maxValue     = maxValue;
        this.maxAttempts  = maxAttempts;
        this.description  = description;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public String getDisplayName() { return displayName; }
    public int    getMinValue()    { return minValue; }
    public int    getMaxValue()    { return maxValue; }
    public int    getMaxAttempts() { return maxAttempts; }
    public String getDescription() { return description; }

    /** Returns the range label, e.g. "1 – 100". */
    public String getRangeLabel()  { return minValue + " – " + maxValue; }

    /** Returns the total number of values in this range. */
    public int rangeSize() { return maxValue - minValue + 1; }

    @Override
    public String toString() { return displayName; }
}
