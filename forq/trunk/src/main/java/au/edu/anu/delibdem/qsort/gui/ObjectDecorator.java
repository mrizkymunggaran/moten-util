package au.edu.anu.delibdem.qsort.gui;
public class ObjectDecorator {
		private Object object;
		private String name;

		public Object getObject() {
			return object;
		}

		public void setObject(Object object) {
			this.object = object;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public ObjectDecorator(Object object, String name) {
			super();
			this.object = object;
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}
