package moten.david.markup;

import java.awt.Color;
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
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.math.BigDecimal;
import java.util.ArrayList;
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

	private static final int stripeWidth = 8;
	private static final int stripesMarginLeft = 3;

	private Color getInvertedColor(int tagId) {
		int rgb = colors.get(tagId);
		return new Color(~rgb);
	}

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
		text = new JTextPane() {
			@Override
			protected void paintComponent(Graphics g) {
				synchronized (study) {
					super.paintComponent(g);

					Graphics2D g2d = (Graphics2D) g;
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
					controller.event(new StartingToPaintTags());
					try {
						for (DocumentTag documentTag : document
								.getDocumentTag()) {
							if (MainPanel.this.isVisible(documentTag.getId())) {
								for (int i = 0; i <= documentTag.getLength() - 1; i++) {
									int characterStart = documentTag.getStart()
											+ i;

									// count the number of tags visible at
									// characterStart
									int index = 0;
									int count = 0;
									for (DocumentTag dt : document
											.getDocumentTag()) {
										if (MainPanel.this
												.isVisible(dt.getId()))
											if (characterStart >= dt.getStart()
													&& characterStart < dt
															.getStart()
															+ dt.getLength()) {
												if (dt == documentTag)
													index = count;
												count++;
											}
									}

									// draw the slice of tag at characterStart
									Rectangle r = text
											.modelToView(characterStart);
									Rectangle r2 = text
											.modelToView(characterStart + 1);
									if (r2.x > r.x && r2.y == r.y) {
										g
												.setXORMode(getInvertedColor(documentTag
														.getId()));
										int step = r.height / count;
										int stepHeight = step;
										if (index == count - 1)
											stepHeight = r.height - (count - 1)
													* step;
										g.fillRect(r.x, r.y + index * step,
												r2.x - r.x, stepHeight);
									}
								}
							}
						}
						g.setPaintMode();
						g.setColor(Color.black);
						Map<DocumentTag, Line2D.Float> stripeVerticalBounds = new HashMap<DocumentTag, Line2D.Float>();
						for (DocumentTag documentTag : document
								.getDocumentTag()) {
							Rectangle rStart = text.modelToView(documentTag
									.getStart());
							Rectangle rEnd = text.modelToView(documentTag
									.getStart()
									+ documentTag.getLength());
							g.setColor(new Color(colors
									.get(documentTag.getId())));

							int index = getTagIndex(documentTag.getId());
							index = 2;

							int x = stripesMarginLeft + index * stripeWidth;
							int y = (rEnd.y + rEnd.height - rStart.y) / 2
									+ rStart.y;
							String name = tags.get(documentTag.getId())
									.getName();
							int stringWidth = g2d.getFontMetrics().stringWidth(
									name);
							int stringX = stripesMarginLeft + index
									* stripeWidth + stripeWidth;
							int stringY = y + g2d.getFontMetrics().getAscent()
									/ 2;
							Rectangle r = new Rectangle(x, rStart.y,
									stripeWidth, rEnd.y + rEnd.height
											- rStart.y);
							g2d.fillRect(r.x, r.y, r.width, r.height);
							AffineTransform transform = g2d.getTransform();
							AffineTransform at = new AffineTransform();
							Point rotationOrigin = new Point(stringX, stringY
									- g.getFontMetrics().getAscent() / 2);
							at.setToRotation(-Math.PI / 2.0, rotationOrigin.x,
									rotationOrigin.y);
							at.translate(-stringWidth / 2, 0);
							// at.translate(0, g.getFontMetrics().getDescent());
							g2d.setTransform(at);
							g2d.setFont(g2d.getFont().deriveFont(9f));
							g.setColor(Color.black);
							g2d.drawString(name, rotationOrigin.x,
									rotationOrigin.y);

							// revert the transform
							g2d.setTransform(transform);

							// use a point object to hold the minY and maxY for
							// the extents of the box with string
							Point extentsY = new Point(Math.min(r.y, stringY
									- stringWidth / 2), Math.max(
									r.y + r.height, stringY + stringWidth / 2));
							g2d.drawLine(r.x + r.width, extentsY.x, r.x
									+ r.width, extentsY.y);
						}

					} catch (BadLocationException e) {
						throw new RuntimeException(e);
					}
				}
			}

		};

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

		loadDocument(0);
		// text.getStyledDocument().addDocumentListener(createDocumentListener());
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
			text.setMargin(new Insets(2, study.getTag().size() * stripeWidth
					+ stripesMarginLeft, 2, 2));
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
			tags.put(tag.getId(), tag);
			if (tag.getColor() != null)
				colors.put(tag.getId(), tag.getColor());
			else {
				float b = 1.0f;
				float s = 0.2f;
				float h = (float) index / tags.size();
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