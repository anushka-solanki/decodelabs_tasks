package ui;

import ui.components.RoundedButton;
import ui.components.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Scrollable Help / User Guide dialog.
 * Shows keyboard shortcuts, game rules, hint system, and scoring guide.
 */
public class HelpDialog extends JDialog {

    public HelpDialog(JFrame owner) {
        super(owner, "Help & User Guide", true);
        setSize(620, 540);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        ThemeManager tm = ThemeManager.getInstance();
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(tm.bgSecondary());
        setContentPane(root);

        // ── Header ────────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(tm.bgCard());
        header.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel title = new JLabel("📖  Help & User Guide");
        title.setFont(ThemeManager.FONT_TITLE);
        title.setForeground(tm.accent());
        header.add(title, BorderLayout.WEST);

        root.add(header, BorderLayout.NORTH);

        // ── Content ───────────────────────────────────────────────────────────
        JTextPane text = new JTextPane();
        text.setContentType("text/html");
        text.setEditable(false);
        text.setBackground(tm.bgSecondary());
        text.setBorder(new EmptyBorder(16, 24, 16, 24));
        text.setText(buildHtmlContent(tm));

        JScrollPane scroll = new JScrollPane(text);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(tm.bgSecondary());
        root.add(scroll, BorderLayout.CENTER);

        // ── Footer ────────────────────────────────────────────────────────────
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
        footer.setBackground(tm.bgCard());
        RoundedButton closeBtn = new RoundedButton("Close", RoundedButton.Variant.PRIMARY);
        closeBtn.setPreferredSize(new Dimension(110, 36));
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn);
        root.add(footer, BorderLayout.SOUTH);
    }

    private String buildHtmlContent(ThemeManager tm) {
        String bg      = toHex(tm.bgSecondary());
        String fg      = toHex(tm.textPrimary());
        String muted   = toHex(tm.textMuted());
        String accent  = toHex(tm.accent());
        String success = toHex(tm.success());

        return """
            <html><body style='font-family:Segoe UI;font-size:13px;
                  color:%s;background:%s;margin:0;padding:0;'>

              <h3 style='color:%s;margin-top:0;'>🎮 How to Play</h3>
              <ol>
                <li>Select a <b>Difficulty</b> from the sidebar (Easy, Medium, Hard, Expert).</li>
                <li>Enter a number guess in the input field and press <b>Enter</b> or click <b>Guess</b>.</li>
                <li>The game tells you if the secret number is <b>Too High</b>, <b>Too Low</b>, or <b>Correct!</b></li>
                <li>You win by guessing correctly within the allowed attempts.</li>
                <li>Run out of attempts and the secret number is revealed.</li>
              </ol>

              <h3 style='color:%s;'>⚡ Difficulty Levels</h3>
              <table style='width:100%%;border-collapse:collapse;'>
                <tr style='background:%s;'>
                  <th align='left' style='padding:6px 10px;'>Level</th>
                  <th align='left' style='padding:6px 10px;'>Range</th>
                  <th align='left' style='padding:6px 10px;'>Max Attempts</th>
                </tr>
                <tr><td style='padding:5px 10px;'>Easy</td><td>1–50</td><td>15</td></tr>
                <tr><td style='padding:5px 10px;'>Medium</td><td>1–100</td><td>10</td></tr>
                <tr><td style='padding:5px 10px;'>Hard</td><td>1–500</td><td>12</td></tr>
                <tr><td style='padding:5px 10px;'>Expert</td><td>1–1,000</td><td>15</td></tr>
              </table>

              <h3 style='color:%s;'>💡 Hint System</h3>
              <p>After <b>3 wrong guesses</b>, hints are revealed one at a time:</p>
              <ul>
                <li><b>Hint 1:</b> Odd or Even</li>
                <li><b>Hint 2:</b> Prime or Not Prime</li>
                <li><b>Hint 3:</b> Multiple of 5 or not</li>
                <li><b>Hint 4:</b> Narrowed range (within ±spread)</li>
              </ul>

              <h3 style='color:%s;'>🏆 Scoring</h3>
              <ul>
                <li><b>Base Score:</b> (Max Attempts − Attempts Used) × 15</li>
                <li><b>Time Bonus:</b> max(0, 60 − seconds) × 4</li>
                <li><b>First Guess Bonus:</b> +200 points if you guess on attempt 1!</li>
              </ul>

              <h3 style='color:%s;'>⌨️ Keyboard Shortcuts</h3>
              <table style='width:100%%;border-collapse:collapse;'>
                <tr style='background:%s;'>
                  <th align='left' style='padding:6px 10px;'>Key</th>
                  <th align='left' style='padding:6px 10px;'>Action</th>
                </tr>
                <tr><td style='padding:5px 10px;'><b>Enter</b></td><td>Submit guess</td></tr>
                <tr><td style='padding:5px 10px;'><b>Ctrl+N</b></td><td>New game</td></tr>
                <tr><td style='padding:5px 10px;'><b>Ctrl+R</b></td><td>Restart game</td></tr>
                <tr><td style='padding:5px 10px;'><b>Ctrl+T</b></td><td>Toggle theme</td></tr>
                <tr><td style='padding:5px 10px;'><b>F1</b></td><td>Open this Help dialog</td></tr>
                <tr><td style='padding:5px 10px;'><b>Ctrl+Q</b></td><td>Quit application</td></tr>
              </table>

            </body></html>
            """.formatted(fg, bg, accent, accent, toHex(tm.bgCard()),
                          accent, accent, accent, accent, toHex(tm.bgCard()));
    }

    private String toHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }
}
