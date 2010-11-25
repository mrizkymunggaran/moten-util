package moten.david.flashcard;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JPanel;

public class CardPanel extends JPanel {

    public CardPanel(int numWords, final Words words) {
        setLayout(new GridLayout(1, 1));

        final JButton button = new JButton("Press to continue");
        button.setFont(button.getFont().deriveFont(50f));
        add(button);
        List<Entry<String, String>> entries = new ArrayList<Entry<String, String>>(
                words.getMeanings().entrySet());
        for (int i = entries.size(); i > 0 && i > numWords; i--) {
            Random random = new Random();
            int j = random.nextInt(entries.size());
            entries.remove(j);
        }
        System.out.println(entries);

        final List<Entry<String, String>> list = entries;
        Collections.shuffle(list);
        button.addMouseListener(new MouseAdapter() {

            Iterator<Entry<String, String>> iterator = list.iterator();
            Entry<String, String> current;

            @Override
            public void mousePressed(MouseEvent e) {
                if (current != null)
                    button.setText(current.getKey() + " -> "
                            + current.getValue());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!iterator.hasNext()) {
                    Collections.shuffle(list);
                    iterator = list.iterator();
                }
                current = iterator.next();
                button.setText(current.getKey());
            }
        });

    }
}
