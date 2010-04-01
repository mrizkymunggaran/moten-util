package moten.david.ete.memory;

import moten.david.ete.Identifier;

public interface MyIdentifiersListener {
	void added(Identifier identifier);

	void removed(Identifier identifier);
}
