package org.moten.david.util.xsd.simplified;

public class Element implements Particle {
	private String name;
	private String displayName;
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	private int minOccurs = 1;
	private QName type;
	private MaxOccurs maxOccurs = new MaxOccurs();
	private boolean maxUnbounded = false;
	public String validation;
	public String before;
	public String after;
	public Integer lines;
	public Integer cols;

	public Integer getCols() {
		return cols;
	}

	public void setCols(Integer cols) {
		this.cols = cols;
	}

	public Integer getLines() {
		return lines;
	}

	public void setLines(Integer lines) {
		this.lines = lines;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setMaxUnbounded(boolean maxUnbounded) {
		this.maxUnbounded = maxUnbounded;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "\n\tElement [name=" + name + ", minOccurs=" + minOccurs
				+ ", maxOccurs=" + maxOccurs + ", maxUnbounded=" + maxUnbounded
				+ ", type=" + type + "]";
	}

	public String getValidation() {
		return validation;
	}

	public void setValidation(String validation) {
		this.validation = validation;
	}

	public int getMinOccurs() {
		return minOccurs;
	}

	public MaxOccurs getMaxOccurs() {
		return maxOccurs;
	}

	public QName getType() {
		return type;
	}

	public boolean isMaxUnbounded() {
		return maxUnbounded;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setMinOccurs(int minOccurs) {
		this.minOccurs = minOccurs;
	}

	public void setMaxOccurs(MaxOccurs maxOccurs) {
		this.maxOccurs = maxOccurs;
	}

	public void setType(QName type) {
		this.type = type;
	}

	public Element() {

	}

	public static class Builder {
		private final Element e;

		public Builder() {
			e = new Element();
		}

		public Element build() {
			return e;
		}

		public Builder name(String name) {
			e.name = name;
			return this;
		}

		public Builder type(QName type) {
			e.type = type;
			return this;
		}

		public Builder displayName(String displayName) {
			e.displayName = displayName;
			return this;
		}

		public Builder description(String description) {
			e.description = description;
			return this;
		}

		public Builder minOccurs(int minOccurs) {
			e.minOccurs = minOccurs;
			return this;
		}

		public Builder maxOccurs(MaxOccurs maxOccurs) {
			e.maxOccurs = maxOccurs;
			return this;
		}

		public void validation(String validation) {
			e.validation = validation;
		}

		public void before(String before) {
			e.before = before;
		}

		public void after(String after) {
			e.after = after;
		}

		public void lines(Integer lines) {
			e.lines = lines;
		}

		public void cols(Integer cols) {
			e.cols = cols;
		}

	}

	public String getBefore() {
		return before;
	}

	public void setBefore(String before) {
		this.before = before;
	}

	public String getAfter() {
		return after;
	}

	public void setAfter(String after) {
		this.after = after;
	}
}