package snakegame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Board extends JPanel implements ActionListener {

    private Image appleImage, dotImage, headImage;

    private final int allDots = 900;
    private final int dotSize = 10;
    private final int randomPosition = 29;

    private final int[] applePosition = new int[2];

    private final int[] xAxis = new int[allDots];
    private final int[] yAxis = new int[allDots];

    //For now we are using the left, right, up, down variables
    //But later we will change it to using this array
    //private boolean[] directions = {false, true, false, false};

    private boolean leftDirection = false;
    //Initially we want the snake to move
    //towards the right direction only
    //When the game starts
    //That's why only right direction is true
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;

    private boolean isVisible = false;

    private boolean inGame = true;

    private int dots;

    private Timer timer, blinkingTimer;

    Board() {
        addKeyListener(new TAdapter());

        setBackground(new Color(0,0,0));
        setPreferredSize(new Dimension(310, 310));
        setFocusable(true);


        loadImages();
        initializeGame();

    }

    public void loadImages() {
        ImageIcon appleImageIcon = new ImageIcon(ClassLoader.getSystemResource("snakegame/icons/apple.png"));
        this.appleImage = appleImageIcon.getImage();

        ImageIcon dotImageIcon = new ImageIcon(ClassLoader.getSystemResource("snakegame/icons/dot.png"));
        this.dotImage = dotImageIcon.getImage();

        ImageIcon headImageIcon = new ImageIcon(ClassLoader.getSystemResource("snakegame/icons/head.png"));
        this.headImage = headImageIcon.getImage();
    }

    public void initializeGame() {
        this.dots = 3;

        for (int i = 0; i < dots; i++) {
            this.yAxis[i] = 50;
            this.xAxis[i] = 50 - (i * this.dotSize);
        }

        locateApple();

        this.timer = new Timer(140, this);
        this.timer.start();
    }

    public void locateApple() {
        int randomX = (int) (Math.random() * this.randomPosition);
        //X-Axis position of the apple
        this.applePosition[0] = randomX * this.dotSize;

        int randomY = (int) (Math.random() * this.randomPosition);
        //Y-Axis position of the apple
        this.applePosition[1] = randomY * this.dotSize;
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        draw(graphics);
    }

    public void draw(Graphics graphics) {

        if (inGame) {
            graphics.drawImage(appleImage, applePosition[0], applePosition[1], this);

            for (int i = 0; i < dots; i++) {
                if (i == 0) {
                    graphics.drawImage(headImage, xAxis[i], yAxis[i], this);
                } else {
                    graphics.drawImage(dotImage, xAxis[i], yAxis[i], this);
                }
            }

            Toolkit.getDefaultToolkit().sync();
        } else {
            gameOver(graphics);
        }
    }

    public void gameOver(Graphics graphics) {
        String message = "Game Over";

        Font customFont = new Font("Book Antiqua", Font.BOLD, 20);
        Color color = Color.WHITE;
        FontMetrics metrics = getFontMetrics(customFont);

        graphics.setFont(customFont);
        graphics.setColor(color);

        // Calculate horizontal and vertical center coordinates
        //Used Gemini here to enhance the placing of "Game Over" message
        int x = (graphics.getClipBounds().width - metrics.stringWidth(message)) / 2;
        int y = ((graphics.getClipBounds().height - metrics.getHeight()) / 2) + metrics.getAscent();

        // Draw the message at the calculated center coordinates
//        graphics.drawString(message, x, y);

        //Used Gemini to add the message as blinking
        //Instead of just keeping a static message

        if (blinkingTimer == null) {
            blinkingTimer = new Timer(500, e -> { // Timer with 500ms delay
                isVisible = !isVisible;
                repaint(); // Trigger repaint to redraw the message
            });
            blinkingTimer.start();
        }

        if (isVisible) {
            graphics.setFont(customFont);
            graphics.setColor(color);
            graphics.drawString(message, x, y);
        }
    }

    public void moveSnake() {
        for (int i = dots; i > 0; i--) {
            xAxis[i] = xAxis[i - 1];
            yAxis[i] = yAxis[i - 1];
        }

        if (leftDirection) {
            xAxis[0] -= this.dotSize;
        } else if (rightDirection) {
            xAxis[0] += this.dotSize;
        } else if (upDirection) {
            yAxis[0] -= this.dotSize;
        } else if (downDirection) {
            yAxis[0] += this.dotSize;
        }

//        xAxis[0] += dotSize;
        //We only want the snake to move
        //Left, right, up, and down
        //Not Diagonally
//        yAxis[0] += dotSize;
    }

    public void checkApple() {
        if ((xAxis[0] == applePosition[0]) && (yAxis[0] == applePosition[1])) {
            this.dots += 1;
            locateApple();
        }
    }

    public void checkCollision() {

        for (int i = dots; i > 0; i--) {
            if ((i > 4) && (xAxis[0] == xAxis[i]) && (yAxis[0] == yAxis[i])) {
                this.inGame = false;
            }
        }

        if (xAxis[0] >= 300) {
            this.inGame = false;
        } else if (yAxis[0] >= 300) {
            this.inGame = false;
        } else if (xAxis[0] < 0) {
            this.inGame = false;
        } else if (yAxis[0] < 0) {
            this.inGame = false;
        }

        if (!this.inGame) {
            this.timer.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (this.inGame) {
            checkApple();
            checkCollision();
            moveSnake();
        }

        repaint();
    }

    public class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent keyEvent) {
            int key = keyEvent.getKeyCode();

            if (key == KeyEvent.VK_LEFT && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            } else if (key == KeyEvent.VK_RIGHT && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            } else if (key == KeyEvent.VK_UP && (!downDirection)) {
                upDirection = true;
                leftDirection = false;
                rightDirection = false;
            } else if (key == KeyEvent.VK_DOWN && (!upDirection)) {
                downDirection = true;
                leftDirection = false;
                rightDirection = false;
            }
        }
    }

    public static void main(String[] args) {
        new Board();
    }
}
