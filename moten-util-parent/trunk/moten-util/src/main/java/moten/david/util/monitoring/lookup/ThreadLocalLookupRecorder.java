package moten.david.util.monitoring.lookup;

import java.util.HashMap;
import java.util.Map;

import moten.david.util.concurrent.ThreadLocalHashMap;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 
 * Records accesses made to a key value store in the current thread only.
 * 
 * @author dxm
 * 
 */
public class ThreadLocalLookupRecorder implements MethodInterceptor {

    Map<String, String> map = new ThreadLocalHashMap<String, String>();

    /**
     * Clear the record of accesses for the current thread
     */
    public void clear() {
        map.clear();
    }

    /**
     * Returns the accesses made since last clear but only from the current
     * thread. The returned map will have the same values if called in a
     * different thread (a copy is returned).
     * 
     * @return
     */
    public Map<String, String> getLookups() {
        // need to create a new one to return because use of map in a thread
        // other than the current one would return an empty map
        return new HashMap<String, String>(map);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (invocation.getMethod().getName().equals("get")
                && invocation.getArguments().length == 2) {
            // Lookup only has one method
            // String context = (String) invocation.getArguments()[0];
            String key = (String) invocation.getArguments()[1];
            String result = (String) invocation.proceed();
            map.put(key, result);
            return result;
        } else
            return invocation.proceed();
    }
}
