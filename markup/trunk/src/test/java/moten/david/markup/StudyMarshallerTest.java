package moten.david.markup;

import java.io.InputStream;

import junit.framework.Assert;
import moten.david.markup.xml.study.Study;

import org.junit.Test;

public class StudyMarshallerTest {

    @Test
    public void testMarshalling() {
        StudyMarshaller m = new StudyMarshaller();
        InputStream is = getClass().getResourceAsStream(
                "/study1/markup/study.xml");
        Study study = m.unmarshal(is);
        Assert.assertEquals("Pro Cycling Interview", study.getName());
        Assert.assertTrue(study.getDocument().size() > 0);

        m.marshal(study, System.out);
    }

    @Test
    public void testRegex() {
        String s = "PRDN:=AusSAR:190";
        s = s.replaceFirst("^PRDN:=(.*)", "PRDN:INMARSATC=$1");
        Assert.assertEquals("replaced address", "PRDN:INMARSATC=AusSAR:190", s);
    }
}
