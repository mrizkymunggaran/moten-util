package moten.david.matchstack.datastore;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import moten.david.matchstack.types.Identifier;
import moten.david.matchstack.types.TimedIdentifier;
import moten.david.matchstack.types.impl.MyIdentifier;
import moten.david.matchstack.types.impl.MyIdentifierType;
import moten.david.matchstack.types.impl.MyTimedIdentifier;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class Cts {

    private static DateFormat df = new SimpleDateFormat(
            "dd/MM/yyyy h:mm:ss.SSSSSS a");
    @Inject
    private DatastoreImmutableFactory factory;

    public void init() {
        Injector injector = Guice.createInjector(new InjectorModule());
        injector.injectMembers(this);
    }

    private DatastoreImmutable createDatastore() {
        ImmutableSet<Set<TimedIdentifier>> a = ImmutableSet.of();
        DatastoreImmutable d = factory.create(a,
                new HashMap<Identifier, Object>());
        return d;
    }

    public void load(InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;

        init();
        DatastoreImmutable ds = createDatastore();
        Map<String, Double> strengths = new HashMap<String, Double>() {
            {
                put("MMSI", 2.0);
                put("IMO Number", 3.0);
                put("Callsign", 1.0);
                put("Inmarsat C Mobile Number", 1.5);
                put("Terminal ID", 1.5);
            }
        };
        Map<String, MyIdentifierType> types = new HashMap<String, MyIdentifierType>();
        for (String name : strengths.keySet())
            types.put(name, new MyIdentifierType(name, strengths.get(name)));

        try {
            // skip header line
            br.readLine();
            String lastFixId = null;
            Builder<TimedIdentifier> builder = ImmutableSet.builder();
            long count = 0;
            long start = System.currentTimeMillis();
            System.out.println(new Date());
            Point2D.Double pt = null;
            while ((line = br.readLine()) != null) {
                String[] items = line.split("\t");
                String fixId = items[0];
                String name = items[1];
                String value = items[2];
                double lat = Double.parseDouble(items[3]);
                double lon = Double.parseDouble(items[4]);
                pt = new Point2D.Double(lat, lon);
                long time = df.parse(items[3]).getTime();
                if (name.equals("IMO Number") && value.length() == 7) {
                    // System.out.println(fixId + "," + name + "," + value + ","
                    // + new Date(time));
                    MyIdentifierType type = types.get(name);
                    MyIdentifier id = new MyIdentifier(type, value);
                    MyTimedIdentifier ti = new MyTimedIdentifier(id, time);
                    count++;
                    if (lastFixId != null && !fixId.equals(lastFixId)) {
                        ImmutableSet<TimedIdentifier> set = builder.build();
                        ds = ds.add(set, pt);
                        builder = ImmutableSet.builder();
                    }
                    builder.add(ti);
                    lastFixId = fixId;
                    if (count % 5000 == 0) {
                        // System.out.println(ds.toString());
                        System.out.println("sets =" + ds.sets().size());
                        System.out.println(count);
                    }
                }
            }
            ds.add(builder.build(), pt);
            // System.out.println(ds.toString());
            System.out.println("sets =" + ds.sets().size());
            System.out.println(count);
            long durationMs = System.currentTimeMillis() - start;
            System.out.println(durationMs);
            System.out.println(count / 1.0 / durationMs * 1000);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        new Cts().load(new FileInputStream("/home/dave/Desktop/fixes.txt"));
    }

}
