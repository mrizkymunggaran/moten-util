package org.moten.david.util.xsd;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.moten.david.util.xsd.simplified.BasicGroup;
import org.moten.david.util.xsd.simplified.Choice;
import org.moten.david.util.xsd.simplified.ComplexType;
import org.moten.david.util.xsd.simplified.Element;
import org.moten.david.util.xsd.simplified.Group;
import org.moten.david.util.xsd.simplified.MaxOccurs;
import org.moten.david.util.xsd.simplified.QName;
import org.moten.david.util.xsd.simplified.Restriction;
import org.moten.david.util.xsd.simplified.Schema;
import org.moten.david.util.xsd.simplified.Sequence;
import org.moten.david.util.xsd.simplified.SimpleType;
import org.moten.david.util.xsd.simplified.XsdDateTime;
import org.moten.david.util.xsd.simplified.XsdDecimal;
import org.moten.david.util.xsd.simplified.XsdInteger;
import org.moten.david.util.xsd.simplified.XsdString;
import org.moten.david.util.xsd.simplified.XsdType;
import org.w3._2001.xmlschema.ExplicitGroup;
import org.w3._2001.xmlschema.Facet;
import org.w3._2001.xmlschema.LocalElement;
import org.w3._2001.xmlschema.NoFixedFacet;
import org.w3._2001.xmlschema.OpenAttrs;
import org.w3._2001.xmlschema.Pattern;
import org.w3._2001.xmlschema.TopLevelComplexType;
import org.w3._2001.xmlschema.TopLevelElement;
import org.w3._2001.xmlschema.TopLevelSimpleType;

@SuppressWarnings("restriction")
public class Convertor {

	private static Logger log = Logger.getLogger(Convertor.class.getName());

	public Schema convert(org.w3._2001.xmlschema.Schema s) {
		log.info("converting schema: " + s.getTargetNamespace());
		Schema.Builder builder = new Schema.Builder();
		builder.namespace(s.getTargetNamespace());
		for (OpenAttrs a : s.getSimpleTypeOrComplexTypeOrGroup()) {
			if (a instanceof TopLevelComplexType) {
				TopLevelComplexType t = (TopLevelComplexType) a;
				builder.complexType(convert(t, s.getTargetNamespace()));
			} else if (a instanceof TopLevelSimpleType) {
				TopLevelSimpleType t = (TopLevelSimpleType) a;
				builder.simpleType(convert(t, s.getTargetNamespace()));
			} else if (a instanceof TopLevelElement) {
				TopLevelElement t = (TopLevelElement) a;
				builder.element(convert(t));
			}
		}
		return builder.build();
	}

	private Element convert(TopLevelElement e) {
		log.info("converting top level element: " + e.getName());
		Map<javax.xml.namespace.QName, String> attributes;
		if (e.getAnnotation() == null)
			attributes = null;
		else
			attributes = e.getAnnotation().getOtherAttributes();
		return convertElement(e.getName(), e.getType(), attributes,
				e.getMinOccurs(), e.getMaxOccurs());
	}

	private static QName toQName(javax.xml.namespace.QName q) {
		return new QName(q.getNamespaceURI(), q.getLocalPart());
	}

	private SimpleType convert(TopLevelSimpleType t, String targetNamespace) {
		log.info("converting top level simple type: " + t.getName());
		return new SimpleType(new QName(targetNamespace, t.getName()),
				convert(t.getRestriction()));
	}

	private Restriction convert(org.w3._2001.xmlschema.Restriction restriction) {
		log.info("converting restriction");
		if (restriction == null)
			return null;
		javax.xml.namespace.QName base = restriction.getBase();
		Restriction.Builder builder = new Restriction.Builder();
		if (base != null)
			builder.base(toQName(base));
		for (Object facet : restriction.getFacets()) {
			if (facet instanceof JAXBElement) {

				JAXBElement<?> e = (JAXBElement<?>) facet;
				log.info("JAXBElement facet: " + e.getName());
				if (isFacet(e, "enumeration")) {
					NoFixedFacet f = (NoFixedFacet) e.getValue();
					XsdType<?> xsdType = getXsdType(f, base);
					builder.enumeration(xsdType);
				} else if (isFacet(e, "maxInclusive"))
					builder.maxInclusive(getBigDecimalFromFacet(e));
				else if (isFacet(e, "minInclusive"))
					builder.minInclusive(getBigDecimalFromFacet(e));
				else if (isFacet(e, "maxExclusive"))
					builder.maxExclusive(getBigDecimalFromFacet(e));
				else if (isFacet(e, "minExclusive"))
					builder.minExclusive(getBigDecimalFromFacet(e));
				else
					throw new RuntimeException("unsupported facet: "
							+ e.getValue());
			} else if (facet instanceof Pattern) {
				Pattern pattern = (Pattern) facet;
				log.info("pattern=" + pattern.getValue());
				builder.pattern(pattern.getValue());
			} else
				throw new RuntimeException("unsupported facet "
						+ facet.getClass());
		}
		return builder.build();
	}

