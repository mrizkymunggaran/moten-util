package moten.david.markup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import moten.david.markup.xml.study.Study;

public class StudyFolder {

	private final StudyMarshaller marshaller;

	public StudyFolder(StudyMarshaller marshaller) {
		this.marshaller = marshaller;
	}

	public Study getStudy(String directory) {

		try {
			return marshaller.unmarshal(new FileInputStream(new File(directory,
					"study.xml")));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}
