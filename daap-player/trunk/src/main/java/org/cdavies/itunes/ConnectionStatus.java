package org.cdavies.itunes;


public class ConnectionStatus {
	
	private int _requestNo;
	private ItunesHost _host;
	
	public ConnectionStatus(ItunesHost host) {
		
		_requestNo = 0;
		_host = host;
		
	}
	
	public int getNextRequestNumber() {
		
		return ++_requestNo;
		
	}
	
	public ItunesHost getItunesHost() {
		
		return _host;
		
	}
		
	
}
