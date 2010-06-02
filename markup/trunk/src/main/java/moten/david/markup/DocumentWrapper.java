package moten.david.markup;

import moten.david.markup.xml.study.Document;

public class DocumentWrapper {
    private final Document document;

    public DocumentWrapper(Document document) {
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }

    @Override
    public String toString() {
        return document.getName();
    }

}
