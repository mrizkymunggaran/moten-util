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
	public Check(String name, String expression, String checkLevel,
			String resultLevel, List<String> failurePolicies,
			List<String> unknownPolicies, List<String> exceptionPolicies,
			List<String> okPolicies) {

		DisclosurePanel d = new DisclosurePanel();
		{
			HorizontalPanel item = new HorizontalPanel();
			String imageName = resultLevel;
			item.add(createImage(imageName));
			{
				Label label = new Label(name);
				label.setStyleName("checkLabel");
				item.add(label);
			}
			// TODO do alignment with css
			item.setCellVerticalAlignment(item.getWidget(0), ALIGN_MIDDLE);
			item.setCellVerticalAlignment(item.getWidget(1), ALIGN_MIDDLE);

			d.setHeader(item);
		}
		VerticalPanel content = new VerticalPanel();
		content.setStyleName("checkContent");
		Tree tree = new Tree();
		{
			HorizontalPanel panel = new HorizontalPanel();
			panel.add(createKeyValueWidget("Status:", resultLevel));
			tree.addItem(new TreeItem(panel));
		}
		{
			HorizontalPanel panel = new HorizontalPanel();
			panel.add(createKeyValueWidget("Level:", checkLevel));
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

	private Widget createImage(String level) {
		Image image = new Image("images/" + level + ".jpg");
		image.setStyleName("checkImage");
		return image;
	}
}
