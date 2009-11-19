package moten.david.util.uml.eclipse;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Example {

	public static void main(String[] args) throws IOException {
		UmlProducer p = new UmlProducer(ClassFilter.STANDARD,
				new UmlProducerOptionsImpl(false));
		OutputStream out = new FileOutputStream("target/result.uml");

		String xmi = p.getUmlXmi(ClassFilter.class, ClassWrapper.class,
				Dependency.class, UmlProducer.class,
				UmlProducerOptionsImpl.class);

		System.out.println(xmi);
		out.write(xmi.getBytes());
		out.close();
	}

}
