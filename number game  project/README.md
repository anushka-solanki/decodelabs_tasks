# Smart Number Guessing Game Pro

> A professional, portfolio-quality Java Swing desktop application with MVC architecture,
> animations, sound effects, leaderboard, analytics dashboard, and more.

---

## 🎮 Features at a Glance

| Feature | Status |
|---|---|
| Dark / Light Theme Toggle | ✅ |
| 4 Difficulty Levels (Easy → Expert) | ✅ |
| Timer Challenge Mode | ✅ |
| Hint System (4 types) | ✅ |
| Score System + Time Bonus | ✅ |
| Animated Progress Bar | ✅ |
| Confetti on Win | ✅ |
| Guess History Sidebar | ✅ |
| Leaderboard (CSV-backed) | ✅ |
| Analytics Dashboard with Graph | ✅ |
| Player Profile + Avatar | ✅ |
| Sound Effects (synthesized) | ✅ |
| CSV Export of Game History | ✅ |
| Attractive Splash Screen | ✅ |
| Settings Panel | ✅ |
| Help / User Guide Dialog | ✅ |
| About Developer Dialog | ✅ |
| Keyboard Shortcuts | ✅ |
| Input Validation + Error Shake | ✅ |
| Auto-save High Scores | ✅ |
| MVC Architecture | ✅ |
| Singleton Controllers | ✅ |
| File-based Logging | ✅ |

---

## 📁 Project Structure

```
number game  project/
├── src/
│   ├── Main.java                          ← Entry point
│   ├── model/
│   │   ├── Difficulty.java                ← Enum: EASY / MEDIUM / HARD / EXPERT
│   │   ├── HintType.java                  ← Enum: 4 hint categories
│   │   ├── GameRecord.java                ← Immutable game session record
│   │   ├── Player.java                    ← Player profile + stats
│   │   ├── ScoreCalculator.java           ← Score formula
│   │   └── GameModel.java                 ← Core game logic (pure Java)
│   ├── controller/
│   │   ├── GameController.java            ← Main controller (model ↔ view)
│   │   ├── ProfileController.java         ← Profile load/save
│   │   └── LeaderboardController.java     ← Leaderboard load/save/sort
│   ├── ui/
│   │   ├── SplashScreen.java              ← Animated launch screen
│   │   ├── MainWindow.java                ← JFrame + sidebar navigation
│   │   ├── GamePanel.java                 ← Primary gameplay UI
│   │   ├── DashboardPanel.java            ← Analytics + bar chart
│   │   ├── LeaderboardPanel.java          ← Styled JTable leaderboard
│   │   ├── SettingsPanel.java             ← Theme / sound / difficulty
│   │   ├── ProfilePanel.java              ← Name + avatar setup
│   │   ├── HelpDialog.java                ← HTML help guide
│   │   ├── AboutDialog.java               ← Developer credits
│   │   ├── ConfettiPanel.java             ← Physics confetti overlay
│   │   └── components/
│   │       ├── ThemeManager.java          ← Palette + fonts + toggle
│   │       ├── RoundedButton.java         ← Custom gradient button
│   │       ├── AnimatedProgressBar.java   ← Smooth animated bar
│   │       └── GraphPanel.java            ← Score history chart
│   ├── sound/
│   │   └── SoundManager.java              ← Synthesized sound effects
│   └── utils/
│       ├── Constants.java                 ← App-wide constants
│       ├── Logger.java                    ← Singleton file logger
│       ├── Validator.java                 ← Input validation
│       └── CSVExporter.java               ← CSV read/write
├── data/                                  ← Auto-created at runtime
│   ├── highscores.csv
│   ├── history.csv
│   ├── profile.dat
│   └── game.log
├── out/                                   ← Compiled .class files
├── docs/
│   ├── ProjectReport.md
│   ├── UML_Diagram.md
│   ├── Flowchart.md
│   └── InstallationGuide.md
├── compile.bat                            ← Windows compile script
└── run.bat                                ← Windows run script
```

---

## 🚀 Quick Start

### Prerequisites
- **Java 17+** (JDK, not JRE)
- Windows OS (for .bat scripts) — on Linux/Mac use the javac/java commands directly

### Run on Windows
```bat
run.bat
```
This auto-compiles if needed, then launches the game.

### Manual Compile & Run
```bat
javac -encoding UTF-8 -sourcepath src -d out src\Main.java
java -cp out Main
```

---

## ⌨️ Keyboard Shortcuts

| Key | Action |
|---|---|
| `Enter` | Submit guess |
| `Ctrl+N` | New game |
| `Ctrl+R` | Restart current game |
| `Ctrl+T` | Toggle Dark/Light theme |
| `F1` | Open Help dialog |
| `Ctrl+Q` | Quit (with confirmation) |

---

## 🏆 Scoring System

```
score = (maxAttempts - attemptsUsed) × 15
      + max(0, 60 - elapsedSeconds) × 4
      + 200  (if guessed on first attempt!)
```

---

## 💡 Hint System

Hints are revealed automatically after **3 wrong guesses**, one at a time:
1. Odd or Even
2. Prime or Not Prime
3. Multiple of 5
4. Narrowed range (secret ± spread)

---

## 📊 Analytics Dashboard

Tracks per-session and lifetime stats:
- Games Played / Won / Lost
- Win Rate %
- Best Score (all-time)
- Fastest Win
- Average Attempts
- Accuracy %
- Score History Bar Chart

---

## 🎨 Theme System

Two built-in palettes:
- **Dark (Deep Space)** — default, deep blue-grey tones, blue accent `#58A6FF`
- **Light (Clean Slate)** — clean white, blue accent `#0969DA`

Toggle via `Ctrl+T`, the ⚙️ Settings panel, or the View menu.

---

## 📄 License

© 2026 Smart Games Inc. All Rights Reserved.

---

## 👨‍💻 Developer

**Smart Games Inc.**  
📧 support@smartgames.dev
