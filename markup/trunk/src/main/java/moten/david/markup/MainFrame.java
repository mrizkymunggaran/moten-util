package moten.david.markup;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import moten.david.markup.events.SelectionModeChanged;
import moten.david.util.controller.Controller;
import moten.david.util.swing.PositionRememberingWindowListener;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.MultiSplitLayout;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class MainFrame extends JFrame {

    private final Controller controller;
    private final MainPanel mainPanel;

    @Inject
    public MainFrame(MainPanel mainPanel, TagsPanel tagsPanel,
            Controller controller) {
        super("Markup");
        this.mainPanel = mainPanel;
        this.controller = controller;

        setJMenuBar(createMenuBar());
        setLayout(new GridLayout(1, 1));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // tagsPanel.setMinimumSize(new Dimension(150, 0));
        getContentPane().add(createMultiSplitPane(tagsPanel, mainPanel));
        addWindowListener(new PositionRememberingWindowListener(this, "main"));
    }

    private static void setLookAndFeel() {
        try {
            if (true)
                UIManager
                        .setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
            else
                UIManager.setLookAndFeel(UIManager
                        .getSystemLookAndFeelClassName());
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
                        mode.equals(SelectionMode.SENTENCE));
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

        String layoutDef = "(ROW (LEAF name=left weight=0.1) (LEAF name=right weight=0.9))";

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

    public static void main(String[] args) {
        setLookAndFeel();
        Injector injector = Guice.createInjector(new InjectorModule());
        final JFrame frame = injector.getInstance(MainFrame.class);
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                frame.setVisible(true);
            }
        });

    }
}
