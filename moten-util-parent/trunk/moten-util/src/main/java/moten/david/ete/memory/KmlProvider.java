package moten.david.ete.memory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Logger;

import moten.david.ete.Identifier;
import moten.david.util.text.StringUtil;
import moten.david.util.xml.TaggedOutputStream;

import org.apache.commons.lang.StringEscapeUtils;

public class KmlProvider {

    private static Logger log = Logger.getLogger(KmlProvider.class.getName());

    public String getKml(Enumeration<MyFix> fixes) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        TaggedOutputStream t = new TaggedOutputStream(bytes, true);
        t.startTag("kml");
        t.addAttribute("xmlns", "http://earth.google.com/kml/2.1");
        t.startTag("Document");
        addStyle(t);
        t.startTag("Folder");
        t.startTag("name");
        t.append("LRIT");
        t.closeTag();
        t.startTag("description");
        t.append(new Date().toString());
        t.closeTag();
        while (fixes.hasMoreElements()) {
            MyFix fix = fixes.nextElement();
            t.startTag("Placemark");
            t.startTag("name");
            t.append(getName(fix));
            t.closeTag();
            t.startTag("description");
            t.append(getDescription(fix));
            t.closeTag();
            t.startTag("styleUrl");
            t.append("#craft");
            t.closeTag();
            if (fix.getVelocity() != null
                    & fix.getVelocity().getCourseDegrees() != null) {
                t.startTag("Style");
                t.startTag("IconStyle");
                t.startTag("heading");
                // add 180 because icon points straight down initially
                t
                        .append((fix.getVelocity().getCourseDegrees()
                                .doubleValue() + 180)
                                + "");
                t.closeTag();
                t.closeTag();
                t.closeTag();
            }
            t.startTag("Point");
            t.startTag("altitudeMode");
            t.append("clampToGround");
            t.closeTag();
            t.startTag("coordinates");
            t.append(fix.getPosition().getLongitude() + ", "
                    + fix.getPosition().getLatitude() + ", 0");
            t.closeTag();
            t.closeTag();
            t.closeTag();
        }
        t.closeTag();
        t.closeTag();
        t.closeTag();
        t.close();
        return bytes.toString();
    }

    private void addStyle(TaggedOutputStream t) {
        if (true)
            return;
        try {
            t.append(StringUtil.readString(getClass().getResourceAsStream(
                    "/kml-style.txt")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getDescription(MyFix fix) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        TaggedOutputStream t = new TaggedOutputStream(bytes, true);
        t.startTag("html");
        t.startTag("table");

        for (Identifier id : fix.getIdentifiers()) {
            addRow(t, ((MyIdentifierType) id.getIdentifierType()).getName(),
                    ((MyIdentifier) id).getValue());
        }
        for (String key : fix.getProperties().keySet()) {
            addRow(t, key, fix.getProperties().get(key));
        }
        if (fix.getVelocity() != null
                && fix.getVelocity().getCourseDegrees() != null)
            addRow(t, "Course", fix.getVelocity().getCourseDegrees() + "");
        if (fix.getVelocity() != null
                && fix.getVelocity().getSpeedMetresPerSecond() != null)
            addRow(t, "Speed (m/s)", fix.getVelocity()
                    .getSpeedMetresPerSecond()
                    + "");
        t.closeTag();
        t.closeTag();
        t.close();
        return StringEscapeUtils.escapeXml(bytes.toString());
    }

    private void addRow(TaggedOutputStream t, String name, String value) {

        t.startTag("tr");
        t.startTag("td");
        t.append(StringEscapeUtils.escapeHtml(name));
        t.closeTag();
        t.startTag("td");
        t.append(StringEscapeUtils.escapeHtml(value));
        t.closeTag();
        t.closeTag();
    }

    private String getName(MyFix fix) {
        for (Identifier id : fix.getIdentifiers())
            if ("Name".equalsIgnoreCase(((MyIdentifierType) id
                    .getIdentifierType()).getName()))
                return ((MyIdentifier) id).getValue();
        for (String key : fix.getProperties().keySet())
            if ("Name".equalsIgnoreCase(key))
                return fix.getProperties().get(key);
        return null;
    }
}
