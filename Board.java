import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

    // Define constants for the board dimensions, dot size, and game delay
    private final int B_WIDTH = 600;
    private final int B_HEIGHT = 600;
    private final int DOT_SIZE = 10;
    private final int ALL_DOTS = (B_WIDTH * B_HEIGHT) / (DOT_SIZE * DOT_SIZE);
    private final int RAND_POS = B_WIDTH / DOT_SIZE - 1;
    private final int DELAY = 140;

    // Arrays to store the x and y coordinates of the snake
    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];

    // Variables for the number of dots, apple position, and snake movement direction
    private int dots;
    private int apple_x;
    private int apple_y;

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;

    private Timer timer; // Timer to control game speed
    private Image ball;  // Image for the snake's body segments
    private Image apple; // Image for the apple
    private Image snake; // Image for the snake's head

    public Board() {
        initBoard(); // Initialize the game board
    }
    
    private void initBoard() {
        // Set up key listener, background color, and game properties
        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadImages(); // Load images for the game
        initGame();   // Initialize the game
    }

    private void loadImages() {
        // Load and resize images for the snake, apple, and ball
        ImageIcon iid = new ImageIcon("src/resources/dot.jpg");
        ball = resizeImage(iid.getImage(), DOT_SIZE, DOT_SIZE);

        ImageIcon iia = new ImageIcon("src/resources/apple.jpg");
        apple = resizeImage(iia.getImage(), DOT_SIZE, DOT_SIZE);

        ImageIcon iih = new ImageIcon("src/resources/snake.png");
        snake = resizeImage(iih.getImage(), DOT_SIZE, DOT_SIZE);
    }

    private Image resizeImage(Image img, int width, int height) {
        // Resize the given image to the specified width and height
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = resizedImage.createGraphics();
        g.drawImage(img, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    private void initGame() {
        // Initialize the snake and place the first apple
        dots = 3; // Initial number of dots in the snake

        for (int z = 0; z < dots; z++) {
            x[z] = 50 - z * DOT_SIZE; // Set initial x position
            y[z] = 50;                // Set initial y position
        }
        
        locateApple(); // Place the first apple

        timer = new Timer(DELAY, this); // Set up a timer for the game loop
        timer.start(); // Start the timer
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g); // Custom drawing method
    }
    
    private void doDrawing(Graphics g) {
        if (inGame) {
            // Draw the apple and the snake
            g.drawImage(apple, apple_x, apple_y, this);
            for (int z = 0; z < dots; z++) {
                if (z == 0) {
                    g.drawImage(snake, x[z], y[z], this); // Draw the snake's head
                } else {
                    g.drawImage(ball, x[z], y[z], this); // Draw the snake's body
                }
            }
            Toolkit.getDefaultToolkit().sync(); // Sync the drawing to the screen
        } else {
            gameOver(g); // Show game over message
        }        
    }

    private void gameOver(Graphics g) {
        // Display "Game Over" message
        String msg = "Game Over";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
    }

    private void checkApple() {
        // Check if the snake has eaten the apple
        if ((x[0] == apple_x) && (y[0] == apple_y)) {
            dots++; // Increase the length of the snake
            locateApple(); // Place a new apple
        }
    }

    private void move() {
        // Move the snake by updating its body segments
        for (int z = dots; z > 0; z--) {
            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];
        }

        if (leftDirection) {
            x[0] -= DOT_SIZE; // Move left
        }

        if (rightDirection) {
            x[0] += DOT_SIZE; // Move right
        }

        if (upDirection) {
            y[0] -= DOT_SIZE; // Move up
        }

        if (downDirection) {
            y[0] += DOT_SIZE; // Move down
        }
    }

    private void checkCollision() {
        // Check for collisions with itself or the walls
        for (int z = dots; z > 0; z--) {
            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false; // End the game if colliding with itself
            }
        }

        if (y[0] >= B_HEIGHT || y[0] < 0 || x[0] >= B_WIDTH || x[0] < 0) {
            inGame = false; // End the game if colliding with walls
        }
        
        if (!inGame) {
            timer.stop(); // Stop the game timer
        }
    }

    private void locateApple() {
        // Place a new apple at a random location
        int r = (int) (Math.random() * RAND_POS);
        apple_x = ((r * DOT_SIZE));

        r = (int) (Math.random() * RAND_POS);
        apple_y = ((r * DOT_SIZE));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            checkApple();   // Check if the snake has eaten the apple
            checkCollision(); // Check for collisions
            move(); // Move the snake
        }

        repaint(); // Repaint the board
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            // Change direction based on key pressed
            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }
    }
}
