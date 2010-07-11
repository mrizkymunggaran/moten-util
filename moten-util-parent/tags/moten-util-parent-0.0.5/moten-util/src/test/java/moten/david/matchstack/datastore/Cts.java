package moten.david.matchstack.datastore;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
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
import moten.david.util.kml.KmlMarshaller;
import moten.david.util.kml.PositionsToKmlConverter;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
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
                put("Serial Number", 1.5);
                put("Registration", 1.5);
            }
        };
        Map<String, MyIdentifierType> types = new HashMap<String, MyIdentifierType>();
        for (String name : strengths.keySet())
            types.put(name, new MyIdentifierType(name, strengths.get(name)));

        try {
            // skip header line
            br.readLine();
            Builder<TimedIdentifier> builder = ImmutableSet.builder();
            long count = 0;
            long start = System.currentTimeMillis();
            System.out.println(new Date());
            Fix lastFix = null;
            while ((line = br.readLine()) != null) {
                String[] items = line.split("\t");
                Fix fix = new Fix();
                fix.fixId = Long.parseLong(items[0]);
                fix.name = items[1];
                fix.value = items[2];
                fix.time = df.parse(items[3]).getTime();
                fix.lat = new BigDecimal(items[4]);
                fix.lon = new BigDecimal(items[5]);
                if (isValid(fix)) {
                    MyIdentifierType type = types.get(fix.name);
                    Preconditions.checkNotNull(type, fix.name + " not in list");
                    MyIdentifier id = new MyIdentifier(type, fix.value);
                    MyTimedIdentifier ti = new MyTimedIdentifier(id, fix.time);
                    count++;
                    if (lastFix != null && fix.fixId != lastFix.fixId) {
                        ImmutableSet<TimedIdentifier> set = builder.build();
                        if (set.size() > 0)
                            ds = ds.add(set, Preconditions
                                    .checkNotNull(new Info(lastFix.time,
                                            lastFix.lat, lastFix.lon)));
                        builder = ImmutableSet.builder();
                    }
                    builder.add(ti);
                    lastFix = fix;

                    if (count % 5000 == 0) {
                        System.out.println("sets =" + ds.sets().size());
                        System.out.println(count);
                    }
                }
            }
            ds = ds.add(builder.build(), new Info(lastFix.time, lastFix.lat,
                    lastFix.lon));
            System.out.println(ds);
            System.out.println("sets =" + ds.sets().size());
            System.out.println(count);
            long durationMs = System.currentTimeMillis() - start;
            System.out.println(durationMs);
            System.out.println(count / 1.0 / durationMs * 1000);
            com.google.common.collect.ImmutableSetMultimap.Builder<Info, Identifier> b2 = ImmutableSetMultimap
                    .builder();
            for (Identifier id : ds.getAncillary().keySet()) {
                b2.put((Info) ds.getAncillary().get(id), id);
            }
            ImmutableSetMultimap<Info, Identifier> multi = b2.build();
            for (Info inf : multi.keySet())
                System.out.println(inf + "=" + multi.get(inf));

            PositionsToKmlConverter converter = new PositionsToKmlConverter(
                    "CTS Engine v2");
            for (Info inf : multi.keySet()) {
                MyIdentifier id = (MyIdentifier) multi.get(inf).iterator()
                        .next();
                StringBuffer s = new StringBuffer();
                for (Identifier ident : multi.get(inf)) {
                    s
                            .append(((MyIdentifierType) ident
                                    .getIdentifierType()).getName()
                                    + "="
                                    + ((MyIdentifier) ident).getValue()
                                    + "<br/>");
                }
                s.append(new Date(inf.time) + "<br/>");
                converter.add(inf.lat, inf.lon, ((MyIdentifierType) id
                        .getIdentifierType()).getName()
                        + "=" + id.getValue(), s.toString(), null);
            }
            String kml = new KmlMarshaller().getKmlAsString(converter
                    .getKmlType(), true);
            FileOutputStream fos = new FileOutputStream("target/fixes.kml");
            fos.write(kml.getBytes());
            fos.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isValid(Fix fix) {
        return !fix.name.equals("IMO Number")
                || (fix.name.equals("IMO Number") && fix.value.length() == 7);
    }

    private static class Info {
        long time;
        BigDecimal lat;
        BigDecimal lon;

        public Info(long time, BigDecimal lat, BigDecimal lon) {
            super();
            this.time = time;
            this.lat = lat;
            this.lon = lon;
        }

        @Override
        public String toString() {
            return "Info [lat=" + lat + ", lon=" + lon + ", time="
                    + new Date(time) + "]";
        }
    }

    private static class Fix {

        public BigDecimal lon;
        public BigDecimal lat;
        long fixId;
        String name;
        String value;
        long time;

    }

    public static void main(String[] args) throws FileNotFoundException {
        new Cts().load(new FileInputStream("../fixes.txt"));
    }

}
