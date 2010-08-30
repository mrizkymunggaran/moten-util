/*
 * Created on Aug 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package itunes.client.swing;

import org.cdavies.itunes.ConnectionStatus;


/**
 * @author blackmad
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SongData {
		public String fileName;
		public String server;
		public String hostName;
		public int port;
		public int dbId;
		public int songId;
		public int size;
		public String songFmt;
		public int sessionId;
		public ConnectionStatus status;
		public String dir;
		public int downloadStatus;
		public int progress;

		public SongData(
			String fileName,
			String server,
			String hostName,
			int port,
			int dbId,
			int songId,
			String songFmt,
			int size,
			int sessionId,
			ConnectionStatus status,
			String dir
		) {
			this.fileName = fileName; 
			this.hostName = hostName;
			this.server = server;
			this.port = port;
			this.dbId = dbId;
			this.songId = songId;
			this.songFmt = songFmt;
			this.sessionId = sessionId;
			this.status = status;
			this.dir = dir;
			this.size = size;
			 
			downloadStatus = SongBuffer.PENDING;
		}
}
