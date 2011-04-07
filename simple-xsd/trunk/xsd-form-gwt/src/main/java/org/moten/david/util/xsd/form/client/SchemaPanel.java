package org.moten.david.util.xsd.form.client;

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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

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
		VerticalPanel p = new VerticalPanel();
		Type t = getType(schema, element);
		if (t instanceof ComplexType) {
			p.add(createComplexTypePanel(element.getName(), (ComplexType) t));
		} else if (t instanceof SimpleType)
			p.add(createSimpleType(element.getName(), (SimpleType) t));
		else
			throw new RuntimeException("could not find type: "
					+ element.getType());
		return decorate(p);
	}

	private Widget createSimpleType(String name, SimpleType t) {
		HorizontalPanel p = new HorizontalPanel();
		if (t.getRestriction() != null) {
			p.add(new Label(name));
			List<XsdType<?>> xsdTypes = t.getRestriction().getEnumerations();
			if (xsdTypes.size() > 0) {
				ListBox listBox = new ListBox();
				for (XsdType<?> x : xsdTypes) {
					listBox.addItem(x.getValue().toString());
				}
				p.add(listBox);
			} else {
				TextBox text = new TextBox();
				text.setText(t.getRestriction().getPattern());
				p.add(text);
			}
		} else if (t.getName().getLocalPart().equals("boolean")) {
			CheckBox c = new CheckBox(name);
			p.add(c);
		} else {
			p.add(new Label(name));
			TextBox text = new TextBox();
			text.setText(t.getName().getLocalPart());
			p.add(text);
		}
		return decorate(p);
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
			p.add(createSimpleType(particle.getClass().getName(),
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
			p.add(new Label("choice"));
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