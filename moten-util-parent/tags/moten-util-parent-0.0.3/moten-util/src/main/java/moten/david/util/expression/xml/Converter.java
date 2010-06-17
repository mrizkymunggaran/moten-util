package moten.david.util.expression.xml;

import java.io.ByteArrayOutputStream;

import moten.david.util.expression.Expression;
import moten.david.util.expression.Operation;
import moten.david.util.xml.TaggedOutputStream;

public class Converter {

	public String toXml(Expression expression) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		TaggedOutputStream t = new TaggedOutputStream(bytes, true);
		add(expression, t);
		t.close();
		return bytes.toString();
	}

	private void add(Expression expression, TaggedOutputStream t) {
		if (expression instanceof Operation) {
			String name = expression.getClass().getSimpleName();
			t.startTag(name);
			for (Expression e : ((Operation) expression).getExpressions())
				add(e, t);
			t.closeTag();
		}
	}

}
