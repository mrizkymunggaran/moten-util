package moten.david.util.monitoring.gwt.client.widget;

import java.util.List;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Check extends HorizontalPanel {
	public Check(String name, String expression, String level, boolean ok,
			List<String> failurePolicies, List<String> unknownPolicies,
			List<String> exceptionPolicies, List<String> okPolicies) {

		HorizontalPanel item = new HorizontalPanel();

		item.add(createImage("images/circle-green.jpg"));
		item.add(new Label(name));

		// TODO do alignment with css
		item.setCellVerticalAlignment(item.getWidget(0), ALIGN_MIDDLE);
		item.setCellVerticalAlignment(item.getWidget(1), ALIGN_MIDDLE);

		DisclosurePanel d = new DisclosurePanel();

		VerticalPanel content = new VerticalPanel();
		content.setStyleName("checkContent");
		Tree tree = new Tree();
		{
			HorizontalPanel panel = new HorizontalPanel();
			panel.add(createKeyValueWidget("Level:", level));
			tree.addItem(new TreeItem(panel));
		}
		{
			TreeItem itm = new TreeItem("Expression");
			tree.addItem(itm);
			Label label = new Label(expression);
			label.setStyleName("expression");
			itm.addItem(new TreeItem(label));
			itm.setState(true);
		}
		{
			TreeItem policies = new TreeItem("Policies");
			if (failurePolicies.size() > 0)
				policies.addItem(createKeyValueWidget("Failure",
						format(failurePolicies)));
			if (unknownPolicies.size() > 0)
				policies.addItem(createKeyValueWidget("Unknown",
						format(unknownPolicies)));
			if (exceptionPolicies.size() > 0)
				policies.addItem(createKeyValueWidget("Exception",
						format(exceptionPolicies)));
			if (okPolicies.size() > 0)
				policies
						.addItem(createKeyValueWidget("Ok", format(okPolicies)));
			policies.setState(true);
			tree.addItem(policies);
		}

		content.add(tree);

		d.setHeader(item);
		d.setContent(content);
		add(d);
	}

	private String format(List<String> list) {
		return list.toString().replaceAll("\\[", "").replaceAll("\\]", "");
	}

	private Widget createKeyValueWidget(String key, String value) {
		HorizontalPanel panel = new HorizontalPanel();
		Label keyLabel = new Label(key);
		keyLabel.setStyleName("keyValueLabel");
		Label valueLabel = new Label(value);
		valueLabel.setWordWrap(true);
		valueLabel.setStyleName("keyValueValue");
		panel.add(keyLabel);
		panel.add(valueLabel);
		return panel;
	}

	private Widget createImage(String url) {
		Image image = new Image(url);
		image.setStyleName("checkImage");
		return image;
	}
}
