package org.moten.david.util.xsd.simplified;

import java.math.BigDecimal;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.w3._2001.xmlschema.ExplicitGroup;
import org.w3._2001.xmlschema.LocalElement;
import org.w3._2001.xmlschema.NoFixedFacet;
import org.w3._2001.xmlschema.OpenAttrs;
import org.w3._2001.xmlschema.TopLevelComplexType;
import org.w3._2001.xmlschema.TopLevelElement;
import org.w3._2001.xmlschema.TopLevelSimpleType;

public class Convertor {

	public Schema convert(org.w3._2001.xmlschema.Schema s) {
		Schema.Builder builder = new Schema.Builder();
		builder.namespace(s.getTargetNamespace());
		for (OpenAttrs a : s.getSimpleTypeOrComplexTypeOrGroup()) {
			if (a instanceof TopLevelComplexType) {
				TopLevelComplexType t = (TopLevelComplexType) a;
				builder.complexType(convert(t));
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

	private Element convert(TopLevelElement t) {
		Element.Builder builder = new Element.Builder();
		builder.name(t.getName()).type(t.getType());
		builder.maxOccurs(MaxOccurs.parse(t.getMaxOccurs()));
		if (t.getMinOccurs() != null)
			builder.minOccurs(t.getMinOccurs().intValue());

		return builder.build();
	}

	private SimpleType convert(TopLevelSimpleType t, String targetNamespace) {
		Restriction restriction = null;
		if (t.getRestriction() != null) {
			QName base = t.getRestriction().getBase();
			Restriction.Builder builder = new Restriction.Builder();
			for (Object facet : t.getRestriction().getFacets()) {
				if (facet instanceof JAXBElement) {
					JAXBElement e = (JAXBElement) facet;
					if (isNoFixedFacet(e)) {
						NoFixedFacet f = (NoFixedFacet) e.getValue();
						XsdType<?> xsdType = getXsdType(f, base);
						builder.enumeration(xsdType);
					}
				}
			}
			restriction = builder.build();
		}
		return new SimpleType(new QName(targetNamespace), restriction);
	}

	private boolean isNoFixedFacet(JAXBElement e) {
		return e.getName().toString()
				.equals("{http://www.w3.org/2001/XMLSchema}enumeration")
				&& e.getValue() instanceof NoFixedFacet;
	}

	private XsdType<?> getXsdType(NoFixedFacet f, QName base) {
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
				return new XsdDateTime(date.toGregorianCalendar());
			} catch (DatatypeConfigurationException e) {
				throw new RuntimeException(e);
			}

		} else
			throw new RuntimeException("base not supported:" + base);
	}

	private ComplexType convert(org.w3._2001.xmlschema.ComplexType t) {
		t.getAttributeOrAttributeGroup();
		if (t.getChoice() != null) {
			Group group = convert(t.getChoice());

		} else if (t.getSequence() != null) {
			Group group = convert(t.getSequence());
		}
		return null;
	}

	private Group convert(ExplicitGroup g) {
		BasicGroup.Builder builder = new BasicGroup.Builder();
		builder.minOccurs(g.getMinOccurs().intValue());
		builder.maxOccurs(MaxOccurs.parse(g.getMaxOccurs()));
		for (Object p : g.getParticle()) {
			if (p instanceof JAXBElement) {
				JAXBElement<?> e = (JAXBElement<?>) p;
				System.out.println(e.getDeclaredType() + " " + e.getName());

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

	private Element convert(LocalElement e) {
		Element.Builder builder = new Element.Builder();
		builder.name(e.getName()).type(e.getType());
		if (e.getMinOccurs() != null)
			builder.minOccurs(e.getMinOccurs().intValue());
		builder.maxOccurs(MaxOccurs.parse(e.getMaxOccurs()));
		return builder.build();
	}
}
