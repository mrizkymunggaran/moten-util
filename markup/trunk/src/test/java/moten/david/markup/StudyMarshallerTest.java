package moten.david.markup;

import java.io.InputStream;

import junit.framework.Assert;
import moten.david.markup.xml.study.Study;

import org.junit.Test;

public class StudyMarshallerTest {

	@Test
	public void test() {
		StudyMarshaller m = new StudyMarshaller();
		InputStream is = getClass().getResourceAsStream(
				"/study1/markup/study.xml");
		Study study = m.unmarshal(is);
		Assert.assertEquals("Pro Cycling Interview", study.getName());
		Assert.assertEquals(1, study.getDocument().size());
	}
}
