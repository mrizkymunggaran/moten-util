package moten.david.util.swing;

import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import moten.david.markup.MainPanel;

public class PositionRememberingWindowListener implements WindowListener {

	private static Logger log = Logger
			.getLogger(PositionRememberingWindowListener.class.getName());
	private final String key;
	private final Window frame;

	public PositionRememberingWindowListener(Window frame, String key) {
		this.frame = frame;
		this.key = key;
	}

	@Override
	public void windowActivated(WindowEvent e) {

	}

	@Override
	public void windowClosed(WindowEvent e) {

	}

	@Override
	public void windowClosing(WindowEvent e) {
		Preferences p = Preferences.userNodeForPackage(MainPanel.class);
		p.put(key + ".width", frame.getWidth() + "");
		p.put(key + ".height", frame.getHeight() + "");
		p.put(key + ".left", frame.getLocation().x + "");
		p.put(key + ".top", frame.getLocation().y + "");
		try {
			p.flush();
		} catch (BackingStoreException e1) {
			log.warning(e1.getMessage());
		}

	}

	@Override
	public void windowDeactivated(WindowEvent e) {

	}

	@Override
	public void windowDeiconified(WindowEvent e) {

	}

	@Override
	public void windowIconified(WindowEvent e) {

	}

	@Override
	public void windowOpened(WindowEvent e) {
		Preferences p = Preferences.userNodeForPackage(MainPanel.class);
		int width = p.getInt(key + ".width", 500);
		int height = p.getInt(key + ".height", 500);
		int left = p.getInt(key + ".left", 200);
		int top = p.getInt(key + ".top", 200);
		frame.setSize(width, height);
		frame.setLocation(left, top);
	}

}
