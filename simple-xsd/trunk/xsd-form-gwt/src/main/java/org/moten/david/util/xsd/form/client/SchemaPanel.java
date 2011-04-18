package org.moten.david.util.xsd.form.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.moten.david.util.xsd.simplified.Choice;
import org.moten.david.util.xsd.simplified.ComplexType;
import org.moten.david.util.xsd.simplified.Element;
import org.moten.david.util.xsd.simplified.Group;
import org.moten.david.util.xsd.simplified.Particle;
import org.moten.david.util.xsd.simplified.Restriction;
import org.moten.david.util.xsd.simplified.Schema;
import org.moten.david.util.xsd.simplified.Sequence;
import org.moten.david.util.xsd.simplified.SimpleType;
import org.moten.david.util.xsd.simplified.Type;
import org.moten.david.util.xsd.simplified.XsdType;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

public class SchemaPanel extends VerticalPanel {

	private int depth = 1;

	private final Schema schema;

	public SchemaPanel(Schema schema) {
		this.schema = schema;
		Label namespace = new Label(schema.getNamespace());
		List<Runnable> list = new ArrayList<Runnable>();
		for (Element element : schema.getElements()) {
			add(createElementPanel(element, list));
		}
		Button submit = new Button("Submit");
		add(submit);
	}

	private Widget createElementPanel(Element element, List<Runnable> validators) {
		// use a parent so we can add multiple elements if required
		final VerticalPanel parent = new VerticalPanel();
		parent.add(createElementPanel(parent, element, validators));
		return decorate(parent);
	}

	private Label createLabel(String name) {
		Label label = new Label(name);
		label.addStyleName("label");
		return label;
	}

