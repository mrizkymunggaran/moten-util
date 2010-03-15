package moten.david.util.expression;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class UrlAvailable implements BooleanExpression {

	private final String urlString;
	private final long timeoutMs;

	public UrlAvailable(String urlString, long timeoutMs) {
		this.urlString = urlString;
		this.timeoutMs = timeoutMs;
	}

	public UrlAvailable(String urlString) {
		this(urlString, 500);
	}

	@Override
	public boolean evaluate() {
		try {
			if (urlString == null)
				return false;
			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();
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

}
