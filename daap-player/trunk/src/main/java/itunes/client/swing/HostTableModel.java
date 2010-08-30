/*
 * Created on Aug 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package itunes.client.swing;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;
import javax.swing.event.TableModelEvent;

import org.cdavies.itunes.ItunesHost;


/**
 * @author blackmad
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class HostTableModel extends AbstractTableModel {

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    
    String[] columnNames = {"Vis", "Hostname", "IP Address", "Database Size", "Status"};
    final static int VISIBLE = 0;
    final static int HOSTNAME = 1;
    final static int ADDRESS= 2;
    final static int DBSIZE = 3; 
    final static int STATUS = 4;  
    
    private ArrayList hosts;
    private TableSorter songTable;
    private int numHidden;
    
    String[] hostStatus = {"Resolving", 
            		"Connecting", 
            		"Connected", 
            		"Disconnected", 
            		"Failed",
            		"Viewing",
            		"Downloading"
    };
    public final static int RESOLVING = 0;
    public final static int CONNECTING = 1;
    public final static int CONNECTED = 2; 
    public final static int DISCONNECTED = 3;  
    public final static int FAILED = 4;
    public final static int VIEWING = 5;  
    public final static int DOWNLOADING = 6; 
    
    public HostTableModel(TableSorter sorter) {
        hosts = new ArrayList();
        songTable = sorter;
        numHidden = 0;
    }
    
    public String getColumnName(int col){
        return columnNames[col];
    }
    
    public int getColumnCount() {
        return columnNames.length;
    }
    
    public Class getColumnClass(int column) {
        if(column==VISIBLE){
            return Boolean.class;
        }
        
        return super.getColumnClass(column);
    }

    public boolean isCellEditable(int row, int col){ 
    	int status = ((HostEntry)hosts.get(row)).status;
        if(col==VISIBLE && status != DISCONNECTED && status != FAILED){
            return true;
        }
        return false;
    }

    public void setValueAt(Object value, int row, int col) {
        System.out.println(value + " at " + row + "," + col);
        HostEntry he = (HostEntry)hosts.get(row);
        he.setVisible(((Boolean)value).booleanValue());
        //rowData[row][col] = value;
        fireTableCellUpdated(row, col);
        
        if (((Boolean)value).booleanValue() == false)
        	numHidden++;
        else
        	numHidden--;
        
        if(songTable!=null){
            songTable.tableChanged(new TableModelEvent(this));
        }
            
    }

    
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        // TODO Auto-generated method stub
        return hosts.size();
    }
    
    public ItunesHost getHostAt(int rowIndex) {
        // TODO Auto-generated method stub
        HostEntry he = (HostEntry) hosts.get(rowIndex);
        return he.ith;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        // TODO Auto-generated method stub
        HostEntry he = (HostEntry) hosts.get(rowIndex);
        switch(columnIndex){
    		case VISIBLE:
    		    return new Boolean(he.getVisible());
        	case HOSTNAME:
        	    return he.getName();
        	case ADDRESS:
        	    return he.getAddress();
        	case DBSIZE:
        	    return new Integer(he.num_songs);
        	case STATUS:
        	    return hostStatus[he.status];
        	
        }
        return null;
    }
    
    public void addHost(ItunesHost h){      
        HostEntry he = new HostEntry(h, 0, RESOLVING);
        hosts.add(he);
        fireTableRowsInserted(hosts.size(), hosts.size());
    }
    
    public void updateSize(String address, int dbsize){
        Iterator iter = hosts.iterator();
        
        while(iter.hasNext()){
            HostEntry he = (HostEntry) iter.next();
            if(he.getAddress().equals(address)){
                he.num_songs = dbsize;
                break;
            }
        }
        fireTableDataChanged();
    }
    
    public void updateStatus(String name, int status){
        Iterator iter = hosts.iterator();
        
        while(iter.hasNext()){
            HostEntry he = (HostEntry) iter.next();
            if(he.getName().equals(name) || he.getAddress().equals(name)){
                he.status = status;
                break;
            }
        }
        
        fireTableDataChanged();
    }
    
    public boolean anyHiddenHosts()
    {
    	return (numHidden > 0);
    }
    
    private class HostEntry { 
        public int status;
        public ItunesHost ith;
        public int num_songs;
        
        public HostEntry(ItunesHost h, int num_songs, int status){
            this.ith = h;
            this.num_songs = num_songs;
            this.status = status;
        }
        
        public String getName() { 
            return ith.getName();
        }
        
        public String getAddress(){
            return ith.getAddress();
        }
        
        public boolean getVisible(){
            return ith.getVisible();
        }
        
        public void setVisible(boolean b){
            ith.setVisible(b);
        }
        
        public boolean equals(Object o){
            return getName().equals(((HostEntry)o).getName());
        }
    }

}

