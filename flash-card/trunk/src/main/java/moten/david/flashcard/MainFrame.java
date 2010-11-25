package moten.david.flashcard;

import java.awt.Toolkit;

import javax.swing.JFrame;


public class MainFrame extends JFrame {

    public static void main(String[] args) {
        MainFrame frame = new MainFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        frame.setLocation(0, 0);
        frame.getContentPane().add(
                new CardPanel(6,new Words(MainFrame.class
                        .getResourceAsStream("/words-translated.txt"))));
        frame.setVisible(true);
    }

}
