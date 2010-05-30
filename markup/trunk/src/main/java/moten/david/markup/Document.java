package moten.david.markup;

public class Document {
    private String filename;
    private DocumentTags documentTags;

    public Document(String filename, DocumentTags documentTags) {
        super();
        this.filename = filename;
        this.documentTags = documentTags;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public DocumentTags getDocumentTags() {
        return documentTags;
    }

    public void setDocumentTags(DocumentTags documentTags) {
        this.documentTags = documentTags;
    }
}
