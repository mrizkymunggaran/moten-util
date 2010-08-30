// Copyright (C) 2002 Strangeberry Inc.
// @(#)Main.java, 1.15, 03/05/2003
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

package com.strangeberry.rendezvous.tools;

import java.io.*;
import java.net.*;
import com.strangeberry.rendezvous.*;

/**
 * Main sample program for Rendezvous.
 * 
 * @author Arthur van Hoff
 * @version 1.15, 03/05/2003
 */
public class Main {

    static class SampleListener implements ServiceListener {

        public void addService(Rendezvous rendezvous, String type, String name) {
            System.out.println("ADD: "
                    + rendezvous.getServiceInfo(type, name, 3 * 1000));
        }

        public void removeService(Rendezvous rendezvous, String type,
                String name) {
            System.out.println("REMOVE: " + name);
        }

        public void resolveService(Rendezvous rendezvous, String type,
                String name, ServiceInfo info) {
            System.out.println("RESOLVED: " + info);
        }
    }

    public static void main(String argv[]) throws IOException {
        int argc = argv.length;
        boolean debug = false;
        InetAddress intf = null;

        if ((argc > 0) && "-d".equals(argv[0])) {
            System.arraycopy(argv, 1, argv, 0, --argc);
            System.getProperties().put("rendezvous.debug", "1");
            debug = true;
        }
        if ((argc > 1) && "-i".equals(argv[0])) {
            intf = InetAddress.getByName(argv[1]);
            System.arraycopy(argv, 2, argv, 0, argc -= 2);
        }
        if (intf == null) {
            intf = InetAddress.getLocalHost();
        }

        Rendezvous rendezvous = new Rendezvous(intf);

        if ((argc == 0) || ((argc >= 1) && "-browse".equals(argv[0]))) {
            if (argc > 1) {
                String types[] = new String[argc - 1];
                System.arraycopy(argv, 1, types, 0, argc - 1);
                new Browser(rendezvous, types);
            } else {
                new Browser(rendezvous);
            }
        } else if ((argc == 3) && "-bs".equals(argv[0])) {
            rendezvous.addServiceListener(argv[1] + "." + argv[2],
                    new SampleListener());
        } else if ((argc == 6) && "-rs".equals(argv[0])) {
            String type = argv[2] + "." + argv[3];
            String name = argv[1] + "." + type;
            rendezvous.registerService(new ServiceInfo(type, name, rendezvous
                    .getInterface(), Integer.parseInt(argv[4]), 0, 0, argv[5]));
        } else if ((argc == 2) && "-f".equals(argv[0])) {
            new Responder(rendezvous, argv[1]);
        } else if (!debug) {
            System.out.println();
            System.out.println("jrendezvous:");
            System.out.println("     -d						- output debugging info");
            System.out
                    .println("     -i <addr>					- specify the interface address");
            System.out
                    .println("     -browse [<type>...]			         - GUI browser (default)");
            System.out.println("     -bs <type> <domain>				- browse service");
            System.out
                    .println("     -rs <name> <type> <domain> <port> <txt>		- register service");
            System.out.println("     -f <file>					- rendezvous responder");
            System.out.println();
            System.exit(1);
        }
    }
}