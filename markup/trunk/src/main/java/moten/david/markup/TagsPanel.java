package moten.david.markup;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import moten.david.markup.events.TagSelectionChanged;
import moten.david.markup.events.TagsChanged;
import moten.david.markup.events.TextTagged;
import moten.david.util.controller.Controller;
import moten.david.util.controller.ControllerListener;

import com.google.inject.Inject;

public class TagsPanel extends JPanel {

    private final Controller controller;

    @Inject
    public TagsPanel(final Controller controller) {
        this.controller = controller;
        controller.addListener(TagsChanged.class, createTagChangedListener());
    }

    private ControllerListener<TagsChanged> createTagChangedListener() {
        return new ControllerListener<TagsChanged>() {
            @Override
            public void event(TagsChanged event) {
                setTags(event.getTags());
            }
        };
    }

    public void setTags(final List<Tag> tags) {

        DefaultMutableTreeNode top = new DefaultMutableTreeNode("Tags");
        createNodes(top, tags);
        JTree tree = new JTree(top);

        JScrollPane treeView = new JScrollPane(tree);
        removeAll();
        setLayout(new BorderLayout());
        add(treeView, BorderLayout.NORTH);
        tree.addTreeSelectionListener(createTreeSelectionListener(tree));
        controller
                .addListener(TextTagged.class, createTextTaggedListener(tree));
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
        for (TreePath path : tree.getSelectionPaths()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
                    .getLastPathComponent();
            Tag tag = (Tag) node.getUserObject();
            list.add(tag);
        }
        controller.event(new TagSelectionChanged(list));
    }

    private void createNodes(DefaultMutableTreeNode top, List<Tag> tags) {
        for (Tag tag : tags) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(tag);
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
                tree.addSelectionPath(path);
                fireChanged(tree);
            }
        };
    }
}
