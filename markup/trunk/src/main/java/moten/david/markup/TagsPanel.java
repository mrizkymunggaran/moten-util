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
import moten.david.markup.events.TextTagged;
import moten.david.util.controller.Controller;
import moten.david.util.controller.ControllerListener;

public class TagsPanel extends JPanel {

    public TagsPanel(final Controller controller, final List<String> tags) {
        setLayout(new GridLayout(1, 1));
        DefaultListModel model = new DefaultListModel();
        for (String tag : tags) {
            model.addElement(tag);
        }
        final JList list = new JList(model);
        add(list);
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                List<String> tags = new ArrayList<String>();
                for (Object obj : list.getSelectedValues()) {
                    tags.add(obj.toString());
                }
                controller.event(new TagSelectionChanged(tags));
            }
        });
        controller.addListener(TextTagged.class,
                new ControllerListener<TextTagged>() {

                    @Override
                    public void event(TextTagged event) {
                        list.setSelectedValue(event.getTag(), false);
                        controller.event(new TagSelectionChanged(tags));
                    }
                });

    }

}
