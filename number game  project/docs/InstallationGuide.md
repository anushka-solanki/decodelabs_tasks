# Installation Guide — Smart Number Guessing Game Pro

## System Requirements

| Requirement | Minimum | Recommended |
|---|---|---|
| Java Version | JDK 17 | JDK 21 |
| OS | Windows 10 | Windows 11 |
| RAM | 128 MB | 256 MB |
| Disk Space | 10 MB | 20 MB |
| Display | 1024×600 | 1280×800+ |

---

## Step 1 — Check Java Installation

Open PowerShell or Command Prompt and run:

```bat
java -version
```

Expected output (Java 17+):
```
java version "17.x.x" 2021-...
Java(TM) SE Runtime Environment
Java HotSpot(TM) 64-Bit Server VM
```

If Java is not installed, download JDK 21 from:
https://adoptium.net/temurin/releases/?version=21

---

## Step 2 — Download / Extract the Project

Place the `number game  project/` folder anywhere on your machine.
The recommended path is: `C:\Users\<YourName>\Desktop\number game  project\`

---

## Step 3 — Compile

Double-click `compile.bat` **or** run in Command Prompt:

```bat
cd "C:\Users\YourName\Desktop\number game  project"
compile.bat
```

Expected output:
```
Compiling Smart Number Guessing Game Pro...
[SUCCESS] Compilation complete! Class files written to out\
```

---

## Step 4 — Run the Game

Double-click `run.bat` **or** run:

```bat
run.bat
```

The splash screen will appear, then the main window opens automatically.

---

## Manual Compile & Run (without .bat files)

```bat
REM Navigate to project root
cd "C:\Users\YourName\Desktop\number game  project"

REM Create output directory
mkdir out
mkdir data

REM Compile
javac -encoding UTF-8 -sourcepath src -d out src\Main.java

REM Run
java -cp out Main
```

---

## Data Files

The `data/` folder is created automatically on first run. It contains:

| File | Description |
|---|---|
| `profile.dat` | Serialized player profile |
| `history.csv` | Full game history (append-only) |
| `highscores.csv` | Leaderboard entries |
| `game.log` | Application event log |

> **Tip:** Delete `data/profile.dat` to reset your player profile.
> Delete `data/highscores.csv` to clear the leaderboard.

---

## Troubleshooting

| Problem | Solution |
|---|---|
| `'javac' is not recognized` | Add JDK `bin` folder to your PATH environment variable |
| Blank screen / crash | Check `data/game.log` for stack trace |
| Sound not playing | Open ⚙️ Settings and ensure Sound is enabled |
| Profile not saving | Ensure the project folder is not read-only |
| Chinese/emoji not displaying | Use JDK 17+ on Windows; Segoe UI Emoji is included |

---

## GitHub-Ready Structure

To push to GitHub:

```bat
cd "C:\Users\YourName\Desktop\number game  project"
git init
echo out/ > .gitignore
echo data/ >> .gitignore
git add .
git commit -m "Initial commit: Smart Number Guessing Game Pro v1.0.0"
git remote add origin https://github.com/yourusername/number-game-pro.git
git push -u origin main
```

> Add a `.gitignore` to exclude `out/` (compiled classes) and `data/` (user data).
