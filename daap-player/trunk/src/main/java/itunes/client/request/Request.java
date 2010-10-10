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

import itunes.FieldPair;
import itunes.client.swing.One2OhMyGod;
import itunes.util.Hasher;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;

import org.cdavies.itunes.ConnectionStatus;

/**
 * @author jbarnett
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class Request {
	public static final int ITUNES_PORT = 3689;
	protected String server;
	protected int port;
	public byte[] data;
	protected int offset;
	protected int expectedLength;
	protected String dataType;
	protected ArrayList fieldPairs;
	protected ArrayList mlitIndexes;
	protected ArrayList mlclIndexes;
	protected ArrayList mdclIndexes;
	protected String requestString;
	protected ConnectionStatus _status;
	
	
	public Request(String server, int port, String rs,ConnectionStatus status) throws NoServerPermissionException {
		this.server = server;
		this.port = port;
		dataType = "";
		offset = 0;
		fieldPairs = new ArrayList();
		mlitIndexes = new ArrayList();
		mlclIndexes = new ArrayList();
		mdclIndexes = new ArrayList();
		requestString = rs;
		_status = status;
		
		Query();
		Process();
		
	}
	
	protected void Query() throws NoServerPermissionException {
		URL url =null;
		try {
		    System.out.println(new Date() + " querying: " + requestString);
			url = new URL("http://"+server+":"+port+"/"+requestString);
			if (One2OhMyGod.debug)
				System.out.println("Processing Request: "+ server+":"+port+"/"+requestString);
			URLConnection urlc = url.openConnection();
			String hashCode = Hasher.GenerateHash("/" + requestString,1,_status,-1);
			
			One2OhMyGod.debugPrint("Created hash for version " + _status.getItunesHost().getVersion());
			
			urlc.addRequestProperty("Client-DAAP-Validation", hashCode);
			urlc.addRequestProperty("Client-DAAP-Access-Index", "1");

			DataInputStream in = new DataInputStream(urlc.getInputStream());
			int len = urlc.getContentLength();
			if (len == -1) {
				return;
			}
			data = new byte[len];
			in.readFully(data);
		} catch (MalformedURLException e) {
			if (One2OhMyGod.debug)
				System.out.println("Malformed URL");	
		} catch (IOException ioe) {
			if (One2OhMyGod.debug)
				System.out.println(ioe.getLocalizedMessage());
			throw new NoServerPermissionException();
		}
	}
	
	protected static int readSize(String data) {
		return readSize(data, 4);
	}
	
	protected static int readSize(String data, int j) {
		String elength = "";
		for (int i = 0; i < j; i++) {
			elength += (data.charAt(i)>15?"":"0") + Integer.toHexString(data.charAt(i));
		}
		return Integer.valueOf(elength,16).intValue();
	}
	
	protected String dataString(int i) {
		return readString(data,offset,i);
	}
	
	public static String readString(byte[] data, int offset, int i) {
		String a = "";
		try {
			a = new String(data, offset,i,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();	
		}
		return a;	
	}
	
	protected int dataInt() {
		return readInt(data, offset,4);
	}
	
	protected static int readInt(byte[] data, int offset) {
		int i = 0;
		try {
			ByteArrayInputStream b = new ByteArrayInputStream(data, offset, 4);
			DataInputStream d = new DataInputStream(b);
			i = d.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return i;
	}
	
	public static int readInt(byte[] data, int offset, int size) {
		int i = 0;
		try {
			ByteArrayInputStream b = new ByteArrayInputStream(data, offset, size);
			DataInputStream d = new DataInputStream(b);
			int pow = size*2 - 1;
			for (int j = 0;j<size;j++) {
				int num = (0xFF&d.readByte());
				int up = ((int)Math.pow(16,pow))*(num/16);
				pow--;
				int down= ((int)Math.pow(16,pow))*(num%16);
				i+= up + down;
				pow--;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return i;
	}
	
	protected void Process() throws NoServerPermissionException {
	    System.out.println(new Date() + "processing");
		if (data.length==0) {
			return;
		}
		dataType= dataString(4);
		offset += 4;
		int size = dataInt();
		offset += 4;
		
		fieldPairs = processDataFields();	
		System.out.println(new Date() + "processed");
	}
	
	protected ArrayList processDataFields(byte[] data, int offset) {
		ArrayList fieldPairs = new ArrayList();
		while (offset < data.length) {
			String name="";
			name = readString(data,offset,4);
			offset +=4;
			int size=readInt(data,offset);
			offset+=4;
			FieldPair fp = new FieldPair(name, data, offset, size);
			offset += size;
			fieldPairs.add(fp);
			if (name.equals("mlcl")) {
				mlclIndexes.add(new Integer(fieldPairs.size()-1));
			} else if (name.equals("mlit")) {
				mlitIndexes.add(new Integer(fieldPairs.size()-1));
			} else if (name.equals("mdcl")) {
				mdclIndexes.add(new Integer(fieldPairs.size()-1));
			}
		}
		return fieldPairs;
	}
	
	protected ArrayList processDataFields() {
		return processDataFields(data,offset);
//		ArrayList fieldPairs = new ArrayList();
//		while (offset < data.length) {
//			String name="";
//			name = dataString(4);
//			offset +=4;
//			int size=dataInt();
//			offset+=4;
//			FieldPair fp = new FieldPair(name, data, offset, size);
//			offset += size;
//			fieldPairs.add(fp);
//			if (name.equals("mlcl")) {
//				mlclIndexes.add(new Integer(fieldPairs.size()-1));
//			} else if (name.equals("mlit")) {
//				mlitIndexes.add(new Integer(fieldPairs.size()-1));
//			} else if (name.equals("mdcl")) {
//				mdclIndexes.add(new Integer(fieldPairs.size()-1));
//			}
//		}
//		return fieldPairs;
	}
	
	@Override
    public String toString() {
		String ret = "";
		for (int i = 0; i < fieldPairs.size();i++) {
			FieldPair fp = (FieldPair)fieldPairs.get(i);
			ret += fp.toString()+"\n";		
		}
		return ret;	
	}
}

