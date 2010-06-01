package moten.david.markup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import com.google.common.base.Preconditions;

public class StudyFolder {

    public Study getStudy(String directory) {

        try {
            File base = new File(directory);
            File markup = new File(base, "markup");
            Preconditions
                    .checkArgument(markup.exists() && markup.isDirectory());
            String name = getName(markup);
            Tags tags = new Tags(new FileInputStream("tags.txt"));
            List<Document> documents = getDocuments(markup);
            Study study = new Study(name, documents, tags);
            return study;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Document> getDocuments(File markup) {
        // TODO
        return null;
    }

    private String getName(File markup) {
        Properties p = new Properties();
        try {
            p.load(new FileInputStream(new File(markup, "study")));
            return p.getProperty("name");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
