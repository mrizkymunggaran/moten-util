package moten.david.markup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import moten.david.markup.xml.study.Document;
import moten.david.markup.xml.study.Study;

import org.apache.commons.io.IOUtils;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class CurrentStudy implements Provider<Study> {

    private static final String STUDY_PATH = "src/test/resources/study1";
    private final Study study;
    private final StudyFolder studyFolder;

    @Inject
    public CurrentStudy(StudyFolder studyFolder) {
        this.studyFolder = studyFolder;
        study = studyFolder.getStudy(STUDY_PATH);
    }

    @Override
    public Study get() {
        return study;
    }

    public void save() {
        studyFolder.writeStudy(study, STUDY_PATH);
    }

    public String getText(String filename) {
        File parent = new File(STUDY_PATH);
        for (Document document : study.getDocument())
            if (document.getName().equals(filename)) {
                File file = new File(parent, filename);
                try {
                    String text = IOUtils.toString(new FileInputStream(file));
                    return text;
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        throw new RuntimeException(filename + " not found");
    }

}
