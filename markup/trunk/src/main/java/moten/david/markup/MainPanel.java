package moten.david.markup;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
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
import moten.david.util.swing.PositionRememberingWindowListener;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.MultiSplitLayout;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * @author dave
 * 
 */
public class MainPanel extends JPanel {

	private static Logger log = Logger.getLogger(MainPanel.class.getName());

	private final JTextPane text;
	private final Controller controller;
	private final Tags tags;
	private SelectionMode selectionMode = SelectionMode.SENTENCE;
	private final Set<DocumentTag> documentTags = new HashSet<DocumentTag>();
	private Set<Tag> visibleTags = new HashSet<Tag>();

	@Inject
	public MainPanel(Controller controller, Tags tags, StripesPanel stripes) {
		this.controller = controller;
		this.tags = tags;
		setLayout(new GridLayout(1, 1));
		text = new JTextPane() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				try {
					for (DocumentTag documentTag : documentTags) {
						if (visibleTags.contains(documentTag.getTag())) {
							for (int i = 0; i <= documentTag.getLength() - 1; i++) {
								int characterStart = documentTag.getStart() + i;
								int index = 0;
								int count = 0;
								for (DocumentTag dt : documentTags) {
									if (visibleTags.contains(dt.getTag()))
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
				visibleTags = ImmutableSet.of(event.getList().toArray(
						new Tag[] {}));
				refresh();
			}
		};
	}

	private void refresh() {
		log.info("refreshing");
		StyledDocument doc = text.getStyledDocument();
		doc.setCharacterAttributes(0, doc.getLength(),
				SimpleAttributeSet.EMPTY, true);
		for (DocumentTag documentTag : documentTags) {
			if (visibleTags.contains(documentTag.getTag())) {
				Style style = doc
						.addStyle(documentTag.getTag().getName(), null);
				StyleConstants.setBackground(style, documentTag.getTag()
						.getColor());
				// doc.setCharacterAttributes(documentTag.getStart(),
				// documentTag
				// .getLength(), doc.getStyle(documentTag.getTag()
				// .getName()), true);
			}
		}
		text.setStyledDocument(doc);
	}

	private MouseListener createTextMouseListener(Tags tags) {
		final JPopupMenu popup = new JPopupMenu();
		for (final Tag tag : tags.get()) {
			JMenuItem code = new JMenuItem(tag.getName());
			popup.add(code);
			code.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int start = text.getSelectionStart();
					int finish = text.getSelectionEnd();
					StyledDocument doc = text.getStyledDocument();
					Element element = doc.getParagraphElement(start);
					Element element2 = doc.getParagraphElement(finish);

					if (selectionMode.equals(SelectionMode.PARAGRAPH))
						documentTags.add(new DocumentTag<Boolean>(tag, element
								.getStartOffset(), element2.getEndOffset()
								- element.getStartOffset() + 1, true));
					else if (selectionMode.equals(SelectionMode.SENTENCE)) {
						selectDelimitedBy(doc, ".", start, finish, tag
								.getName());
					} else {
						// exact
						documentTags.add(new DocumentTag<Boolean>(tag, start,
								finish - start + 1, true));
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
						documentTags.add(new DocumentTag<Boolean>(tag, i, j - i
								+ 1, true));
					} catch (BadLocationException e1) {
						throw new RuntimeException(e1);
					}
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

	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		{
			JMenu menu = new JMenu("File", true);
			menu.setMnemonic(KeyEvent.VK_F);
			{
				JMenuItem item = new JMenuItem("Open Study...");
				item.setMnemonic(KeyEvent.VK_O);
				item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
						ActionEvent.CTRL_MASK));
				menu.add(item);
			}
			{
				JMenuItem item = new JMenuItem("Add File...");
				item.setMnemonic(KeyEvent.VK_A);
				item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
						ActionEvent.CTRL_MASK));
				menu.add(item);
			}
			{
				JMenuItem item = new JMenuItem("Save");
				item.setMnemonic(KeyEvent.VK_S);
				item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
						ActionEvent.CTRL_MASK));
				menu.add(item);
			}
			{
				JMenuItem item = new JMenuItem("Save As...");
				menu.add(item);
			}
			{
				menu.addSeparator();
			}
			{
				JMenuItem item = new JMenuItem("Exit");
				item.setMnemonic(KeyEvent.VK_X);
				menu.add(item);
			}
			menuBar.add(menu);
		}
		{
			JMenu menu = new JMenu("Selection", true);
			menu.setMnemonic(KeyEvent.VK_S);
			ButtonGroup group = new ButtonGroup();
			for (final SelectionMode mode : SelectionMode.values()) {
				JRadioButtonMenuItem item = new JRadioButtonMenuItem(
						StringUtils.capitalize(mode.toString().toLowerCase()),
						mode.equals(selectionMode));
				menu.add(item);
				group.add(item);
				item.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						controller.event(new SelectionModeChanged(mode));
					}
				});
			}
			menuBar.add(menu);
		}
		{
			JMenu menu = new JMenu("Search", true);
			menu.setMnemonic(KeyEvent.VK_E);
			{
				JMenuItem item = new JMenuItem("Find...");
				item.setMnemonic(KeyEvent.VK_F);
				item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
						ActionEvent.CTRL_MASK));
				menu.add(item);
			}
			menuBar.add(menu);
		}

		return menuBar;
	}

	private JXMultiSplitPane createMultiSplitPane(JComponent panel,
			JComponent panel2) {

		JXMultiSplitPane msp = new JXMultiSplitPane();

		String layoutDef = "(ROW (LEAF name=left weight=0.25) (LEAF name=right weight=0.7))";

		MultiSplitLayout.Node modelRoot = MultiSplitLayout
				.parseModel(layoutDef);
		msp.getMultiSplitLayout().setModel(modelRoot);

		msp.add(panel, "left");
		panel2.setPreferredSize(new Dimension(200, 0));
		msp.add(panel2, "right");

		// ADDING A BORDER TO THE MULTISPLITPANE CAUSES ALL SORTS OF ISSUES
		msp.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		return msp;
	}

	private static void setLookAndFeel() {
		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		setLookAndFeel();
		Injector injector = Guice.createInjector(new InjectorModule());
		final JFrame frame = new JFrame("Markup");
		final TagsPanel tagsPanel = injector.getInstance(TagsPanel.class);
		final MainPanel panel = injector.getInstance(MainPanel.class);

		frame.setJMenuBar(panel.createMenuBar());
		frame.setLayout(new GridLayout(1, 1));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// tagsPanel.setMinimumSize(new Dimension(150, 0));
		frame.getContentPane()
				.add(panel.createMultiSplitPane(tagsPanel, panel));
		frame.addWindowListener(new PositionRememberingWindowListener(frame,
				"main"));
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				frame.setVisible(true);
				panel.requestFocus();
			}
		});

	}

}