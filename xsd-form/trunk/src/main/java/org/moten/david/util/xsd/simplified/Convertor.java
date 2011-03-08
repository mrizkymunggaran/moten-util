package org.moten.david.util.xsd.simplified;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

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
		if (t.getMaxOccurs() != null) {
			if ("unbounded".equals(t.getMaxOccurs()))
				builder.maxUnbounded(true);
			else
				builder.maxOccurs(Integer.parseInt(t.getMaxOccurs()));
		}
		if (t.getMinOccurs() != null)
			builder.minOccurs(t.getMinOccurs().intValue());

		return builder.build();
	}

	private SimpleType<?> convert(TopLevelSimpleType t, String targetNamespace) {

		if (t.getRestriction() != null) {
			QName base = t.getRestriction().getBase();
			for (Object facet : t.getRestriction().getFacets()) {
				if (facet instanceof JAXBElement) {
					JAXBElement e = (JAXBElement) facet;
					if (e.getName()
							.toString()
							.equals("{http://www.w3.org/2001/XMLSchema}enumeration")
							&& e.getValue() instanceof NoFixedFacet) {
						NoFixedFacet f = (NoFixedFacet) e.getValue();
					}
				}
			}
		}
		SimpleType s = new SimpleType(new QName(targetNamespace));
		return s;
	}

	private ComplexType convert(TopLevelComplexType t) {

		// TODO Auto-generated method stub
		return null;
	}
}
