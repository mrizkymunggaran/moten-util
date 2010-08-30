package org.cdavies.itunes;

import itunes.client.swing.One2OhMyGod;

public class ItunesHost {
	
	private String _address;
	private int _version;
	private String _name;
	//private ConnectionStatus status;
	private boolean isVisible;
	
	public static int LEGACY = 1;
	public static int ITUNES_4 = 2;
	public static int ITUNES_45 = 3;
	
	
	public ItunesHost(String addr, String name, int version) {
		
	    isVisible = true;
	    _address = addr;
		_name = name;
		
		One2OhMyGod.debugPrint("\n\n\n HERE, MOFO ___" + addr + "____" + name + "___");
		
		if (_version > 3)
			_version = ITUNES_45;
		else
			_version = version;
		
	}
	
	public synchronized int getVersion() {
		
		return _version;
		
	}
	
	public synchronized void setVersion(int v) {
		
		_version = v;
		
	}
	
	public synchronized String getName() {
		
		return _name;
		
	}
	
	public synchronized void setName(String s) {
		
		_name=s;
		
	}
	
	public synchronized String getAddress() {
		
		return _address;
		
	}
	
	public synchronized void setAddress(String address) {
		
		_address=address;
		
	}
	
	public synchronized String toString() {
		
		return _name;
		
	}
	
	public boolean equals(Object o) {
	    //System.out.println(_name + " vs " + ((ItunesHost)o).getName());
		return getName().equals(((ItunesHost)o).getName());
	}
	
	public synchronized void setVisible(boolean b){
	    isVisible = b;
	}

	public synchronized boolean getVisible(){
	    return isVisible ;
	}
}
