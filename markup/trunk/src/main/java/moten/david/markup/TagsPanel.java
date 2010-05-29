package moten.david.markup;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
        setLayout(new GridLayout(1, 1));
        controller.addListener(TagsChanged.class,
                new ControllerListener<TagsChanged>() {
                    @Override
                    public void event(TagsChanged event) {
                        setTags(event.getTags());
                    }
                });
    }

    public void setTags(final List<Tag> tags) {
        DefaultListModel model = new DefaultListModel();
        for (Tag tag : tags) {
            model.addElement(tag);
        }
        final JList list = new JList(model);
        add(list);
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                fireChanged(list);
            }
        });
        controller.addListener(TextTagged.class,
                new ControllerListener<TextTagged>() {

                    @Override
                    public void event(TextTagged event) {
                        list.setSelectedValue(event.getTag(), true);
                        fireChanged(list);
                    }
                });
    }

    private void fireChanged(JList list) {
        List<Tag> tags = new ArrayList<Tag>();
        for (Object obj : list.getSelectedValues()) {
            tags.add((Tag) obj);
        }
        controller.event(new TagSelectionChanged(tags));
    }

}
