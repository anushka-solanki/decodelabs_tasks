package ui;

import controller.GameController;
import controller.ProfileController;
import ui.components.RoundedButton;
import ui.components.ThemeManager;
import utils.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Main application window hosting a sidebar navigation + CardLayout content area.
 *
 * Navigation tabs:
 *   🎮 Game        — GamePanel (default)
 *   📊 Dashboard   — DashboardPanel
 *   🏆 Leaderboard — LeaderboardPanel
 *   ⚙️ Settings    — SettingsPanel
 *   👤 Profile     — ProfilePanel
 *
 * Menu bar: File > New Game, Restart, Export CSV, Exit
 *           View > Dark/Light Mode
 *           Help > Help, About
 */
public class MainWindow extends JFrame {

    // ── Navigation constants ──────────────────────────────────────────────────
    private static final String CARD_GAME        = "GAME";
    private static final String CARD_DASHBOARD   = "DASHBOARD";
    private static final String CARD_LEADERBOARD = "LEADERBOARD";
    private static final String CARD_SETTINGS    = "SETTINGS";
    private static final String CARD_PROFILE     = "PROFILE";

    // ── UI references ─────────────────────────────────────────────────────────
    private final ThemeManager   tm = ThemeManager.getInstance();
    private final GameController gc = GameController.getInstance();

    private CardLayout      cardLayout;
    private JPanel          contentArea;
    private JLayeredPane    layeredPane;

    private GamePanel       gamePanel;
    private DashboardPanel  dashboardPanel;
    private LeaderboardPanel leaderboardPanel;

    private final JButton[] navButtons = new JButton[5];
    private String          currentCard = CARD_GAME;

    // ── Constructor ───────────────────────────────────────────────────────────

