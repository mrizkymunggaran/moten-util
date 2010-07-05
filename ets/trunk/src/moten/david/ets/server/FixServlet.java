package moten.david.ets.server;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import moten.david.ets.client.model.Fix;
import moten.david.ets.client.model.MyEntity;

import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.repackaged.com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.vercer.engine.persist.ObjectDatastore;

@Singleton
public class FixServlet extends HttpServlet {

    private static final long serialVersionUID = 3256289411943263970L;
    private final ObjectDatastore datastore;
    private final Entities entities;

    @Inject
    public FixServlet(ObjectDatastore datastore, Entities entities) {
        this.datastore = datastore;
        this.entities = entities;
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        String ids = Preconditions
                .checkNotNull(
                        request.getParameter("ids"),
                        "ids parameter cannot be null and should contain name value pairs with colon ':' delimiting name and value and the pairs delimited by semicolon ';'");
        System.out.println("ids=" + ids);
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
            double lat = Double.parseDouble(Preconditions.checkNotNull(request
                    .getParameter("lat"), "parameter cannot be null: lat"));

            double lon = Double.parseDouble(Preconditions.checkNotNull(request
                    .getParameter("lon"), "parameter cannot be null: lon"));
            Fix fix = new Fix();
            fix.setId(System.currentTimeMillis());
            fix.setLat(lat);
            fix.setLon(lon);
            fix.setTime(new Date(time));
            MyFix f = new MyFix(fix, builder.build());
            entities.add(f);
        } catch (NumberFormatException ex) {
            throw new RuntimeException("a parameter is not a valid number", ex);
        }
        QueryResultIterator<MyEntity> e = datastore.find(MyEntity.class);
        int count = 0;
        while (e.hasNext()) {
            e.next();
            count++;
        }
        response.getOutputStream().println(
                "there are " + count + " entities eh");
    }
}
