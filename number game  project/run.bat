@echo off
REM =========================================================
REM  Smart Number Guessing Game Pro — Run Script
REM  Runs the game. Compiles first if out/ doesn't exist.
REM =========================================================

if not exist out (
    echo [INFO] No compiled classes found. Running compile.bat first...
    call compile.bat
    if %ERRORLEVEL% neq 0 (
        echo [ERROR] Compilation failed. Cannot start game.
        pause
        exit /b 1
    )
)

echo.
echo Starting Smart Number Guessing Game Pro...
echo.

java -cp out Main

if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERROR] Game crashed. Check data\game.log for details.
    pause
)
