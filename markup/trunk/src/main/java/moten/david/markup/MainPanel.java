package moten.david.markup;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import moten.david.markup.events.ClearTagsFromSelection;
import moten.david.markup.events.DocumentSelectionChanged;
import moten.david.markup.events.FilterChanged;
import moten.david.markup.events.SelectionModeChanged;
import moten.david.markup.events.StartingToPaintTags;
import moten.david.markup.events.TagSelectionChanged;
import moten.david.markup.events.TextTagged;
import moten.david.markup.xml.study.BasicType;
import moten.david.markup.xml.study.Document;
import moten.david.markup.xml.study.DocumentTag;
import moten.david.markup.xml.study.Study;
import moten.david.markup.xml.study.Tag;
import moten.david.util.controller.Controller;
import moten.david.util.controller.ControllerListener;

import com.google.inject.Inject;

/**
 * @author dave
 */
public class MainPanel extends JPanel {

    private static final int INTERSECT_TOLERANCE_PIXELS = 5;

    private static final long serialVersionUID = -8442599211410850234L;

    private static Logger log = Logger.getLogger(MainPanel.class.getName());

    private final JTextPane text;
    private final Controller controller;
    private SelectionMode selectionMode = SelectionMode.SENTENCE;
    private final Study study;
    private Document document;

    private final HashMap<Integer, Boolean> visible = new HashMap<Integer, Boolean>();
    private HashMap<Integer, Tag> tags = new HashMap<Integer, Tag>();
    private final HashMap<Integer, Integer> colors = new HashMap<Integer, Integer>();

    private final CurrentStudy current;

    private boolean filterEnabled;

    private static final int borderWidth = 80;
    private static final int borderMarginLeft = 5;
    private static final int borderMarginRight = 5;

    private boolean isVisible(int tagId) {
        Boolean result = visible.get(tagId);
        return (result != null) && result;
    }

    @Inject
    public MainPanel(final Controller controller, CurrentStudy current) {
        this.controller = controller;
        this.current = current;
        this.study = current.get();
        // initialize to the first document in the list

        loadTagMap(study.getTag());

        setLayout(new GridLayout(1, 1));
        text = createTextPane();
        text.setBorder(createBorder());
        text.setOpaque(false);
        text.setFont(text.getFont().deriveFont(12f));

        add(new JScrollPane(text));

        controller.addListener(TagSelectionChanged.class,
                createTagSelectionChangedListener());
        controller.addListener(SelectionModeChanged.class,
                createSelectionModeChangedListener());
        controller.addListener(FilterChanged.class,
                createFilterChangedListener());
        controller.addListener(DocumentSelectionChanged.class,
                createDocumentSelectionChangedListener());
        text.addMouseListener(createTextMouseListener(study.getTag()));
        text.addMouseMotionListener(createMouseMotionListener());

        loadDocument(0);
        // text.getStyledDocument().addDocumentListener(createDocumentListener());
    }

