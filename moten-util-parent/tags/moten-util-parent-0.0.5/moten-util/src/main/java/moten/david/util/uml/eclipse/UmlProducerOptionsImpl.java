package moten.david.util.uml.eclipse;

public class UmlProducerOptionsImpl implements UmlProducerOptions {

	private final boolean includeAssociationEndLabels;
	private final boolean useFullClassNames;

	public UmlProducerOptionsImpl(boolean includeAssociationEndLabels,
			boolean useFullClassNames) {
		this.includeAssociationEndLabels = includeAssociationEndLabels;
		this.useFullClassNames = useFullClassNames;
	}

	public UmlProducerOptionsImpl() {
		this(false, false);
	}

	@Override
	public boolean includeAssociationEndLabels() {
		return includeAssociationEndLabels;
	}

	@Override
	public boolean useFullClassNames() {
		return useFullClassNames;
	}

}
