package org.moten.david.util.xsd.form.client;

public class Item {
	private boolean displayNumberIfEnabled;
	private Integer number = null;
	private final String name;
	private final String description;
	private final String validationMessage;
	private final String before;
	private final String after;
	private final int minOccurs;
	private final String displayName;

	public boolean isDisplayNumberIfEnabled() {
		return displayNumberIfEnabled;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Item(boolean displayNumberIfEnabled, Integer number, String name,
			String displayName, String description, String validationMessage,
			String before, String after, int minOccurs) {
		super();
		this.displayNumberIfEnabled = displayNumberIfEnabled;
		this.number = number;
		this.name = name;
		this.displayName = displayName;
		this.description = description;
		this.validationMessage = validationMessage;
		this.before = before;
		this.after = after;
		this.minOccurs = minOccurs;
	}

	public Item setNumber(Integer number) {
		this.number = number;
		return this;
	}

	public Item setDisplayNameIfEnabled(boolean value) {
		this.displayNumberIfEnabled = value;
		return this;
	}

	public boolean displayNumberIfEnabled() {
		return displayNumberIfEnabled;
	}

	public Integer getNumber() {
		return number;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getValidationMessage() {
		return validationMessage;
	}

	public String getBefore() {
		return before;
	}

	public String getAfter() {
		return after;
	}

	public int getMinOccurs() {
		return minOccurs;
	}

}
