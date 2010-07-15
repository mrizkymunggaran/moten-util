package moten.david.ets.server;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import moten.david.ets.client.model.Identity;
import moten.david.ets.client.model.MyEntity;
import moten.david.ets.client.model.MyParent;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.vercer.engine.persist.ObjectDatastore;

/**
 * Manages the datastore. Can delete all entities in one command using the url
 * <code>/datastore?command=clear</code>.
 * 
 * @author dxm
 */
@Singleton
public class DatastoreServlet extends HttpServlet {

    private static final long serialVersionUID = -2953298192796896179L;
    private final ObjectDatastore datastore;

    /**
     * Constructor.
     * 
     * @param datastore
     */
    @Inject
    public DatastoreServlet(ObjectDatastore datastore) {
        this.datastore = datastore;
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // get the command parameter
        String command = checkNotNull(request.getParameter("command"),
                "command parameter cannot be null");

        // if command is 'clear'
        if ("clear".equals(command)) {
            // remove all data from the datastore
            datastore.deleteAll(Identity.class);
            datastore.deleteAll(MyEntity.class);
            datastore.deleteAll(MyParent.class);
        }

        // write the response
        response.getOutputStream().print("deleted all entities");
    }

}
