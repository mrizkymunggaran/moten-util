package org.moten.david.util.xsd;

public class Util {
	public static String toDisplay(String s) {
		String separators = "-_.";

		StringBuffer a = new StringBuffer();
		Character last = null;
		for (char ch : s.toCharArray()) {
			if (separators.contains(ch + "")) {
				a.append(" ");
			} else {
				if (last != null && Character.isUpperCase(ch)
						&& Character.isLowerCase(last))
					a.append(" ");
				if (a.length() == 0)
					a.append(Character.toUpperCase(ch));
				else {
					a.append(Character.toLowerCase(ch));
				}
			}
			last = ch;
		}
		return a.toString();
	}

}