    public MainWindow() {
        setTitle(Constants.APP_NAME + " v" + Constants.APP_VERSION);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        buildLayeredPane();
        buildMenuBar();
        setContentPane(layeredPane);
        registerGlobalKeys();
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { confirmExit(); }
        });
        tm.addChangeListener(this::applyTheme);
    }

    // ── Layered pane (for confetti overlay) ───────────────────────────────────

    private void buildLayeredPane() {
        layeredPane = new JLayeredPane();
        layeredPane.setLayout(new OverlayLayout(layeredPane));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(tm.bgPrimary());

        mainPanel.add(buildSidebar(), BorderLayout.WEST);
        mainPanel.add(buildContent(), BorderLayout.CENTER);

        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, tm.bgSecondary(),
                    0, getHeight(), tm.bgCard()));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(tm.border());
                g2.drawLine(getWidth()-1, 0, getWidth()-1, getHeight());
                g2.dispose();
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setOpaque(false);

        // App logo / title
        JPanel logoArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        logoArea.setOpaque(false);
        logoArea.setBorder(new EmptyBorder(20, 12, 20, 12));
        JLabel icon = new JLabel("🎯");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        JLabel appTitle = new JLabel("<html><b>Number</b><br>Game Pro</html>");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        appTitle.setForeground(tm.accent());
        logoArea.add(icon);
        logoArea.add(appTitle);
        sidebar.add(logoArea);

        // Separator
        sidebar.add(separator());

        // Navigation buttons
        String[][] navItems = {
            {"🎮", "Game",        CARD_GAME},
            {"📊", "Dashboard",   CARD_DASHBOARD},
            {"🏆", "Leaderboard", CARD_LEADERBOARD},
            {"⚙️", "Settings",    CARD_SETTINGS},
            {"👤", "Profile",     CARD_PROFILE},
        };

        for (int i = 0; i < navItems.length; i++) {
            final String card  = navItems[i][2];
            final int    idx   = i;
            JButton btn = buildNavButton(navItems[i][0], navItems[i][1]);
            btn.addActionListener(e -> switchTo(card, idx));
            navButtons[i] = btn;
            sidebar.add(btn);
        }

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(separator());

        // Player info at bottom
        ProfileController pc = ProfileController.getInstance();
        JLabel playerInfo = new JLabel("  " + pc.getPlayer().toString());
        playerInfo.setFont(ThemeManager.FONT_SMALL);
        playerInfo.setForeground(tm.textMuted());
        playerInfo.setBorder(new EmptyBorder(12, 12, 12, 12));
        sidebar.add(playerInfo);

        // Set initial active state
        highlightNav(0);
        return sidebar;
    }

    private JButton buildNavButton(String emoji, String label) {
        JButton btn = new JButton(emoji + "  " + label) {
            @Override protected void paintComponent(Graphics g) {
                if (getClientProperty("active") == Boolean.TRUE) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(new Color(tm.accent().getRed(),
                        tm.accent().getGreen(), tm.accent().getBlue(), 40));
                    g2.fillRoundRect(4, 2, getWidth()-8, getHeight()-4, 10, 10);
                    // Left accent bar
                    g2.setColor(tm.accent());
                    g2.fillRect(0, 4, 4, getHeight()-8);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(tm.textPrimary());
        btn.setBackground(new Color(0, 0, 0, 0));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 16, 10, 16));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private Component separator() {
        JPanel sep = new JPanel();
        sep.setOpaque(false);
        sep.setPreferredSize(new Dimension(0, 1));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, tm.border()));
        return sep;
    }

    // ── Content Area (CardLayout) ─────────────────────────────────────────────

    private JPanel buildContent() {
        cardLayout  = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(tm.bgPrimary());

        gamePanel        = new GamePanel(layeredPane);
        dashboardPanel   = new DashboardPanel();
        leaderboardPanel = new LeaderboardPanel();

        contentArea.add(gamePanel,           CARD_GAME);
        contentArea.add(dashboardPanel,      CARD_DASHBOARD);
        contentArea.add(leaderboardPanel,    CARD_LEADERBOARD);
        contentArea.add(new SettingsPanel(), CARD_SETTINGS);
        contentArea.add(new ProfilePanel(),  CARD_PROFILE);

        return contentArea;
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    private void switchTo(String card, int navIdx) {
        currentCard = card;
        cardLayout.show(contentArea, card);
        highlightNav(navIdx);

        // Refresh data when visiting stat-heavy panels
        if (card.equals(CARD_DASHBOARD))   dashboardPanel.refreshStats();
        if (card.equals(CARD_LEADERBOARD)) leaderboardPanel.populateTable();
    }

    private void highlightNav(int activeIdx) {
        for (int i = 0; i < navButtons.length; i++) {
            boolean active = (i == activeIdx);
            navButtons[i].putClientProperty("active", active);
            navButtons[i].setFont(new Font("Segoe UI",
                active ? Font.BOLD : Font.PLAIN, 14));
            navButtons[i].setForeground(active ? tm.accent() : tm.textPrimary());
            navButtons[i].repaint();
        }
    }

    // ── Menu Bar ──────────────────────────────────────────────────────────────

    private void buildMenuBar() {
        JMenuBar bar = new JMenuBar();
        bar.setBackground(tm.bgCard());
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, tm.border()));

        // ── File ──────────────────────────────────────────────────────────────
        JMenu file = styledMenu("File");
        file.add(styledItem("🆕  New Game",    "ctrl N",  e -> gc.startNewGame()));
        file.add(styledItem("🔄  Restart",     "ctrl R",  e -> gc.restartGame()));
        file.addSeparator();
        file.add(styledItem("📤  Export CSV",  null,
            e -> JOptionPane.showMessageDialog(this,
                "History saved to: data/history.csv", "Export",
                JOptionPane.INFORMATION_MESSAGE)));
        file.addSeparator();
        file.add(styledItem("🚪  Exit",        "ctrl Q",  e -> confirmExit()));

        // ── View ──────────────────────────────────────────────────────────────
        JMenu view = styledMenu("View");
        view.add(styledItem("🌗  Toggle Theme", "ctrl T",
            e -> { tm.toggleTheme(); applyTheme(); }));

        // ── Help ──────────────────────────────────────────────────────────────
        JMenu help = styledMenu("Help");
        help.add(styledItem("📖  Help / Guide", "F1",
            e -> new HelpDialog(this).setVisible(true)));
        help.add(styledItem("ℹ️  About",         null,
            e -> new AboutDialog(this).setVisible(true)));

        bar.add(file);
        bar.add(view);
        bar.add(help);
        setJMenuBar(bar);
    }

    private JMenu styledMenu(String text) {
        JMenu m = new JMenu(text);
        m.setFont(ThemeManager.FONT_BODY);
        m.setForeground(tm.textPrimary());
        return m;
    }

    private JMenuItem styledItem(String text, String accel,
                                  java.awt.event.ActionListener action) {
        JMenuItem mi = new JMenuItem(text);
        mi.setFont(ThemeManager.FONT_BODY);
        mi.setForeground(tm.textPrimary());
        mi.setBackground(tm.bgCard());
        if (accel != null) mi.setAccelerator(KeyStroke.getKeyStroke(accel));
        if (action != null) mi.addActionListener(action);
        return mi;
    }

    // ── Global Key Bindings ───────────────────────────────────────────────────

    private void registerGlobalKeys() {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "help");
        getRootPane().getActionMap().put("help",
            new AbstractAction() {
                @Override public void actionPerformed(ActionEvent e) {
                    new HelpDialog(MainWindow.this).setVisible(true);
                }
            });

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK), "theme");
        getRootPane().getActionMap().put("theme",
            new AbstractAction() {
                @Override public void actionPerformed(ActionEvent e) {
                    tm.toggleTheme();
                    applyTheme();
                }
            });

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK), "quit");
        getRootPane().getActionMap().put("quit",
            new AbstractAction() {
                @Override public void actionPerformed(ActionEvent e) { confirmExit(); }
            });
    }

    // ── Exit Confirmation ─────────────────────────────────────────────────────

    private void confirmExit() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit the game?\nYour progress is saved automatically.",
            "Exit " + Constants.APP_NAME,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            ProfileController.getInstance().save();
            utils.Logger.getInstance().info("Application exited by user.");
            System.exit(0);
        }
    }

    // ── Theme Refresh ─────────────────────────────────────────────────────────

    private void applyTheme() {
        // Repaint all key containers
        getJMenuBar().setBackground(tm.bgCard());
        contentArea.setBackground(tm.bgPrimary());
        repaint();
        revalidate();
    }

    // ── Accessors for subpanels ───────────────────────────────────────────────

    public void showGame() { switchTo(CARD_GAME, 0); }
}
