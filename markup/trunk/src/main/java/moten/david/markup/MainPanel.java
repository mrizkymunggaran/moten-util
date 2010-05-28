package moten.david.markup;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import moten.david.util.controller.Controller;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * @author dave
 * 
 */
public class MainPanel extends JPanel {

	private final JTextPane text;
	private final Controller controller;
	private Style red;
	private Style blue;
	private List<String> tags;
	private SelectionMode selectionMode = SelectionMode.SENTENCE;

	@Inject
	public MainPanel(Controller controller) {
		this.controller = controller;
		setLayout(new GridLayout(1, 1));
		text = new JTextPane();
		JScrollPane scroll = new JScrollPane(text);
		add(scroll);
		init();

	}

	private enum SelectionMode {
		EXACT, WORD, SENTENCE, PARAGRAPH;
	}

	private MouseListener createTextMouseListener(List<String> tags) {
		final JPopupMenu popup = new JPopupMenu();
		for (final String tag : tags) {
			JMenuItem code = new JMenuItem(tag);
			popup.add(code);
			code.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int start = text.getSelectionStart();
					int finish = text.getSelectionEnd();
					StyledDocument doc = text.getStyledDocument();

					Element element = doc.getParagraphElement(start);
					Element element2 = doc.getParagraphElement(finish);

					SelectionMode selectionMode = SelectionMode.SENTENCE;
					if (selectionMode.equals(SelectionMode.PARAGRAPH))
						doc.setCharacterAttributes(element.getStartOffset(),
								element2.getEndOffset()
										- element.getStartOffset(), doc
										.getStyle(tag), false);
					else if (selectionMode.equals(SelectionMode.SENTENCE)) {
						int i = start;
						try {
							while (i >= element.getStartOffset()
									&& !".".equals(doc.getText(i, 1)))
								i--;
							if (".".equals(doc.getText(i, 1)))
								i++;
							int j = finish;
							while (j <= element2.getEndOffset()
									&& !".".equals(doc.getText(j, 1)))
								j++;
							if (".".equals(doc.getText(j, 1)))
								j--;
							doc.setCharacterAttributes(i, j - i + 1, doc
									.getStyle(tag), false);
						} catch (BadLocationException e1) {
							throw new RuntimeException(e1);
						}

					}
				}
			});
		}

		return new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
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

	private void init() {
		try {
			String s = IOUtils.toString(getClass().getResourceAsStream(
					"/example.txt"));
			text.setText(s);
			List<String> list = IOUtils.readLines(getClass()
					.getResourceAsStream("/tags.txt"));
			tags = new ArrayList<String>();
			StyledDocument doc = text.getStyledDocument();
			for (String line : list) {
				line = line.trim();
				if (line.length() > 0 && !line.startsWith("#")) {
					String[] items = line.split("\t");
					String name = items[0];
					Color colour = Color.decode("0x" + items[1]);
					tags.add(name);
					Style style = doc.addStyle(name, null);
					StyleConstants.setBackground(style, colour);
				}
			}
			text.addMouseListener(createTextMouseListener(tags));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		{
			JMenu menu = new JMenu("File", true);
			JMenuItem fileOpen = new JMenuItem("Open...");
			menu.add(fileOpen);
			menuBar.add(menu);
		}
		{
			JMenu menu = new JMenu("Selection", true);
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
						selectionMode = mode;
					}
				});
			}
			menuBar.add(menu);
		}

		return menuBar;
	}

	public static void main(String[] args) throws InterruptedException,
			InvocationTargetException, ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {
		UIManager
				.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		Injector injector = Guice.createInjector(new InjectorModule());
		final JFrame frame = new JFrame("Markup");
		final MainPanel panel = injector.getInstance(MainPanel.class);
		frame.setJMenuBar(panel.createMenuBar());
		frame.setLayout(new GridLayout(1, 1));
		frame.setSize(800, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(panel);

		SwingUtilities.invokeAndWait(new Runnable() {

			@Override
			public void run() {
				frame.setVisible(true);
				panel.requestFocus();
			}
		});

	}
}
