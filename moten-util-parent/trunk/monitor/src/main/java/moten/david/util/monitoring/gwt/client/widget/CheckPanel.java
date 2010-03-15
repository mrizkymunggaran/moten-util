package moten.david.util.monitoring.gwt.client.widget;

import java.util.List;

import moten.david.util.monitoring.gwt.client.check.AppCheck;
import moten.david.util.monitoring.gwt.client.check.AppCheckResult;
import moten.david.util.monitoring.gwt.client.check.AppDependency;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CheckPanel extends HorizontalPanel {
    public CheckPanel(AppCheck check, AppCheckResult[] results) {

        AppCheckResult result = Util.findResult(results, check.getName());
        DisclosurePanel d = new DisclosurePanel();
        {
            HorizontalPanel item = new HorizontalPanel();
            String imageName = result.getLevel();
            item.add(createImage(imageName));
            {
                Label label = new Label(check.getName());
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
            panel.add(createKeyValueWidget("Status:", result.getLevel()));
            tree.addItem(new TreeItem(panel));
        }
        {
            HorizontalPanel panel = new HorizontalPanel();
            panel.add(createKeyValueWidget("Level:", result.getLevel()));
            tree.addItem(new TreeItem(panel));
        }
        {
            TreeItem itm = new TreeItem("Expression");
            tree.addItem(itm);
            Label label = new Label(check.getExpression());
            label.setStyleName("expression");
            itm.addItem(new TreeItem(label));
            itm.setState(true);
        }
        addPolicies(check, tree);
        addDependencies(check, results, tree);
        content.add(tree);

        d.setContent(content);
        add(d);
    }

    private void addDependencies(AppCheck check, AppCheckResult[] results,
            Tree tree) {
        TreeItem deps = new TreeItem("Dependencies");
        if (check.getDependencies() != null)
            for (AppDependency dep : check.getDependencies()) {
                deps.addItem(new DependencyPanel(dep, results));
            }
        deps.setState(true);
        if (deps.getChildCount() > 0)
            tree.addItem(deps);
    }

    private void addPolicies(AppCheck check, Tree tree) {
        TreeItem policies = new TreeItem("Policies");
        if (hasAtLeastOne(check.getFailurePolicies()))
            policies.addItem(createKeyValueWidget("Failure", format(check
                    .getFailurePolicies())));
        if (hasAtLeastOne(check.getUnknownPolicies()))
            policies.addItem(createKeyValueWidget("Unknown", format(check
                    .getUnknownPolicies())));
        if (hasAtLeastOne(check.getExceptionPolicies()))
            policies.addItem(createKeyValueWidget("Exception", format(check
                    .getExceptionPolicies())));
        if (hasAtLeastOne(check.getOkPolicies()))
            policies.addItem(createKeyValueWidget("Ok", format(check
                    .getOkPolicies())));
        policies.setState(true);
        if (policies.getChildCount() > 0)
            tree.addItem(policies);
    }

    private boolean hasAtLeastOne(List<String> list) {
        return list != null && list.size() > 0;
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
