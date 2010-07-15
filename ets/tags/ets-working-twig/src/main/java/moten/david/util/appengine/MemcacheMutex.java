package moten.david.util.appengine;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

/**
 * Copied from <a href=
 * "http://www.answercow.com/2010/05/app-engine-locking-with-memcache.html"
 * >here</a>.
 * 
 * @author dxm
 */
public class MemcacheMutex {
    private final String key;
    private final int maxTimeoutMs;
    private boolean locked;

    /**
     * Constructor.
     * 
     * @param key
     * @param maxTimeoutMs
     */
    public MemcacheMutex(String key, int maxTimeoutMs) {
        this.key = "mcmutex." + key;
        this.maxTimeoutMs = maxTimeoutMs;
    }

    /**
     * Tries to get a lock.
     * 
     * @return
     */
    public boolean tryLock() {
        if (locked)
            return true;

        MemcacheService mc = MemcacheServiceFactory.getMemcacheService();

        locked = mc.put(key, "not used",
                Expiration.byDeltaMillis(maxTimeoutMs),
                MemcacheService.SetPolicy.ADD_ONLY_IF_NOT_PRESENT);

        return locked;
    }

    /**
     * Releases the lock.
     */
    public void unlock() {
        if (locked)
            MemcacheServiceFactory.getMemcacheService().delete(key);
        locked = false;
    }
}