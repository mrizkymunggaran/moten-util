package au.edu.anu.delibdem.qsort.gui;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import moten.david.util.gui.swing.v1.SwingUtil;

public class QuickFrame extends JFrame {

	private static final long serialVersionUID = -2482700386414560817L;

	public QuickFrame(JPanel panel) {
		setLayout(new GridLayout(1,1));
		add(panel);
		setSize(1000,600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SwingUtil.centre(this);
		setVisible(true);
	}
	
}
