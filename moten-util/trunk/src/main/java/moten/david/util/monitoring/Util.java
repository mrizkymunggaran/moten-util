package moten.david.util.monitoring;

import moten.david.util.monitoring.lookup.Lookup;
import moten.david.util.monitoring.lookup.MapLookup;
import moten.david.util.monitoring.lookup.MapLookupFactory;
import moten.david.util.monitoring.lookup.ThreadLocalLookupRecorder;

import com.google.inject.Binder;
import com.google.inject.assistedinject.FactoryProvider;
import com.google.inject.matcher.Matchers;

public class Util {

	private static ThreadLocalLookupRecorder lookupRecorder;

	public synchronized static ThreadLocalLookupRecorder getLookupRecorder() {
		if (lookupRecorder == null)
			lookupRecorder = new ThreadLocalLookupRecorder();
		return lookupRecorder;
	}

	public static void bindDefaults(Binder binder) {
		binder.bindInterceptor(Matchers.subclassesOf(Lookup.class), Matchers
				.any(), getLookupRecorder());
		binder.bind(MapLookupFactory.class).toProvider(
				FactoryProvider.newFactory(MapLookupFactory.class,
						MapLookup.class));
	}
}
