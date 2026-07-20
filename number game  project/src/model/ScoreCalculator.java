package model;

import utils.Constants;

/**
 * Calculates game scores based on attempts saved and time elapsed.
 * Formula: score = basePart + timeBonusPart + firstGuesBonus
 *
 * - basePart  = (maxAttempts - attemptsUsed) * BASE_PER_ATTEMPT_SAVED
 * - timeBonus = max(0, TIME_BONUS_MAX_SEC - elapsedSec) * TIME_BONUS_PER_SEC
 * - firstGuessBonus = FIRST_GUESS_BONUS if attemptsUsed == 1
 */
public final class ScoreCalculator {

    private ScoreCalculator() {}

    /**
     * Computes the final score for a won game.
     *
     * @param attemptsUsed  how many guesses the player made
     * @param maxAttempts   the maximum guesses allowed for the difficulty
     * @param elapsedSec    time taken in seconds
     * @return computed score (always ≥ 0)
     */
    public static int calculate(int attemptsUsed, int maxAttempts, long elapsedSec) {
        int basePart = Math.max(0,
            (maxAttempts - attemptsUsed) * Constants.SCORE_BASE_PER_ATTEMPT_SAVED);

        long secondsUnderLimit = Math.max(0,
            Constants.SCORE_TIME_BONUS_MAX_SEC - elapsedSec);
        int timeBonus = (int)(secondsUnderLimit * Constants.SCORE_TIME_BONUS_PER_SEC);

        int firstGuessBonus = (attemptsUsed == 1) ? Constants.SCORE_FIRST_GUESS_BONUS : 0;

        return basePart + timeBonus + firstGuessBonus;
    }

    /**
     * Returns 0 for a lost game.
     */
    public static int calculateLoss() {
        return 0;
    }

    /**
     * Computes accuracy percentage for display.
     * attemptsUsed / maxAttempts ratio inverted to represent efficiency.
     */
    public static double accuracyPercent(int attemptsUsed, int maxAttempts) {
        if (maxAttempts <= 0) return 0.0;
        return Math.max(0.0, (1.0 - ((double) attemptsUsed / maxAttempts)) * 100.0);
    }
}
