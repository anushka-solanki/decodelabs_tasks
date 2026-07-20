@echo off
REM =========================================================
REM  Smart Number Guessing Game Pro — Compile Script
REM  Compiles all Java source files in src/ into out/
REM =========================================================

echo Compiling Smart Number Guessing Game Pro...
echo.

REM Create output directory
if not exist out mkdir out
if not exist data mkdir data

REM Compile all sources (Java 17+)
javac -encoding UTF-8 ^
      -sourcepath src ^
      -d out ^
      src\Main.java ^
      src\utils\Constants.java ^
      src\utils\Logger.java ^
      src\utils\Validator.java ^
      src\utils\CSVExporter.java ^
      src\model\Difficulty.java ^
      src\model\HintType.java ^
      src\model\GameRecord.java ^
      src\model\Player.java ^
      src\model\ScoreCalculator.java ^
      src\model\GameModel.java ^
      src\sound\SoundManager.java ^
      src\ui\components\ThemeManager.java ^
      src\ui\components\RoundedButton.java ^
      src\ui\components\AnimatedProgressBar.java ^
      src\ui\components\GraphPanel.java ^
      src\controller\ProfileController.java ^
      src\controller\LeaderboardController.java ^
      src\controller\GameController.java ^
      src\ui\ConfettiPanel.java ^
      src\ui\HelpDialog.java ^
      src\ui\AboutDialog.java ^
      src\ui\SplashScreen.java ^
      src\ui\ProfilePanel.java ^
      src\ui\SettingsPanel.java ^
      src\ui\LeaderboardPanel.java ^
      src\ui\DashboardPanel.java ^
      src\ui\GamePanel.java ^
      src\ui\MainWindow.java

if %ERRORLEVEL% == 0 (
    echo.
    echo [SUCCESS] Compilation complete! Class files written to out\
    echo Run the game with:  run.bat
) else (
    echo.
    echo [ERROR] Compilation failed. Check the errors above.
)
pause
