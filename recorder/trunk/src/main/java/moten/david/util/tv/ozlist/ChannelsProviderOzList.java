package moten.david.util.tv.ozlist;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import moten.david.util.tv.DataFor;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.inject.Inject;

public class ChannelsProviderOzList implements ChannelsProvider {

	private static Logger log = Logger.getLogger(ChannelsProviderOzList.class
			.getName());

	private final Configuration configuration;

	@Inject
	public ChannelsProviderOzList(Configuration configuration) {
		this.configuration = configuration;
	}

	@Override
	public Channel[] getChannels() {
		try {
			InputStream is = new FileInputStream(configuration.getDataList());
			ArrayList<Channel> stations = new ArrayList<Channel>();
			log.info("document builder factory");
			DocumentBuilderFactory domFactory = DocumentBuilderFactory
					.newInstance();
			domFactory.setNamespaceAware(true); // never forget this!
			log.info("new document builder");
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse(is);

			log.info("new xpath factory");
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			log.info("compiling xpath");
			XPathExpression expr = xpath.compile("//tv/channel");

			log.info("evaluating xpath expression");
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++) {
				Node channel = nodes.item(i);
				String id = channel.getAttributes().getNamedItem("id")
						.getNodeValue();
				NodeList children = channel.getChildNodes();
				Channel station = new Channel();
				station.setId(id);
				for (int j = 0; j < children.getLength(); j++) {
					Node node = children.item(j);
					if (node.getNodeName().equals("display-name"))
						station.setDisplayName(node.getTextContent());
					else if (node.getNodeName().equals("base-url"))
						station.getBaseUrls().add(node.getTextContent());
					else if (node.getNodeName().equals("datafor")) {
						DataFor dataFor = new DataFor();
						{
							DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
							dataFor.setDate(df.parse(node.getTextContent()));
						}
						{
							Node nd = node.getAttributes().getNamedItem(
									"lastmodified");
							DateFormat df = new SimpleDateFormat(
									"yyyyMMddHHmmss Z");
							dataFor
									.setLastModified(df
											.parse(nd.getNodeValue()));
						}
						station.getDataFor().add(dataFor);
					}
				}
				stations.add(station);
			}
			log.info("done");
			return stations.toArray(new Channel[] {});
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		} catch (DOMException e) {
			throw new RuntimeException(e);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	private void getDisplayName(XPath xpath, Document doc, Channel station)
			throws XPathExpressionException {
		XPathExpression search = xpath.compile("//tv/channel[@id='"
				+ station.getId() + "']/display-name");
		NodeList nodes = (NodeList) search
				.evaluate(doc, XPathConstants.NODESET);
		for (int j = 0; j < nodes.getLength(); j++) {
			Node node = nodes.item(j);
			station.setDisplayName(node.getTextContent());
		}

	}

	private void getBaseUrls(XPath xpath, Document doc, Channel station)
			throws XPathExpressionException, DOMException, ParseException {
		XPathExpression search = xpath.compile("//tv/channel[@id='"
				+ station.getId() + "']/base-url");
		NodeList nodes = (NodeList) search
				.evaluate(doc, XPathConstants.NODESET);
		for (int j = 0; j < nodes.getLength(); j++) {
			Node node = nodes.item(j);
			station.getBaseUrls().add(node.getTextContent());
		}

	}

	private void getDataFor(XPath xpath, Document doc, Channel station)
			throws XPathExpressionException, DOMException, ParseException {
		XPathExpression search = xpath.compile("//tv/channel[@id='"
				+ station.getId() + "']/datafor");
		NodeList nodes = (NodeList) search
				.evaluate(doc, XPathConstants.NODESET);
		for (int j = 0; j < nodes.getLength(); j++) {
			Node node = nodes.item(j);
			DataFor dataFor = new DataFor();
			{
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				dataFor.setDate(df.parse(node.getTextContent()));
			}
			{
				Node nd = node.getAttributes().getNamedItem("lastmodified");
				DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss Z");
				dataFor.setLastModified(df.parse(nd.getNodeValue()));
			}
			station.getDataFor().add(dataFor);
		}

	}

	public static void main(String[] args) {
		ChannelsProviderOzList s = new ChannelsProviderOzList(
				new Configuration());
		s.getChannels();
	}
}
