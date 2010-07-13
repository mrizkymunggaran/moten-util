package moten.david.ets.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import moten.david.ets.client.model.Fix;
import moten.david.ets.client.model.MyEntity;
import moten.david.util.appengine.CouldNotObtainLockException;

import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.repackaged.com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.vercer.engine.persist.ObjectDatastore;

/**
 * Processes a single fix via Http GET or a number of fixes via Http POST.
 * 
 * @author dxm
 */
@Singleton
public class ProcessFixServlet extends HttpServlet {

    private static Logger log = Logger.getLogger(EnqueueFixServlet.class
            .getName());
    private static final long serialVersionUID = 3256289411943263970L;
    private final ObjectDatastore datastore;
    private final Entities entities;
    private final FixesMarshaller marshaller;
    private final EnqueueFixHandler enqueueFixHandler;

    @Inject
    public ProcessFixServlet(ObjectDatastore datastore, Entities entities,
            FixesMarshaller marshaller, EnqueueFixHandler enqueueFixHandler) {
        this.datastore = datastore;
        this.entities = entities;
        this.marshaller = marshaller;
        this.enqueueFixHandler = enqueueFixHandler;
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // process a fix
        try {
            String ids = Preconditions
                    .checkNotNull(
                            request.getParameter("ids"),
                            "ids parameter cannot be null and should contain name value pairs with colon ':' delimiting name and value and the pairs delimited by semicolon ';'");
            log.info("ids=" + ids);
            String[] items = ids.split(";");
            Builder<String, String> builder = ImmutableMap.builder();
            for (String item : items) {
                String[] parts = item.split(":");
                String name = parts[0];
                String value = parts[1];
                builder.put(name, value);
            }
            Long time = Long
                    .parseLong(Preconditions
                            .checkNotNull(request.getParameter("time"),
                                    "time parameter must be specified (long UNIX time value in ms)"));

            try {
                double lat = Double.parseDouble(Preconditions.checkNotNull(
                        request.getParameter("lat"),
                        "parameter cannot be null: lat"));

                double lon = Double.parseDouble(Preconditions.checkNotNull(
                        request.getParameter("lon"),
                        "parameter cannot be null: lon"));
                Fix fix = new Fix();
                fix.setId(UUID.randomUUID().toString());
                fix.setLat(lat);
                fix.setLon(lon);
                fix.setExtra(request.getParameter("extra"));
                fix.setTime(new Date(time));
                MyFix f = new MyFix(fix, builder.build());
                entities.add(ImmutableList.of(f));
            } catch (NumberFormatException ex) {
                throw new RuntimeException("a parameter is not a valid number",
                        ex);
            }
            QueryResultIterator<MyEntity> e = datastore.find(MyEntity.class);
            int count = 0;
            while (e.hasNext()) {
                e.next();
                count++;
            }
            response.getOutputStream().println(
                    "there are " + count + " entities eh");
        } catch (RuntimeException e) {
            log.log(Level.WARNING, e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getOutputStream().println(e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        try {
            String s = Preconditions.checkNotNull(
                    request.getParameter("fixes"),
                    "fixes parameter cannot be null");
            List<MyFix> fixes = marshaller.unmarshal(new ByteArrayInputStream(s
                    .getBytes()));
            int size = fixes.size();
            final int MAX_QUEUEINGS = 10;
            final int MAX_SIZE = 10;
            if (size <= MAX_SIZE) {
                log.info("adding fixes");
                entities.add(fixes);
                log.info("added fixes");
            } else {
                List<List<MyFix>> lists = Lists.partition(fixes, Math.max(1,
                        size / MAX_QUEUEINGS));
                for (List<MyFix> list : lists) {
                    log.info("enqueuing " + list.size() + " fixes");
                    enqueueFixHandler.doPost(marshaller.marshall(list));
                }
            }
        } catch (CouldNotObtainLockException e) {
            log.log(Level.WARNING, e.getMessage());
        } catch (RuntimeException e) {
            log.log(Level.WARNING, e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getOutputStream().println(e.getMessage());
        }
    }
}
