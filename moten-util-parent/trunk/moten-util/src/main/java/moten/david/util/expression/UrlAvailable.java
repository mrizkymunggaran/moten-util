package moten.david.util.expression;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import moten.david.util.guice.ConstantProvider;

import com.google.inject.Provider;

public class UrlAvailable implements BooleanExpression, Provided<String> {

    private final Provider<String> urlProvider;
    private final Provider<Long> timeoutMsProvider;

    public UrlAvailable(Provider<String> provider,
            Provider<Long> timeoutMsProvider) {
        this.urlProvider = provider;
        this.timeoutMsProvider = timeoutMsProvider;
    }

    public UrlAvailable(String urlString, long timeoutMs) {
        this(new ConstantProvider<String>(urlString),
                new ConstantProvider<Long>(timeoutMs));
    }

    public UrlAvailable(String urlString) {
        this(urlString, 500);
    }

    @Override
    public boolean evaluate() {
        try {
            String urlString = urlProvider.get();
            if (urlString == null)
                throw new RuntimeException("url is null");
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            long timeoutMs = timeoutMsProvider.get();
            connection.setConnectTimeout((int) timeoutMs);
            if (connection instanceof HttpURLConnection) {
                return ((HttpURLConnection) connection).getResponseCode() != HttpURLConnection.HTTP_ACCEPTED;
            } else
                return connection.getContentLength() > 0;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }

    }

    @Override
    public Provider<String> getProvider() {
        return urlProvider;
    }

}
