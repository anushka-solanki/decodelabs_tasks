package controller;

import model.*;
import model.GameModel.GameStateListener;
import model.GameModel.GuessResult;
import sound.SoundManager;
import utils.CSVExporter;
import utils.Logger;
import utils.Validator;
import utils.Validator.ValidationResult;

import javax.swing.Timer;
import java.util.ArrayList;
import java.util.List;

/**
 * Main game controller — bridges the GameModel with the UI layer.
 *
 * Responsibilities:
 *  - Own a GameModel instance and configure it
 *  - Process guess submissions from the UI (validate → model → notify view)
 *  - Manage the Swing Timer that drives the countdown display
 *  - Record completed games to the leaderboard and CSV history
 *  - Notify the UI via {@link GameController.ViewCallback}
 */
public class GameController implements GameStateListener {

    // ── Singleton ─────────────────────────────────────────────────────────────

    private static GameController instance;
    public static GameController getInstance() {
        if (instance == null) instance = new GameController();
        return instance;
    }

    // ── Fields ────────────────────────────────────────────────────────────────

    private final GameModel          model   = new GameModel();
    private final ProfileController  profile = ProfileController.getInstance();
    private final LeaderboardController lb   = LeaderboardController.getInstance();
    private final List<ViewCallback> views   = new ArrayList<>();
    private Timer                    swingTimer;

    // ── Construction ──────────────────────────────────────────────────────────

    private GameController() {
        model.setListener(this);
        initTimer();
    }

    private void initTimer() {
        swingTimer = new Timer(utils.Constants.TIMER_TICK_MS, e -> model.tickTimer());
        swingTimer.setRepeats(true);
    }

    // ── View Registration ─────────────────────────────────────────────────────

    public void addView(ViewCallback v)    { views.add(v); }
    public void removeView(ViewCallback v) { views.remove(v); }

    // ── Game Flow ─────────────────────────────────────────────────────────────

    /** Starts a new game with the currently set difficulty. */
    public void startNewGame() {
        swingTimer.stop();
        model.newGame();
        swingTimer.restart();
        sound.SoundManager.getInstance().play(SoundManager.Sound.CLICK);
    }

    /** Restarts the current game with the same difficulty. */
    public void restartGame() {
        startNewGame();
    }

    /**
     * Submits a raw guess string from the UI input field.
     * Handles validation, delegates to model, and notifies views.
     *
     * @param rawInput the text from the UI input field
     */
    public void submitGuess(String rawInput) {
        Difficulty d = model.getDifficulty();
        ValidationResult vr = Validator.validateGuess(rawInput, d.getMinValue(), d.getMaxValue());

        if (!vr.valid) {
            for (ViewCallback v : views) v.onInputError(vr.errorMessage);
            SoundManager.getInstance().play(SoundManager.Sound.CLICK);
            return;
        }

        GuessResult result = model.submitGuess(vr.parsedValue);
        Logger.getInstance().debug("Guess " + vr.parsedValue + " → " + result);
    }

    /** Changes difficulty and starts a fresh game. */
    public void setDifficulty(Difficulty d) {
        model.setDifficulty(d);
    }

    public Difficulty getDifficulty()       { return model.getDifficulty(); }
    public int        getMaxAttempts()      { return model.getMaxAttempts(); }
    public int        getAttemptsUsed()     { return model.getAttemptsUsed(); }
    public int        getAttemptsRemaining(){ return model.attemptsRemaining(); }
    public boolean    isGameActive()        { return model.isGameActive(); }
    public double     getProgressFraction() { return model.progressFraction(); }

    // ── GameStateListener (from GameModel) ────────────────────────────────────

    @Override
    public void onGameStarted(Difficulty difficulty) {
        for (ViewCallback v : views) v.onGameStarted(difficulty);
    }

    @Override
    public void onTooLow(int guess, int attemptsLeft) {
        SoundManager.getInstance().play(SoundManager.Sound.TOO_LOW);
        for (ViewCallback v : views) v.onTooLow(guess, attemptsLeft);
    }

    @Override
    public void onTooHigh(int guess, int attemptsLeft) {
        SoundManager.getInstance().play(SoundManager.Sound.TOO_HIGH);
        for (ViewCallback v : views) v.onTooHigh(guess, attemptsLeft);
    }

    @Override
    public void onCorrectGuess(int score, int attempts, long elapsedSec) {
        swingTimer.stop();
        SoundManager.getInstance().play(SoundManager.Sound.WIN_FANFARE);

        // Save game record
        GameRecord record = new GameRecord(
            profile.getPlayer().getName(),
            model.getDifficulty(),
            model.getSecretNumber(),
            attempts,
            model.getMaxAttempts(),
            score,
            true,
            elapsedSec
        );
        profile.getPlayer().recordGame(record);
        profile.save();
        lb.recordGame(record);
        CSVExporter.appendHistory(record);

        for (ViewCallback v : views) v.onCorrectGuess(score, attempts, elapsedSec);
    }

    @Override
    public void onGameOver(int secretNumber) {
        swingTimer.stop();
        SoundManager.getInstance().play(SoundManager.Sound.GAME_OVER);

        // Save loss record
        GameRecord record = new GameRecord(
            profile.getPlayer().getName(),
            model.getDifficulty(),
            secretNumber,
            model.getAttemptsUsed(),
            model.getMaxAttempts(),
            0,
            false,
            model.getElapsedSeconds()
        );
        profile.getPlayer().recordGame(record);
        profile.save();
        CSVExporter.appendHistory(record);

        for (ViewCallback v : views) v.onGameOver(secretNumber);
    }

    @Override
    public void onHintRevealed(HintType type, String hintText) {
        for (ViewCallback v : views) v.onHintRevealed(type, hintText);
    }

    @Override
    public void onTimerTick(long elapsedSeconds) {
        for (ViewCallback v : views) v.onTimerTick(elapsedSeconds);
    }

    // ── View Callback Interface ───────────────────────────────────────────────

    /**
     * Implemented by UI panels to receive game events from the controller.
     * Extends GameStateListener with UI-specific events.
     */
    public interface ViewCallback {
        void onGameStarted(Difficulty difficulty);
        void onTooLow(int guess, int attemptsLeft);
        void onTooHigh(int guess, int attemptsLeft);
        void onCorrectGuess(int score, int attempts, long elapsedSec);
        void onGameOver(int secretNumber);
        void onHintRevealed(HintType type, String hintText);
        void onTimerTick(long elapsedSeconds);
        void onInputError(String message);
    }
}
