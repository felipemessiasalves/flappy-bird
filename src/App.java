import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        int LarguraBorda = 360;
        int AlturaBorda = 640;

        JFrame janela = new JFrame("Flappy Bird");

        janela.setSize(LarguraBorda, AlturaBorda);
        janela.setLocationRelativeTo(null);
        janela.setResizable(false);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        FlappyBird flappyBird = new FlappyBird();
        janela.add(flappyBird);
        janela.pack();
        janela.setVisible(true);
    }
}
