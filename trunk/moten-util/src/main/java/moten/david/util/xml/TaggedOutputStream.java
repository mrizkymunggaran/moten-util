package moten.david.util.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Stack;

public class TaggedOutputStream {

	public String indentString = "  ";

	private final Stack<String> stack = new Stack<String>();

	private boolean tagOpen = false;

	private boolean prettyPrint = false;

	private int startIndent = 0;

	private final OutputStream out;

	private boolean lastOperationWasCloseTag = false;

	public TaggedOutputStream(OutputStream out, boolean prettyPrint) {

		this.out = out;
		this.prettyPrint = prettyPrint;
	}

	public TaggedOutputStream(OutputStream out, boolean prettyPrint,
			int startIndent) {

		this.out = out;
		this.startIndent = startIndent;
		this.prettyPrint = prettyPrint;

	}

	public void startTag(String tag) {
		closeBracket();
		if (prettyPrint) {
			writeString("\n");
			for (int i = 0; i < stack.size() + this.startIndent; i++)
				writeString(this.indentString);
		}
		writeString("<" + tag);
		stack.push(tag);
		tagOpen = true;
		lastOperationWasCloseTag = false;
	}

	private void closeBracket() {
		if (tagOpen) {
			writeString(">");
			tagOpen = false;
		}
	}

	public void addAttribute(String key, String value) {

		writeString(" " + key + "=\"" + value + "\"");
	}

	public void addAttribute(String key, double d) {

		DecimalFormat df = new DecimalFormat("#.0000000");
		writeString(" " + key + "=\"" + df.format(d) + "\"");

	}

	public void newLine() {
		writeString("\n");
	}

	public void closeTag() {
		if (tagOpen) {
			writeString("/");
			closeBracket();
			stack.pop();
		} else {
			if (lastOperationWasCloseTag && prettyPrint) {
				// if we are closing a compound tag then
				// put a new line and an indent in.
				writeString("\n");
				for (int i = 0; i < stack.size() - 1 + this.startIndent; i++)
					writeString(this.indentString);
			}
			String tag = stack.pop();
			writeString("</" + tag + ">");
		}
		lastOperationWasCloseTag = true;
	}

	public void close() {
		if (stack.size() > 0)
			throw new Error(stack.size() + "unclosed tags!");
	}

	private void writeString(String str) {
		if (str != null) {
			try {
				out.write(str.getBytes());
			} catch (IOException e) {
				throw new Error("IOException occurred: " + e.getMessage());
			}
		}
	}

	public void append(String str) {
		closeBracket();
		writeString(str);
	}

	public void append(boolean b) {
		append(new Boolean(b).toString());
	}

	public void append(double d) {
		DecimalFormat df = new DecimalFormat("#.0000000");
		append(df.format(d));
	}

	public void append(long d) {
		append(new Long(d).toString());
	}

	public OutputStream getOutputStream() {
		return this.out;
	}

	public static void main(String[] args) {

		TaggedOutputStream t = new TaggedOutputStream(System.out, true);
		t.startTag("info");
		t.startTag("name");
		t.append("johnno");
		t.closeTag();
		t.startTag("type");
		t.addAttribute("location", "Canberra");
		t.startTag("size");
		t.startTag("range");
		t.append("large");
		t.startTag("distrubution");
		t.append("uniform");
		t.closeTag();
		t.closeTag();
		t.closeTag();
		t.closeTag();
		t.startTag("size");
		t.startTag("range");
		t.append("large");
		t.startTag("distrubution");
		t.append("uniform");
		t.closeTag();
		t.closeTag();
		t.closeTag();
		t.closeTag();
	}

}