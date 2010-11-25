package moten.david.flashcard;

import com.google.api.translate.Language;
import com.google.api.translate.Translate;

public class Translator {

    private final Language from;
    private final Language to;

    public Translator(Language from, Language to) {
        this.from = from;
        this.to = to;
    }
    
    public String translate(String text) {
        System.out.println("looking up: " + text);
        Translate.setHttpReferrer("http://code.google.com/p/moten-util/");

        String translatedText;
        try {
            translatedText = Translate.execute(text, from , to);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return translatedText;
    }
}
