package moten.david.util.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;

public class ReaderEnumeration implements Enumeration<String> {
    BufferedReader br;
    String line;
    private final InputStream is;

    public ReaderEnumeration(InputStream is) {
        this.is = is;
    }

    private void init() {
        try {
            br = new BufferedReader(new InputStreamReader(is));
            line = br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasMoreElements() {
        if (br == null)
            init();
        return line != null;
    }

    @Override
    public String nextElement() {
        String result = line;
        try {
            line = br.readLine();
            if (line == null)
                br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
