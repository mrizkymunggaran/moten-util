// Copyright (C) 2002 Strangeberry Inc.
// @(#)SampleRegistration.java, 1.3, 03/05/2003
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

package com.strangeberry.rendezvous.sample;

import java.io.*;
import java.util.*;
import com.strangeberry.rendezvous.*;

/**
 * Trivial example of service registration...
 * 
 * @author Arthur van Hoff
 * @version 1.3, 03/05/2003
 */
public class SampleRegistration implements ServiceListener {

    /**
     * This method is called when rendezvous discovers a service for the first
     * time. Only its name and type are known. We can now request the service
     * information.
     */
    public void addService(Rendezvous rendezvous, String type, String name) {
        System.out.println("ADD: " + name);
        rendezvous.requestServiceInfo(type, name);
    }

    /**
     * This method is called when the ServiceInfo record is resolved.
     */
    public void resolveService(Rendezvous rendezvous, String type, String name,
            ServiceInfo info) {
        System.out.println("RESOLVE: " + info);
    }

    /**
     * This method is called when a service is no longer available.
     */
    public void removeService(Rendezvous rendezvous, String type, String name) {
        System.out.println("REMOVE: " + name);
    }

    /**
     * The main program creates an instance of Rendezvous and registers a
     * service.
     */
    public static void main(String argv[]) throws IOException {
        String type = "_http._tcp.local.";
        String name = (argv.length > 0) ? argv[0] : "Hello World";
        int port = 80;
        Hashtable props = new Hashtable();
        props.put("path", "index.html");

        Rendezvous rendezvous = new Rendezvous();
        ServiceInfo info = new ServiceInfo(type, name, rendezvous
                .getInterface(), port, props);
        rendezvous.registerService(info);
    }
}

