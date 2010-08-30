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
public class DatabasesRequest extends Request {

	protected ArrayList mlclDataFields;
	protected ArrayList mlitDataFields;
	
	public DatabasesRequest(String server, int port, int sessionId, ConnectionStatus status)  throws NoServerPermissionException{
		super(server, port, "databases?session-id="+sessionId, status);
	}
	
	public DatabasesRequest(String server, int port, String rs, ConnectionStatus status)  throws NoServerPermissionException{
		super(server, port, rs, status);
	}
	protected void Process() throws NoServerPermissionException {
		super.Process();
		parseMLCL();
	}
	
	protected void parseMLCL() {		
		for (int i = 0; i < mlclIndexes.size(); i++) {
			byte[] mlclData = ((FieldPair)fieldPairs.get(((Integer)mlclIndexes.get(i)).intValue())).value;
			mlclDataFields = this.processDataFields(mlclData,0);
		}
		parseMLIT();
	}
	
	protected void parseMLIT() {
		mlitDataFields = new ArrayList();
		for (int i = 0; i < mlitIndexes.size(); i++) {
			byte[] mlitData = ((FieldPair)mlclDataFields.get(((Integer)mlitIndexes.get(i)).intValue())).value;
			mlitDataFields.add(processDataFields(mlitData,0)); 
		}
	}

	public int getLibraryCount() {
		return mlclDataFields.size();		
	}
	
	public int getDbId(int i) {
		ArrayList currMlitDataFields = ((ArrayList)mlitDataFields.get(i));
		int index = currMlitDataFields.indexOf(new FieldPair("miid",null,0,0));
		FieldPair fp = (FieldPair)currMlitDataFields.get(index);
		return Request.readInt(fp.value,0);
	}

	public int getSongCount(int i) {
		ArrayList currMlitDataFields = ((ArrayList)mlitDataFields.get(i));
		int index = currMlitDataFields.indexOf(new FieldPair("mimc",null,0,0));
		FieldPair fp = (FieldPair)currMlitDataFields.get(index);
		return Request.readInt(fp.value,0);
	}
	
	public ArrayList getDbs() {
		ArrayList dbs = new ArrayList();
		for (int i = 0; i < mlitDataFields.size();i++) {
			ArrayList fps = ((ArrayList)mlitDataFields.get(i));
			String name=null;
			int id=0;
			Database d = new Database();
			for (int j = 0; j < fps.size();j++) {
				FieldPair fp =((FieldPair)fps.get(j));
				if (fp.name.equals("miid")) {
					d.id = Request.readInt(fp.value,0);
				} else if (fp.name.equals("minm")) {
					d.name = Request.readString(fp.value,0,fp.value.length);
				} 
			}
			dbs.add(d);
		}
		return dbs;
	}
	
	public String toString() {
		String ret = super.toString();
		for (int i = 0; i < mlclDataFields.size();i++) {
			ret += ((FieldPair)mlclDataFields.get(i)).toString() + "\n";
		}
		for (int i=0; i < mlitDataFields.size();i++) {
			ArrayList fps = ((ArrayList)mlitDataFields.get(i));
			for (int j = 0; j < fps.size();j++) {
				ret += ((FieldPair)fps.get(j)).toString() + "\n";
			}
		}
		return ret;
	}
}
