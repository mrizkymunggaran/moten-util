package moten.david.flashcard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

public class Words {

    private final Map<String,String> map= new TreeMap<String,String>();
    
    public Words(InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        try {
            while ((line=br.readLine())!=null)
            if (line.trim().length()>0 && !line.startsWith("#")&& line.contains("=")){
                String[] items = line.split("=");
                map.put(items[0].trim(),items[1].trim());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public Map<String, String> getMeanings() {
        return map;
    }
    
}
