package moten.david.ets.server;

class MyServletModule extends com.google.inject.servlet.ServletModule {
    @Override
    protected void configureServlets() {
        serve("/fix").with(FixServlet.class);
        serve("/datastore").with(DatastoreServlet.class);
    }
}