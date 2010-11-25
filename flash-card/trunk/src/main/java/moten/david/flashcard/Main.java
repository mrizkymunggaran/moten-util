package moten.david.flashcard;

import java.awt.Toolkit;
import java.io.File;

import javax.swing.JFrame;

import com.google.api.translate.Language;


public class Main extends JFrame {

    public static void main(String[] args) {
        Main frame = new Main();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        frame.setLocation(0, 0);
        frame.getContentPane().add(
                new CardPanel(6,new Words(new File("src/main/resources/words-translated.txt"),Language.ENGLISH, Language.INDONESIAN)));
        frame.setVisible(true);
    }

}
