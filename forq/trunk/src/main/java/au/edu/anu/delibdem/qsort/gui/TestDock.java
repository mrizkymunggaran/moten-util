package au.edu.anu.delibdem.qsort.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.defaults.DefaultDockingPort;

public class TestDock extends JPanel {

	private static final long serialVersionUID = 8285194161418746233L;

	public TestDock() {
		setLayout(new BorderLayout());
		DefaultDockingPort globalPort = createDockingPort();

		JComponent centerComponent = createDockableComponent("Center");
		DockingManager.dock(centerComponent, (DockingPort) globalPort);

		JComponent leftComponent = createDockableComponent("Left");
		DockingManager.dock(leftComponent, centerComponent,
				DockingConstants.WEST_REGION, 0.25f);

		JComponent moreLeftComponent = createDockableComponent("More Left");
		DockingManager.dock(moreLeftComponent, leftComponent,
				DockingConstants.WEST_REGION, 0.25f);

		JComponent northComponent = createDockableComponent("Top");
		DockingManager.dock(northComponent, centerComponent,
				DockingConstants.NORTH_REGION, 0.25f);

		JComponent rightComponent = createDockableComponent("Right");
		DockingManager.dock(rightComponent, centerComponent,
				DockingConstants.EAST_REGION, 0.25f);

		JComponent bottomComponent = createDockableComponent("Bottom");
		DockingManager.dock(bottomComponent, centerComponent,
				DockingConstants.SOUTH_REGION, 0.25f);

		add(globalPort, BorderLayout.CENTER);
	}

	private static DefaultDockingPort createDockingPort() {
		DefaultDockingPort port = new DefaultDockingPort();
		port.setPreferredSize(new Dimension(100, 100));
		return port;
	}

	private static JComponent createDockableComponent(String name) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createLineBorder(Color.blue));
		panel.add(new JLabel(name),BorderLayout.PAGE_START);
		return panel;
	}

	public static void main(String[] args) {
		new QuickFrame(new TestDock());
	}
}
