/*
 * Created on May 7, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
Copyright 2003 Joseph Barnett

This File is part of "one 2 oh my god"

"one 2 oh my god" is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
Free Software Foundation; either version 2 of the License, or
your option) any later version.

"one 2 oh my god" is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with "one 2 oh my god"; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 */
package itunes.client.request;
import itunes.client.swing.One2OhMyGod;
import itunes.util.Hasher;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.cdavies.itunes.ConnectionStatus;
import org.cdavies.itunes.ItunesHost;

/**
 * @author jbarnett
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SongRequest extends Request {
	protected BufferedInputStream b;
	
	public SongRequest(String server, int port, int dbId, int songId, String songFmt, int sessionId, ConnectionStatus status) throws NoServerPermissionException {
		super(server, port, "databases/"+dbId+"/items/"+songId+"."+songFmt+"?session-id="+sessionId,status);
	}
	
	protected void Query() throws NoServerPermissionException {
		URL url =null;
		try {
			url = new URL("http://"+server+":"+port+"/"+requestString);
			
			int reqid = _status.getNextRequestNumber();

			if (One2OhMyGod.debug)
				System.out.println("Processing Request: "+ server+":"+port+"/"+requestString);
			URLConnection urlc = url.openConnection();
			String hashCode = Hasher.GenerateHash("/" + requestString,1,_status,reqid);
			urlc.addRequestProperty("Client-DAAP-Validation", hashCode);
			urlc.addRequestProperty("Client-DAAP-Access-Index", "1");
			
			
			if (_status.getItunesHost().getVersion() == ItunesHost.ITUNES_45) 
				urlc.addRequestProperty("Client-DAAP-Request-ID", new Integer(reqid).toString());
			
			int len = urlc.getContentLength();
			b = new BufferedInputStream(urlc.getInputStream());
			if (len == -1) {
				return;
			} else if (len == 0) {
				throw new NoServerPermissionException();
			}
		} catch (MalformedURLException e) {
			if (One2OhMyGod.debug)
				System.out.println("Malformed URL");	
		} catch (IOException ioe) {
			if (One2OhMyGod.debug)
				System.out.println(ioe.getLocalizedMessage());
			throw new NoServerPermissionException();
		}
	}

	protected void Process() throws NoServerPermissionException{
		return;
	//	if (data.length == 0) {
	//		throw new NoServerPermissionException();
	//	}
	}
	
	public InputStream getStream() {
		return b;
	}
}

