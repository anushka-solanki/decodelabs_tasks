# Project Report — Smart Number Guessing Game Pro

**Version:** 1.0.0  
**Language:** Java 17+  
**UI Framework:** Java Swing  
**Architecture:** Model-View-Controller (MVC)  
**Developer:** Smart Games Inc.

---

## 1. Executive Summary

Smart Number Guessing Game Pro is a professional-grade Java desktop application that elevates
the classic number guessing game into a polished, feature-rich gaming experience. Built entirely
with the Java Standard Library (no external dependencies), the project demonstrates:

- Advanced Swing UI development with custom-painted components
- Clean MVC architecture with strict separation of concerns
- Object-oriented design using enums, interfaces, and the singleton pattern
- Persistent data management using Java serialization and CSV files
- Real-time UI feedback through the Observer pattern and Swing timers

---

## 2. System Architecture

### 2.1 MVC Pattern

```
┌────────────────┐     events      ┌───────────────────┐
│    VIEW        │ ◄─────────────  │   CONTROLLER       │
│  (ui package)  │                 │ (controller package)│
│                │ ──user actions─►│                     │
└────────────────┘                 └─────────┬───────────┘
                                             │
                                    delegates│
                                             ▼
                                   ┌─────────────────┐
                                   │     MODEL        │
                                   │ (model package)  │
                                   │  Pure Java, no   │
                                   │  Swing imports   │
                                   └─────────────────┘
```

### 2.2 Key Design Patterns

| Pattern | Usage |
|---|---|
| **Singleton** | ThemeManager, GameController, ProfileController, LeaderboardController, Logger, SoundManager |
| **Observer** | GameModel.GameStateListener → GameController → ViewCallback → GamePanel |
| **Strategy** | ScoreCalculator provides interchangeable scoring algorithm |
| **Factory** | ThemeManager provides Color/Font factory methods per theme |
| **Data Transfer Object** | GameRecord carries game result across layers |
| **Template Method** | RoundedButton extends JButton and overrides paintComponent |

---

## 3. Package Structure

| Package | Responsibility |
|---|---|
| `model` | Pure game logic, state, entities — zero Swing imports |
| `controller` | Orchestration between model and views, I/O, persistence |
| `ui` | All Swing panels, windows, dialogs |
| `ui.components` | Reusable custom Swing components (buttons, bars, charts) |
| `sound` | Sound effect synthesis via javax.sound.sampled |
| `utils` | Cross-cutting utilities: logging, validation, CSV, constants |

---

## 4. Feature Implementation Details

### 4.1 Game Engine (GameModel)

- Random number generated using `java.util.Random` within the difficulty range
- Each guess decrements the attempt counter and triggers hint logic after N wrong guesses
- A `GameStateListener` interface decouples the model from any UI dependency
- `switch` expression (Java 14+) used for clean hint text generation

### 4.2 Scoring Algorithm

```
score = (maxAttempts − attemptsUsed) × 15   // Efficiency bonus
      + max(0, 60 − elapsedSeconds)  × 4    // Time bonus
      + 200 if first guess correct           // Bonus
```

This rewards both few attempts and fast solving, making every second matter.

### 4.3 Hint System

Four hint types are revealed sequentially after 3 wrong guesses:
1. **Odd/Even** — parity check using `n % 2`
2. **Prime Check** — trial division up to √n
3. **Multiple of 5** — `n % 5 == 0`
4. **Narrowed Range** — dynamically calculated spread based on difficulty range size

### 4.4 Custom UI Components

**RoundedButton** — extends JButton, overrides `paintComponent` to:
- Paint a `RoundRectangle2D` gradient fill
- Darken/lighten on hover/press using HSB color arithmetic
- Draw a glow border on hover

**AnimatedProgressBar** — uses a 60 fps Swing Timer with ease-out interpolation:
```
displayValue += (targetValue - displayValue) × 0.12
```
Color shifts from green→orange→red as usage increases.

**GraphPanel** — custom bar chart with:
- Entrance animation (bars grow from 0 upward)
- Grid lines with Y-axis labels
- Gradient fill per bar, value label on top

**ConfettiPanel** — physics simulation:
- 120 particles with random initial position, velocity, rotation
- Each tick: `vy += 0.04` (gravity), `x += vx`, `rotation += rotSpeed`
- Alpha fade-out over last 500ms
- Self-removes from JLayeredPane when complete

### 4.5 Theme System

ThemeManager holds two complete palettes (12 color tokens each):
- **Dark (Deep Space):** primary `#0D1117`, accent `#58A6FF`
- **Light (Clean Slate):** primary `#F6F8FA`, accent `#0969DA`

A `List<Runnable>` change listener list allows any panel to react to theme toggle via `SwingUtilities.invokeLater`.

### 4.6 Sound Effects

All sounds are synthesized using PCM byte arrays and `javax.sound.sampled.Clip`:
- Sine wave generation: `amp × sin(2π × freq × i / sampleRate)`
- Frequency sweep for descending tones
- Major chord fanfare for win: C5→E5→G5→C6
- Played on a daemon thread pool to avoid blocking the EDT

### 4.7 Persistence

| Data | Format | Location |
|---|---|---|
| Player Profile | Java Serialization (ObjectOutputStream) | data/profile.dat |
| Game History | CSV (append-only) | data/history.csv |
| High Scores | CSV (overwrite sorted) | data/highscores.csv |
| Log | Plain text | data/game.log |

All files are auto-created on first run via `Files.createDirectories()`.

---

## 5. Testing Strategy

### 5.1 Unit-Test-Ready Design

- All model classes are pure Java → directly testable with JUnit without Swing
- `ScoreCalculator` and `Validator` are fully static → trivially testable
- `GameModel` accepts a mock `GameStateListener` for behavioral verification

### 5.2 Boundary Tests

| Test | Expected |
|---|---|
| Guess = secret number, attempt 1 | Score = 200 + base + time bonus |
| Guess outside range | ValidationResult.valid == false |
| Empty input | "Please enter a number." |
| All attempts exhausted | GAME_OVER result, sound plays |
| Profile with invalid name | Validation error, no save |

---

## 6. Future Enhancements

- Online multiplayer (compare guesses in real time via sockets)
- Daily challenge mode (same secret number for all players daily)
- Achievement / badge system
- Localization (multi-language UI)
- Android port via Kotlin Compose
- Web version via GWT or WASM

---

## 7. Conclusion

Smart Number Guessing Game Pro demonstrates production-quality Java Swing development,
showcasing MVC architecture, custom graphics, observer-based event handling, and robust
data persistence — all without external libraries, making it a truly self-contained,
portfolio-worthy Java project.
