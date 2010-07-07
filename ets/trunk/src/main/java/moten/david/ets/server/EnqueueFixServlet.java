package moten.david.ets.server;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;
import com.google.inject.Singleton;

@Singleton
public class EnqueueFixServlet extends HttpServlet {

    private static final long serialVersionUID = 355060481242131732L;
    private static Logger log = Logger.getLogger(EnqueueFixServlet.class
            .getName());

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        Queue queue = QueueFactory.getDefaultQueue();
        TaskOptions options = url("/processFix").method(Method.GET);
        for (String param : new String[] { "ids", "time", "lat", "lon" }) {
            options = options.param(param, request.getParameter(param));
        }
        queue.add(options);
    }
}
