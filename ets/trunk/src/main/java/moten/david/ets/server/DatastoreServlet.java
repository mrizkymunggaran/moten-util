package moten.david.ets.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import moten.david.ets.client.model.Identity;
import moten.david.ets.client.model.MyEntity;
import moten.david.ets.client.model.MyParent;

import com.google.appengine.repackaged.com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.vercer.engine.persist.ObjectDatastore;

@Singleton
public class DatastoreServlet extends HttpServlet {

    private static final long serialVersionUID = -2953298192796896179L;
    private final ObjectDatastore datastore;

    @Inject
    public DatastoreServlet(ObjectDatastore datastore) {
        this.datastore = datastore;
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        String command = request.getParameter("command");
        Preconditions.checkNotNull(command);
        if ("clear".equals(command)) {
            datastore.deleteAll(Identity.class);
            datastore.deleteAll(MyEntity.class);
            datastore.deleteAll(MyParent.class);
        }
        response.getOutputStream().print("deleted all entities");
    }

}
