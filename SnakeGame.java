import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

// --- 1. MODIFIED SnakeGame Class ---
/**
 * Main class for the Snake game.
 * Now uses BorderLayout to hold a ScorePanel and a GameBoard.
 */
public class SnakeGame extends JFrame {

    public SnakeGame() {
        // Set the layout for the main window
        setLayout(new BorderLayout()); // <-- NEW LAYOUT

        // 1. Create the score panel
        ScorePanel scorePanel = new ScorePanel();

        // 2. Create the game board and pass it a reference to the score panel
        GameBoard gameBoard = new GameBoard(scorePanel);

        // 3. Add the panels to the window
        add(scorePanel, BorderLayout.NORTH); // <-- ADDED panel to the TOP
        add(gameBoard, BorderLayout.CENTER); // <-- ADDED game board to the CENTER

        setTitle("Classic Snake Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack(); // Sizes the window to fit the panels
        setLocationRelativeTo(null);
        setVisible(true);

        // IMPORTANT: Request focus for the game board so it can receive key events
        gameBoard.requestFocusInWindow();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame ex = new SnakeGame();
            ex.setVisible(true);
        });
    }
}

// --- 2. NEW ScorePanel Class ---
/**
 * A new class that acts as a dedicated panel to display the score.
 */
class ScorePanel extends JPanel {

    private int score;
    private int highScore;
    private final int PANEL_HEIGHT = 40;
    private final int BOARD_WIDTH = 600; // Must match GameBoard's width

    public ScorePanel() {
        this.score = 0;
        this.highScore = 0;
        setBackground(new Color(30, 30, 30)); // Dark background for the panel
        setPreferredSize(new Dimension(BOARD_WIDTH, PANEL_HEIGHT));
    }

    /**
     * This method is called by the GameBoard to update the scores
     */
    public void updateScores(int score, int highScore) {
        this.score = score;
        this.highScore = highScore;
        repaint(); // Trigger a redraw of *this* panel
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        String scoreMsg = "Score: " + score;
        String highMsg = "High Score: " + highScore;

        g.setColor(Color.WHITE);
        g.setFont(new Font("Helvetica", Font.BOLD, 20));
        FontMetrics fm = g.getFontMetrics();

        // Draw Score on the left
        g.drawString(scoreMsg, 20, (PANEL_HEIGHT + fm.getAscent()) / 2 - 2);

        // Draw High Score on the right
        g.drawString(highMsg, BOARD_WIDTH - fm.stringWidth(highMsg) - 20, (PANEL_HEIGHT + fm.getAscent()) / 2 - 2);
    }
}


// --- 3. MODIFIED GameBoard Class ---
/**
 * The GameBoard class now focuses only on the game logic.
 * It no longer draws the score.
 */
class GameBoard extends JPanel implements ActionListener, KeyListener {

    // --- Game Constants ---
    private final int BOARD_WIDTH = 600;
    private final int BOARD_HEIGHT = 600;
    private final int DOT_SIZE = 20;
    private final int ALL_DOTS = (BOARD_WIDTH * BOARD_HEIGHT) / (DOT_SIZE * DOT_SIZE);
    private final int DELAY = 140;

    // --- Game State Variables ---
    private List<Point> snake;
    private Point food;
    private int score;
    private boolean inGame = true;
    private Timer timer;
    private Direction direction = Direction.RIGHT;
    private Direction lastMovedDirection = Direction.RIGHT;

    // --- High Score Variables ---
    private int highScore;
    private Preferences prefs;

