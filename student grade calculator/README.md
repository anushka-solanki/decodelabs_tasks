# Student Grade Calculator

A professional, simple, and reliable Java desktop application for calculating student grades. Built using Java Swing and Object-Oriented Programming principles.

## Features

- **Dynamic Subject Entry**: Add, edit, or remove as many subjects as needed.
- **Automatic Calculations**: Calculates Total Marks, Percentage, Grade, and Pass/Fail status.
- **Smart Insights**: Automatically identifies best and worst performing subjects and provides a personalized performance message.
- **Automatic Result History**: Saves all calculated results locally to a `history.csv` file automatically.
- **Export to CSV**: Save individual calculated results to a specified local file.
- **Modern UI**: Attractive layout with Light and Dark mode themes.
- **Robust Validation**: Prevents invalid inputs (empty fields, negative marks, marks over 100).

## Technologies Used

- Java 8+ (Core Java)
- Java Swing (GUI)
- Object-Oriented Programming (OOP)

## Project Structure

- `src/com/gradecalculator/model`: Contains data structures and calculation logic.
- `src/com/gradecalculator/ui`: Contains the UI components and theme management.
- `src/com/gradecalculator/util`: Contains validation and file handling utilities.
- `src/com/gradecalculator/Main.java`: The application entry point.

## How to Run

1. Clone or download the repository.
2. Open the project in your preferred IDE (e.g., IntelliJ IDEA, Eclipse, VS Code).
3. Ensure you have the Java Development Kit (JDK) installed.
4. Run the `Main.java` class located in `src/com/gradecalculator/Main.java`.

## Future Enhancements
- Save records in a lightweight embedded database (like SQLite).
- Add functionality to export as PDF.
- Add bar chart visualization (could be implemented via `java.awt.Graphics`).
