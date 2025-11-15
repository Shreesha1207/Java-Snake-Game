#  Classic Snake Game in Java

This is a complete, desktop version of the classic "Snake" game, built entirely in Java using the Swing library for the graphical interface.



It's a faithful recreation of the original, addictive game where the player controls a growing snake, eats food to score points, and must avoid hitting the walls or its own tail.

---

##  Features

* **Classic Gameplay:** Smooth, grid-based movement.
* **High Score System:** Your highest score is automatically saved and reloaded every time you play.
* **Score Reset:** You can clear the high score by pressing 'C' on the "Game Over" screen.
* **Clean UI:** A dedicated score panel at the top keeps the game board clean.
* **Responsive Controls:** Simple and intuitive keyboard input.

---

##  How to Play

* **Arrow Keys (↑, ↓, ←, →):** Control the snake's direction.
* **'R' Key:** Restart the game after a "Game Over."
* **'C' Key:** Clear the saved high score from the "Game Over" screen.

**Goal:** Eat the red food to grow your snake and increase your score. The game ends if you run into the walls or any part of your own snake.

---

##  Technologies Used

* **Language:** Java
* **GUI:** Java Swing (using `JFrame`, `JPanel`, and `Timer`)
* **Input:** `KeyListener` for keyboard controls.
* **Data Persistence:** `java.util.prefs.Preferences` API to save and load the high score.