    // --- NEW: Reference to the ScorePanel ---
    private ScorePanel scorePanel;

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    /**
     * MODIFIED Constructor: It now accepts the ScorePanel
     */
    public GameBoard(ScorePanel panel) {
        this.scorePanel = panel; // <-- Store the reference

        addKeyListener(this);
        setFocusable(true);
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));

        // Initialize preferences and load the high score
        prefs = Preferences.userNodeForPackage(GameBoard.class);
        highScore = prefs.getInt("snakeHighScore", 0);

        initGame();
    }

    /**
     * Initializes all game variables to start a new game.
     */
    private void initGame() {
        inGame = true;
        direction = Direction.RIGHT;
        lastMovedDirection = Direction.RIGHT;
        score = 0;

        // Create the initial snake
        snake = new ArrayList<>();
        snake.add(new Point(100, 100)); // Head
        snake.add(new Point(80, 100));
        snake.add(new Point(60, 100)); // Tail

        placeFood();

        // --- NEW: Update score panel on game start ---
        scorePanel.updateScores(score, highScore);

        // Start the game loop timer
        timer = new Timer(DELAY, this);
        timer.start();
    }

    // ... (placeFood and isSnakeOn methods are unchanged) ...
    private void placeFood() {
        int x, y;
        do {
            x = (int) (Math.random() * (BOARD_WIDTH / DOT_SIZE)) * DOT_SIZE;
            y = (int) (Math.random() * (BOARD_HEIGHT / DOT_SIZE)) * DOT_SIZE;
            food = new Point(x, y);
        } while (isSnakeOn(food)); 
    }
    private boolean isSnakeOn(Point p) {
        for (Point snakePart : snake) {
            if (snakePart.equals(p)) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        if (inGame) {
            // Draw the food
            g.setColor(Color.RED);
            g.fillOval(food.x, food.y, DOT_SIZE, DOT_SIZE);

            // Draw the snake
            for (int i = 0; i < snake.size(); i++) {
                Point p = snake.get(i);
                if (i == 0) {
                    g.setColor(Color.WHITE); // The head
                } else {
                    g.setColor(new Color(45, 180, 0)); // The body
                }
                g.fillRect(p.x, p.y, DOT_SIZE, DOT_SIZE);
            }
            
            // --- REMOVED: No longer draws score here ---
            // drawScore(g); 

        } else {
            // Game is over
            gameOver(g);
        }
    }

    // --- REMOVED: drawScore method is gone ---

    /**
     * MODIFIED: gameOver method is simplified
     */
    private void gameOver(Graphics g) {
        String msg = "Game Over";
        String restartMsg = "Press 'R' to Restart";
        String clearMsg = "Press 'C' to Clear High Score";

        g.setColor(Color.WHITE);
        g.setFont(new Font("Helvetica", Font.BOLD, 24));
        FontMetrics fm = g.getFontMetrics();

        // We only need to draw the game over messages
        g.drawString(msg, (BOARD_WIDTH - fm.stringWidth(msg)) / 2, BOARD_HEIGHT / 2 - 40);
        
        g.setFont(new Font("Helvetica", Font.PLAIN, 16));
        fm = g.getFontMetrics();
        g.drawString(restartMsg, (BOARD_WIDTH - fm.stringWidth(restartMsg)) / 2, BOARD_HEIGHT / 2);
        g.drawString(clearMsg, (BOARD_WIDTH - fm.stringWidth(clearMsg)) / 2, BOARD_HEIGHT / 2 + 30);
    }

    /**
     * MODIFIED: actionPerformed now updates the score panel
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            move();
            checkCollision();
            // --- NEW: Update the score panel on every game tick ---
            scorePanel.updateScores(score, highScore);
        }
        repaint(); // Repaint the *game board*
    }

    /**
     * Updates the snake's position based on the current direction.
     */
    private void move() {
        Point head = snake.get(0);
        Point newHead = new Point(head.x, head.y);

        switch (direction) {
            case UP:    newHead.y -= DOT_SIZE; break;
            case DOWN:  newHead.y += DOT_SIZE; break;
            case LEFT:  newHead.x -= DOT_SIZE; break;
            case RIGHT: newHead.x += DOT_SIZE; break;
        }

        snake.add(0, newHead);

        if (newHead.equals(food)) {
            score++;
            placeFood();
        } else {
            snake.remove(snake.size() - 1);
        }
        
        lastMovedDirection = direction;
    }

    /**
     * MODIFIED: checkCollision now updates score panel on high score
     */
    private void checkCollision() {
        Point head = snake.get(0);

        // Wall collision
        if (head.x < 0 || head.x >= BOARD_WIDTH || head.y < 0 || head.y >= BOARD_HEIGHT) {
            inGame = false;
        }

        // Self collision
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                inGame = false;
                break;
            }
        }

        if (!inGame) {
            timer.stop();

            if (score > highScore) {
                highScore = score;
                prefs.putInt("snakeHighScore", highScore);
                // --- NEW: Update panel if high score changes ---
                scorePanel.updateScores(score, highScore); 
            }
        }
    }

    /**
     * MODIFIED: keyPressed now updates score panel on reset
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // Directional controls
        if ((key == KeyEvent.VK_LEFT) && (lastMovedDirection != Direction.RIGHT)) {
            direction = Direction.LEFT;
        } else if ((key == KeyEvent.VK_RIGHT) && (lastMovedDirection != Direction.LEFT)) {
            direction = Direction.RIGHT;
        } else if ((key == KeyEvent.VK_UP) && (lastMovedDirection != Direction.DOWN)) {
            direction = Direction.UP;
        } else if ((key == KeyEvent.VK_DOWN) && (lastMovedDirection != Direction.UP)) {
            direction = Direction.DOWN;
        }

        // Restart game
        if ((key == KeyEvent.VK_R) && (!inGame)) {
            initGame();
        }

        // Clear high score
        if ((key == KeyEvent.VK_C) && (!inGame)) {
            highScore = 0;
            prefs.putInt("snakeHighScore", 0);
            // --- NEW: Update panel when score is cleared ---
            scorePanel.updateScores(score, highScore);
            repaint(); // Repaint game board to show "Game Over"
        }
    }

    // --- Unused KeyListener methods ---
    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}