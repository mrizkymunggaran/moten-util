package org.moten.david.util.xsd.form.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.moten.david.util.xsd.Convertor;
import org.moten.david.util.xsd.Marshaller;
import org.moten.david.util.xsd.form.client.GreetingService;
import org.moten.david.util.xsd.form.shared.FieldVerifier;
import org.moten.david.util.xsd.simplified.Schema;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	private static Logger log = Logger.getLogger(GreetingServiceImpl.class
			.getName());

	public String greetServer(String input) throws IllegalArgumentException {
		// Verify that the input is valid.
		if (!FieldVerifier.isValidName(input)) {
			// If the input is not valid, throw an IllegalArgumentException back
			// to
			// the client.
			throw new IllegalArgumentException(
					"Name must be at least 4 characters long");
		}

		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		// Escape data from the client to avoid cross-site script
		// vulnerabilities.
		input = escapeHtml(input);
		userAgent = escapeHtml(userAgent);

		return "Hello, " + input + "!<br><br>I am running " + serverInfo
				+ ".<br><br>It looks like you are using:<br>" + userAgent;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html
	 *            the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}

	public Schema getSchema(String namespace) {
		try {
			Convertor c = new Convertor();
			Marshaller m = new Marshaller();
			org.w3._2001.xmlschema.Schema s = m
					.unmarshal(GreetingServiceImpl.class
							.getResourceAsStream("/test-complex.xsd"));
			Schema schema = c.convert(s);
			return schema;
		} catch (RuntimeException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
	}
}
