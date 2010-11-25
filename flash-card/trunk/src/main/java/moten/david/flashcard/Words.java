package moten.david.flashcard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import com.google.api.translate.Language;

public class Words {

    private final Map<String, String> map = new TreeMap<String, String>();

    public Words(File file, Language from, Language to) {
        try {
            Set<String> unknown = new TreeSet<String>();
            Translator translator = new Translator(from,to);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null)
                if (line.trim().length() > 0 && !line.startsWith("#")) {
                    line = line.trim();
                    if (line.contains("=")) {
                        String[] items = line.split("=");
                        map.put(items[0].trim(), items[1].trim());
                    }
                    else {
                        String translated = translator.translate(line);
                        if (translated!=null)
                            map.put(line, translated);
                        else
                            unknown.add(line);
                    }
                }
            br.close();
            FileOutputStream fos = new FileOutputStream(file);
            for (Entry<String,String> entry: map.entrySet())
                fos.write((entry.getKey() + " = " + entry.getValue()+"\n").getBytes());
            for (String s:unknown)
                fos.write((s + "\n").getBytes());
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> getMeanings() {
        return map;
    }

}
