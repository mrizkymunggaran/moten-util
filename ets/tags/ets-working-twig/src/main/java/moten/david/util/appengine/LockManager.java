package moten.david.util.appengine;

import com.google.inject.Singleton;

/**
 * Provides locking in Google App Engine using a memcache entry as the monitor.
 * 
 * @author dxm
 */
@Singleton
public class LockManager {

    /**
     * Uses a mutually exclusive locking object to ensure synchronization of
     * this method. If a lock is not obtained throws
     * {@link CouldNotObtainLockException}. <code>runnable</code> is run in the
     * current thread and any RuntimeException thrown by the runnable will be
     * thrown by this method.
     * 
     * @param lockName
     * @param runnable
     */
    public void performWithLock(String lockName, Runnable runnable,
            int timeoutMs) {
        // use memcache as a lock manager to ensure synchronization on this
        // method. Mutex = mutual exclusion object.

        // get a lock from the appengine memcache
        MemcacheMutex mutex = new MemcacheMutex(lockName, timeoutMs);

        // check lock was obtained
        if (!mutex.tryLock())
            throw new CouldNotObtainLockException(
                    "addFix could not obtain lock");
        try {
            runnable.run();
        } finally {
            // unlock the mutex
            mutex.unlock();
        }
    }

}
