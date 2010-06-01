package moten.david.markup;

import java.util.List;

public class Study {
    private final String name;

    public Study(String name, List<Document> documents, Tags tags) {
        super();
        this.name = name;
        this.documents = documents;
        this.tags = tags;
    }

    private final List<Document> documents;
    private final Tags tags;

    public String getName() {
        return name;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public Tags getTags() {
        return tags;
    }
}
