package moten.david.util.monitoring.gwt.client.widget;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Check extends HorizontalPanel {
    public Check(String name, String expression, String level, boolean ok) {

        HorizontalPanel item = new HorizontalPanel();

        item.add(createImage("images/circle-green.jpg"));
        item.add(new Label(name));

        // TODO do alignment with css
        item.setCellVerticalAlignment(item.getWidget(0), ALIGN_MIDDLE);
        item.setCellVerticalAlignment(item.getWidget(1), ALIGN_MIDDLE);

        DisclosurePanel d = new DisclosurePanel();

        VerticalPanel content = new VerticalPanel();
        Tree tree = new Tree();
        tree.addItem(new TreeItem("Expression: " + expression));
        tree.addItem(new TreeItem("Level: " + level));
        content.add(tree);

        d.setHeader(item);
        d.setContent(content);
        add(d);
    }

    private Widget createImage(String url) {
        Image image = new Image(url);
        image.setStyleName("checkImage");
        return image;
    }
}
