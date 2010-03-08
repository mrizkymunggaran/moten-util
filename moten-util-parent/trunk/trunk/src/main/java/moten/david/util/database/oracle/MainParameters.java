package moten.david.util.database.oracle;

/*
 * Created on 13/01/2006
 *
 * Author: DMoten
 */

import java.util.ArrayList;

public class MainParameters {

	String[] args;

	public MainParameters(String[] args) {
		this.args = args;
	}

	public boolean optionExists(String option) {
		boolean found = false;
		for (int i = 0; i < args.length; i++) {
			found = found || args[i].equalsIgnoreCase(option);
		}
		return found;
	}

	public String[] removeOption(String option) {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < args.length; i++) {
			if (!args[i].equalsIgnoreCase(option)) {
				list.add(args[i]);
			}
		}
		String[] argsNew = new String[list.size()];
		for (int i = 0;i<argsNew.length;i++) {
			argsNew[i]=(String) list.get(i);
		}
		this.args = argsNew;
		return args;
	}
	
	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		for (int i=0;i<args.length;i++) {
			s.append(args[i]+"\n");
		}
		return s.toString();
	}
	
	public static void main(String[] args) {
		args = new String[] {"a","b","c"};
		MainParameters m = new MainParameters(args);
		m.removeOption("b");
	}
}
