package moten.david.markup;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import moten.david.markup.events.TagSelectionChanged;
import moten.david.markup.events.TextTagged;
import moten.david.markup.xml.study.Tag;
import moten.david.util.controller.Controller;
import moten.david.util.controller.ControllerListener;
import moten.david.util.swing.CheckTreeManager;

import com.google.inject.Inject;

public class TagsPanel extends JPanel {

    private static final long serialVersionUID = 6719110697421637597L;
    private final Controller controller;
    private CheckTreeManager checkTreeManager;
    private static Logger log = Logger.getLogger(TagsPanel.class.getName());
    private final Presentation presentation;

    @Inject
    public TagsPanel(final Controller controller, CurrentStudy study,
            Presentation presentation) {
        this.controller = controller;
        this.presentation = presentation;
        setTags(study.get().getTag());
    }

    private void setTags(final List<Tag> tags) {

        DefaultMutableTreeNode top = new DefaultMutableTreeNode("Tags");
        createNodes(top, tags);
        JTree tree = new JTree(top);
        tree.setRootVisible(true);
        DefaultTreeCellRenderer renderer = createTreeCellRenderer();
        renderer.setLeafIcon(null);
        tree.setCellRenderer(renderer);
        JScrollPane treeView = new JScrollPane(tree);
        removeAll();
        setLayout(new GridLayout(1, 1));
        add(treeView);

        controller
                .addListener(TextTagged.class, createTextTaggedListener(tree));
        checkTreeManager = new CheckTreeManager(tree);
        checkTreeManager.getSelectionModel().addTreeSelectionListener(
                createTreeSelectionListener(tree));
    }

    private DefaultTreeCellRenderer createTreeCellRenderer() {

        return new DefaultTreeCellRenderer() {

            @Override
            public Component getTreeCellRendererComponent(JTree tree,
                    Object value, boolean sel, boolean expanded, boolean leaf,
                    int row, boolean hasFocus) {
                Component component = super.getTreeCellRendererComponent(tree,
                        value, sel, expanded, leaf, row, hasFocus);
                if (value instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                    if (node.getUserObject() instanceof TagWrapper) {
                        Tag tag = ((TagWrapper) node.getUserObject()).getTag();
                        Color color = new Color(presentation.colors.get(tag
                                .getId()));
                        component.setBackground(color);
                        ((JLabel) component).setOpaque(true);
                    } else {
                        component.setBackground(TagsPanel.this.getBackground());
                        ((JLabel) component).setOpaque(false);
                    }
                }
                return component;
            }
        };
    }

    private TreeSelectionListener createTreeSelectionListener(final JTree tree) {
        return new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                fireChanged(tree);
            }
        };
    }

    private void fireChanged(JTree tree) {
        List<Tag> list = new ArrayList<Tag>();
        // to get the paths that were checked
        TreePath checkedPaths[] = checkTreeManager.getSelectionModel()
                .getSelectionPaths();
        if (checkedPaths == null)
            checkedPaths = new TreePath[] {};
        for (TreePath path : checkedPaths) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
                    .getLastPathComponent();
            addNodeToList(node, list);
        }
        controller.event(new TagSelectionChanged(list));
    }

    @SuppressWarnings("unchecked")
    private void addNodeToList(DefaultMutableTreeNode node, List<Tag> list) {
        if (node.getUserObject() instanceof TagWrapper) {
            Tag tag = ((TagWrapper) node.getUserObject()).getTag();
            list.add(tag);
        } else {
            Enumeration<DefaultMutableTreeNode> en = node.children();
            while (en.hasMoreElements()) {
                DefaultMutableTreeNode n = en.nextElement();
                addNodeToList(n, list);
            }
        }
    }

    private void createNodes(DefaultMutableTreeNode top, List<Tag> tags) {
        for (Tag tag : tags) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(
                    new TagWrapper(tag));
            top.add(node);
        }
    }

    private ControllerListener<TextTagged> createTextTaggedListener(
            final JTree tree) {
        return new ControllerListener<TextTagged>() {

            @Override
            public void event(TextTagged event) {
                TreePath path = tree.getNextMatch(event.getTag().getName(), 0,
                        Position.Bias.Forward);
                checkTreeManager.getSelectionModel().addSelectionPath(path);
                fireChanged(tree);
            }
        };
    }
}
