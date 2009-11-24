package moten.david.util.tv.ozlist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import moten.david.util.tv.Channel;
import moten.david.util.tv.ChannelsProvider;
import moten.david.util.tv.Configuration;
import moten.david.util.tv.programme.Programme;
import moten.david.util.tv.programme.ProgrammeItem;
import moten.david.util.tv.programme.ProgrammeProvider;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.inject.Inject;

public class ProgrammeProviderOzList implements ProgrammeProvider {

	private final ChannelsProvider stationsProvider;
	private final Configuration configuration;

	private static final Logger log = Logger
			.getLogger(ProgrammeProviderOzList.class.getName());

	@Inject
	public ProgrammeProviderOzList(Configuration configuration,
			ChannelsProvider stationsProvider) {
		this.configuration = configuration;
		this.stationsProvider = stationsProvider;
	}

	@Override
	public Programme getProgramme(Channel station, Date date) {
		File file1 = configuration.getProgrammeFile(station, new Date(date
				.getTime() - 24 * 3600 * 1000));
		Programme programme = getProgramme(file1);
		File file2 = configuration.getProgrammeFile(station, date);
		programme.addAll(getProgramme(file2));
		File file3 = configuration.getProgrammeFile(station, new Date(date
				.getTime() + 24 * 3600 * 1000));
		programme.addAll(getProgramme(file3));
		HashSet<ProgrammeItem> remove = new HashSet<ProgrammeItem>();
		for (ProgrammeItem item : programme) {
			if (item.getStop().before(date))
				remove.add(item);
		}
		programme.removeAll(remove);
		log.info("returning " + programme.size() + " items for " + station
				+ " " + date);
		return programme;
	}

	private Programme getProgramme(File file) {
		try {
			Programme schedule = new Programme();
			if (!file.exists())
				return schedule;
			InputStream is = new FileInputStream(file);
			DocumentBuilderFactory domFactory = DocumentBuilderFactory
					.newInstance();
			domFactory.setNamespaceAware(true); // never forget this!
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse(is);

			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile("//tv/programme");

			NodeList nodes = (NodeList) expr.evaluate(doc,
					XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				ProgrammeItem item = new ProgrammeItem();
				setItemFields(node, item);
				schedule.add(item);
			}
			return schedule;
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (DOMException e) {
			throw new RuntimeException(e);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

	}

	private void setItemFields(Node node, ProgrammeItem item)
			throws DOMException, ParseException {
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss Z");
		Date start = df.parse(node.getAttributes().getNamedItem("start")
				.getNodeValue());
		Date stop = df.parse(node.getAttributes().getNamedItem("stop")
				.getNodeValue());
		String channel = node.getAttributes().getNamedItem("channel")
				.getNodeValue();
		item.setStart(start);
		item.setStop(stop);
		item.setChannelId(channel);
		for (int i = 0; i < node.getChildNodes().getLength(); i++) {
			Node nd = node.getChildNodes().item(i);
			if (nd.getNodeName().equals("desc"))
				item.setDescription(nd.getTextContent());
			else if (nd.getNodeName().equals("title"))
				item.setTitle(nd.getTextContent());
			else if (nd.getNodeName().equals("sub-title"))
				item.setSubTitle(nd.getTextContent());
			else if (nd.getNodeName().equals("category"))
				item.getCategories().add(nd.getTextContent());
			else
				// ignore
				;
		}
	}

	@Override
	public Channel[] getStations() {
		return stationsProvider.getChannels();
	}

	public static void main(String[] args) {
		Configuration configuration = new Configuration();
		ChannelsProvider stationsProvider = new ChannelsProviderOzList(
				configuration);
		ProgrammeProvider scheduleProvider = new ProgrammeProviderOzList(
				configuration, stationsProvider);

		for (Channel station : stationsProvider.getChannels()) {
			Programme schedule = scheduleProvider.getProgramme(station,
					new Date());
			if (schedule.size() > 0)
				System.out.println(schedule);
		}
	}
}
