// Copyright (C) 2002 Strangeberry Inc.
// @(#)SampleListener.java, 1.4, 03/05/2003
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
import com.strangeberry.rendezvous.*;

/**
 * Trivial example of a service listener...
 * 
 * @author Arthur van Hoff
 * @version 1.4, 03/05/2003
 */
public class SampleListener implements ServiceListener {

    /**
     * This method is called when rendezvous discovers a service for the first
     * time. Only its name and type are known. We can now request the service
     * information.
     */
    public void addService(Rendezvous rendezvous, String type, String name) {
        System.out.println("ADD: " + name + type);
        rendezvous.requestServiceInfo(type, name);
    }

    /**
     * This method is called when the ServiceInfo record is resolved. The
     * ServiceInfo.getURL() constructs an http url given the addres, port, and
     * path properties found in the ServiceInfo record.
     */
    public void resolveService(Rendezvous rendezvous, String type, String name,
            ServiceInfo info) {
        if (info != null) {
            System.out.println("RESOLVE: " + info.getURL());
        } else {
            System.out.println("FAILED TO RESOLVE: " + name + type);
        }
    }

    /**
     * This method is called when a service is no longer available.
     */
    public void removeService(Rendezvous rendezvous, String type, String name) {
        System.out.println("REMOVE: " + name);
    }

    /**
     * The main program creates an instance of Rendezvous and listens for
     * services of a given type.
     */
    public static void main(String argv[]) throws IOException {
        System.getProperties().put("rendezvous.debug", "1");
        String type = (argv.length > 0) ? argv[0] : "_http._tcp.local.";
        Rendezvous rendezvous = new Rendezvous();
        rendezvous.addServiceListener(type, new SampleListener());
    }
}