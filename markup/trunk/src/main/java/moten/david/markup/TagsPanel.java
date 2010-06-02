package moten.david.markup;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

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

	private final Controller controller;
	private CheckTreeManager checkTreeManager;
	private static Logger log = Logger.getLogger(TagsPanel.class.getName());

	@Inject
	public TagsPanel(final Controller controller, CurrentStudy study) {
		this.controller = controller;
		setTags(study.get().getTag());
	}

	private void setTags(final List<Tag> tags) {

		DefaultMutableTreeNode top = new DefaultMutableTreeNode("Tags");
		createNodes(top, tags);
		JTree tree = new JTree(top);
		tree.setRootVisible(true);
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
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
		if (checkedPaths != null) {
			for (TreePath path : checkedPaths) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
						.getLastPathComponent();
				addNodeToList(node, list);
			}
			controller.event(new TagSelectionChanged(list));
		}
	}

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
