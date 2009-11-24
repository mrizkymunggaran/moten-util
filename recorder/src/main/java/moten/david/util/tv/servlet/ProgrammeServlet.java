package moten.david.util.tv.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import moten.david.util.tv.Channel;
import moten.david.util.tv.ChannelsProvider;
import moten.david.util.tv.Util;
import moten.david.util.tv.programme.Programme;
import moten.david.util.tv.programme.ProgrammeItem;
import moten.david.util.tv.programme.ProgrammeProvider;

import com.google.inject.Inject;
import com.jamesmurty.utils.XMLBuilder;

public class ProgrammeServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int CHANNEL_WIDTH = 100;
	@Inject
	private ProgrammeProvider programmeProvider;
	@Inject
	private ChannelsProvider channelsProvider;
	private final Channel[] allChannels;

	public ProgrammeServlet() {
		ApplicationInjector.getInjector().injectMembers(this);
		allChannels = channelsProvider.getChannels();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		List<String> channels = readChannels();
		try {

			XMLBuilder builder = XMLBuilder.create("html").e("head").e("title")
					.t("Programme").up().e("link").a("rel", "stylesheet").a(
							"href", "style.css").up().up().e("body");

			for (String channelName : channels) {
				builder = builder.e("table").a(
						"style",
						"width:" + (CHANNEL_WIDTH + getWidth(24 * 60) * 3 / 2)
								+ "px;table-layout:fixed");
				Channel channel = Util.getChannel(channelName, allChannels);
				builder = builder.e("tr").e("td").a("style",
						"width:" + CHANNEL_WIDTH + "px").e("p").t(
						channel.getDisplayName()).up().up();
				Programme programme = programmeProvider.getProgramme(channel,
						new Date());
				if (programme.size() > 0) {
					DateFormat df = new SimpleDateFormat("HH");
					DateFormat df2 = new SimpleDateFormat("mm");
					Date start = programme.get(0).getStart();
					int minutes = Integer.parseInt(df.format(start)) * 60
							+ Integer.parseInt(df2.format(start));
					builder = builder.e("td").a("style",
							"width:" + getWidth(minutes) + "px").up();
				}
				for (ProgrammeItem item : programme) {
					DateFormat df = new SimpleDateFormat("HH:mm");
					int width = getWidth(item.getDurationMinutes());
					builder = builder.e("td").a("style",
							"width:" + width + "px").e("p").t(
							df.format(item.getStart()) + " " + item.getTitle()
									+ " " + item.getDurationMinutes() + "mins")
							.up().up();
				}
				builder = builder.up().up();
			}

			Properties outputProperties = new Properties();

			// Explicitly identify the output as an XML document
			outputProperties.put(javax.xml.transform.OutputKeys.METHOD, "xml");

			// Pretty-print the XML output (doesn't work in all cases)
			outputProperties.put(javax.xml.transform.OutputKeys.INDENT, "yes");

			// Get 2-space indenting when using the Apache transformer
			outputProperties.put("{http://xml.apache.org/xslt}indent-amount",
					"2");
			String result = builder.asString(outputProperties);
			result = result.substring(result.indexOf("\n") + 1);
			resp.getOutputStream().write(result.getBytes());
		} catch (ParserConfigurationException e) {
			throw new ServletException(e);
		} catch (FactoryConfigurationError e) {
			throw new ServletException(e);
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}
	}

	private int getWidth(int minutes) {
		return 4 * minutes;
	}

	private List<String> readChannels() {
		BufferedReader br = new BufferedReader(new InputStreamReader(getClass()
				.getResourceAsStream("/channels.txt")));
		ArrayList<String> list = new ArrayList<String>();
		String line;
		try {
			while ((line = br.readLine()) != null) {
				if (line.trim().length() > 0) {
					String[] items = line.split("\t");
					list.add(items[0]);
				}
			}
			return list;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
