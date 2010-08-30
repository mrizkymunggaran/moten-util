/*
 * Created on Aug 22, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package itunes.client.swing;

import itunes.client.request.NoServerPermissionException;

import org.cdavies.itunes.ConnectionStatus;
import org.cdavies.itunes.ItunesHost;


class ConnectWorker extends Thread {

	private final One2OhMyGod one2;
	public boolean paused;

    public ConnectWorker(One2OhMyGod god)
	{
		super();
        this.one2 = god;
        paused = false;
	}
	
	public void run()
	{
	    One2OhMyGod.debugPrint("STARTING THE THREAD\n");
	    ItunesHost h = null ;
	    ConnectionStatus _status = null;

	    
	    while(true){
	        while(this.one2.seenhosts.size() == 0 || isPaused()){		            
	            try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
	        }
	    
	        h = (ItunesHost) one2.seenhosts.remove(0);
	        One2OhMyGod.debugPrint("trying host: " + h.getAddress());
	        _status = new ConnectionStatus(h);
	    
	        final String connectString =
				new String(
					"Connecting to Host: "
						+ h.getName()
						+ " ("
						+ h.getAddress()
						+ ")");

	        
	        one2.doStatusUpdate(connectString);
	        
	        try  {
	            one2.connectToHost(h.getAddress(), _status);
	        } catch (NoServerPermissionException e1) {
	            one2.hostQueueModel.updateStatus(h.getAddress(), HostTableModel.FAILED);
	        } catch (OutOfMemoryError e) {
	        	System.gc();
	        	one2.hostQueueModel.updateStatus(h.getAddress(), HostTableModel.FAILED);
	        }
	        
	       one2.doStatusUpdate(" ");
	    }
	}
	
	public synchronized boolean switchPausedStatus(){
		return (paused = !paused);
	}
	
	public synchronized boolean isPaused(){
		return paused;
	}
}