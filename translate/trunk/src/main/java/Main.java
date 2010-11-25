import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.google.api.translate.Language;
import com.google.api.translate.Translate;

public class Main {
  public static void main(String[] args) throws Exception {
    // Set the HTTP referrer to your website address.
    Translate.setHttpReferrer("http://code.google.com/p/moten-util/");

    String translatedText = Translate.execute("Bonjour le monde", Language.FRENCH, Language.ENGLISH);

    System.out.println(translatedText);
    
    BufferedReader br = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("/words.txt")));
    String line;
    while ((line=br.readLine())!=null) {
        String translated= Translate.execute(line, Language.ENGLISH, Language.INDONESIAN);
        System.out.println(line + " = " + translated);
    }
    br.close();
  }
}