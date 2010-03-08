package moten.david.util.monitoring.monitor;

import model.AssociationEndPrimary;
import model.AssociationEndSecondary;
import model.Attribute;
import model.AttributeReferential;
import model.CallEvent;
import model.Class;
import model.Primitive;
import model.State;
import moten.david.xuml.model.Generator;
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
		Class lookupName = createClass(pkg, "LookupName", "");
		Attribute name = createAttribute(lookupName, "name");
		createIdentifierPrimary(name, Generator.NOT_GENERATED);
		{
			AssociationEndPrimary primary = createAssociationEndPrimary(lookup,
					Multiplicity.MANY, "categorizes");
			AssociationEndSecondary secondary = createAssociationEndSecondary(
					lookupName, Multiplicity.ONE, "is categorized by");
			createAssociation("R2", primary, secondary);
			AttributeReferential ref = createAttributeReferential(lookup,
					secondary, null);
			Attribute time = createAttribute(lookup, "time",
					Primitive.TIMESTAMP);
			createIdentifierNonPrimary(lookup, "I2", new Attribute[] { time },
					new AttributeReferential[] { ref });
		}

		createAttribute(lookup, "problem").setMandatory(false);
		CallEvent available = createCallEvent(lookup, "Available");
		State availableState = createState(lookup, "Available");
		createTransition(lookup.getStateMachine().getInitialState(),
				availableState, available);
		CallEvent addProperty = createCallEvent(lookup, "AddProperty");
		createParameter(addProperty, "name");
		createParameter(addProperty, "value");
		createTransition(availableState, availableState, addProperty);
		CallEvent notAvailable = createCallEvent(lookup, "NotAvailable");
		State notAvailableState = createState(lookup, "NotAvailable");
		createTransition(lookup.getStateMachine().getInitialState(),
				notAvailableState, notAvailable);
		CallEvent remove = createCallEvent(lookup, "Remove");
		createTransition(availableState, lookup.getStateMachine()
				.getFinalState(), remove);
		createTransition(notAvailableState, lookup.getStateMachine()
				.getFinalState(), remove);
		{
			{
				AssociationEndPrimary primary = createAssociationEndPrimary(
						lookup, Multiplicity.ZERO_ONE, "has latest");
				AssociationEndSecondary secondary = createAssociationEndSecondary(
						lookupName, Multiplicity.ZERO_ONE, "is latest for");
				createAssociation("R3", primary, secondary);
			}
		}

		Class property = createClassWithArbitraryId(pkg, "Property", "");
		Attribute propertyName = createAttribute(property, "name");
		propertyName.setMandatory(true);
		Attribute propertyValue = createAttribute(property, "value");
		propertyValue.setMandatory(true);
		createIdentifierNonPrimary(property, "I2", new Attribute[] {
				propertyName, propertyValue }, null);
		{
			AssociationEndPrimary e1 = createAssociationEndPrimary(lookup,
					Multiplicity.ONE, "returned from");
			AssociationEndSecondary e2 = createAssociationEndSecondary(
					property, Multiplicity.MANY, "return");
			createAssociation("R1", e1, e2);
		}

	}
}
