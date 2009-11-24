package au.edu.anu.delibdem.qsort.gui;

public class Preferences {

	public static final String EIGENVALUE_THRESHOLD = "Eigenvalue Threshold";
	public static final String EIGENVALUE_THRESHOLD_DEFAULT = "1.0";
	public static final String VENN_MAX_STANDARD_ERRORS = "Venn Diagram Max SE";
	private static Preferences instance;

	public synchronized static Preferences getInstance() {
		if (instance == null)
			instance = new Preferences();
		return instance;
	}

	private final java.util.prefs.Preferences prefs;

	public Preferences() {
		// props = new Properties();
		// props.setProperty(EIGENVALUE_THRESHOLD, "1");
		prefs = java.util.prefs.Preferences.userNodeForPackage(this.getClass());
	}

	public String getProperty(String key, String defaultValue) {
		return prefs.get(key, defaultValue);
	}

	public void setProperty(String key, String value) {
		prefs.put(key, value);
	}

	public double getDouble(String key, Double defaultValue) {
		return Double.parseDouble(getProperty(key, defaultValue.toString()));
	}

	public float getFloat(String key, Float defaultValue) {
		return Float.parseFloat(getProperty(key, defaultValue.toString()));
	}

}
