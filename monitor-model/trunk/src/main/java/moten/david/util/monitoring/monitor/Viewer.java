package moten.david.util.monitoring.monitor;

import moten.david.xuml.model.viewer.ViewerUtil;

public class Viewer {
	public static void main(String[] args) throws Exception {
		ViewerUtil.view("src/viewer", Definition.class);
	}
}