	private Widget createElementPanel(final VerticalPanel parent,
			final Element element, final List<Runnable> validators) {
		final VerticalPanel p = new VerticalPanel();

		Type t = getType(schema, element);
		if (t instanceof ComplexType) {
			p.add(createComplexTypePanel(element.getDisplayName(),
					(ComplexType) t, element.getBefore(), element.getAfter(),
					validators));
		} else if (t instanceof SimpleType)
			p.add(createSimpleType(element.getDisplayName(),
					element.getDescription(), element.getValidation(),
					element.getLines(), element.getCols(), (SimpleType) t,
					element.getMinOccurs(), element.getBefore(),
					element.getAfter(), validators));
		else
			throw new RuntimeException("could not find type: "
					+ element.getType());
		if (element.getMaxOccurs() != null
				&& (element.getMaxOccurs().isUnbounded() || element
						.getMaxOccurs().getMaxOccurs() > 1)) {
			final HorizontalPanel h = new HorizontalPanel();
			h.addStyleName("add");
			final Button add = new Button("Add");
			h.add(add);
			final Button remove = new Button("Remove");
			h.add(remove);
			p.add(h);
			add.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					// h.setVisible(false);
					parent.add(createElementPanel(parent, element, validators));
				}
			});
			remove.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					p.setVisible(false);
				}
			});
		}
		if (element.getAfter() != null) {
			HTML html = new HTML(element.getAfter());
			p.add(html);
			html.addStyleName("after");
		}
		return decorate(p);
	}

	private Widget createSimpleType(String name, String description,
			final String validationMessage, Integer lines, Integer cols,
			final SimpleType t, final int minOccurs, String before,
			String after, List<Runnable> validators) {

		HorizontalPanel p = new HorizontalPanel();
		p.addStyleName("simpleType");
		if (t.getRestriction() != null) {
			addRestrictionWidget(p, t, name, description, validationMessage,
					validators, before, after);
		} else if (t.getName().getLocalPart().equals("boolean")) {
			// checkboxes
			p.add(createCheckBox(name, description));
		} else if (t.getName().getLocalPart().equals("date")) {
			p.add(createDateWidget(name));
		} else if (t.getName().getLocalPart().equals("dateTime")) {
			p.add(createTextWidget(name, description + "TODO dateTime", lines,
					cols, minOccurs, validators));
		} else {
			p.add(createTextWidget(name, description, lines, cols, minOccurs,
					validators));
		}
		return decorate(p);
	}

	private Widget createCheckBox(String name, String description) {
		CheckBox c = new CheckBox(name);
		c.addStyleName("item");
		VerticalPanel p = new VerticalPanel();
		p.add(addDescription(c, description));
		p.addStyleName("itemGroup");
		return p;
	}

	private Widget createTextWidget(String name, String description,
			Integer lines, Integer cols, final int minOccurs,
			List<Runnable> validators) {
		// plain text box
		final TextBoxBase text;
		if (lines != null && lines > 1) {
			TextArea textArea = new TextArea();
			textArea.setVisibleLines(lines);
			if (cols != null && cols > 0)
				textArea.setCharacterWidth(cols);
			else
				textArea.setCharacterWidth(50);
			text = textArea;
			text.addStyleName("textArea");
		} else {
			text = new TextBox();
			text.addStyleName("item");
		}

		ChangeHandlerFactory factory = new ChangeHandlerFactory() {

			public ChangeHandler create(HasChangeHandlers item,
					final Label validation) {
				final Runnable validator = new Runnable() {
					public void run() {
						boolean isValid = (text.getText() != null && text
								.getText().trim().length() > 0)
								|| minOccurs == 0;
						updateValidation(isValid, text, validation, "mandatory");
					}
				};
				return new ChangeHandler() {
					public void onChange(ChangeEvent event) {
						validator.run();
					}
				};
			}
		};

		return layout(name, text, "You must put an answer here", description,
				null, null, factory);
	}

	private Widget createDateWidget(String name) {
		HorizontalPanel p = new HorizontalPanel();
		p.addStyleName("itemGroup");
		final Label label = createLabel(name);
		p.add(label);
		final TextBox text = new TextBox();
		text.setReadOnly(true);
		text.addStyleName("item");
		p.add(text);

		// Create a date picker
		DatePicker datePicker = new DatePicker();

		// Set the value in the text box when the user selects a date
		datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
			public void onValueChange(ValueChangeEvent<Date> event) {
				Date date = event.getValue();
				String dateString = DateTimeFormat.getFormat(
						PredefinedFormat.DATE_FULL).format(date);
				text.setText(dateString);
			}
		});

		// Set the default value
		datePicker.setValue(new Date(), true);
		datePicker.addStyleName("item");
		DisclosurePanel d = new DisclosurePanel("");
		d.setContent(datePicker);
		p.add(d);
		return p;
	}

	/**
	 * Layouts an item as a {@link Widget}
	 * 
	 * @param label
	 * @param item
	 * @param validation
	 * @param description
	 * @param before
	 * @param after
	 * @return
	 */
	private Widget layout(String label, Widget item, String validation,
			String description, String before, String after,
			ChangeHandlerFactory factory) {

		Panel vp = new FlowPanel();
		vp.addStyleName("itemGroup");
		vp.add(createBeforeWidget(before));
		vp.add(createLabelWidget(label));
		vp.add(item);
		item.addStyleName("item");
		Label validationLabel = createValidationWidget(validation);
		vp.add(validationLabel);
		vp.add(createDescriptionWidget(description));
		vp.add(createAfterWidget(after));
		if (factory != null && item instanceof HasChangeHandlers) {
			HasChangeHandlers itm = (HasChangeHandlers) item;
			itm.addChangeHandler(factory.create(itm, validationLabel));
		}
		return vp;
	}

	private Label createLabel(String message, String styleName) {
		Label widget = new Label(message);
		widget.addStyleName(styleName);
		return widget;
	}

	private Label createValidationWidget(String validation) {
		Label label = createLabel(validation, "validation");
		label.setVisible(false);
		return label;
	}

	private Widget createDescriptionWidget(String description) {
		return createLabel(description, "description");
	}

	private Widget createLabelWidget(String label) {
		Label widget = new Label(label);
		widget.addStyleName("label");
		return widget;
	}

	private Widget createBeforeWidget(String before) {
		return createLabel(before, "before");
	}

	private Widget createAfterWidget(String after) {
		return createLabel(after, "after");
	}

	private boolean isEnumeration(Restriction r) {
		return r.getEnumerations().size() > 0;
	}

	private void addRestrictionWidget(HorizontalPanel p, SimpleType t,
			String name, String description, String validationMessage,
			List<Runnable> validators, String before, String after) {
		if (isEnumeration(t.getRestriction())) {
			// list boxes
			ListBox listBox = createListBox(t.getRestriction());
			p.add(layout(name, listBox, validationMessage, description, null,
					null, null));
		} else if (t.getRestriction().getPattern() != null) {
			// patterns
			p.add(createPatternWidget(name, t.getRestriction().getPattern(),
					description, validationMessage, before, after));

		} else if (t.getRestriction().getBase() != null
				&& t.getRestriction().getBase().getLocalPart()
						.equals("integer")) {
			// integers
			p.add(createIntegerWidget(name, t.getRestriction(), description,
					validationMessage, before, after));
		} else {
			// plain text box
			String defaultValue = t.getName().getLocalPart()
					+ "unsupported restriction";
			TextBox text = new TextBox();
			text.setText(defaultValue);
			p.add(layout(name, text, validationMessage, description, before,
					after, null));
		}

	}

	private ListBox createListBox(Restriction restriction) {
		List<XsdType<?>> xsdTypes = restriction.getEnumerations();
		ListBox listBox = new ListBox();
		for (XsdType<?> x : xsdTypes) {
			listBox.addItem(x.getValue().toString());
		}
		return listBox;
	}

	private Widget createPatternWidget(String name, final String pattern,
			final String description, final String validationMessage,
			String before, String after) {

		ChangeHandlerFactory factory = new ChangeHandlerFactory() {

			public ChangeHandler create(HasChangeHandlers item, Label validation) {
				return createPatternChangeHandler(pattern, (TextBoxBase) item,
						validationMessage, validation);
			}
		};

		return layout(name, new TextBox(), validationMessage, description,
				before, after, factory);
	}

	private Widget createIntegerWidget(String name,
			final Restriction restriction, final String description,
			final String validationMessage, String before, String after) {

		ChangeHandlerFactory factory = new ChangeHandlerFactory() {

			public ChangeHandler create(final HasChangeHandlers item,
					final Label validation) {

				return createIntegerChangeHandler(restriction, (TextBox) item,
						validation);
			}
		};

		return layout(name, new TextBox(), validationMessage, description,
				before, after, factory);
	}

	private ChangeHandler createIntegerChangeHandler(
			final Restriction restriction, final TextBox item,
			final Label validation) {
		return new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				boolean isValid = true;
				int i = 0;
				try {
					i = Integer.parseInt(item.getText());
				} catch (NumberFormatException e) {
					isValid = false;
				}
				if (restriction.getMinInclusive() != null
						&& i < restriction.getMinInclusive().doubleValue())
					isValid = false;
				if (restriction.getMinExclusive() != null
						&& i <= restriction.getMinExclusive().doubleValue())
					isValid = false;
				if (restriction.getMaxInclusive() != null
						&& i > restriction.getMaxInclusive().doubleValue())
					isValid = false;
				if (restriction.getMaxExclusive() != null
						&& i >= restriction.getMaxExclusive().doubleValue())
					isValid = false;
				updateValidation(isValid, item, validation, "invalid");
			}
		};
	}

	private void updateValidation(boolean isValid, Widget item,
			Label validation, String defaultValidationMessage) {
		if (!isValid) {
			if (validation.getText() == null
					|| validation.getText().trim().length() == 0)
				validation.setText(defaultValidationMessage);
			validation.setVisible(true);
			item.addStyleName("invalidItem");
		} else {
			validation.setVisible(false);
			item.removeStyleName("invalidItem");
		}
	}

	private ChangeHandler createPatternChangeHandler(final String pattern,
			final TextBoxBase text, final String validationMessage,
			final Label validation) {
		return new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				RegExp regex = RegExp.compile("^" + pattern + "$");
				boolean isValid = regex.test(text.getText());
				updateValidation(isValid, text, validation, "invalid format");
			}
		};
	}

	private Widget addDescription(Widget widget, String description) {
		VerticalPanel vp = new VerticalPanel();
		vp.add(widget);
		if (description != null) {
			Label label = new Label(description);
			vp.add(label);
			label.addStyleName("description");
		}
		return vp;
	}

	private Widget createComplexTypePanel(String displayName, ComplexType t,
			String before, String after, List<Runnable> validators) {
		VerticalPanel p = new VerticalPanel();
		depth++;
		p.add(new HTML("<h" + depth + ">" + displayName + "</h" + depth + ">"));
		for (Particle particle : t.getParticles()) {
			p.add(createParticle(particle, validators));
		}
		depth--;
		return decorate(p);
	}

	private Widget createParticle(Particle particle, List<Runnable> validators) {
		VerticalPanel p = new VerticalPanel();
		if (particle instanceof Element)
			p.add(createElementPanel((Element) particle, validators));
		else if (particle instanceof SimpleType)
			p.add(createSimpleType(particle.getClass().getName(), null, null,
					null, null, (SimpleType) particle, 1, null, null,
					validators));
		else if (particle instanceof Group)
			p.add(createGroup((Group) particle, validators));
		else
			throw new RuntimeException("unknown particle:" + particle);
		return decorate(p);
	}

	private Widget decorate(Widget w) {
		VerticalPanel p = new VerticalPanel();
		p.add(w);
		p.addStyleName("box");
		return p;
	}

	private int groupCount = 1;

	private synchronized int nextGroup() {
		return groupCount++;
	}

	private Widget createGroup(Group group, List<Runnable> validators) {
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
				final Widget particlePanel = createParticle(particle,
						validators);
				particlePanel.addStyleName("uncheckedRadioButtonContent");
				rb.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						if (lastChecked[0] != null) {
							lastChecked[0]
									.addStyleName("uncheckedRadioButtonContent");
						}
						particlePanel
								.removeStyleName("uncheckedRadioButtonContent");
						particlePanel.addStyleName("checkedRadioButtonContent");
						lastChecked[0] = particlePanel;
					}
				});
				p.add(particlePanel);
				if (first) {
					rb.setValue(true);
					particlePanel.removeStyleName("checkedRadioButtonContent");
					lastChecked[0] = particlePanel;
				}
				first = false;
			}
		} else if (group instanceof Sequence) {
			p.add(new Label("sequence"));
			for (Particle particle : group.getParticles()) {
				final Widget particlePanel = createParticle(particle,
						validators);
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