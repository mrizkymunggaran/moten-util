package moten.david.ets.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class ServerRequester {

    public static void addFix(String ids, long time, double lat, double lon) {
        try {

            URL url = new URL("http://localhost:8888/fix?ids="
                    + ids.replace(":", "%3A").replace(";", "%3B") + "&time="
                    + (time * 1000000) + "&lat=" + lat + "&lon=" + lon);
            String s = read(url.openStream());
            System.out.println(s);
            if (s.contains("Exception"))
                throw new RuntimeException("failed");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String read(InputStream is) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        int ch;
        try {
            while ((ch = is.read()) != -1)
                bytes.write(ch);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bytes.toString();
    }

    public static void main(String[] args) throws MalformedURLException,
            IOException {
        // http://localhost:8888/_ah/admin
        new URL("http://localhost:8888/datastore?command=clear").openStream();
        long time = 0;
        addFix("name:dave", time++, 121.0, 141.0);
        addFix("name:dave", time++, 122.0, 142.0);
        addFix("name:dave", time++, 123.0, 143.0);
        addFix("name:dave", time++, 124.0, 144.0);
        addFix("name:dave;nickname:davo", time++, 125.0, 145.0);
        addFix("nickname:davo", time++, 126.0, 146.0);
        addFix("nickname:davo;licence-no:1", time++, 127.0, 147.0);
        addFix("licence-no:1", time++, 128.0, 148.0);
        addFix("licence-no:2", time++, 129.0, 149.0);
        for (int i = 0; i < 1; i++)
            addFix("name:dave" + new Random().nextInt(100), time++,
                    120 + time / 1000.0, 140 + time / 1000.0);
    }
}
