package moten.david.imatch.memory;

import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class Profiler implements MethodInterceptor {

	private static Profiler instance;

	public synchronized static Profiler getInstance() {
		if (instance == null)
			instance = new Profiler();
		return instance;
	}

	private final Map<String, Long> times = new HashMap<String, Long>();

	@Override
	public Object invoke(MethodInvocation inv) throws Throwable {
		String name = inv.getMethod().getName();
		long t = System.currentTimeMillis();
		Object result = inv.proceed();
		synchronized (this) {
			if (times.get(name) == null)
				times.put(name, 0L);
			times.put(name, times.get(name) + System.currentTimeMillis() - t);
		}
		return result;
	}

	@Override
	public String toString() {
		return "Profiler [" + (times != null ? "times=" + times : "") + "]";
	}

}
