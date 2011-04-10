package org.moten.david.util.xsd.form.client;

import java.util.Date;
import java.util.List;

import org.moten.david.util.xsd.simplified.Choice;
import org.moten.david.util.xsd.simplified.ComplexType;
import org.moten.david.util.xsd.simplified.Element;
import org.moten.david.util.xsd.simplified.Group;
import org.moten.david.util.xsd.simplified.Particle;
import org.moten.david.util.xsd.simplified.Schema;
import org.moten.david.util.xsd.simplified.Sequence;
import org.moten.david.util.xsd.simplified.SimpleType;
import org.moten.david.util.xsd.simplified.Type;
import org.moten.david.util.xsd.simplified.XsdType;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

public class SchemaPanel extends VerticalPanel {

	private final Schema schema;

	public SchemaPanel(Schema schema) {
		this.schema = schema;
		Label namespace = new Label(schema.getNamespace());
		add(namespace);
		for (Element element : schema.getElements()) {
			add(createElementPanel(element));
		}
	}

	private Widget createElementPanel(Element element) {
		// use a parent so we can add multiple elements if required
		final VerticalPanel parent = new VerticalPanel();
		parent.add(createElementPanel(parent, element));
		return decorate(parent);
	}

	private Label createLabel(String name) {
		Label label = new Label(name);
		label.setStyleName("label");
		return label;
	}

