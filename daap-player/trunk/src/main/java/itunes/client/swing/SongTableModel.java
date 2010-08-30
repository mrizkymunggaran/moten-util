/*
 * Created on May 8, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 * 
 * Copyright 2003 Joseph Barnett

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
package itunes.client.swing;

import itunes.client.*;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import org.cdavies.itunes.*;

/**
 * @author jbarnett
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SongTableModel extends AbstractTableModel {

	public final static String[] columnNames = {"Host", "Artist","Album","Title","Track","Size","Time","Bitrate"};
	public static final int HOST_COLUMN = 0;
	public static final int ARTIST_COLUMN = 1;
	public static final int ALBUM_COLUMN = 2;
	public static final int TITLE_COLUMN = 3;
	public static final int TRACK_COLUMN = 4;
	public static final int SIZE_COLUMN = 5;
	public static final int TIME_COLUMN = 6;
	public static final int BITRATE_COLUMN = 7;

	public ArrayList data;
	
	class SongRowData {
		public int sessionId;
		public int playdb;
		public ConnectionStatus status;
		public Song s;
		
		public SongRowData(Song s, int sessionId, int playdb,
				ConnectionStatus status){
			this.s = s;
			this.playdb = playdb;
			this.sessionId = sessionId;
			this.status = status;
		}
	}

	public SongTableModel() {
		super();
		data = new ArrayList();
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public void clear() {
		data = new ArrayList();
	}

	public void AddRow(
		Song s,
		String host,
		int sessionId,
		int playdb,
		ConnectionStatus status) {
		
		data.add(new SongRowData(s, sessionId, playdb, status));
		
	}
	

	public int getRowCount() {
		return data.size();
	}
	
	public Song getSongAt(int row){
		return ((SongRowData)data.get(row)).s;
	}
	
	public ItunesHost getItunesHostAt(int row){
		return ((SongRowData)data.get(row)).status.getItunesHost();
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public Class getColumnClass(int c) {
		//return "".getClass();
		//System.out.println("COLUMN: " + c);
		return getValueAt(0, c).getClass();
	}
	
	public Integer getTimeAt(int row) {
		return new Integer(getSongAt(row).getTime());
	}
	
	public Integer getSizeAt(int row) {
		return new Integer(getSongAt(row).getSize());
	}
	
	public Integer getBitrateAt(int row) {
		return new Integer(getSongAt(row).getRate());
	}

	public String getArtistAt(int row) {
		return getSongAt(row).getArtist();
	}

	public String getAlbumAt(int row) {
		return getSongAt(row).getAlbum();
	}

	public String getSongNameAt(int row) {
		return getSongAt(row).getName();
	}

	public Integer getTrackNumAt(int row) {
		return new Integer(getSongAt(row).getTrack());
	}

	public String getRatingAt(int row) {
		Song s = getSongAt(row);
		String rateString = "";
		for (int i = 0; i < s.rating / 20; i++) {
			rateString += "*";
		}
		return rateString;
	}

	public Integer getSongIDAt(int row) {
		Song s = getSongAt(row);
		return new Integer(s.getId());
	}

	public String getFormatAt(int row) {
		return getSongAt(row).getFormat();
	}

	public String getAddressAt(int row) {
		return getItunesHostAt(row).getAddress();
	}
	
	public String getHostNameAt(int row) {
		return getItunesHostAt(row).getName();
	}
	
	public boolean getVisibleAt(int row) {
		return getItunesHostAt(row).getVisible();
	}

	public Integer getSessionIDAt(int row) {
		return new Integer(((SongRowData)data.get(row)).sessionId);
	}

	public Integer getDBIDAt(int row) {
		return new Integer(((SongRowData)data.get(row)).playdb);
	}

	public ConnectionStatus getStatusAt(int row) {
		return ((SongRowData)data.get(row)).status;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
	    if(columnIndex==HOST_COLUMN){
			String name = getHostNameAt(rowIndex);
			if(name == null || name.length() == 0 || name.equals(" ")){
			    name = getAddressAt(rowIndex);
			}
			return name;
	    }
		return getDataAt(rowIndex, columnIndex);
	}
	
	
	public Object getDataAt(int rowIndex, int columnIndex) {;
		switch(columnIndex){
			case HOST_COLUMN:
				return getHostNameAt(rowIndex);
			case ARTIST_COLUMN:
				return getArtistAt(rowIndex);
			case ALBUM_COLUMN:
				return getAlbumAt(rowIndex);
			case TITLE_COLUMN:
				return getSongNameAt(rowIndex);
			case TRACK_COLUMN:
				return getTrackNumAt(rowIndex);
			case SIZE_COLUMN:
				return getSizeAt(rowIndex);
			case TIME_COLUMN:
				return getTimeAt(rowIndex);
			case BITRATE_COLUMN:
				return getBitrateAt(rowIndex);
			default:
				return null;
		}
	}
	
	public void printRow(int row) {
	    System.out.println(data.get(row));
	}


	public void removeHost(String hostName)
	{
		for (int i = getRowCount() - 1; i >= 0; --i){
			if (getHostNameAt(i).equals(hostName)){
				data.remove(i);
			}
		}
	}
}
