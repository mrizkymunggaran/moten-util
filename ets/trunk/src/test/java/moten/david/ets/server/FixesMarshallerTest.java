package moten.david.ets.server;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.datatype.DatatypeConfigurationException;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.easymock.EasyMock;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class FixesMarshallerTest {

    @Test
    public void test() throws DatatypeConfigurationException {
        FixesMarshaller m = new FixesMarshaller();
        List<MyFix> fixes = m.unmarshal(getClass().getResourceAsStream(
                "/fixes.xml"));
        Assert.assertEquals(1, fixes.size());
        Assert.assertEquals(-12.5, fixes.get(0).getFix().getLat(), 0.0001);
        Assert.assertEquals(142.7, fixes.get(0).getFix().getLon(), 0.0001);
        System.out.println(fixes);
        Assert.assertEquals(2, fixes.get(0).getIds().size());

    }

    @Test
    public void testServlet() throws IOException, ServletException {

        String xml = IOUtils.toString(getClass().getResourceAsStream(
                "/fixes.xml"));
        HttpServletRequest request = EasyMock
                .createMock(HttpServletRequest.class);
        HttpServletResponse response = EasyMock
                .createMock(HttpServletResponse.class);
        EasyMock.expect(request.getParameter("fixes")).andReturn(xml)
                .anyTimes();
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        EasyMock.expectLastCall().once();
        EasyMock.expect(response.getOutputStream()).andReturn(
                new ServletOutputStream() {
                    @Override
                    public void write(int b) throws IOException {
                        // do nothing
                    }
                });

        EasyMock.replay(request, response);
        Injector injector = Guice.createInjector(new InjectorModule());
        ProcessFixServlet servlet = injector
                .getInstance(ProcessFixServlet.class);
        // servlet.doPost(request, response);

    }
}
