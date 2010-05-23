package moten.david.util.words;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class DictionarySowpods implements Dictionary {

    private static Logger log = Logger.getLogger(DictionarySowpods.class
            .getName());
    private final Set<String> set;

    public DictionarySowpods() {
        set = load("/sowpods.txt");
    }

    private static Set<String> load(String resourceName) {
        log.info("loading");
        BufferedReader br = new BufferedReader(new InputStreamReader(
                DictionarySowpods.class.getResourceAsStream(resourceName)));
        String line;
        Builder<String> builder = ImmutableSet.builder();
        try {
            while ((line = br.readLine()) != null) {
                builder.add(line.trim());
            }
            Set<String> set = builder.build();
            log.info("loaded");
            return set;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isValid(String word) {
        return set.contains(word.trim().toUpperCase());
    }

}
