package utils;

/**
 * Input validation helpers for the Smart Number Guessing Game Pro.
 * All methods are stateless and static for easy reuse.
 */
public final class Validator {

    private Validator() {}

    /**
     * Checks whether the given string represents a valid integer.
     *
     * @param text raw input text
     * @return true if text can be parsed as an integer
     */
    public static boolean isValidInteger(String text) {
        if (text == null || text.isBlank()) return false;
        try {
            Integer.parseInt(text.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks whether the integer value falls within [min, max] (inclusive).
     *
     * @param value the integer to check
     * @param min   lower bound (inclusive)
     * @param max   upper bound (inclusive)
     * @return true if value is within range
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    /**
     * Validates a guess string: must be a valid integer within [min, max].
     *
     * @param text raw input from UI
     * @param min  difficulty min value
     * @param max  difficulty max value
     * @return a {@link ValidationResult} carrying success flag and error message
     */
    public static ValidationResult validateGuess(String text, int min, int max) {
        if (text == null || text.isBlank()) {
            return ValidationResult.fail("Please enter a number.");
        }
        if (!isValidInteger(text)) {
            return ValidationResult.fail("Invalid input. Please enter a whole number.");
        }
        int value = Integer.parseInt(text.trim());
        if (!isInRange(value, min, max)) {
            return ValidationResult.fail(
                "Number must be between " + min + " and " + max + ".");
        }
        return ValidationResult.ok(value);
    }

    /**
     * Validates a player name: non-blank, 2–20 characters, alphanumeric + spaces.
     */
    public static ValidationResult validatePlayerName(String name) {
        if (name == null || name.isBlank()) {
            return ValidationResult.fail("Player name cannot be empty.");
        }
        String trimmed = name.trim();
        if (trimmed.length() < 2) {
            return ValidationResult.fail("Name must be at least 2 characters.");
        }
        if (trimmed.length() > 20) {
            return ValidationResult.fail("Name must be 20 characters or fewer.");
        }
        if (!trimmed.matches("[a-zA-Z0-9 _\\-]+")) {
            return ValidationResult.fail("Name may only contain letters, digits, spaces, _ or -.");
        }
        return ValidationResult.ok(0);
    }

    // ── Inner Result Class ────────────────────────────────────────────────────

    /**
     * Encapsulates the outcome of a validation check.
     */
    public static class ValidationResult {
        public final boolean valid;
        public final String  errorMessage;
        public final int     parsedValue;  // only meaningful when valid == true

        private ValidationResult(boolean valid, String errorMessage, int parsedValue) {
            this.valid        = valid;
            this.errorMessage = errorMessage;
            this.parsedValue  = parsedValue;
        }

        static ValidationResult ok(int value)       { return new ValidationResult(true,  null,  value); }
        static ValidationResult fail(String msg)    { return new ValidationResult(false, msg,   0);     }
    }
}
