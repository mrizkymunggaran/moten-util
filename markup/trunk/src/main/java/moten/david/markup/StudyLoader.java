package moten.david.markup;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import moten.david.markup.xml.study.Study;

import com.google.inject.Inject;

public class StudyLoader {

    private final StudyMarshaller marshaller;

    @Inject
    public StudyLoader(StudyMarshaller marshaller) {
        this.marshaller = marshaller;
    }

    public Study getStudy(String studyFilename) {

        try {
            return marshaller.unmarshal(new FileInputStream(studyFilename));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeStudy(Study study, String studyFilename) {
        try {
            marshaller.marshal(study, new FileOutputStream(studyFilename));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
