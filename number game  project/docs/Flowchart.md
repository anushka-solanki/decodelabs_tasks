# Flowchart — Smart Number Guessing Game Pro

## Application Startup Flow

```
┌─────────────────────┐
│    main() called    │
└────────┬────────────┘
         │
         ▼
┌─────────────────────┐
│ Set System LAF      │
│ Set AA properties   │
└────────┬────────────┘
         │
         ▼
┌─────────────────────┐
│ Load Player Profile │◄── profile.dat exists?
│ (ProfileController) │    YES: deserialize Player
└────────┬────────────┘    NO: create default Player
         │
         ▼
┌─────────────────────────────────┐
│ Show SplashScreen               │
│  · Gradient background          │
│  · Pulsing logo animation       │
│  · Progress bar 0→100%  (3.2s)  │
│  · Fade-out transition          │
└────────┬────────────────────────┘
         │
         ▼
┌─────────────────────┐
│  Launch MainWindow  │
│  · Build sidebar    │
│  · Build CardLayout │
│  · Register keys    │
│  · Show menu bar    │
└────────┬────────────┘
         │
         ▼
┌─────────────────────┐
│  startNewGame()     │
│  · Pick secret num  │
│  · Reset state      │
│  · Start timer      │
└────────┬────────────┘
         │
         ▼
┌═══════════════════════════════════════┐
║           GAME LOOP                   ║
╠═══════════════════════════════════════╣
║                                       ║
║  Player types a number  ────────────► ║
║                                        ║
║  ┌─────────────────────────────────┐  ║
║  │   Validate Input                │  ║
║  │   Is it a valid integer?        │  ║
║  │   Is it in [min, max]?          │  ║
║  └─────────┬──────────┬────────────┘  ║
║            │ INVALID  │ VALID         ║
║            ▼          ▼               ║
║     Show error    Submit to model     ║
║     Shake input                       ║
║                   │                   ║
║       ┌───────────┼───────────┐       ║
║       ▼           ▼           ▼       ║
║   TOO LOW     TOO HIGH    CORRECT     ║
║   Show ↑      Show ↓       🎉         ║
║   Play buzz   Play blip   Play fanfare║
║   Add to hist  Add hist    Confetti   ║
║   Tick attempt Tick attempt  Win!     ║
║       │           │                   ║
║       └─────┬─────┘                   ║
║             ▼                         ║
║   Attempts remaining > 0?             ║
║       YES ──── NO                     ║
║       │         │                     ║
║   Continue   Game Over                ║
║              Reveal secret            ║
║              Play game-over sfx       ║
║                                       ║
║   Every second: Timer tick            ║
║     Update elapsed display            ║
║     Hints revealed after 3 wrong      ║
║                                       ║
╚═══════════════════════════════════════╝
         │ (game ends)
         ▼
┌─────────────────────┐
│  Record GameRecord  │
│  Update Player stats│
│  Save profile.dat   │
│  Append history.csv │
│  Update leaderboard │
│  (if won)           │
└────────┬────────────┘
         │
         ▼
┌─────────────────────┐
│  Player can:        │
│  · New Game         │
│  · View Dashboard   │
│  · View Leaderboard │
│  · Settings         │
│  · Exit (confirm)   │
└─────────────────────┘
```

## Hint Revelation Logic

```
Wrong guesses counter ──► reaches 3?
                               │ YES
                               ▼
                    Reveal Hint #1: Odd/Even
                          │ wrong again
                               ▼
                    Reveal Hint #2: Prime Check
                          │ wrong again
                               ▼
                    Reveal Hint #3: Multiple of 5
                          │ wrong again
                               ▼
                    Reveal Hint #4: Narrowed Range
                          │ no more hints
                               ▼
                    (play without additional hints)
```

## Score Calculation Algorithm

```
INPUT: attemptsUsed, maxAttempts, elapsedSeconds

basePart  = max(0, (maxAttempts - attemptsUsed) × 15)
timeBonus = max(0, (60 - elapsedSeconds)) × 4
firstBonus= 200  IF attemptsUsed == 1  ELSE 0

score = basePart + timeBonus + firstBonus

OUTPUT: score (always ≥ 0)
```
