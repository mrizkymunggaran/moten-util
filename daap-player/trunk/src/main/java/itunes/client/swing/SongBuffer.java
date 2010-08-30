package itunes.client.swing;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

// Buffer.java
/*
 Holds the transactions for the worker
 threads. 
*/

import javax.swing.table.AbstractTableModel;

/**
 * @author jbarnett
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SongBuffer extends AbstractTableModel {
   
    private ArrayList songs;
    public final static String[] columnNames = {"#", "Filename", "Host", "Status"};
    int length;
    int position;

	public SongBuffer(){
	    super();
		songs = new ArrayList();
		length = 0;    
		position = 0;
	}
	
	public synchronized void clearFinished(){		
	    Iterator iter = songs.iterator();
	    while(iter.hasNext()){
	       SongData sd = (SongData) iter.next();
	       if(sd.downloadStatus == COMPLETE || sd.downloadStatus == FAILED){
	           iter.remove();
	           position--;
	       }
	    }
	    fireTableDataChanged();
	}
	
	public void clearSelected(int[] selectedRows)
	{
		synchronized(songs)
		{
			ListIterator iter= songs.listIterator();
			int count = songs.size()-1;
			int rowIndex = selectedRows.length-1;
			while(iter.hasNext())
				iter.next();
	
			while(iter.hasPrevious())
			{
				if( rowIndex <0 )
					break;
				
				SongData sd = (SongData)iter.previous();
				if(selectedRows[rowIndex] == count)
				{
/*					if (sd.downloadStatus != DOWNLOADING)
					{
	*/					iter.remove();
						if(position>count)
							position--;
	/*				}
					else
						System.out.println(position + " SHIT SHIT SHIT RACE CONDITION " + sd.fileName );
		*/			rowIndex--;
				}
				count--;
			}
			fireTableDataChanged();
		}
	}
	
	
	public synchronized void add(SongData newSong){		
		songs.add(newSong);  

		fireTableRowsInserted(songs.size()-1, songs.size());
		notifyAll();
	}

	public synchronized int size(){
	    return songs.size()-position;
	}
	
	public synchronized boolean isEmpty() {
	    return songs.size() == position;
	}

	public synchronized SongData get(int i){
	    return (SongData) songs.get(i);
	}
	    

    public synchronized SongData remove(){    
        while(true){
                if(!isEmpty())
                {
            		SongData returner;
            		
           		    returner = (SongData)songs.get(position);
           		    position++;

            		return returner;	
                }          
                One2OhMyGod.debugPrint("Stooped here");
			try{	wait();	} catch(InterruptedException ignored) {}
		}
	}
	
	
	public String getColumnName(int col) {
		return columnNames[col];
	}
	
	public  void clear() {

	}
	
	public  void AddRow(SongData p) {

	}

	public synchronized int getRowCount() {
		return songs.size();
	}

	public synchronized int getColumnCount() {
		return columnNames.length;
	}

	public synchronized Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}
	
	public synchronized int getStatusAt(int rowIndex, int columnIndex) {
	    SongData p =  (SongData)songs.get(rowIndex);
		return p.downloadStatus;
	}
	
	public synchronized int getProgressAt(int rowIndex, int columnIndex) {
	    SongData p =  (SongData)songs.get(rowIndex);
		return p.progress;
	}
	
	public synchronized int getProgressAsPercentageAt(int rowIndex, int columnIndex) {
	    SongData p =  (SongData)songs.get(rowIndex);
	  //  System.out.println(p.progress + "/" + p.size);
		return (p.progress*100)/p.size;
	}
    

    //	public synchronized final String[] columnNames = {"Artist","Album","Title","Track","Host"};

	public synchronized Object getValueAt(int rowIndex, int columnIndex) {
	    SongData p =  (SongData)songs.get(rowIndex);
	    if(columnIndex == POSITION){ return Integer.toString(rowIndex+1); }
	    if(columnIndex == FILENAME){ return p.fileName; }
	    if(columnIndex == HOSTNAME){ return p.server; }
	    if(columnIndex == STATUS_COLUMN){ return statusNames[p.downloadStatus]; }
	    return null;
	}
	
	public synchronized void updateCurrentStatus(int status, SongData sd)
	{
		if(position > 0)
		{
			SongData data = (SongData) songs.get(position-1);
			if(data == sd){
				data.downloadStatus = status;
				fireTableCellUpdated(position-1, STATUS_COLUMN);
			}
		}
	}
	
	public static final int POSITION = 0;
	public static final int FILENAME = 1;
	public static final int HOSTNAME = 2;
	public static final int STATUS_COLUMN = 3;
	
	
	public static final int PENDING = 0;
	public static final int DOWNLOADING = 1;
	public static final int FAILED = 2;
	public static final int COMPLETE = 3;
	
	private Thread currentWaitingThread;
	String[] statusNames = {"Pending", "Downloading", "Failed", "Complete" } ;
}
