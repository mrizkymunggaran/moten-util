package moten.david.util.tv.ozlist;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

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
			DocumentBuilderFactory domFactory = DocumentBuilderFactory
					.newInstance();
			domFactory.setNamespaceAware(true); // never forget this!
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse(is);

			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile("//tv/channel/@id");

			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				String id = node.getNodeValue();
				Channel station = new Channel();
				station.setId(id);
				{
					getDisplayName(xpath, doc, station);
					getBaseUrls(xpath, doc, station);
					getDataFor(xpath, doc, station);
				}
				station.setAlias(getAlias(station.getId()));
				stations.add(station);
			}

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

	private String getAlias(String id) {
		// TODO Auto-generated method stub
		return null;
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
