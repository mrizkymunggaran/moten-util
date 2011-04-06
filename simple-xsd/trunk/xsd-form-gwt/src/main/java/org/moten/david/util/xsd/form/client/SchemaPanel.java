package org.moten.david.util.xsd.form.client;

import org.moten.david.util.xsd.simplified.Choice;
import org.moten.david.util.xsd.simplified.ComplexType;
import org.moten.david.util.xsd.simplified.Element;
import org.moten.david.util.xsd.simplified.Group;
import org.moten.david.util.xsd.simplified.Particle;
import org.moten.david.util.xsd.simplified.Schema;
import org.moten.david.util.xsd.simplified.SimpleType;
import org.moten.david.util.xsd.simplified.Type;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
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
		p.add(new Label(element.getName()));
		Type t = getType(schema, element);
		if (t instanceof ComplexType) {
			p.add(createComplexTypePanel((ComplexType) t));
		} else if (t instanceof SimpleType)
			p.add(createSimpleType((SimpleType) t));
		else
			throw new RuntimeException("could not find type: "
					+ element.getType());
		return decorate(p);
	}

	private Widget createSimpleType(SimpleType t) {
		HorizontalPanel p = new HorizontalPanel();
		p.add(new Label(t.getName().getLocalPart()));
		p.add(new TextBox());
		return decorate(p);
	}

	private Widget createComplexTypePanel(ComplexType t) {
		VerticalPanel p = new VerticalPanel();
		p.add(new Label(t.getName().toString()));
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
			p.add(createSimpleType((SimpleType) particle));
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
			for (Particle particle : group.getParticles()) {
				RadioButton rb = new RadioButton(groupName, "");
				if (first)
					rb.setValue(true);
				first = false;
				p.add(rb);
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