	private Widget createElementPanel(final VerticalPanel parent,
			final Element element) {
		VerticalPanel p = new VerticalPanel();
		if (element.getBefore() != null) {
			HTML html = new HTML(element.getBefore());
			p.add(html);
			html.setStyleName("before");
		}
		Type t = getType(schema, element);
		if (t instanceof ComplexType) {
			p.add(createComplexTypePanel(element.getName(), (ComplexType) t));
		} else if (t instanceof SimpleType)
			p.add(createSimpleType(element.getDisplayName(),
					element.getDescription(), (SimpleType) t));
		else
			throw new RuntimeException("could not find type: "
					+ element.getType());
		if (element.getMaxOccurs() != null
				&& (element.getMaxOccurs().isUnbounded() || element
						.getMaxOccurs().getMaxOccurs() > 1)) {
			final HorizontalPanel h = new HorizontalPanel();
			h.setStyleName("add");
			final Button button = new Button("Add");
			h.add(button);
			final Button remove = new Button("Remove");
			h.add(remove);
			p.add(h);
			button.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent arg0) {
					// h.setVisible(false);
					parent.add(createElementPanel(parent, element));
				}
			});
		}
		if (element.getAfter() != null) {
			HTML html = new HTML(element.getAfter());
			p.add(html);
			html.setStyleName("after");
		}
		return decorate(p);
	}

	private Widget createSimpleType(String name, String description,
			final SimpleType t) {
		HorizontalPanel p = new HorizontalPanel();
		if (t.getRestriction() != null) {
			// list boxes
			p.add(createLabel(name));
			List<XsdType<?>> xsdTypes = t.getRestriction().getEnumerations();
			if (xsdTypes.size() > 0) {
				ListBox listBox = new ListBox();
				for (XsdType<?> x : xsdTypes) {
					listBox.addItem(x.getValue().toString());
				}
				listBox.setStyleName("item");
				p.add(addDescription(listBox, description));
			} else {
				// patterns
				final Label validation = new Label();
				validation.setVisible(false);
				final TextBox text = new TextBox();
				text.setText(t.getRestriction().getPattern());
				text.setStyleName("item");
				text.addChangeHandler(new ChangeHandler() {
					public void onChange(ChangeEvent event) {
						RegExp pattern = RegExp.compile(t.getRestriction()
								.getPattern());
						if (pattern.exec(text.getText()) != null) {

						}

					}
				});

				p.add(addDescription(text, description));
			}
		} else if (t.getName().getLocalPart().equals("boolean")) {
			// checkboxes
			CheckBox c = new CheckBox(name);
			c.setStyleName("item");
			p.add(addDescription(c, description));
		} else if (t.getName().getLocalPart().equals("date")) {
			final Label label = createLabel(name);
			p.add(label);
			final TextBox text = new TextBox();
			text.setReadOnly(true);
			text.setStyleName("item");
			p.add(text);

			// Create a date picker
			DatePicker datePicker = new DatePicker();

			// Set the value in the text box when the user selects a date
			datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
				public void onValueChange(ValueChangeEvent<Date> event) {
					Date date = event.getValue();
					String dateString = DateTimeFormat.getMediumDateFormat()
							.format(date);
					text.setText(dateString);
				}
			});

			// Set the default value
			datePicker.setValue(new Date(), true);
			datePicker.setStyleName("item");
			DisclosurePanel d = new DisclosurePanel("");
			d.setContent(datePicker);
			p.add(d);
		} else if (t.getName().getLocalPart().equals("dateTime")) {

		} else {
			// plain text box
			p.add(createLabel(name));
			TextBox text = new TextBox();
			text.setText(t.getName().getLocalPart());
			text.setStyleName("item");
			p.add(addDescription(text, description));
		}
		return decorate(p);
	}

	private Widget addDescription(Widget widget, String description) {
		VerticalPanel vp = new VerticalPanel();
		vp.add(widget);
		if (description != null) {
			Label label = new Label(description);
			vp.add(label);
			label.setStyleName("description");
		}
		return vp;
	}

	private Widget createComplexTypePanel(String name, ComplexType t) {
		VerticalPanel p = new VerticalPanel();
		p.add(new Label(name));
		for (Particle particle : t.getParticles()) {
			p.add(createParticle(particle));
		}
		return decorate(p);
	}

	private Widget createParticle(Particle particle) {
		VerticalPanel p = new VerticalPanel();
		if (particle instanceof Element)
			p.add(createElementPanel((Element) particle));
		else if (particle instanceof SimpleType)
			p.add(createSimpleType(particle.getClass().getName(), null,
					(SimpleType) particle));
		else if (particle instanceof Group)
			p.add(createGroup((Group) particle));
		else
			throw new RuntimeException("unknown particle:" + particle);
		return decorate(p);
	}

	private Widget decorate(Widget w) {
		VerticalPanel p = new VerticalPanel();
		p.add(w);
		p.setStyleName("box");
		return p;
	}

	private int groupCount = 1;

	private synchronized int nextGroup() {
		return groupCount++;
	}

	private Widget createGroup(Group group) {
		VerticalPanel p = new VerticalPanel();
		if (group instanceof Choice) {
			String groupName = "group" + nextGroup();
			boolean first = true;
			final Widget[] lastChecked = new Widget[] { null };
			int count = 1;
			for (Particle particle : group.getParticles()) {
				RadioButton rb = new RadioButton(groupName, "option " + count);
				count++;
				p.add(rb);
				final Widget particlePanel = createParticle(particle);
				particlePanel.setStyleName("uncheckedRadioButtonContent");
				rb.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						if (lastChecked[0] != null)
							lastChecked[0]
									.setStyleName("uncheckedRadioButtonContent");
						particlePanel.setStyleName("checkedRadioButtonContent");
						lastChecked[0] = particlePanel;
					}
				});
				p.add(particlePanel);
				if (first) {
					rb.setValue(true);
					particlePanel.setStyleName("checkedRadioButtonContent");
					lastChecked[0] = particlePanel;
				}
				first = false;
			}
		} else if (group instanceof Sequence) {
			p.add(new Label("sequence"));
			for (Particle particle : group.getParticles()) {
				final Widget particlePanel = createParticle(particle);
				p.add(particlePanel);
			}
		}
		return decorate(p);
	}

	private Type getType(Schema schema, Element element) {
		for (ComplexType t : schema.getComplexTypes())
			if (element.getType().equals(t.getQName())) {
				return t;
			}
		for (SimpleType t : schema.getSimpleTypes())
			if (element.getType().equals(t.getQName())) {
				return t;
			}
		// otherwise assume is an xsd simpleType
		return new SimpleType(element.getType());

	}
}