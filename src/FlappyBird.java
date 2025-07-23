import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int LarguraBorda = 360;
    int AlturaBorda = 640;

    Image birdImage;
    Image backgroundImage;
    Image bottomPipeImage;
    Image topPipeImage;

    int birdX = LarguraBorda / 8;
    int birdY = AlturaBorda / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }
    }

    int PipeX = LarguraBorda;
    int PipeY = 0;
    int PipeWidth = 64;
    int PipeHeight = 512;

    class Pipe {
        int x = PipeX;
        int y = PipeY;
        int width = PipeWidth;
        int height = PipeHeight;
        Image img;
        boolean Passed = false;
        boolean isScorePipe = false;

        Pipe(Image img) {
            this.img = img;
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }
    }

    Bird bird;
    int VelocityX = -4;
    double VelocityY = 0;
    double gravity = 0.5;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipesTimer;

    int score = 0;
    boolean gameOver = false;

    FlappyBird() {
        setPreferredSize(new Dimension(LarguraBorda, AlturaBorda));
        setFocusable(true);
        addKeyListener(this);

        try {
          backgroundImage = new ImageIcon(getClass().getResource("/flappybirdbg.png")).getImage();
          birdImage = new ImageIcon(getClass().getResource("/flappybird.png")).getImage();
          topPipeImage = new ImageIcon(getClass().getResource("/toppipe.png")).getImage();
          bottomPipeImage = new ImageIcon(getClass().getResource("/bottompipe.png")).getImage();
        } catch (Exception e) {
          System.err.println("Erro ao carregar imagens: " + e.getMessage());
        }


        bird = new Bird(birdImage);
        pipes = new ArrayList<Pipe>();

        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void placePipes() {
        int randomPipeY = (int) (PipeY - PipeHeight / 4 - Math.random() * (PipeHeight / 2));
        int openingSpace = LarguraBorda / 4;

        Pipe topPipe = new Pipe(topPipeImage);
        topPipe.y = randomPipeY;
        topPipe.isScorePipe = true;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImage);
        bottomPipe.y = topPipe.y + PipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImage, 0, 0, LarguraBorda, AlturaBorda, null);

        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        g.setColor(new Color(0, 0, 0, 150));
        g.drawString(String.valueOf(score), LarguraBorda / 2 - 10 + 2, AlturaBorda / 10 + 2);
        g.setColor(Color.white);
        g.drawString(String.valueOf(score), LarguraBorda / 2 - 10, AlturaBorda / 10);


        if (gameOver) {
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            String gameOverText = "Game Over!";
            String restartText = "Pressione ESPAÇO para Reiniciar";
            FontMetrics fm = g.getFontMetrics();

            int x = (LarguraBorda - fm.stringWidth(gameOverText)) / 2;
            int y = AlturaBorda / 2 - fm.getHeight() / 2;
            g.drawString(gameOverText, x, y);

            g.setFont(new Font("Arial", Font.PLAIN, 24));
            fm = g.getFontMetrics();
            x = (LarguraBorda - fm.stringWidth(restartText)) / 2;
            y = AlturaBorda / 2 + fm.getHeight();
            g.drawString(restartText, x, y);
        }
    }

    public void move() {
        if (gameOver) {
            return;
        }

        VelocityY += gravity;
        bird.y += (int) VelocityY;
        bird.y = Math.max(bird.y, 0);

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += VelocityX;

            if (pipe.isScorePipe && !pipe.Passed && bird.x > pipe.x + pipe.width) {
                score++;
                pipe.Passed = true;
            }

            if (bird.getBounds().intersects(pipe.getBounds())) {
                gameOver = true;
            }
        }

        pipes.removeIf(pipe -> pipe.x + pipe.width < 0);

        if (bird.y + bird.height > AlturaBorda) {
            gameOver = true;
        }

        if (gameOver) {
            gameLoop.stop();
            placePipesTimer.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameOver) {
                resetGame();
            } else {
                VelocityY = -9.0;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    public void resetGame() {
        bird.y = birdY;
        VelocityY = 0;
        pipes.clear();
        score = 0;
        gameOver = false;

        placePipesTimer.start();
        gameLoop.start();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird");
        FlappyBird gamePanel = new FlappyBird();

        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