	private BigDecimal getBigDecimalFromFacet(JAXBElement<?> e) {
		Facet f = (Facet) e.getValue();
		return new BigDecimal(f.getValue());
	}

	private boolean isFacet(JAXBElement<?> e, String name) {
		javax.xml.namespace.QName qName = new javax.xml.namespace.QName(
				Schema.XML_SCHEMA_NAMESPACE, name);
		return e.getName().equals(qName) && e.getValue() instanceof Facet;
	}

	private XsdType<?> getXsdType(NoFixedFacet f, javax.xml.namespace.QName base) {
		log.info("converting facet to XsdType: " + f.getValue());
		if ("{http://www.w3.org/2001/XMLSchema}decimal".equals(base.toString())) {
			return new XsdDecimal(new BigDecimal(f.getValue()));
		} else if ("{http://www.w3.org/2001/XMLSchema}integer".equals(base
				.toString())) {
			return new XsdInteger(new Integer(f.getValue()));
		} else if ("{http://www.w3.org/2001/XMLSchema}string".equals(base
				.toString())) {
			return new XsdString(f.getValue());
		} else if ("{http://www.w3.org/2001/XMLSchema}dateTime".equals(base
				.toString())) {
			try {
				DatatypeFactory factory = DatatypeFactory.newInstance();
				XMLGregorianCalendar date = factory.newXMLGregorianCalendar(f
						.getValue());
				return new XsdDateTime(date.toGregorianCalendar().getTime());
			} catch (DatatypeConfigurationException e) {
				throw new RuntimeException(e);
			}

		} else
			throw new RuntimeException("base not supported:" + base);
	}

	private ComplexType convert(org.w3._2001.xmlschema.ComplexType t,
			String targetNamespace) {
		t.getAttributeOrAttributeGroup();
		Group group = null;
		if (t.getChoice() != null) {
			group = convert(t.getChoice());

		} else if (t.getSequence() != null) {
			group = convert(t.getSequence());
		} else
			throw new RuntimeException(
					"complexType must have sequence or choice children only");
		ComplexType complexType = new ComplexType(new QName(targetNamespace,
				t.getName()), group, null);
		return complexType;
	}

	private Group convert(ExplicitGroup g) {
		BasicGroup.Builder builder = new BasicGroup.Builder();
		builder.minOccurs(g.getMinOccurs().intValue());
		builder.maxOccurs(MaxOccurs.parse(g.getMaxOccurs()));
		for (Object p : g.getParticle()) {
			if (p instanceof JAXBElement) {
				JAXBElement<?> e = (JAXBElement<?>) p;
				log.info("particle: " + e.getDeclaredType() + " " + e.getName());
				Object value = e.getValue();
				if (value instanceof LocalElement) {
					Element element = convert((LocalElement) value);
					builder.particle(element);
				} else if (value instanceof ExplicitGroup) {
					Group group = convert((ExplicitGroup) value);
					if ("{http://www.w3.org/2001/XMLSchema}choice".equals(e
							.getName().toString())) {
						builder.particle(new Choice(group));
					} else if ("{http://www.w3.org/2001/XMLSchema}sequence"
							.equals(e.getName().toString())) {
						builder.particle(new Sequence(group));
					} else
						throw new RuntimeException("not supported:"
								+ e.getName());
				}
			}
		}
		return builder.build();

	}

	private String getInfo(Map<javax.xml.namespace.QName, String> attributes,
			String name) {
		return attributes.get(new javax.xml.namespace.QName(
				Schema.APPINFO_NAMESPACE, name));
	}

	private Element convertElement(String name,
			javax.xml.namespace.QName qName,
			Map<javax.xml.namespace.QName, String> attributes,
			BigInteger minOccurs, String maxOccurs) {
		Element.Builder builder = new Element.Builder();
		builder.name(name).type(toQName(qName));
		builder.displayName(Util.toDisplay(name));
		if (attributes != null) {
			String displayName = getInfo(attributes, "label");
			if (displayName != null)
				builder.displayName(displayName);
			String description = getInfo(attributes, "description");
			builder.description(description);
			String validation = getInfo(attributes, "validation");
			builder.validation(validation);
			String before = getInfo(attributes, "before");
			builder.before(before);
			String after = getInfo(attributes, "after");
			builder.after(after);
			Integer lines = toInteger(getInfo(attributes, "lines"));
			builder.lines(lines);
			Integer cols = toInteger(getInfo(attributes, "cols"));
			builder.cols(cols);
		}
		if (minOccurs != null)
			builder.minOccurs(minOccurs.intValue());
		builder.maxOccurs(MaxOccurs.parse(maxOccurs));
		return builder.build();
	}

	private Integer toInteger(String s) {
		if (s == null)
			return null;
		else
			return Integer.parseInt(s);
	}

	private Map<javax.xml.namespace.QName, String> getAttributes(LocalElement e) {
		if (e.getAnnotation() == null)
			return null;
		else
			return e.getAnnotation().getOtherAttributes();
	}

	private Element convert(LocalElement e) {
		return convertElement(e.getName(), e.getType(), getAttributes(e),
				e.getMinOccurs(), e.getMaxOccurs());
	}
}
