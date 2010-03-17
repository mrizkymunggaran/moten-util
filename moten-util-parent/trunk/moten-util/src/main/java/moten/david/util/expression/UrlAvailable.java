package moten.david.util.expression;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class UrlAvailable implements BooleanExpression, Operation {

    private final StringExpression urlString;
	private final NumericExpression timeoutMs;

	public UrlAvailable(StringExpression urlString, NumericExpression timeoutMs) {
		this.urlString = urlString;
		this.timeoutMs = timeoutMs;
    }

    public UrlAvailable(String urlString) {
        this(new Stringy(urlString), new Numeric(500));
    }

    @Override
    public boolean evaluate() {
        try {
            String urlString = this.urlString.evaluate();
            if (urlString == null)
                throw new RuntimeException("url is null");
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            long timeoutMs = this.timeoutMs.evaluate().longValue();
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
	public Expression[] getExpressions() {
		return new Expression[] {urlString, timeoutMs};
	}


}
