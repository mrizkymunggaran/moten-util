package moten.david.ets.server;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import moten.david.ets.client.model.MyEntity;
import moten.david.ets.client.model.MyParent;
import moten.david.util.kml.KmlMarshaller;
import moten.david.util.kml.PositionsToKmlConverter;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.vercer.engine.persist.ObjectDatastore;

/**
 * Returns kml of the current entities and their positions and identifiers.
 * 
 * @author dxm
 */
@Singleton
public class CurrentPositionsServlet extends HttpServlet {

    private static Logger log = Logger.getLogger(CurrentPositionsServlet.class
            .getName());
    private static final long serialVersionUID = -6649836825608807371L;
    private final KmlMarshaller marshaller;
    private final ObjectDatastore datastore;

    @Inject
    public CurrentPositionsServlet(ObjectDatastore datastore,
            KmlMarshaller marshaller) {
        this.datastore = datastore;
        this.marshaller = marshaller;
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        try {
            log.info("getting latest positions as kml");
            // start the transaction
            datastore.beginTransaction();

            // get the parent
            MyParent parent = EntitiesGae.getParent(datastore, "main");

            // find all entities with that parent
            Iterator<MyEntity> iterator = datastore.find().type(MyEntity.class)
                    .withAncestor(parent).returnResultsNow();

            // collate the positions
            PositionsToKmlConverter positions = new PositionsToKmlConverter(
                    "Latest");
            while (iterator.hasNext()) {
                MyEntity e = iterator.next();
                positions.add(new BigDecimal(e.getLatestFix().getLat()),
                        new BigDecimal(e.getLatestFix().getLon()), "entity",
                        "<html><p>some comment here</p></html>", null);
            }
            // marshall kml
            String kml = marshaller
                    .getKmlAsString(positions.getKmlType(), true);
            log.fine(kml);

            // set the response headers for kml
            response.setContentType("application/vnd.google-earth.kml+xml");

            // write the output
            response.getOutputStream().print(kml);
            log.info("returned kml");
        } catch (RuntimeException e) {
            log.log(Level.WARNING, e.getMessage(), e);
        } finally {
            // no changes made so rollback
            if (datastore.getTransaction().isActive())
                datastore.getTransaction().rollback();
        }
    }
}
