/*
 * Created on May 6, 2003
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
import itunes.*;
import itunes.client.*;

import java.util.ArrayList;
import org.cdavies.itunes.*;
/**
 * @author jbarnett
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SingleDatabaseRequest extends DatabasesRequest {


	public SingleDatabaseRequest(String server, int port, int sessionId, int dbId, ConnectionStatus status) throws NoServerPermissionException {
		super(server, port, "databases/"+dbId+"/items?session-id="+sessionId+"&meta=dmap.itemid,dmap.itemname,daap.songalbum,daap.songartist,daap.songtracknumber,daap.songuserrating,daap.songgenre,daap.songformat,daap.songtime,daap.songsize,daap.songbitrate",status);
	}
	public ArrayList getSongs() {
		ArrayList songs = new ArrayList();
		for (int i = 0; i < mlitDataFields.size();i++) {
			ArrayList fps = ((ArrayList)mlitDataFields.get(i));
			String name=null;
			int id=0;
			Song s = new Song();
			for (int j = 0; j < fps.size();j++) {
				FieldPair fp =((FieldPair)fps.get(j));
				if (fp.name.equals("miid")) {
					s.id = Request.readInt(fp.value,0,4);
				} else if (fp.name.equals("minm")) {
					s.name = Request.readString(fp.value,0,fp.value.length);
				} else if (fp.name.equals("asal")) {
					s.album = Request.readString(fp.value,0,fp.value.length);
				} else if (fp.name.equals("asar")) {
					s.artist = Request.readString(fp.value,0,fp.value.length);
				} else if (fp.name.equals("astn")) {
					s.track = Request.readInt(fp.value, 0,2);
				} else if (fp.name.equals("asgn")) {
					s.genre = Request.readString(fp.value,0,fp.value.length);
				} else if (fp.name.equals("asur")) {
					s.rating = Request.readInt(fp.value,0,1);
				} else if (fp.name.equals("asfm")) {
					s.format = Request.readString(fp.value,0,fp.value.length);
				} else if (fp.name.equals("astm")) {
					s.time = Request.readInt(fp.value,0,4);	
				} else if (fp.name.equals("assz")) {
					s.size = Request.readInt(fp.value,0,4);	
				} else if (fp.name.equals("asbr")) {
					s.bitrate = Request.readInt(fp.value,0,2);	
				}
			}
			songs.add(s);
		}
		return songs;
	}
}

