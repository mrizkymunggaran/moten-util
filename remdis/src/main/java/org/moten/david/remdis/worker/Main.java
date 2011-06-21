package org.moten.david.remdis.worker;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.example.MinimalServlets.HelloServlet;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

public class Main {

	public void run() {
		Server server = new Server(8080);
		Context root = new Context(server, "/", Context.SESSIONS);
		root.addServlet(new ServletHolder(new HelloServlet()), "/*");
		try {
			server.start();
			server.join();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		new Main().run();
	}

}
