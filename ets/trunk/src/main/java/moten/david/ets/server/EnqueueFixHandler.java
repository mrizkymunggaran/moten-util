package moten.david.ets.server;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.util.logging.Logger;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;
import com.google.inject.Singleton;

@Singleton
public class EnqueueFixHandler {

    private final Logger log = Logger.getLogger(EnqueueFixHandler.class
            .getName());

    public void doPost(String fixesXml) {
        Queue queue = QueueFactory.getDefaultQueue();
        TaskOptions options = url("/processFix").method(Method.POST);
        options = options.param("fixes", fixesXml);
        queue.add(options);
        log.fine("enqueued fixes via POST\n" + fixesXml);
    }

}
