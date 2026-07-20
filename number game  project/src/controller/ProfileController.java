package controller;

import model.Player;
import utils.Constants;
import utils.Logger;
import java.io.*;
import java.nio.file.*;

/**
 * Manages loading and saving the player profile to disk using Java serialization.
 * Creates a default profile on first launch if none exists.
 */
public class ProfileController {

    private Player currentPlayer;

    // ── Singleton ─────────────────────────────────────────────────────────────

    private static ProfileController instance;
    public static ProfileController getInstance() {
        if (instance == null) instance = new ProfileController();
        return instance;
    }

    private ProfileController() {
        load();
    }

    // ── Load / Save ───────────────────────────────────────────────────────────

    /** Loads the player profile from disk, or creates a fresh one. */
    public void load() {
        Path path = Paths.get(Constants.PROFILE_FILE);
        if (!Files.exists(path)) {
            currentPlayer = new Player("Player");
            Logger.getInstance().info("No profile found — created default player.");
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(path.toFile()))) {
            currentPlayer = (Player) ois.readObject();
            Logger.getInstance().info("Profile loaded: " + currentPlayer.getName());
        } catch (Exception e) {
            Logger.getInstance().warn("Could not load profile, creating fresh: " + e.getMessage());
            currentPlayer = new Player("Player");
        }
    }

    /** Persists the current player profile to disk. */
    public void save() {
        Path path = Paths.get(Constants.PROFILE_FILE);
        try {
            Files.createDirectories(path.getParent());
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(path.toFile()))) {
                oos.writeObject(currentPlayer);
            }
            Logger.getInstance().info("Profile saved: " + currentPlayer.getName());
        } catch (IOException e) {
            Logger.getInstance().error("Failed to save profile", e);
        }
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public Player getPlayer() { return currentPlayer; }

    public void updateName(String name) {
        currentPlayer.setName(name);
        save();
    }

    public void updateAvatar(String emoji) {
        currentPlayer.setAvatarEmoji(emoji);
        save();
    }

    /** Returns true if this is the very first launch (default name not changed). */
    public boolean isFirstLaunch() {
        return currentPlayer.getName().equals("Player")
            && currentPlayer.getTotalGamesPlayed() == 0;
    }
}
