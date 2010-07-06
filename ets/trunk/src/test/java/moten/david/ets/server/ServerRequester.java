package moten.david.ets.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ServerRequester {

    public static void addFix(String ids, long time, double lat, double lon) {
        try {

            URL url = new URL("http://localhost:8888/fix?ids="
                    + ids.replace(":", "%3A").replace(";", "%3B") + "&time="
                    + time + "&lat=" + lat + "&lon=" + lon);
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
        new URL("http://localhost:8888/datastore?command=clear").openStream();
        addFix("name:dave", 1000000, 121.0, 141.0);
        addFix("name:dave", 2000000, 122.0, 142.0);
        addFix("name:dave", 3000000, 123.0, 143.0);
        addFix("name:dave", 4000000, 124.0, 144.0);
        addFix("name:dave;nickname:davo", 5000000, 125.0, 145.0);
        addFix("nickname:davo", 6000000, 126.0, 146.0);
        addFix("nickname:davo;licence-no:1", 7000000, 127.0, 147.0);
        addFix("licence-no:1", 8000000, 128.0, 148.0);
        addFix("licence-no:2", 8000000, 129.0, 149.0);
    }
}
