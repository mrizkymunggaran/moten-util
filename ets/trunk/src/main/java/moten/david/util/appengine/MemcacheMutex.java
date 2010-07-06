package moten.david.util.appengine;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class MemcacheMutex {
    private final String key;
    private final int maxTimeout;
    private boolean locked;

    public MemcacheMutex(String key, int maxTimeout) {
        this.key = "mcmutex." + key;
        this.maxTimeout = maxTimeout;
    }

    public boolean tryLock() {
        if (locked)
            return true;

        MemcacheService mc = MemcacheServiceFactory.getMemcacheService();

        locked = mc.put(key, "not used", Expiration.byDeltaMillis(maxTimeout),
                MemcacheService.SetPolicy.ADD_ONLY_IF_NOT_PRESENT);

        return locked;
    }

    public void unlock() {
        if (locked)
            MemcacheServiceFactory.getMemcacheService().delete(key);
        locked = false;
    }
}