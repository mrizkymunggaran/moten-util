package moten.david.markup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import moten.david.markup.xml.study.Study;

import com.google.inject.Inject;

public class StudyFolder {

	private final StudyMarshaller marshaller;

	@Inject
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

	public void writeStudy(Study study, String directory) {
		try {
			marshaller.marshal(study, new FileOutputStream(new File(directory,
					"study.xml")));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
