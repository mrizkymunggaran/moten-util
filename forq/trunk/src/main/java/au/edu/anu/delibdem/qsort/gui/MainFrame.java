package au.edu.anu.delibdem.qsort.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Stack;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UnsupportedLookAndFeelException;

import moten.david.util.event.Event;
import moten.david.util.event.EventManager;
import moten.david.util.event.EventManagerListener;
import moten.david.util.gui.swing.v1.SwingUtil;
import moten.david.util.math.Matrix;
import moten.david.util.math.MatrixProvider;
import moten.david.util.math.gui.JMatrix;
import au.edu.anu.delibdem.qsort.Data;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 7824719192824923010L;

	public MainFrame() {
		setSize(800, 600);

		getContentPane().setLayout(new BorderLayout());

		createMenuBar();

		MainPanel mainPanel = new MainPanel();
		getContentPane().add(mainPanel, BorderLayout.CENTER);

		createStatusBar();

		createExitListener();

		createFilterListener();

		createOpenObjectListener();

		createSetReferenceListener();

		createEditPreferencesListener();

		setIcon();
	}

	private void createEditPreferencesListener() {
		EventManager.getInstance().addListener(Events.PREFERENCES,
				new EventManagerListener() {
					@Override
					public void notify(Event event) {
						JDialog dialog = PreferencesDialog.getInstance();
						SwingUtil.centre(dialog);
						dialog.setVisible(true);
					}
				});
	}

	private void createSetReferenceListener() {
		EventManager.getInstance().addListener(Events.SET_REFERENCE,
				new EventManagerListener() {
					@Override
					public void notify(Event event) {
						Model.getInstance().setReference(
								(MatrixProvider) event.getObject());
						EventManager.getInstance().notify(
								new Event(event.getObject(),
										Events.REFERENCE_SET));
					}
				});
	}

	private void createOpenObjectListener() {
		final JFrame frame = this;
		EventManager.getInstance().addListener(Events.OPEN_OBJECT,
				new EventManagerListener() {
					@Override
					public void notify(Event event) {
						JDialog dialog = new JDialog(frame);
						dialog.setIconImage(LookAndFeel.getPrimaryIcon()
								.getImage());
						dialog.setTitle("Viewer");
						dialog.setSize(500, 500);
						JPanel panel = new JPanel();
						panel.setLayout(new GridLayout(1, 1));
						if (event.getObject() instanceof Matrix)
							panel.add(new JMatrix((Matrix) event.getObject(),
									true));
						dialog.add(panel);
						dialog.setModal(false);
						SwingUtil.centre(dialog);
						dialog.setVisible(true);
					}
				});
	}

	private void setIcon() {
		setIconImage(LookAndFeel.getPrimaryIcon().getImage());
	}

	private void createExitListener() {
		EventManager.getInstance().addListener(Events.APPLICATION_EXIT,
				new EventManagerListener() {

					public void notify(Event arg0) {
						System.exit(0);
					}
				});
	}

	private void createMenuBar() {
		setJMenuBar(MenuBars.getMain());
	}

	private void createStatusBar() {
		final JLabel status = new JLabel(" ");
		final Stack<String> messages = new Stack<String>();
		getContentPane().add(status, BorderLayout.PAGE_END);
		EventManager.getInstance().addListener(Events.STATUS,
				new EventManagerListener() {
					public void notify(Event event) {
						String message = (String) event.getObject();
						messages.push(message);
						status.setText(message);
					}
				});
		EventManager.getInstance().addListener(Events.STATUS_FINISHED,
				new EventManagerListener() {
					public void notify(Event event) {
						messages.pop();
						if (messages.size() > 0)
							status.setText(messages.lastElement());
						else
							status.setText(" ");
					}
				});

	}

	private void createFilterListener() {
		final JFrame frame = this;
		EventManager.getInstance().addListener(Events.FILTER,
				new EventManagerListener() {
					@Override
					public void notify(Event event) {
						JDialog dialog = new JDialog(frame);
						dialog.setIconImage(LookAndFeel.getPersonIcon()
								.getImage());
						dialog.setTitle("Participants");
						dialog.setSize(300, frame.getHeight() * 2 / 3);
						dialog.getContentPane().setLayout(new GridLayout(1, 1));
						dialog.getContentPane()
								.add(
										new ParticipantsPanel((Data) event
												.getObject()));
						dialog.setModal(false);
						int x = frame.getLocation().x + frame.getWidth() - 2
								* dialog.getWidth() - 50;
						dialog.setLocation(x, frame.getLocation().y + 150);
						dialog.setVisible(true);
					}
				});
	}

	public static void main(String[] args) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {
		LookAndFeel.setLookAndFeel();
		MainFrame frame = new MainFrame();
		frame.setTitle("ForQ");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SwingUtil.centre(frame);
		frame.setVisible(true);

	}
}
