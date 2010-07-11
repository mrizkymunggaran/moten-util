package moten.david.util.monitoring.lookup;

import java.util.HashMap;

public class LookupParameters extends HashMap<LookupType, String> {

	private static final long serialVersionUID = 987306568289150470L;

	public LookupParameters(LookupParameters parameters) {
		super(parameters);
	}

	public LookupParameters() {
		super();
	}

	public LookupParameters(LookupType lookupType, String value) {
		super();
		this.put(lookupType, value);
	}

}
