package moten.david.markup;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Logger;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import moten.david.markup.events.SelectionModeChanged;
import moten.david.markup.events.TagSelectionChanged;
import moten.david.markup.events.TextTagged;
import moten.david.util.controller.Controller;
import moten.david.util.controller.ControllerListener;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

/**
 * @author dave
 */
public class MainPanel extends JPanel {

    private static Logger log = Logger.getLogger(MainPanel.class.getName());

    private final JTextPane text;
    private final Controller controller;
    private final Tags tags;
    private SelectionMode selectionMode = SelectionMode.SENTENCE;
    private final Document document = new Document("dummy", new DocumentTags());

    @Inject
    public MainPanel(Controller controller, Tags tags) {
        this.controller = controller;
        this.tags = tags;
        setLayout(new GridLayout(1, 1));
        text = new JTextPane() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    for (DocumentTag documentTag : document.getDocumentTags()
                            .getList()) {
                        if (document.getDocumentTags().isVisible(documentTag)) {
                            for (int i = 0; i <= documentTag.getLength() - 1; i++) {
                                int characterStart = documentTag.getStart() + i;
                                int index = 0;
                                int count = 0;
                                for (DocumentTag dt : document
                                        .getDocumentTags().getList()) {
                                    if (document.getDocumentTags()
                                            .isVisible(dt))
                                        if (characterStart >= dt.getStart()
                                                && characterStart < dt
                                                        .getStart()
                                                        + dt.getLength()) {
                                            if (dt == documentTag)
                                                index = count;
                                            count++;
                                        }
                                }
                                Rectangle r = text.modelToView(characterStart);
                                Rectangle r2 = text
                                        .modelToView(characterStart + 1);
                                if (r2.x > r.x && r2.y == r.y) {
                                    g.setXORMode(Color.decode(""
                                            + (~documentTag.getTag().getColor()
                                                    .getRGB())));
                                    int step = r.height / count;
                                    int stepHeight = step;
                                    if (index == count - 1)
                                        stepHeight = r.height - (count - 1)
                                                * step;
                                    g.fillRect(r.x, r.y + index * step, r2.x
                                            - r.x, stepHeight);
                                }
                            }
                        }
                    }

                } catch (BadLocationException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        loadText();
        add(new JScrollPane(text));

        controller.addListener(TagSelectionChanged.class,
                createTagSelectionChangedListener());
        controller.addListener(SelectionModeChanged.class,
                createSelectionModeChangedListener());

        text.addMouseListener(createTextMouseListener(tags));
    }

    private ControllerListener<SelectionModeChanged> createSelectionModeChangedListener() {
        return new ControllerListener<SelectionModeChanged>() {
            @Override
            public void event(SelectionModeChanged event) {
                selectionMode = event.getSelectionMode();
            }
        };
    }

    private ControllerListener<TagSelectionChanged> createTagSelectionChangedListener() {
        return new ControllerListener<TagSelectionChanged>() {

            @Override
            public void event(TagSelectionChanged event) {
                document.getDocumentTags().getVisible().clear();
                document.getDocumentTags().getVisible().addAll(
                        ImmutableSet.of(event.getList().toArray(new Tag[] {})));
                refresh();
            }
        };
    }

    private void refresh() {
        log.info("refreshing");
        StyledDocument doc = text.getStyledDocument();
        doc.setCharacterAttributes(0, doc.getLength(),
                SimpleAttributeSet.EMPTY, true);
        for (DocumentTag documentTag : document.getDocumentTags().getList()) {
            if (document.getDocumentTags().isVisible(documentTag)) {
                Style style = doc
                        .addStyle(documentTag.getTag().getName(), null);
                StyleConstants.setBackground(style, documentTag.getTag()
                        .getColor());
            }
        }
        text.setStyledDocument(doc);
    }

    private MouseListener createTextMouseListener(Tags tags) {
        final JPopupMenu popup = new JPopupMenu();
        for (final Tag tag : tags.get()) {
            JMenuItem code = new JMenuItem(tag.getName()
                    + (!tag.getType().equals(Boolean.class) ? "..." : ""));
            popup.add(code);
            code.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Object value = true;
                    // if tag type is boolean then it is a theme that refers to
                    // the selection.
                    if (!tag.getType().equals(Boolean.class)) {
                        value = JOptionPane.showInputDialog(MainPanel.this, tag
                                .getName(), "Tag value for "
                                + tag.getScope().toString().toLowerCase(),
                                JOptionPane.PLAIN_MESSAGE, null, null, "");
                    }
                    int start = text.getSelectionStart();
                    int finish = text.getSelectionEnd();
                    StyledDocument doc = text.getStyledDocument();
                    Element element = doc.getParagraphElement(start);
                    Element element2 = doc.getParagraphElement(finish);

                    if (selectionMode.equals(SelectionMode.PARAGRAPH))
                        document.getDocumentTags().getList().add(
                                new DocumentTag(tag, element.getStartOffset(),
                                        element2.getEndOffset()
                                                - element.getStartOffset() + 1,
                                        value));
                    else if (selectionMode.equals(SelectionMode.SENTENCE)) {
                        selectDelimitedBy(doc, ".", start, finish, tag
                                .getName());
                    } else {
                        // exact
                        document.getDocumentTags().getList().add(
                                new DocumentTag(tag, start, finish - start + 1,
                                        value));
                    }
                    controller.event(new TextTagged(tag));
                    refresh();
                }

                private void selectDelimitedBy(StyledDocument doc,
                        String delimiter, int start, int finish, String style) {
                    Element element = doc.getParagraphElement(start);
                    Element element2 = doc.getParagraphElement(finish);
                    int i = start;
                    try {
                        while (i > element.getStartOffset()
                                && !delimiter.equals(doc.getText(i, 1)))
                            i--;
                        while (delimiter.equals(doc.getText(i, 1))
                                || Character.isWhitespace(doc.getText(i, 1)
                                        .charAt(0)))
                            i++;
                        int j = finish;
                        while (j < element2.getEndOffset()
                                && !delimiter.equals(doc.getText(j, 1)))
                            j++;
                        if (delimiter.equals(doc.getText(j, 1))
                                || Character.isWhitespace(doc.getText(j, 1)
                                        .charAt(0)))
                            j--;
                        document.getDocumentTags().getList().add(
                                new DocumentTag<Boolean>(tag, i, j - i + 1,
                                        true));
                    } catch (BadLocationException e1) {
                        throw new RuntimeException(e1);
                    }
                }
            });
        }

        popup.addSeparator();
        {
            JMenuItem item = new JMenuItem("Clear tags from selection");
            popup.add(item);
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                }
            });
        }

        return new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (!e.isPopupTrigger()) {
                    StyledDocument doc = text.getStyledDocument();
                    int i = text.getSelectionStart();
                    Enumeration<?> names = doc.getCharacterElement(i)
                            .getAttributes().getAttributeNames();
                    while (names.hasMoreElements()) {
                        Object attribute = names.nextElement();
                        System.out.println(attribute.getClass().getName() + ":"
                                + attribute);
                    }
                    try {
                        Rectangle modelToView = text.modelToView(i);
                    } catch (BadLocationException e1) {
                        throw new RuntimeException(e1);
                    }
                }
                maybeShowPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }

        };
    }

    @Override
    public void requestFocus() {
        super.requestFocus();
        text.requestFocus();
    }

    private void loadText() {
        try {
            String s = FileUtils.readFileToString(new File(
                    "src/test/resources/study1/example.txt"));
            text.setText(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}