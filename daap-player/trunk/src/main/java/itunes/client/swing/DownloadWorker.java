/*
 * Created on Aug 22, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package itunes.client.swing;

import itunes.client.request.LoginRequest;
import itunes.client.request.LogoutRequest;
import itunes.client.request.NoServerPermissionException;
import itunes.client.request.Request;
import itunes.client.request.SongRequest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.cdavies.itunes.ConnectionStatus;
import org.cdavies.itunes.ItunesHost;


class DownloadWorker extends Thread {

	private final One2OhMyGod one2;
    protected int sessionId;
	protected String host;
	protected String connectedHost;
	protected ConnectionStatus _status;
		
	public DownloadWorker(One2OhMyGod god)
	{
		super();
        this.one2 = god;
	}

	private boolean tryLogin(SongData songData) {
	    ItunesHost h = null;
	    
	    for (int i = 0; i < this.one2.knownIPs.size(); i++) {	        
	        h = (ItunesHost) this.one2.knownIPs.get(i);
	        if (h.getAddress().equals(songData.server))
	            break;
	    }
	    
	    _status = new ConnectionStatus(h);
	    
	    if (sessionId != -1) {
	        try {
	            LogoutRequest lr =
	                new LogoutRequest(
	                        connectedHost,
	                        Request.ITUNES_PORT,
	                        sessionId,
	                        _status);
	        } catch (NoServerPermissionException e) {
	        }
	        sessionId = -1;
	    }
	    One2OhMyGod.debugPrint("songData.server in try: " + songData.server);
	    LoginRequest l = null;
	    try {
	        l = new LoginRequest(songData.server, Request.ITUNES_PORT, _status);
	        connectedHost = songData.server;
	    } catch (NoServerPermissionException e) {
	        One2OhMyGod.debugPrint("couldn't connect to host " + songData.server);
	        sessionId = -1;
	        connectedHost = null;
	        
	    }
	    
	    if(l != null)
	        sessionId = l.getSessionId();
	    if (sessionId == -1) {
	        JOptionPane.showMessageDialog(
	                this.one2.frame,
	                "Error connecting to " + songData.server);
	        return false;
	    }
	    //host = songData.server;
	    One2OhMyGod.debugPrint("logged in: session " + sessionId);
	    return true;
	}
	
	public void run() {
	    while (true) {
	        SongData songData = null;
	        final Integer curSize;
	        final int iCurSize;
	        int tries = 0;
	        yield();
	        
	        // if we made it through, songData = null -- so get a new one
	        // otherwise do nothing
	        
	        iCurSize = One2OhMyGod.songQueue.size();
	        
	        if(songData == null){
                songData = (SongData) One2OhMyGod.songQueue.remove();
	        }
	        
	        if (!songData.server.equals(connectedHost)) {
	            One2OhMyGod.debugPrint(
	                    "not connect to "
	                    + songData.server
	                    + " instead connected to "
	                    + connectedHost);
	            if (connectedHost != null){ 
	                try {
	                    this.one2.hostQueueModel.updateStatus(connectedHost, HostTableModel.VIEWING);
	                    Request lr =
	                        new LogoutRequest(
	                                connectedHost,
	                                Request.ITUNES_PORT,
	                                sessionId,
	                                _status);
	                } catch (NoServerPermissionException e) {
	                }
	            }
	            if(!tryLogin(songData)){
	                String badHost = songData.server;
	                synchronized (One2OhMyGod.songQueue) {
	                    while(songData.server.equals(badHost) && !One2OhMyGod.songQueue.isEmpty()){
	                        songData = (SongData) One2OhMyGod.songQueue.remove();
	                    }
	                }
	                
	                // if our last popped songData is still on this host
	                // set it to null
	                // in any case, go to the next loop iteration
	                if(songData.server.equals(badHost)){
	                    songData = null;
	                }
	                
	                this.one2.doStatusUpdate("");		              
	                
	                continue;
	            }
	        }
	        
	   	 	this.one2.hostQueueModel.updateStatus(songData.server, HostTableModel.DOWNLOADING);
	        
	        SongRequest sr = null;
	        while (songData != null && sr == null && tries < 2) {
	            One2OhMyGod.songQueue.updateCurrentStatus(SongBuffer.DOWNLOADING, songData);
	            try {
	                sr =
	                    new SongRequest(
	                            songData.server,
	                            songData.port,
	                            songData.dbId,
	                            songData.songId,
	                            songData.songFmt,
	                            sessionId,
	                            // songData.status
	                            _status);
	            } catch (NoServerPermissionException e) {
	                //e.printStackTrace();
	                One2OhMyGod.debugPrint(
	                "that request didn't work ... try again");
	                /*if (!songData.server.equals(connectedHost))*/ 
	                tryLogin(songData);
	                tries++;
	            }
	        }
	        
	        if(songData == null){	
	            this.one2.doStatusUpdate("");
                One2OhMyGod.songQueue.updateCurrentStatus(SongBuffer.FAILED, songData);
	            
	            continue;
	        }
	        else {
	            One2OhMyGod.songQueue.updateCurrentStatus(SongBuffer.DOWNLOADING, songData);
	        }
	        
	        final String filename = songData.fileName;
	        One2OhMyGod.debugPrint("Popped to queue: " + filename);		       
	        
	        int newSize = iCurSize -1;
	        if(iCurSize == 0) {newSize = 0;}
	         String downloadString =
	                    new String(
	                            "Downloading "
	                            + "("
	                            + Integer.toString(newSize)
	                            + " remaining): " 
	                            + filename
	                    );

		    this.one2.doStatusUpdate(downloadString);
	        
	        if(sr != null){
	            try {
	                String filesep = System.getProperty("file.separator");
	                int index = filename.lastIndexOf(filesep);
	                if(index > 0){
	                    String path = filename.substring(0, index);
	                    One2OhMyGod.debugPrint("PATHS: " + path);
	                    (new File(songData.dir, path)).mkdirs();
	                }
	                
	                BufferedOutputStream out =
	                    new BufferedOutputStream(
	                            new FileOutputStream(songData.dir + filename));
	                byte[] buffer = new byte[512];
	                int byte_total= 0;
	                while (true) {
	                    int read = (sr.getStream()).read(buffer);
	                    if (read == -1)
	                        break;	                  
	                    out.write(buffer,0,read);
	                    byte_total += read;
	                    
	                    songData.progress=byte_total;
	                    One2OhMyGod.songQueue.updateCurrentStatus(SongBuffer.DOWNLOADING, songData);
	                }	              
	                out.close();
	                
	                One2OhMyGod.debugPrint("comparison: " + byte_total + " " + songData.size);
	                
	               if(byte_total != songData.size){
	                // File (or directory) to be moved
	                File oldFile = new File(songData.dir + filename);
	                
	                // Move file to new directory
	                oldFile.renameTo(new File(songData.dir + "INCOMPLETE-" + filename));
	               }
	               else {
	               		One2OhMyGod.songQueue.updateCurrentStatus(SongBuffer.COMPLETE, songData);
	               }
	            } catch (FileNotFoundException e) {
	                System.err.println("ResourceModel:File not found");
	            } catch (SecurityException e) {
	                System.err.println("ResourceModel:Security Exception");
	            } catch (IOException e) {
	                System.err.println("ResourceModel:IOException");
	            }
	            
	            if (iCurSize <= 1) {
			        this.one2.doStatusUpdate("");
	            }
	     
	         songData = null;   
	        }
	    }
	}
}