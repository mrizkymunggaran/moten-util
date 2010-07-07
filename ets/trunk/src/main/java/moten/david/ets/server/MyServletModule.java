package moten.david.ets.server;

class MyServletModule extends com.google.inject.servlet.ServletModule {
    @Override
    protected void configureServlets() {
        serve("/fix").with(EnqueueFixServlet.class);
        serve("/datastore").with(DatastoreServlet.class);
        serve("/processFix").with(ProcessFixServlet.class);
        serve("/latest").with(CurrentPositionsServlet.class);
    }
}