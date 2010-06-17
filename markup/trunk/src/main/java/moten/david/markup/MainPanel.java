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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
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
import moten.david.markup.xml.study.And;
import moten.david.markup.xml.study.BasicType;
import moten.david.markup.xml.study.Document;
import moten.david.markup.xml.study.DocumentTag;
import moten.david.markup.xml.study.Expression;
import moten.david.markup.xml.study.LogicalTag;
import moten.david.markup.xml.study.ObjectFactory;
import moten.david.markup.xml.study.SimpleTag;
import moten.david.markup.xml.study.Study;
import moten.david.markup.xml.study.Tag;
import moten.david.markup.xml.study.TagReference;
import moten.david.util.controller.Controller;
import moten.david.util.controller.ControllerListener;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
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

    private final Study study;
    private Document document;

    private HashMap<Integer, Tag> tags = new HashMap<Integer, Tag>();

    private final CurrentStudy current;

    private boolean filterEnabled;

    private static final int borderWidth = 80;
    private static final int borderMarginLeft = 5;
    private static final int borderMarginRight = 5;
    private static final int textLeftMargin = 5;

    private final Presentation presentation;

    private boolean isVisible(int tagId) {
        Boolean result = presentation.visible.get(tagId);
        return (result != null) && result;
    }

    @Inject
    public MainPanel(final Controller controller, CurrentStudy current,
            Presentation presentation) {
        this.controller = controller;
        this.current = current;
        this.presentation = presentation;
        this.study = current.get();
        // initialize to the first document in the list

        loadTagMapAndColors(study.getTag());

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

            private static final long serialVersionUID = -4060072656452834570L;

            @Override
            public synchronized void paintBorder(Component c,
                    Graphics graphics, int x, int y, int width, int height) {
                super.paintBorder(c, graphics, x, y, width, height);

                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                controller.event(new StartingToPaintTags());
                paintLabels(g);
                g.setColor(Color.LIGHT_GRAY);
                g.drawLine(borderWidth, 0, borderWidth, text.getHeight());
                g.dispose();
            }

            private int[] getExtentY(Graphics2D g, DocumentTag dt) {
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
                List<DocumentTag> documentTags = getVirtualDocumentTags(document);
                for (DocumentTag documentTag : documentTags) {
                    Rectangle rStart = modelToView(documentTag.getStart());
                    Rectangle rEnd = modelToView(documentTag.getStart()
                            + documentTag.getLength());
                    g.setColor(new Color(presentation.colors.get(documentTag
                            .getId())));

                    Integer index = find(indexes, documentTag) - 1;
                    if (index == null)
                        throw new RuntimeException(
                                "index not found for docTag "
                                        + documentTag.getId());
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

            private Integer find(Map<DocumentTag, Integer> indexes,
                    DocumentTag documentTag) {
                for (DocumentTag dt : indexes.keySet())
                    if (equals(dt, documentTag))
                        return indexes.get(dt);
                return null;
            }

            private boolean equals(DocumentTag a, DocumentTag b) {
                return a.getStart() == b.getStart()
                        && a.getLength() == b.getLength()
                        && a.getId() == b.getId();
            }

            private Map<DocumentTag, Integer> getIndexes(Graphics2D g) {
                Map<DocumentTag, Integer> indexes = new HashMap<DocumentTag, Integer>();
                List<DocumentTag> list = sortByStart(g,
                        getVirtualDocumentTags(document));
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
                            for (DocumentTag documentTag : list) {
                                Integer tagIndex = indexes.get(documentTag);
                                if (tagIndex != null && tagIndex == i)
                                    if (intersect(g, dt, documentTag,
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

            private Insets createInsets() {
                return new Insets(0, 0, 0, 0);
            }

            private Insets updateInsets(Insets insets) {
                insets.right = insets.top = insets.bottom = 2;
                insets.left = borderWidth + textLeftMargin;
                return insets;
            }

            @Override
            public Insets getBorderInsets(Component c) {
                Insets insets = createInsets();
                return updateInsets(insets);
            }

            @Override
            public Insets getBorderInsets(Component c, Insets insets) {
                updateInsets(insets);
                return insets;
            }

        };
        return border;
    }

    private List<DocumentTag> getVirtualDocumentTags(Document document) {
        List<DocumentTag> documentTags = new ArrayList<DocumentTag>(document
                .getDocumentTag());
        documentTags.addAll(document.getDocumentTag());
        for (DocumentTag dt : getLogicalDocumentTags(document))
            if (!contains(documentTags, dt))
                documentTags.add(dt);
        return documentTags;
    }

    private Collection<? extends DocumentTag> getLogicalDocumentTags(
            Document document) {
        List<DocumentTag> list = new ArrayList<DocumentTag>();
        for (Tag tag : study.getTag()) {
            if (tag instanceof LogicalTag) {
                SortedSet<Interval> intervals = getMatches(document, tag);
                for (Interval interval : intervals) {
                    DocumentTag dt = createDocumentTag(tag, interval.start,
                            interval.length, true);
                    if (!contains(list, dt))
                        list.add(dt);
                }
            }
        }
        return list;
    }

    private boolean contains(List<DocumentTag> list, DocumentTag dt) {
        for (DocumentTag documentTag : list)
            if (dt.getId() == documentTag.getId()
                    && dt.getStart() == documentTag.getStart()
                    && dt.getLength() == documentTag.getLength())
                return true;
        return false;
    }

    private List<DocumentTag> filterDocumentTags(Document document, int tagId) {
        List<DocumentTag> list = new ArrayList<DocumentTag>();
        for (DocumentTag dt : document.getDocumentTag())
            if (dt.getId() == tagId)
                list.add(dt);
        return list;
    }

    private SortedSet<Interval> getMatches(Document document, Tag tag) {
        TreeSet<Interval> set = new TreeSet<Interval>();
        if (tag instanceof SimpleTag) {
            for (DocumentTag dt : filterDocumentTags(document, tag.getId())) {
                set.add(new Interval(dt.getStart(), dt.getLength()));
            }
        } else if (tag instanceof LogicalTag) {
            LogicalTag t = (LogicalTag) tag;
            Expression e = t.getExpression();
            set.addAll(getMatches(document, e));
        } else
            throw new RuntimeException("not implemented");
        return set;
    }

    private SortedSet<Interval> getMatches(Document document, Expression e) {
        if (e instanceof TagReference)
            return getMatches(document, tags.get(((TagReference) e).getId()));
        else if (e instanceof And) {
            SortedSet<Interval> a = getMatches(document, ((And) e)
                    .getExpression1());
            SortedSet<Interval> b = getMatches(document, ((And) e)
                    .getExpression2());
            return and(a, b);
        } else
            throw new RuntimeException("not implemented");
    }

    private JTextPane createTextPane() {
        return new JTextPane() {

            private static final long serialVersionUID = 8717199004213750328L;

            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                paintBoxes(g);
                super.paintComponent(graphics);
                g.dispose();
            }

            private boolean tagIsAt(DocumentTag dt, int position) {
                if (tags.get(dt.getId()) instanceof SimpleTag)
                    return position >= dt.getStart()
                            && position < dt.getStart() + dt.getLength();
                else if (tags.get(dt.getId()) instanceof LogicalTag) {
                    SortedSet<Interval> intervals = getMatches(document, tags
                            .get(dt.getId()));
                    for (Interval interval : intervals)
                        if (interval.start >= position
                                && interval.start + interval.length <= position)
                            return true;
                    return false;
                } else
                    throw new RuntimeException("unknown tag type: "
                            + tags.get(dt.getId()).getClass());
            }

            private void paintBoxes(Graphics2D g) {
                List<DocumentTag> documentTags = getVirtualDocumentTags(document);
                for (DocumentTag documentTag : documentTags) {
                    if (MainPanel.this.isVisible(documentTag.getId())) {
                        for (int i = 0; i <= documentTag.getLength() - 1; i++) {
                            int characterStart = documentTag.getStart() + i;

                            // count the number of tags visible at
                            // characterStart
                            int index = 0;
                            int count = 0;
                            for (DocumentTag dt : documentTags) {
                                if (MainPanel.this.isVisible(dt.getId()))
                                    if (tagIsAt(dt, characterStart)) {
                                        if (dt == documentTag)
                                            index = count;
                                        count++;
                                    }
                            }

                            // draw the slice of tag at characterStart
                            if (count > 0)
                                drawSlicePortionAtPosition(g, documentTag
                                        .getId(), characterStart, index, count);
                        }
                    }
                }
            }

            private void drawSlicePortionAtPosition(Graphics2D g, int tagId,
                    int characterStart, int index, int count) {
                Rectangle r = MainPanel.this.modelToView(characterStart);
                Rectangle r2 = MainPanel.this.modelToView(characterStart + 1);
                if (r2.x > r.x && r2.y == r.y) {
                    int step = r.height / count;
                    int stepHeight = step;
                    if (index == count - 1)
                        stepHeight = r.height - (count - 1) * step;
                    g.setColor(new Color(presentation.colors.get(tagId)));
                    g.fillRect(r.x, r.y + index * step, r2.x - r.x, stepHeight);
                }
            }
        };
    }

    protected static SortedSet<Interval> and(SortedSet<Interval> s1,
            SortedSet<Interval> s2) {
        if (s1.isEmpty() || s2.isEmpty())
            return Sets.newTreeSet();
        Iterator<Interval> it1 = s1.iterator();
        Iterator<Interval> it2 = s2.iterator();
        Interval a = it1.next();
        Interval b = it2.next();
        if (a.start + a.length < b.start) {
            SetView<Interval> s = Sets.difference(s1, Sets.newHashSet(a));
            return and(Sets.newTreeSet(s), s2);
        } else if (b.start + b.length < a.start) {
            SetView<Interval> s = Sets.difference(s2, Sets.newHashSet(b));
            return and(s1, Sets.newTreeSet(s));
        } else // must intersect
        {
            Interval interval = new Interval(Math.max(a.start, b.start), Math
                    .min(a.start + a.length, b.start + b.length)
                    - Math.max(a.start, b.start));
            if (interval.length == 0)
                return Sets.newTreeSet();
            else {
                SortedSet<Interval> set = and(Sets.newTreeSet(Sets.difference(
                        s1, Sets.newHashSet(a))), Sets.newTreeSet(Sets
                        .difference(s2, Sets.newHashSet(b))));
                return Sets.newTreeSet(Sets.union(set, Sets
                        .newHashSet(interval)));
            }
        }
    }

    private static class Interval implements Comparable<Interval> {
        int start;
        int length;

        public Interval(int start, int length) {
            super();
            this.start = start;
            this.length = length;
        }

        @Override
        public int compareTo(Interval x) {
            if (x.start == start)
                return ((Integer) length).compareTo(x.length);
            else
                return ((Integer) start).compareTo(x.start);
        }

        @Override
        public String toString() {
            return "[" + start + ", " + length + "]";
        }
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

    private void loadTagMapAndColors(List<Tag> list) {

        tags = new HashMap<Integer, Tag>();
        int index = 0;
        for (Tag tag : list) {
            log.info(tag.getName());
            tags.put(tag.getId(), tag);
            if (tag.getColor() != null)
                presentation.colors.put(tag.getId(), tag.getColor());
            else {
                float b = 1.0f;
                float s = 0.20f;
                float h = (float) index / (float) list.size();
                log.info(h + "," + s + "," + b);
                Color color = Color.getHSBColor(h, s, b);
                presentation.colors.put(tag.getId(), color.getRGB());
            }
            index++;
        }
    }

    private ControllerListener<SelectionModeChanged> createSelectionModeChangedListener() {
        return new ControllerListener<SelectionModeChanged>() {
            @Override
            public void event(SelectionModeChanged event) {
                presentation.selectionMode = event.getSelectionMode();
            }
        };
    }

    private ControllerListener<TagSelectionChanged> createTagSelectionChangedListener() {
        return new ControllerListener<TagSelectionChanged>() {

            @Override
            public void event(TagSelectionChanged event) {
                presentation.visible.clear();
                for (Tag tag : event.getList())
                    presentation.visible.put(tag.getId(), true);
                refresh();
            }
        };
    }

    private void refresh() {
        log.info("refreshing");
        log.info("visible=" + presentation.visible);
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
        ObjectFactory factory = new ObjectFactory();
        DocumentTag d;
        if (value instanceof Boolean) {
            moten.david.markup.xml.study.Boolean v = factory.createBoolean();
            v.setValue((Boolean) value);
            d = v;
        } else if (value instanceof String) {
            moten.david.markup.xml.study.Text v = factory.createText();
            v.setValue((String) value);
            d = v;
        } else if (value instanceof BigDecimal) {
            moten.david.markup.xml.study.Number v = factory.createNumber();
            v.setValue((BigDecimal) value);
            d = v;
        } else
            throw new RuntimeException("unknown tag value type:"
                    + (value != null ? value.getClass() : "") + "=" + value);
        d.setId(tag.getId());
        d.setStart(start);
        d.setLength(length);
        return d;
    }

    private MouseListener createTextMouseListener(List<Tag> tags) {
        final JPopupMenu popup = new JPopupMenu();
        for (final Tag t : tags)
            if (t instanceof SimpleTag) {
                final SimpleTag tag = (SimpleTag) t;
                JMenuItem code = new JMenuItem(tag.getName()
                        + (!tag.getType().equals(BasicType.BOOLEAN) ? "..."
                                : ""));
                popup.add(code);
                code.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Object value = true;
                        // if tag type is boolean then it is a theme that refers
                        // to
                        // the selection.
                        if (!tag.getType().equals(BasicType.BOOLEAN)) {
                            value = JOptionPane.showInputDialog(MainPanel.this,
                                    tag.getName(), "Tag value for "
                                            + tag.getName(),
                                    JOptionPane.PLAIN_MESSAGE, null, null, "");
                        }
                        int start = text.getSelectionStart();
                        int finish = text.getSelectionEnd();
                        StyledDocument doc = text.getStyledDocument();
                        Element element = doc.getParagraphElement(start);
                        Element element2 = doc.getParagraphElement(finish);

                        synchronized (document) {
                            // synchronize so we don't clash with a delete
                            // action
                            if (presentation.selectionMode
                                    .equals(SelectionMode.PARAGRAPH))
                                document.getDocumentTag().add(
                                        createDocumentTag(tag, element
                                                .getStartOffset(), element2
                                                .getEndOffset()
                                                - element.getStartOffset() + 1,
                                                value));
                            else if (presentation.selectionMode
                                    .equals(SelectionMode.SENTENCE)) {
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
                            String delimiter, int start, int finish,
                            String style) {
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
                            if (Character.isWhitespace(doc.getText(j, 1)
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