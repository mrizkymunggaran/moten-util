/*
 * Created on May 23, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package itunes.client;

import java.util.ArrayList;

import itunes.client.swing.*;

/**
 * @author Matt Richards
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SongFinder {
	protected ArrayList locationQueue;
	protected TableSorter sorter;
	
	public static int MULTIPLE_INSTANCES_FOUND = -5;
	public static int NO_INSTANCE_FOUND = -1;
	
	public SongFinder(TableSorter sorter)
	{
		this.sorter = sorter;
		locationQueue = new ArrayList();
	}
	
	public int find(String query, String column)
	{
		locationQueue.clear();
		query = query.toLowerCase();
		for (int i = 0; i < sorter.getRowCount(); i++){
			String value = ""; 
			if (column.equals("Song")) value = sorter.getSongAt(i);
			else{
				if (column.equals("Artist")) value = sorter.getArtistAt(i);
				else {
					if (column.equals("Album")) value = sorter.getAlbumAt(i);
				}
			}
			if (value.toLowerCase().indexOf(query) != -1) locationQueue.add(new Integer(i));
		}
		
		switch (locationQueue.size()){
			case 0: return NO_INSTANCE_FOUND;
			case 1: return ((Integer)locationQueue.remove(0)).intValue();
			default: return MULTIPLE_INSTANCES_FOUND;
		}
	}
	
	public int getNext()
	{
		return ((Integer)locationQueue.remove(0)).intValue();
	}
	
	public boolean anyMore()
	{
		return locationQueue.size() > 0;
	}
	
}
