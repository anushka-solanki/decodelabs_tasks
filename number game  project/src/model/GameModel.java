package model;

import utils.Constants;
import java.util.Random;

/**
 * Core game model for the Smart Number Guessing Game Pro.
 *
 * Responsibilities:
 * - Maintain game state (secret number, attempts, hints, timer)
 * - Evaluate guesses and return {@link GuessResult}
 * - Generate contextual hints after N failed guesses
 * - Notify observers via {@link GameStateListener}
 *
 * This class is pure Java with no Swing imports (MVC separation).
 */
public class GameModel {

    // ── Guess Result ──────────────────────────────────────────────────────────

    public enum GuessResult { TOO_LOW, TOO_HIGH, CORRECT, GAME_OVER, INVALID }

    // ── State ─────────────────────────────────────────────────────────────────

    private Difficulty  difficulty;
    private int         secretNumber;
    private int         attemptsUsed;
    private boolean     gameActive;
    private boolean     gameWon;
    private long        startTimeMs;
    private long        elapsedSeconds;
    private int         currentScore;
    private int         hintsRevealedCount;

    private GameStateListener listener;

    private static final Random RNG = new Random();

    // ── Construction & Initialization ─────────────────────────────────────────

    public GameModel() {
        this.difficulty = Difficulty.MEDIUM;
    }

    /** Starts a fresh game with the currently set difficulty. */
    public void newGame() {
        secretNumber       = RNG.nextInt(difficulty.getMaxValue() - difficulty.getMinValue() + 1)
                           + difficulty.getMinValue();
        attemptsUsed       = 0;
        gameActive         = true;
        gameWon            = false;
        startTimeMs        = System.currentTimeMillis();
        elapsedSeconds     = 0;
        currentScore       = 0;
        hintsRevealedCount = 0;

        utils.Logger.getInstance().info(
            "New game started: difficulty=" + difficulty.getDisplayName()
            + ", range=[" + difficulty.getMinValue() + "," + difficulty.getMaxValue()
            + "], maxAttempts=" + difficulty.getMaxAttempts());

        if (listener != null) listener.onGameStarted(difficulty);
    }

    // ── Guess Processing ──────────────────────────────────────────────────────

    /**
     * Processes a player's guess.
     *
     * @param guess the integer guessed by the player
     * @return the result of the guess
     */
    public GuessResult submitGuess(int guess) {
        if (!gameActive) return GuessResult.INVALID;

        attemptsUsed++;
        elapsedSeconds = (System.currentTimeMillis() - startTimeMs) / 1000;

        GuessResult result;

        if (guess == secretNumber) {
            result     = GuessResult.CORRECT;
            gameActive = false;
            gameWon    = true;
            currentScore = ScoreCalculator.calculate(
                attemptsUsed, difficulty.getMaxAttempts(), elapsedSeconds);
            utils.Logger.getInstance().info(
                "Player guessed correctly! number=" + secretNumber
                + ", attempts=" + attemptsUsed + ", score=" + currentScore);
            if (listener != null) listener.onCorrectGuess(currentScore, attemptsUsed, elapsedSeconds);

        } else if (guess < secretNumber) {
            result = GuessResult.TOO_LOW;
            if (listener != null) listener.onTooLow(guess, attemptsRemaining());

        } else {
            result = GuessResult.TOO_HIGH;
            if (listener != null) listener.onTooHigh(guess, attemptsRemaining());
        }

        // Check game over (wrong guess and no attempts left)
        if (result != GuessResult.CORRECT && attemptsRemaining() <= 0) {
            result     = GuessResult.GAME_OVER;
            gameActive = false;
            gameWon    = false;
            if (listener != null) listener.onGameOver(secretNumber);
            utils.Logger.getInstance().info(
                "Game over. Secret was " + secretNumber);
        }

        // Reveal hints when needed
        if (gameActive && attemptsUsed >= Constants.HINT_AFTER_ATTEMPTS
                && hintsRevealedCount < HintType.values().length) {
            revealNextHint();
        }

        return result;
    }

    // ── Hint Logic ────────────────────────────────────────────────────────────

    private void revealNextHint() {
        if (hintsRevealedCount >= HintType.values().length) return;
        HintType hintType = HintType.values()[hintsRevealedCount];
        String   hintText = generateHintText(hintType);
        hintsRevealedCount++;

        utils.Logger.getInstance().debug("Hint revealed: " + hintType + " → " + hintText);
        if (listener != null) listener.onHintRevealed(hintType, hintText);
    }

    private String generateHintText(HintType type) {
        return switch (type) {
            case ODD_EVEN    -> "The number is " + (secretNumber % 2 == 0 ? "EVEN" : "ODD") + ".";
            case PRIME       -> "The number is " + (isPrime(secretNumber) ? "PRIME" : "NOT prime") + ".";
            case MULTIPLE_5  -> "The number is "
                                + (secretNumber % 5 == 0 ? "" : "NOT ")
                                + "a multiple of 5.";
            case RANGE_NARROW -> {
                int spread = Math.max(10, (difficulty.getMaxValue() - difficulty.getMinValue()) / 8);
                int lo = Math.max(difficulty.getMinValue(), secretNumber - spread);
                int hi = Math.min(difficulty.getMaxValue(), secretNumber + spread);
                yield "The number lies between " + lo + " and " + hi + ".";
            }
        };
    }

    private boolean isPrime(int n) {
        if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }

    // ── Timer Update ──────────────────────────────────────────────────────────

    /** Called each second by a UI timer to update elapsed time. */
    public void tickTimer() {
        if (gameActive) {
            elapsedSeconds = (System.currentTimeMillis() - startTimeMs) / 1000;
            if (listener != null) listener.onTimerTick(elapsedSeconds);
        }
    }

    // ── Derived State ─────────────────────────────────────────────────────────

    public int     attemptsRemaining()  { return difficulty.getMaxAttempts() - attemptsUsed; }
    public double  progressFraction()   {
        return (double) attemptsUsed / difficulty.getMaxAttempts();
    }
    public int     getAttemptsUsed()    { return attemptsUsed; }
    public int     getMaxAttempts()     { return difficulty.getMaxAttempts(); }
    public boolean isGameActive()       { return gameActive; }
    public boolean isGameWon()          { return gameWon; }
    public int     getCurrentScore()    { return currentScore; }
    public long    getElapsedSeconds()  { return elapsedSeconds; }
    public int     getSecretNumber()    { return secretNumber; }
    public Difficulty getDifficulty()   { return difficulty; }

    // ── Setters ───────────────────────────────────────────────────────────────

    public void setDifficulty(Difficulty d) { this.difficulty = d; }
    public void setListener(GameStateListener l) { this.listener = l; }

    // ── Listener Interface ────────────────────────────────────────────────────

    /**
     * Observer interface for game state changes.
     * Implemented by the GameController to relay events to the UI.
     */
    public interface GameStateListener {
        void onGameStarted(Difficulty difficulty);
        void onTooLow    (int guess, int attemptsLeft);
        void onTooHigh   (int guess, int attemptsLeft);
        void onCorrectGuess(int score, int attempts, long elapsedSec);
        void onGameOver  (int secretNumber);
        void onHintRevealed(HintType type, String hintText);
        void onTimerTick (long elapsedSeconds);
    }
}
