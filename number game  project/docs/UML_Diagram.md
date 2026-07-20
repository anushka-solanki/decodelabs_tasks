# UML Class Diagram — Smart Number Guessing Game Pro

> PlantUML notation. Paste at https://plantuml.com/plantuml to render.

```plantuml
@startuml

skinparam classAttributeIconSize 0
skinparam monochrome false
skinparam backgroundColor #0D1117
skinparam classBackgroundColor #1C2230
skinparam classBorderColor #30363D
skinparam classFontColor #E6EDF3
skinparam arrowColor #58A6FF

' ══════════════ ENUMS ══════════════

enum Difficulty {
  EASY
  MEDIUM
  HARD
  EXPERT
  +getDisplayName(): String
  +getMinValue(): int
  +getMaxValue(): int
  +getMaxAttempts(): int
  +getRangeLabel(): String
}

enum HintType {
  ODD_EVEN
  PRIME
  MULTIPLE_5
  RANGE_NARROW
  +getLabel(): String
  +getQuestion(): String
}

' ══════════════ MODEL ══════════════

class GameModel {
  -difficulty: Difficulty
  -secretNumber: int
  -attemptsUsed: int
  -gameActive: boolean
  -gameWon: boolean
  -startTimeMs: long
  -elapsedSeconds: long
  -currentScore: int
  +newGame(): void
  +submitGuess(int): GuessResult
  +tickTimer(): void
  +setDifficulty(Difficulty): void
  +setListener(GameStateListener): void
}

interface "GameModel.GameStateListener" {
  +onGameStarted(Difficulty)
  +onTooLow(int, int)
  +onTooHigh(int, int)
  +onCorrectGuess(int, int, long)
  +onGameOver(int)
  +onHintRevealed(HintType, String)
  +onTimerTick(long)
}

enum "GameModel.GuessResult" {
  TOO_LOW
  TOO_HIGH
  CORRECT
  GAME_OVER
  INVALID
}

class Player {
  -name: String
  -avatarEmoji: String
  -totalGamesPlayed: int
  -totalWins: int
  -bestScore: int
  -fastestWinSeconds: long
  +recordGame(GameRecord): void
  +getWinPercentage(): double
  +getAverageAttempts(): double
  +getAccuracyPercent(): double
}

class GameRecord {
  -playerName: String
  -difficulty: Difficulty
  -secretNumber: int
  -attempts: int
  -score: int
  -won: boolean
  -elapsedSeconds: long
  -date: LocalDate
  +toCsvRow(): String
}

class ScoreCalculator {
  {static} +calculate(int, int, long): int
  {static} +calculateLoss(): int
  {static} +accuracyPercent(int, int): double
}

' ══════════════ CONTROLLERS ══════════════

class GameController {
  -model: GameModel
  -swingTimer: Timer
  +getInstance(): GameController
  +startNewGame(): void
  +submitGuess(String): void
  +setDifficulty(Difficulty): void
  +addView(ViewCallback): void
}

interface "GameController.ViewCallback" {
  +onGameStarted(Difficulty)
  +onTooLow(int, int)
  +onTooHigh(int, int)
  +onCorrectGuess(int, int, long)
  +onGameOver(int)
  +onHintRevealed(HintType, String)
  +onTimerTick(long)
  +onInputError(String)
}

class ProfileController {
  -currentPlayer: Player
  +getInstance(): ProfileController
  +load(): void
  +save(): void
  +getPlayer(): Player
  +updateName(String): void
  +updateAvatar(String): void
}

class LeaderboardController {
  -entries: List<LeaderboardEntry>
  +getInstance(): LeaderboardController
  +recordGame(GameRecord): void
  +getEntries(): List<LeaderboardEntry>
  +getTopScore(): int
  +reload(): void
}

' ══════════════ UTILS ══════════════

class Constants {
  {static} +APP_NAME: String
  {static} +HISTORY_FILE: String
  {static} +HIGHSCORES_FILE: String
  {static} +PROFILE_FILE: String
}

class Logger {
  {static} +getInstance(): Logger
  +info(String): void
  +warn(String): void
  +error(String): void
}

class Validator {
  {static} +validateGuess(String, int, int): ValidationResult
  {static} +validatePlayerName(String): ValidationResult
}

class CSVExporter {
  {static} +appendHistory(GameRecord): void
  {static} +writeHighScores(List<String>): void
  {static} +readDataRows(String): List<String>
}

' ══════════════ SOUND ══════════════

class SoundManager {
  -soundEnabled: boolean
  +getInstance(): SoundManager
  +play(Sound): void
  +setSoundEnabled(boolean): void
}

' ══════════════ UI ══════════════

class MainWindow {
  -gamePanel: GamePanel
  -dashboardPanel: DashboardPanel
  -leaderboardPanel: LeaderboardPanel
  +showGame(): void
}

class GamePanel {
  -gc: GameController
  +onGameStarted(Difficulty)
  +onCorrectGuess(int, int, long)
  +onGameOver(int)
}

class ThemeManager {
  -currentTheme: Theme
  +getInstance(): ThemeManager
  +toggleTheme(): void
  +isDark(): boolean
  +accent(): Color
  +bgPrimary(): Color
}

' ══════════════ RELATIONSHIPS ══════════════

GameController --> GameModel : owns
GameController --> ProfileController : uses
GameController --> LeaderboardController : uses
GameController --> SoundManager : uses
GameController ..|> "GameModel.GameStateListener" : implements
GameModel --> Difficulty : uses
GameModel --> HintType : uses
GameModel --> ScoreCalculator : uses
GamePanel ..|> "GameController.ViewCallback" : implements
GamePanel --> GameController : registers with
GameController --> "GameController.ViewCallback" : notifies
ProfileController --> Player : manages
LeaderboardController --> GameRecord : indexes
Player --> GameRecord : records
MainWindow --> GamePanel : hosts
MainWindow --> ThemeManager : uses
ThemeManager --> MainWindow : notifies (Runnable)

@enduml
```
