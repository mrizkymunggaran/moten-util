package moten.david.util.uml.eclipse;

public class UmlProducerOptionsImpl implements UmlProducerOptions {

	private final boolean includeAssociationEndLabels;

	public UmlProducerOptionsImpl(boolean includeAssociationEndLabels) {
		this.includeAssociationEndLabels = includeAssociationEndLabels;
	}

	@Override
	public boolean includeAssociationEndLabels() {
		return includeAssociationEndLabels;
	}

}
