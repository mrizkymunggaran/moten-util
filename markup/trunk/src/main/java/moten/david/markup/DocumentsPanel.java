package moten.david.markup;

import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import moten.david.markup.events.DocumentSelectionChanged;
import moten.david.markup.xml.study.Document;
import moten.david.util.controller.Controller;

import com.google.inject.Inject;

public class DocumentsPanel extends JPanel {

    private static final long serialVersionUID = -7045040788786395194L;
    private final Controller controller;

    @Inject
    public DocumentsPanel(Controller controller, CurrentStudy study) {
        this.controller = controller;
        List<String> documents = new ArrayList<String>();
        for (Document document : study.get().getDocument()) {
            documents.add(document.getName());
        }
        JList list = new JList(documents.toArray());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setLayout(new GridLayout(1, 1));
        add(list);
        list.setSelectedIndex(0);
        list.addListSelectionListener(createListSelectionListener(list));
        list.addMouseListener(createMouseListener());
    }

    private MouseListener createMouseListener() {
        return new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseExited(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mousePressed(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // TODO Auto-generated method stub

            }
        };
    }

    private ListSelectionListener createListSelectionListener(final JList list) {
        return new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                controller.event(new DocumentSelectionChanged(list
                        .getSelectedIndex()));
            }

        };
    }
}