    private MouseMotionListener createMouseMotionListener() {
        return new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (e.getPoint().x < 50) {
                }
            }
        };
    }

    private Rectangle modelToView(int position) {
        try {
            return text.modelToView(position);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }

    private Border createBorder() {
        Border border = new AbstractBorder() {

            @Override
            public synchronized void paintBorder(Component c,
                    Graphics graphics, int x, int y, int width, int height) {
                super.paintBorder(c, graphics, x, y, width, height);

                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                controller.event(new StartingToPaintTags());
                paintLabels(g);
                g.dispose();
            }

            private int[] getExtentY(Graphics2D g, DocumentTag dt) {
                Font font = getRotationFont(g);
                String name = tags.get(dt.getId()).getName();
                int stringWidth = g.getFontMetrics().stringWidth(name);
                Rectangle rStart = modelToView(dt.getStart());
                Rectangle rEnd = modelToView(dt.getStart() + dt.getLength());
                // (top, bottom of extent of rectangle with centred
                // label drawn on it)
                int minY = Math.min(rStart.y, (rStart.y + rEnd.y + rEnd.height)
                        / 2 - stringWidth / 2);
                int maxY = Math
                        .max(rEnd.y + rEnd.height,
                                (rStart.y + rEnd.y + rEnd.height) / 2
                                        + stringWidth / 2);
                // Color c = g.getColor();
                // g.setColor(Color.black);
                // g.drawLine(0, minY, 50, maxY);
                return new int[] { minY, maxY };
            }

            private TextLayout getRotationTextLayout(Graphics2D g, String name) {
                Font rotatedFont = getRotationFont(g);
                // define a rotated font and layout for the tag name
                FontRenderContext frc = g.getFontRenderContext();
                TextLayout rotatedTextLayout = new TextLayout(name,
                        rotatedFont, frc);
                return rotatedTextLayout;
            }

            private Font getRotationFont(Graphics2D g) {
                // define the rotation transform
                AffineTransform rotation = new AffineTransform();
                rotation.setToRotation(-Math.PI / 2.0, 0, 0);
                Font rotatedFont = g.getFont().deriveFont(rotation);
                return rotatedFont;
            }

            private void paintLabels(Graphics2D g) {

                g.setColor(Color.black);
                Map<DocumentTag, Integer> indexes = getIndexes(g);
                int maxIndex = 0;
                if (indexes.size() > 0)
                    maxIndex = Collections.max(indexes.values());
                int numStripes = maxIndex + 1;
                int stripeWidth = (borderWidth - borderMarginLeft - borderMarginRight)
                        / numStripes;
                g.setFont(g.getFont().deriveFont(9f));
                for (DocumentTag documentTag : document.getDocumentTag()) {
                    Rectangle rStart = modelToView(documentTag.getStart());
                    Rectangle rEnd = modelToView(documentTag.getStart()
                            + documentTag.getLength());
                    g.setColor(new Color(colors.get(documentTag.getId())));

                    Integer index = indexes.get(documentTag);
                    int x = borderMarginLeft + index * stripeWidth;
                    int y = (rEnd.y + rEnd.height + rStart.y) / 2;
                    String name = tags.get(documentTag.getId()).getName();
                    int stringWidth = g.getFontMetrics().stringWidth(name);
                    int a = g.getFontMetrics().getAscent();
                    int d = g.getFontMetrics().getDescent();
                    int stringX = borderMarginLeft + index * stripeWidth
                            + stripeWidth / 2 + a - (a + d) / 2;
                    int stringY = y;
                    Rectangle r = new Rectangle(x, rStart.y, stripeWidth,
                            rEnd.y + rEnd.height - rStart.y);
                    g.fillRect(r.x, r.y, r.width, r.height);
                    g.setColor(Color.lightGray);
                    g.drawRect(r.x, r.y, r.width, r.height);
                    {
                        // save the current transform
                        AffineTransform saved = g.getTransform();

                        g.setColor(Color.BLACK);

                        // get the origin to draw a rotated string at
                        Point rotationOrigin = new Point(stringX, stringY);

                        // define the translation
                        AffineTransform translation = new AffineTransform();
                        translation.setToTranslation(0, stringWidth / 2);

                        // get the rotated text layout
                        TextLayout rotatedTextLayout = getRotationTextLayout(g,
                                name);

                        // draw the rotated string
                        rotatedTextLayout.draw(g, rotationOrigin.x,
                                rotationOrigin.y + stringWidth / 2);

                        // revert the transform
                        g.setTransform(saved);
                    }
                    g.setColor(Color.black);
                }
            }

            private Map<DocumentTag, Integer> getIndexes(Graphics2D g) {
                Map<DocumentTag, Integer> indexes = new HashMap<DocumentTag, Integer>();
                List<DocumentTag> list = sortByStart(g, document
                        .getDocumentTag());
                // find an index for each document tag
                for (DocumentTag dt : list) {
                    Integer newIndex = null;
                    // prepare maxIndex to have one added to it
                    int maxIndex = -1;
                    if (indexes.size() > 0) {
                        maxIndex = Collections.max(indexes.values());
                        // at each index position check if there is an intersect
                        // with dt
                        for (int i = 0; i <= maxIndex; i++) {
                            boolean intersects = false;
                            // look for an intersect on stripe i
                            for (DocumentTag tag : list) {
                                Integer tagIndex = indexes.get(tag);
                                if (tagIndex != null && tagIndex == i)
                                    if (intersect(g, dt, tag,
                                            INTERSECT_TOLERANCE_PIXELS))
                                        intersects = true;
                            }
                            // if no intersect on stripe i then assign the
                            // document tag to stripe i
                            if (!intersects && newIndex == null)
                                newIndex = i;
                        }
                    }
                    if (newIndex == null)
                        newIndex = maxIndex + 1;
                    indexes.put(dt, newIndex);
                }
                return indexes;
            }

            private boolean intersect(Graphics2D g, DocumentTag a,
                    DocumentTag b, int tolerance) {
                int[] extents = getExtentY(g, a);
                int aMin = extents[0];
                int aMax = extents[1];

                extents = getExtentY(g, b);
                int bMin = extents[0];
                int bMax = extents[1];
                return Util.intersect(aMin, aMax, bMin, bMax, tolerance);
            }

            private List<DocumentTag> sortByStart(final Graphics2D g,
                    List<DocumentTag> list) {
                ArrayList<DocumentTag> sorted = new ArrayList<DocumentTag>(list);
                Collections.sort(sorted, new Comparator<DocumentTag>() {

                    @Override
                    public int compare(DocumentTag a, DocumentTag b) {
                        int[] extents = getExtentY(g, a);
                        int minA = extents[0];
                        extents = getExtentY(g, b);
                        int minB = extents[0];
                        if (minA == minB)
                            // want deterministic result so compare on tag id if
                            // mins are equal
                            return ((Integer) a.getId()).compareTo(b.getId());
                        else
                            return ((Integer) minA).compareTo(minB);
                    }
                });
                return sorted;
            }

            @Override
            public Insets getBorderInsets(Component c) {
                Insets insets = new Insets(2, borderWidth, 2, 2);
                return insets;
            }

            @Override
            public Insets getBorderInsets(Component c, Insets insets) {
                insets.left = insets.top = insets.bottom = 2;
                insets.right = borderWidth;
                return insets;
            }

        };
        return border;
    }

    private JTextPane createTextPane() {
        return new JTextPane() {

            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                paintBoxes(g);
                super.paintComponent(graphics);
                g.dispose();
            }

            private void paintBoxes(Graphics2D g) {
                for (DocumentTag documentTag : document.getDocumentTag()) {
                    if (MainPanel.this.isVisible(documentTag.getId())) {
                        for (int i = 0; i <= documentTag.getLength() - 1; i++) {
                            int characterStart = documentTag.getStart() + i;

                            // count the number of tags visible at
                            // characterStart
                            int index = 0;
                            int count = 0;
                            for (DocumentTag dt : document.getDocumentTag()) {
                                if (MainPanel.this.isVisible(dt.getId()))
                                    if (characterStart >= dt.getStart()
                                            && characterStart < dt.getStart()
                                                    + dt.getLength()) {
                                        if (dt == documentTag)
                                            index = count;
                                        count++;
                                    }
                            }
                            // draw the slice of tag at characterStart
                            Rectangle r = MainPanel.this
                                    .modelToView(characterStart);
                            Rectangle r2 = MainPanel.this
                                    .modelToView(characterStart + 1);
                            if (r2.x > r.x && r2.y == r.y) {
                                // g.setXORMode(getInvertedColor(documentTag
                                // .getId()));
                                int step = r.height / count;
                                int stepHeight = step;
                                if (index == count - 1)
                                    stepHeight = r.height - (count - 1) * step;
                                g.setColor(new Color(colors.get(documentTag
                                        .getId())));
                                g.fillRect(r.x, r.y + index * step, r2.x - r.x,
                                        stepHeight);
                            }
                        }
                    }
                }
            }

        };
    }

    private int getTagIndex(int id) {
        int count = 0;
        for (Tag tag : study.getTag()) {
            if (tag.getId() == id)
                return count;
            count++;
        }
        return -1;
    }

    private ControllerListener<DocumentSelectionChanged> createDocumentSelectionChangedListener() {
        return new ControllerListener<DocumentSelectionChanged>() {
            @Override
            public void event(DocumentSelectionChanged event) {
                loadDocument(event.getIndex());
                refresh();
            }
        };
    }

    private void loadDocument(int i) {
        synchronized (study) {
            document = study.getDocument().get(i);
            String s = current.getText(document.getName());
            text.setDocument(text.getEditorKit().createDefaultDocument());
            text.setText(s);
            text.setSelectionStart(0);
            text.setSelectionEnd(0);
            repaint();
        }
    }

    private DocumentListener createDocumentListener() {
        return new DocumentListener() {

            @Override
            public void changedUpdate(DocumentEvent e) {

            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                synchronized (document) {
                    for (DocumentTag dt : document.getDocumentTag()) {
                        if (e.getOffset() >= dt.getStart()
                                && e.getOffset() <= dt.getStart()
                                        + dt.getLength()) {
                            dt.setLength(dt.getLength() + e.getLength());
                        }
                    }
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                synchronized (document) {
                    List<DocumentTag> removeThese = new ArrayList<DocumentTag>();
                    for (DocumentTag dt : document.getDocumentTag()) {
                        if (e.getOffset() > dt.getStart()
                                && e.getOffset() <= dt.getStart()
                                        + dt.getLength()) {
                            dt.setLength(dt.getLength() - e.getLength());
                        } else if (e.getOffset() < dt.getStart()
                                && e.getOffset() + e.getLength() < dt
                                        .getStart()
                                        + dt.getLength()) {

                            int newStart = e.getOffset() + e.getLength() + 1;
                            dt.setLength(dt.getLength()
                                    - (newStart - dt.getStart()));
                            dt.setStart(newStart);
                        } else if (e.getOffset() < dt.getStart()
                                && e.getOffset() + e.getLength() >= dt
                                        .getStart()
                                        + dt.getLength()) {
                            removeThese.add(dt);
                        }
                    }
                    document.getDocumentTag().removeAll(removeThese);
                }
            }
        };
    }

    private ControllerListener<FilterChanged> createFilterChangedListener() {
        return new ControllerListener<FilterChanged>() {

            @Override
            public void event(FilterChanged event) {
                MainPanel.this.filterEnabled = event.isEnabled();
                // text.setEnabled(!filterEnabled);
                refresh();
            }
        };
    }

    private void loadTagMap(List<Tag> list) {

        tags = new HashMap<Integer, Tag>();
        int index = 0;
        for (Tag tag : list) {
            log.info(tag.getName());
            tags.put(tag.getId(), tag);
            if (tag.getColor() != null)
                colors.put(tag.getId(), tag.getColor());
            else {
                float b = 1.0f;
                float s = 0.25f;
                float h = (float) index / (float) list.size();
                log.info(h + "," + s + "," + b);
                Color color = Color.getHSBColor(h, s, b);
                colors.put(tag.getId(), color.getRGB());
            }
            index++;
        }
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
                visible.clear();
                for (Tag tag : event.getList())
                    visible.put(tag.getId(), true);
                refresh();
            }
        };
    }

    private void refresh() {
        log.info("refreshing");
        log.info("visible=" + visible);
        StyledDocument doc = text.getStyledDocument();
        doc.setCharacterAttributes(0, doc.getLength(),
                SimpleAttributeSet.EMPTY, true);
        if (filterEnabled) {
            Style style = doc.addStyle("hide", null);
            StyleConstants.setFontSize(style, 0);
            doc.setCharacterAttributes(0, doc.getLength(), style, true);
            for (DocumentTag documentTag : document.getDocumentTag()) {
                if (isVisible(documentTag.getId())) {
                    doc.setCharacterAttributes(documentTag.getStart(),
                            documentTag.getLength(), SimpleAttributeSet.EMPTY,
                            true);
                }
            }
        }
        text.setStyledDocument(doc);
        repaint();
    }

    private static DocumentTag createDocumentTag(Tag tag, int start,
            int length, Object value) {
        DocumentTag d = new DocumentTag();
        d.setId(tag.getId());
        d.setStart(start);
        d.setLength(length);
        if (value instanceof Boolean)
            d.setBoolean((Boolean) value);
        else if (value instanceof String)
            d.setText((String) value);
        else if (value instanceof BigDecimal)
            d.setNumber((BigDecimal) value);
        else
            throw new RuntimeException("unknown tag value type:"
                    + (value != null ? value.getClass() : "") + "=" + value);
        return d;
    }

    private MouseListener createTextMouseListener(List<Tag> tags) {
        final JPopupMenu popup = new JPopupMenu();
        for (final Tag tag : tags) {
            JMenuItem code = new JMenuItem(tag.getName()
                    + (!tag.getType().equals(BasicType.BOOLEAN) ? "..." : ""));
            popup.add(code);
            code.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Object value = true;
                    // if tag type is boolean then it is a theme that refers to
                    // the selection.
                    if (!tag.getType().equals(BasicType.BOOLEAN)) {
                        value = JOptionPane.showInputDialog(MainPanel.this, tag
                                .getName(), "Tag value for " + tag.getName(),
                                JOptionPane.PLAIN_MESSAGE, null, null, "");
                    }
                    int start = text.getSelectionStart();
                    int finish = text.getSelectionEnd();
                    StyledDocument doc = text.getStyledDocument();
                    Element element = doc.getParagraphElement(start);
                    Element element2 = doc.getParagraphElement(finish);

                    synchronized (document) {
                        // synchronize so we don't clash with a delete action
                        if (selectionMode.equals(SelectionMode.PARAGRAPH))
                            document.getDocumentTag().add(
                                    createDocumentTag(tag, element
                                            .getStartOffset(), element2
                                            .getEndOffset()
                                            - element.getStartOffset() + 1,
                                            value));
                        else if (selectionMode.equals(SelectionMode.SENTENCE)) {
                            selectDelimitedBy(doc, ".", start, finish, tag
                                    .getName());
                        } else {
                            // exact
                            document.getDocumentTag().add(
                                    createDocumentTag(tag, start, finish
                                            - start + 1, value));
                        }
                    }
                    controller.event(new TextTagged(tag));
                    refresh();
                    save();
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
                        document.getDocumentTag().add(
                                createDocumentTag(tag, i, j - i + 1, true));
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
                    controller.event(new ClearTagsFromSelection(text
                            .getSelectionStart(), text.getSelectionEnd()
                            - text.getSelectionStart()));
                }
            });
        }

        controller.addListener(ClearTagsFromSelection.class,
                new ControllerListener<ClearTagsFromSelection>() {
                    @Override
                    public void event(ClearTagsFromSelection event) {
                        synchronized (document) {
                            List<DocumentTag> removeThese = new ArrayList<DocumentTag>();
                            for (DocumentTag dt : document.getDocumentTag()) {
                                if (dt.getStart() <= event.getPosition()
                                        && dt.getStart() + dt.getLength() > event
                                                .getPosition())
                                    removeThese.add(dt);
                            }
                            document.getDocumentTag().removeAll(removeThese);
                        }
                    }
                });

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

    private void save() {
        current.save();
    }

    @Override
    public void requestFocus() {
        super.requestFocus();
        text.requestFocus();
    }

}