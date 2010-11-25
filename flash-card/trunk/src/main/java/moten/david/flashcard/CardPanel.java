package moten.david.flashcard;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JPanel;

public class CardPanel extends JPanel {

    public CardPanel(int numWords, final Words words) {
        setLayout(new GridLayout(1,1));
        
        final JButton button = new JButton("Hold this button to see meaning, release to move on");
        button.setFont(button.getFont().deriveFont(50f));
        add(button);
        button.addMouseListener(new MouseAdapter() {
            Iterator<Entry<String,String>> iterator = words.getMeanings().entrySet().iterator();
            Entry<String,String> current;
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (current!=null) button.setText(current.getKey() + " -> " + current.getValue());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (iterator.hasNext()) {
                    current = iterator.next();
                    button.setText(current.getKey());
                }
            }});
        
    }
}
