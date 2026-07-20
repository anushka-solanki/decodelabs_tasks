@echo off
echo Compiling ATM Management System...
javac *.java
if %errorlevel% neq 0 (
    echo Compilation failed! Please check your code.
    pause
    exit /b %errorlevel%
)
echo Compilation successful.
echo.
echo Starting ATM...
echo.
java Main
pause
