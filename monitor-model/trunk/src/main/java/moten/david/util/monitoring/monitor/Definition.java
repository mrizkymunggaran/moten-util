package moten.david.util.monitoring.monitor;

import model.AssociationEndPrimary;
import model.AssociationEndSecondary;
import model.Attribute;
import model.Class;
import model.Specialization;
import model.SpecializationGroup;
import moten.david.xuml.model.Multiplicity;
import moten.david.xuml.model.util.SystemBase;

public class Definition extends SystemBase {
	public Definition() {
		super(null, "monitoring");
		initialize();
	}

	private void initialize() {
		model.Package pkg = createRootPackage("monitoring", "monitoring");
		model.Class lookup = createClassWithArbitraryId(pkg, "Lookup", "Lookup");
		Attribute name = createAttribute(lookup, "name");
		Attribute time = createAttribute(lookup, "time");
		createIdentifierNonPrimary(lookup, "I2",
				new Attribute[] { name, time }, null);

		SpecializationGroup lookupAvailableGroup = createSpecializationGroup(
				lookup, "S1", "");
		Specialization lookupAvailable = createSpecialization(
				lookupAvailableGroup, pkg, "LookupAvailable", "");
		Specialization lookupNotAvailable = createSpecialization(
				lookupAvailableGroup, pkg, "LookupNotAvailable", "");
		SpecializationGroup latestLookupGroup = createSpecializationGroup(
				lookup, "S2", "");
		Specialization latestLookup = createSpecialization(latestLookupGroup,
				pkg, "LatestLookup", "");
		createArbitraryId(latestLookup);

		Specialization previousLookup = createSpecialization(latestLookupGroup,
				pkg, "PreviousLookup", "");
		createArbitraryId(previousLookup);

		Class property = createClassWithArbitraryId(pkg, "Property", "");
		Attribute propertyName = createAttribute(property, "name");
		propertyName.setMandatory(true);
		Attribute propertyValue = createAttribute(property, "value");
		propertyValue.setMandatory(true);
		createIdentifierNonPrimary(property, "I2", new Attribute[] {
				propertyName, propertyValue }, null);
		{
			AssociationEndPrimary e1 = createAssociationEndPrimary(
					lookupAvailable, Multiplicity.ONE, "are returned from");
			AssociationEndSecondary e2 = createAssociationEndSecondary(
					property, Multiplicity.MANY, "return");
			createAssociation("R1", e1, e2);
		}

	}
}
