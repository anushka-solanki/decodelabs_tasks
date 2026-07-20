import controller.GameController;
import controller.ProfileController;
import ui.MainWindow;
import ui.SplashScreen;
import ui.components.ThemeManager;
import utils.Logger;

import javax.swing.*;

/**
 * ╔═══════════════════════════════════════════════════════════════════╗
 * ║         Smart Number Guessing Game Pro  — Entry Point            ║
 * ║                                                                   ║
 * ║  Startup sequence:                                                ║
 * ║    1. Set Look & Feel to system LAF                               ║
 * ║    2. Load player profile (ProfileController singleton)           ║
 * ║    3. Show animated SplashScreen (3.2 s)                          ║
 * ║    4. Launch MainWindow on the Swing EDT                          ║
 * ║    5. Start the first game automatically                          ║
 * ╚═══════════════════════════════════════════════════════════════════╝
 */
public class Main {

    public static void main(String[] args) {

        // 1. System Look & Feel (best native rendering on Windows)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            Logger.getInstance().warn("Could not set system LAF: " + e.getMessage());
        }

        // 2. Apply anti-aliasing hints globally
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // 3. Boot on EDT
        SwingUtilities.invokeLater(() -> {

            Logger.getInstance().info("=== Smart Number Guessing Game Pro starting ===");

            // Pre-warm singletons off the EDT would be ideal, but for simplicity
            // we initialise them here; they are all lightweight.
            ProfileController.getInstance();   // loads profile.dat
            GameController.getInstance();      // wires model + timer
            ThemeManager.getInstance();        // initialises DARK theme

            // 4. Splash screen
            SplashScreen splash = new SplashScreen();
            splash.showAndAnimate(() -> {
                // 5. Main window
                MainWindow window = new MainWindow();
                window.setVisible(true);

                // Auto-start first game
                GameController.getInstance().startNewGame();

                Logger.getInstance().info("Main window displayed — game started.");
            });
        });
    }
}
