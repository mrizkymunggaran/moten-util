/*
 * Created on May 7, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 * 
 Copyright 2003 Joseph Barnett

 This File is part of "one 2 oh my god"

 "one 2 oh my god" is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as pworekublished by
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

//
// DONE: move addHost call to thread
// DONE: get length from DB
// DONE: filter by bitrate
// DONE: get size  from database
// DONE: use that to determine if download finishes -- check
// DONE: make it reflect download percentage status in queue table
// TODO: passworded hosts
// TODO: add drop-down to host adder
// TODO: "the X" --> "X, the" option
// TODO: add retry host button (started)
// TODO: integrate jmdns again
// TODO: problem when you kill currently downloading song
// TODO: reblance tables 

package itunes.client.swing;

import itunes.client.Database;
import itunes.client.Song;
import itunes.client.request.DatabasesRequest;
import itunes.client.request.LoginRequest;
import itunes.client.request.LogoutRequest;
import itunes.client.request.NoServerPermissionException;
import itunes.client.request.Request;
import itunes.client.request.ServerInfoRequest;
import itunes.client.request.SingleDatabaseRequest;
import itunes.client.request.SongRequest;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.cdavies.itunes.ConnectionStatus;
import org.cdavies.itunes.ItunesHost;
import org.moten.david.util.media.SystemPlayer;
import org.moten.david.util.media.SystemPlayer.Listener;

import com.strangeberry.rendezvous.Rendezvous;
import com.strangeberry.rendezvous.ServiceInfo;
import com.strangeberry.rendezvous.ServiceListener;

public class One2OhMyGod implements ServiceListener {

    private final String programName = "ourTunes v1.3.3";

    protected String connectedHost;
    protected List seenhosts;
    protected ArrayList knownIPs;

    private final JLabel statusL = new JLabel("");
    public final JLabel viewCountL = new JLabel("Viewing 0 Songs");
    public final JLabel totalCountL = new JLabel("0 Songs Total");
    public JFrame frame;

    protected JButton songDl;
    protected JButton songPl;

    protected JPopupMenu popupMenu;

    protected JTextField searchQuery;

    protected SongTableModel songModel;
    protected JTable songTable;
    protected TableSorter sorter;
    static public SongBuffer songQueue;

    public HostTableModel hostQueueModel;
    protected JTable hostQueueTable;

    protected static String iTunesService = "_daap._tcp.local.";
    protected Rendezvous r;

    protected boolean playstop;
    protected int playingRow;
    protected int playdb;
    protected DownloadWorker worker;
    protected ConnectWorker connectworker;
    protected File currDirectory;
    protected Player p;
    private SystemPlayer systemPlayer;
    int playSessionId = -1;
    String playConnectedHost = null;
    ConnectionStatus _playStatus = null;
    Properties properties;
    String propertiesFilename = "ourTunes.ini";
    private String formatString = "%a - %A - %0 - %t.%f";
    private static final int TITLE_COL_WIDTH = 300;
    private static final int TEXT_COL_WIDTH = 150;
    private static final int OTHER_COL_WIDTH = 45;
    private static final int OT_WINDOW_WIDTH = 800;
    private static final int OT_WINDOW_HEIGHT = 450;

    static public boolean debug = false;

    protected boolean setDLDir() {
        File chosen = createDLDirWindow(currDirectory);

        if (chosen == null) {
            return false;
        }

        currDirectory = chosen;
        if (!currDirectory.isDirectory()) {
            if (!currDirectory.mkdirs())
                currDirectory = null;
        }
        return true;
    }

    protected File createDLDirWindow(File startDir) {
        File chosen = null;

        JFileChooser downloadDirChooser = new JFileChooser();

        if (startDir != null && startDir.isDirectory()) {
            downloadDirChooser.setCurrentDirectory(startDir);
        }

        downloadDirChooser.setDialogTitle("Select a Download Directory");
        downloadDirChooser.setApproveButtonText("Accept");
        downloadDirChooser
                .setApproveButtonToolTipText("Sets the selected directory to be the destination for downloaded songs");
        downloadDirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (downloadDirChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            chosen = downloadDirChooser.getSelectedFile();
        }
        return chosen;
    }

    public void stopPlaying() {
        if (p != null)
            p.stopMusic();
        p = null;
        if (systemPlayer != null)
            systemPlayer.stop();
        systemPlayer = null;
        if (songPl != null) {
            songPl.setText("Play Selected");
            songPl.validate();
        }
        playstop = !playstop;
        frame.setTitle(programName);
        try {
            LogoutRequest lr = new LogoutRequest(playConnectedHost,
                    Request.ITUNES_PORT, playSessionId, _playStatus);
        } catch (Exception e) {
        }
    }

    protected void playSong() {
        ItunesHost playHost = null;

        if (!playstop) {
            stopPlaying();
            playingRow = -1;
            playstop = true;
            return;
        }

        final int selection = songTable.getSelectedRow();
        if (selection > 0) {
            SongData songData = new SongData(null, sorter
                    .getAddressAt(selection), sorter.getHostNameAt(selection),
                    Request.ITUNES_PORT,
                    sorter.getDBIDAt(selection).intValue(), sorter.getSongIDAt(
                            selection).intValue(), sorter
                            .getFormatAt(selection), sorter
                            .getSizeAt(selection), sorter.getSessionIDAt(
                            selection).intValue(), null, null);

            synchronized (knownIPs) {
                for (int i = 0; i < knownIPs.size(); i++) {
                    playHost = (ItunesHost) knownIPs.get(i);
                    if (playHost.getAddress().equals(songData.server))
                        break;
                }
            }

            _playStatus = new ConnectionStatus(playHost);

            if (playSessionId != -1) {
                try {
                    LogoutRequest lr = new LogoutRequest(playConnectedHost,
                            Request.ITUNES_PORT, playSessionId, _playStatus);
                } catch (NoServerPermissionException e) {
                }
                playSessionId = -1;
            }
            debugPrint("songData.server in try: " + songData.server);
            LoginRequest l = null;
            try {
                l = new LoginRequest(songData.server, Request.ITUNES_PORT,
                        _playStatus);
                playConnectedHost = songData.server;
            } catch (NoServerPermissionException e) {
                debugPrint("couldn't connect to host " + songData.server);
                playSessionId = -1;
                playConnectedHost = null;
            }

            if (l != null)
                playSessionId = l.getSessionId();
            if (playSessionId == -1) {
                JOptionPane.showMessageDialog(frame, "Error connecting to "
                        + songData.server);
            }
            // host = songData.server;
            debugPrint("logged in: session " + playSessionId);

            debugPrint("trying to playsong\n");

            if (playstop) {
                SongRequest sr = null;
                try {
                    sr = new SongRequest(songData.server, songData.port,
                            songData.dbId, songData.songId, songData.songFmt,
                            playSessionId,
                            // songData.status
                            _playStatus);
                } catch (NoServerPermissionException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(frame,
                            "Error streaming song!");
                    stopPlaying();
                    return;
                }
                String systemPlayerCommand = System
                        .getProperty("system.player");
                if (systemPlayerCommand != null) {
                    systemPlayer = new SystemPlayer(systemPlayerCommand
                            + " ${file}");
                    systemPlayer.play(sorter.getFormatAt(selection), sr
                            .getStream());
                    systemPlayer.addListener(new Listener() {
                        @Override
                        public void finished() {
                            ListSelectionModel model = songTable
                                    .getSelectionModel();
                                playstop = false;
                                playSong();
                        }
                    });
                } else {
                    p = new Player(this, sr.getStream());
                    p.start();
                }
                frame.setTitle(programName + ": Now Playing \""
                        + sorter.getSongAt(selection) + "\" by "
                        + sorter.getArtistAt(selection));
                songPl.setText("Stop playing");
                songPl.validate();
                playingRow = selection;
                playstop = !playstop;
            }
        }
    }

    protected void downloadSong() {

        final int[] selections = songTable.getSelectedRows();

        if (currDirectory == null) {
            if (setDLDir() == false)
                return;
        }

        for (int i = 0; i < selections.length; i++) {
            int selection = selections[i];
            final int fdb = playdb;
            if (songQueue == null) {
                songQueue = new SongBuffer();
            }

            String filename = createFilename(formatString, sorter, selection);

            songQueue.add(new SongData(filename,
                    sorter.getAddressAt(selection), sorter
                            .getHostNameAt(selection), Request.ITUNES_PORT,
                    sorter.getDBIDAt(selection).intValue(), sorter.getSongIDAt(
                            selection).intValue(), sorter
                            .getFormatAt(selection), sorter
                            .getSizeAt(selection), sorter.getSessionIDAt(
                            selection).intValue(), null,
                    (currDirectory != null ? currDirectory.getAbsolutePath()
                            : "")
                            + File.separator));

            if (worker == null) {
                worker = new DownloadWorker(this);
                worker.start();
            }

            debugPrint("Added to queue: " + filename);
        }

    }

    public void resolveService(Rendezvous r, String type, String name,
            ServiceInfo info) {
        if (info == null) {
            debugPrint("Service not found");
        } else {
            if (name.endsWith("." + type)) {
                name = name.substring(0, name.length() - (type.length() + 1));
                addHost(name, info.getAddress());
            }
        }
    }

    public void addHost(String name, String address) {
        ItunesHost ith = new ItunesHost("", name, 1);
        ConnectionStatus _status = new ConnectionStatus(ith);
        ServerInfoRequest rq = null;
        try {
            rq = new ServerInfoRequest(address, Request.ITUNES_PORT, _status);
        } catch (NoServerPermissionException e) {
            return;
        }

        int version;

        if (rq.getServerVersion() >= 3)
            version = ItunesHost.ITUNES_45;
        else if (rq.getServerVersion() >= 2)
            version = ItunesHost.ITUNES_4;
        else
            version = ItunesHost.LEGACY;

        ith.setVersion(version);
        ith.setAddress(address);

        if (name == null || name.length() == 0) {
            ith.setName(rq.getServerName());
        }

        boolean seenhost = false;
        // boolean isLocal = isLocalHost(address);
        boolean isLocal = false;
        synchronized (knownIPs) {
            seenhost = knownIPs.contains(ith);
            if (!seenhost) {
                knownIPs.add(ith);
                // hostQueueModel.addHost(ith);
            }
        }

        if (!seenhost && !isLocal) {

            debugPrint("Discovered " + name + " at address " + address
                    + ": (Server Version " + version + ")");

            hostQueueModel.addHost(ith);
            // ItunesHost h = new ItunesHost(address, name, version);

            seenhosts.add(ith);
        } else {
            debugPrint("Ignoring new service " + name
                    + " from already known IP " + address);
        }
    }

    /**
     * @param host
     *            The string representation of the host to be tested
     * @return true if the given host is the local host
     */
    private boolean isLocalHost(String host) {
        debugPrint("isLocalHost(" + host + ")...?");
        try {
            InetAddress addr = InetAddress.getByName(host);
            InetAddress[] localAddresses = InetAddress.getAllByName(InetAddress
                    .getLocalHost().getHostName());
            for (int i = 0; i < localAddresses.length; i++) {
                if (localAddresses[i].getHostAddress().compareTo(
                        addr.getHostAddress()) == 0)
                    return true;
            }
            debugPrint(addr.getHostAddress() + " is not a local host");
        } catch (UnknownHostException e) {
            System.err
                    .println("This unknown host exception shouldn't matter, ");
            System.err
                    .println("but we print anyway because it shouldn't happen.");
            e.printStackTrace();
        }
        return false;
    }

    public void addService(Rendezvous r, String type, String name) {
        // System.out.println("trying " + name);
        if (name.endsWith("." + type)) {
            name = name.substring(0, name.length() - (type.length() + 1));
        }

        if (name.indexOf('.') == -1)
            r.requestServiceInfo(iTunesService, name);
    }

    public void removeService(Rendezvous r, String type, String name) {
        if (name.endsWith("." + type)) {
            name = name.substring(0, name.length() - (type.length() + 1));
        }

        ItunesHost ith = null;

        synchronized (knownIPs) {
            int hostIndex = knownIPs.indexOf(new ItunesHost("", name, 1));
            if (hostIndex != -1) {
                ith = (ItunesHost) knownIPs.get(knownIPs
                        .indexOf(new ItunesHost("", name, 1)));
            }
            knownIPs.remove(new ItunesHost("", name, 1));
        }

        songModel.removeHost(name);
        songModel.fireTableDataChanged();
        totalCountL.setText(songModel.getRowCount() + " Songs Total");
        debugPrint(name + " went offline");
        hostQueueModel.updateStatus(name, HostTableModel.DISCONNECTED);
    }

    protected void createSongChooser(String host, int playdb, int sessionId,
            ConnectionStatus _status) throws NoServerPermissionException {
        SingleDatabaseRequest sr = new SingleDatabaseRequest(host,
                Request.ITUNES_PORT, sessionId, playdb, _status);
        ArrayList songs = sr.getSongs();

        hostQueueModel.updateSize(host, songs.size());
        Collections.sort(songs);

        // ItunesHost h = null;
        //		
        // synchronized(knownIPs){
        // for (int i = 0; i < knownIPs.size(); i++) {
        // h = (ItunesHost) knownIPs.get(i);
        // if (h.getAddress().equals(host))
        // break;
        // }
        // }

        int numRowsOld = songModel.getRowCount();

        for (int i = 0; i < songs.size(); i++) {
            Song s = (Song) songs.get(i);
            songModel.AddRow(s, host, sessionId, playdb, _status);
        }

        // songTable.validate();
        // songTable.repaint();

        searchQuery.setEnabled(true);
        int[] rows = songTable.getSelectedRows();
        for (int i = 0; i < rows.length; i++)
            System.out.print(rows[i] + " ");
        songModel.fireTableRowsInserted(numRowsOld, songModel.getRowCount());
        // songModel.fireTableDataChanged();
        System.out.print(" - ");
        for (int i = 0; i < rows.length; i++) {
            System.out.print(rows[i] + " ");
            songTable.addRowSelectionInterval(rows[i], rows[i]);
        }
        System.out.println("");

        frame.validate();
    }

    protected void connectToHost(String host, ConnectionStatus _status)
            throws NoServerPermissionException {
        int sessionId = -1;
        ArrayList dbs = new ArrayList();
        // ItunesHost h = null;
        // System.out.println("before synchro in CTH");
        // synchronized(knownIPs){
        // for (int i = 0; i < knownIPs.size(); i++) {
        // h = (ItunesHost) knownIPs.get(i);
        // if (h.getAddress().equals(host))
        // break;
        // }
        // }
        // System.out.println("after synchro in CTH");
        hostQueueModel.updateStatus(host, HostTableModel.CONNECTING);

        // System.out.println("trying to login");
        debugPrint("Trying to login");

        if (sessionId != -1) {
            // System.out.println("trying to logout");
            try {
                hostQueueModel.updateStatus(connectedHost,
                        HostTableModel.VIEWING);
                LogoutRequest lr = new LogoutRequest(connectedHost,
                        Request.ITUNES_PORT, sessionId, _status);
            } catch (NoServerPermissionException e) {
            }
            sessionId = -1;
        }
        // System.out.println("trying to loginrequest");
        LoginRequest l = null;
        l = new LoginRequest(host, Request.ITUNES_PORT, _status);
        // System.out.println("done login requesting");
        // connectedHost = host;
        sessionId = l.getSessionId();
        if (sessionId == -1) {
            // JOptionPane.showMessageDialog(frame, "Error connecting to " +
            // host);
            hostQueueModel.updateStatus(host, HostTableModel.FAILED);
            return;
        }
        // System.out.println("logged in: session " + sessionId + " " + host);
        debugPrint("logged in: session " + sessionId + " " + host);
        hostQueueModel.updateStatus(host, HostTableModel.CONNECTED);

        // System.out.println("DBRequesting doohicky");

        DatabasesRequest db = new DatabasesRequest(host, Request.ITUNES_PORT,
                sessionId, _status);
        int dbCount = db.getLibraryCount();
        dbs = db.getDbs();

        // System.out.println("DBS have been gotten");

        playdb = ((Database) dbs.get(0)).id;
        // System.out.println("down here?");
        createSongChooser(host, playdb, sessionId, _status);
        // System.out.println("after creation of chooser?");

        try {
            // System.out.println("trying to logout?");
            LogoutRequest lr = new LogoutRequest(host, Request.ITUNES_PORT,
                    sessionId, _status);
            hostQueueModel.updateStatus(host, HostTableModel.VIEWING);
        } catch (NoServerPermissionException e) {
        }

        totalCountL.setText(songModel.getRowCount() + " Songs Total");

    }

    public void selectResults(int row) {
        songTable.getSelectionModel().setSelectionInterval(row, row);
        Rectangle cellRect = songTable.getCellRect(row, 0, true);
        songTable.scrollRectToVisible(cellRect);
    }

    private JPanel buildSearch() {
        JPanel temp = new JPanel();
        JPanel row1 = new JPanel();
        final JPanel row2 = new JPanel();

        temp.setLayout(new BoxLayout(temp, BoxLayout.Y_AXIS));

        searchQuery = new JTextField(20);
        searchQuery.setEnabled(false);

        JButton doSearch = new JButton("Search");

        doSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sorter.reFilter(searchQuery);
            }
        });

        JButton clearSearch = new JButton("Clear Search");

        clearSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchQuery.setText("");
                sorter.reFilter(searchQuery);
            }
        });

        JLabel searchLabel = new JLabel("Search:");

        JCheckBox advCheck = new JCheckBox("Show Filters");
        advCheck.setSelected(false);

        advCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // sorter.setFilters((JCheckBox)e.getSource()).isSelected()
                row2.setVisible(((JCheckBox) e.getSource()).isSelected());
            }
        });

        row1.add(searchLabel);
        row1.add(searchQuery);
        row1.add(doSearch);
        row1.add(clearSearch);
        row1.add(advCheck);

        JCheckBox m4aCheck = new JCheckBox("Include AAC files");
        m4aCheck.setSelected(true);

        JCheckBox mp3Check = new JCheckBox("Include MP3 files");
        mp3Check.setSelected(true);

        mp3Check.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sorter.setMP3(((JCheckBox) e.getSource()).isSelected());
                sorter.reFilter(searchQuery);
            }
        });

        m4aCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sorter.setM4a(((JCheckBox) e.getSource()).isSelected());
                sorter.reFilter(searchQuery);
            }
        });

        JLabel bitrateLabel = new JLabel("Min Bitrate:");
        JTextField bitrateField = new JTextField(3);

        bitrateField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Integer minBitrate = null;
                try {
                    minBitrate = new Integer(((JTextField) e.getSource())
                            .getText());
                } catch (NumberFormatException numException) {
                    // If its the empty string, assume 0
                    if (((JTextField) e.getSource()).getText().equals("")) {
                        minBitrate = new Integer(0);
                    } else { // Otherwise, just don't deal with it.
                        ((JTextField) e.getSource()).setText("");
                        return;
                    }
                }
                sorter.setMinBitrate(minBitrate);
                sorter.reFilter(searchQuery);
            }
        });

        row2.add(m4aCheck);
        row2.add(mp3Check);
        row2.add(bitrateLabel);
        row2.add(bitrateField);

        temp.add(row1);
        temp.add(row2);
        row2.setVisible(false);

        return temp;
    }

    public Component createHostQueueComponents() {
        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());
        hostQueueModel = new HostTableModel(sorter);
        sorter.setHostTableModel(hostQueueModel);
        // hostQueueTable = new JTable(hostQueueModel);

        hostQueueTable = new JTable(hostQueueModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer,
                    int rowIndex, int vColIndex) {
                Component c = super.prepareRenderer(renderer, rowIndex,
                        vColIndex);
                c.setForeground(Color.black);
                String status = (String) hostQueueModel.getValueAt(rowIndex,
                        HostTableModel.STATUS);

                if (status.equals("Disconnected")) {
                    c.setForeground(Color.gray);
                } else {
                    if (status.equals("Connecting")) {
                        c.setForeground(Color.green);
                    } else {
                        if (status.equals("Failed"))
                            c.setForeground(Color.red);
                        else
                            c.setForeground(getForeground());
                    }
                }
                if ((rowIndex % 2 == 0) && !isCellSelected(rowIndex, vColIndex)) {
                    c.setBackground(new Color(220, 220, 220));
                } else {
                    if (isCellSelected(rowIndex, vColIndex)) {
                        c.setBackground(Color.blue);
                    } else {
                        // If not shaded, match the table's background
                        c.setBackground(getBackground());
                    }
                }
                return c;
            }
        };

        for (int i = 0; i < 4; i++) {
            TableColumn column = hostQueueTable.getColumnModel().getColumn(i);
            if (i == HostTableModel.VISIBLE) {
                column.setPreferredWidth(20);
                column.setMaxWidth(20);
                column.setMinWidth(20);
            }
        }

        hostQueueTable.setShowHorizontalLines(false);
        hostQueueTable.setGridColor(new Color(200, 200, 200));

        JScrollPane scrollpane = new JScrollPane(hostQueueTable);
        pane.add(scrollpane, BorderLayout.CENTER);

        JPanel hostButtons = new JPanel(new FlowLayout());

        JButton addHostButton = new JButton("Add Host");
        addHostButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final String host = (String) JOptionPane.showInputDialog(frame,
                        "Enter the IP address", "Add Host",
                        JOptionPane.PLAIN_MESSAGE, null, null, null);

                new Thread() {
                    @Override
                    public void run() {
                        addHost("", getAddressFromHostname(host));
                    }
                }.run();
            }
        });

        // JButton retryHostButton = new JButton("Retry Host");
        // retryHostButton.addActionListener(new java.awt.event.ActionListener()
        // {
        // public void actionPerformed(ActionEvent e) {
        // seenhosts.add(hostQueueModel.getHostAt(hostQueueTable.getSelectedRow()));
        // }});

        final JButton pauseConnectingButton = new JButton("Pause Connecting");
        pauseConnectingButton
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        boolean currPause = connectworker.switchPausedStatus();
                        if (currPause)
                            pauseConnectingButton.setText("Resume Connecting");
                        else
                            pauseConnectingButton.setText("Pause Connecting");
                    }
                });

        hostButtons.add(addHostButton);
        hostButtons.add(pauseConnectingButton);
        pane.add(hostButtons, BorderLayout.SOUTH);

        return pane;
    }

    public String getAddressFromHostname(String hostname) {
        String ipAddrStr = "";

        try {
            InetAddress addr = InetAddress.getByName(hostname);
            byte[] ipAddr = addr.getAddress();

            // Convert to dot representation
            for (int i = 0; i < ipAddr.length; i++) {
                if (i > 0) {
                    ipAddrStr += ".";
                }
                ipAddrStr += ipAddr[i] & 0xFF;
            }
        } catch (UnknownHostException e) {
        }
        return ipAddrStr;
    }

    public Component createSongQueueComponents() {
        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());
        final JTable songQueueTable = new JTable(songQueue);
        songQueueTable
                .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        songQueueTable.setSelectionBackground(Color.BLUE);
        songQueueTable.setSelectionForeground(Color.WHITE);

        for (int i = 0; i < SongBuffer.columnNames.length; i++) {
            TableColumn column = songQueueTable.getColumnModel().getColumn(i);
            if (i == SongBuffer.POSITION) {
                column.setPreferredWidth(20);
                column.setMaxWidth(20);
                column.setMinWidth(20);
                DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
                renderer.setHorizontalAlignment(SwingConstants.RIGHT);
                column.setCellRenderer(renderer);
            }
            if (i == SongBuffer.STATUS_COLUMN) {
                column.setCellRenderer(new ProgressRenderer());
            }
        }

        songQueueTable.setShowHorizontalLines(false);
        songQueueTable.setGridColor(new Color(200, 200, 200));

        JScrollPane scrollpane = new JScrollPane(songQueueTable);
        pane.add(scrollpane, BorderLayout.CENTER);

        // buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton clearButton = new JButton("Clear Finished/Failed");
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                songQueue.clearFinished();
            }
        });
        buttonPanel.add(clearButton);

        JButton clearSelectedButton = new JButton("Clear Selected");
        clearSelectedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                songQueue.clearSelected(songQueueTable.getSelectedRows());
            }
        });
        buttonPanel.add(clearSelectedButton);
        pane.add(buttonPanel, BorderLayout.SOUTH);

        return pane;
    }

    public Component createSongTableComponents() {
        final String host;
        System.out.println("I AM IN CSTC");

        JPanel pane = new JPanel();
        pane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

        pane.add(buildSearch());
        // pane.add(Box.createRigidArea(new Dimension(0, 400)));
        JPanel buttons = createSongTableButtons();
        pane.add(createSongTable());
        pane.add(buttons);

        JPanel statusArea = new JPanel();
        statusArea.setLayout(new BoxLayout(statusArea, BoxLayout.X_AXIS));
        GridLayout gl = new GridLayout(1, 2);
        gl.setHgap(15);
        JPanel babyStatusArea = new JPanel(gl);
        statusArea.setBorder(new EtchedBorder());
        statusArea.add(statusL);
        babyStatusArea.setMaximumSize(new Dimension(250, 20));
        statusArea.add(Box.createHorizontalGlue());
        babyStatusArea.add(viewCountL);
        babyStatusArea.add(totalCountL);
        viewCountL.setAlignmentX(Component.LEFT_ALIGNMENT);
        totalCountL.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusArea.add(babyStatusArea);
        frame.getContentPane().add(statusArea, BorderLayout.SOUTH);

        return pane;
    }

    private JPanel createSongTableButtons() {
        JPanel buttons = new JPanel(new FlowLayout());

        songDl = new JButton("Download Selected");
        songDl.setEnabled(false);
        songDl.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                downloadSong();
            }
        });
        buttons.add(songDl);

        songPl = new JButton("Play Selected");
        songPl.setEnabled(false);
        songPl.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playSong();
            }
        });
        buttons.add(songPl);

        buttons.validate();
        return buttons;
    }

    public static void Center(Component child, Component parent) {
        Rectangle parent_bounds = parent.getBounds();

        child.setLocation(parent_bounds.x
                + (parent_bounds.width - child.getWidth()) / 2, parent_bounds.y
                + (parent_bounds.height - child.getHeight()) / 2);
    }

    private JMenuBar buildMenus() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem dlDir = new JMenuItem("Set Download Directory", KeyEvent.VK_S);
        JMenuItem settings = new JMenuItem("Settings");
        JMenuItem exitItem = new JMenuItem("Exit", KeyEvent.VK_X);

        dlDir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                ActionEvent.CTRL_MASK));
        dlDir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setDLDir();
            }
        });

        settings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createSettingsWindow();
            }
        });

        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                ActionEvent.CTRL_MASK));
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doExit();
            }
        });
        fileMenu.setMnemonic(KeyEvent.VK_F);

        fileMenu.add(dlDir);
        fileMenu.add(settings);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu songMenu = new JMenu("Song");
        JMenuItem dlSelected = new JMenuItem("Download Selected Song(s)",
                KeyEvent.VK_D);

        dlSelected.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
                ActionEvent.CTRL_MASK));
        dlSelected.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                downloadSong();
            }
        });

        songMenu.add(dlSelected);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutButton = new JMenuItem("About", KeyEvent.VK_A);
        aboutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final JFrame j = new JFrame("about");
                j.getContentPane().setLayout(
                        new BoxLayout(j.getContentPane(), BoxLayout.Y_AXIS));

                JLabel content = new JLabel("<html>"
                        + "This is a program written by the OT Crew<BR>"
                        + "Send offers of sex and money to<BR>"
                        + "ourtunes12@yahoo.com<P><P>"
                        + "This program is GPL'd<BR>"
                        + "You can find the source code in the jar<P><P>"
                        + "Don't steal music." + "</html>");

                JButton okB = new JButton("OK!");
                JPanel okBox = new JPanel();
                okBox.setLayout(new FlowLayout());
                okBox.add(okB);

                okB.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        j.dispose();
                    }
                });

                j.getContentPane().add(content);
                j.getContentPane().add(okB);
                j.pack();
                Center(j, One2OhMyGod.this.frame);

                j.show();
            }
        });
        helpMenu.add(aboutButton);

        menuBar.add(fileMenu);
        menuBar.add(songMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    protected void doExit() {
        if (songQueue != null && !songQueue.isEmpty()) {
            if (JOptionPane.showConfirmDialog(frame,
                    "There are still songs in the download queue; still quit?",
                    "ourTunes", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
                return;
        }

        if (playSessionId != -1) {
            try {
                LogoutRequest lr = new LogoutRequest(playConnectedHost,
                        Request.ITUNES_PORT, playSessionId, _playStatus);
            } catch (NoServerPermissionException e) {
            }
        }

        saveParameters();

        System.exit(0);
    }

    private Component createSongTable() {
        sorter = new TableSorter(songModel, viewCountL);
        searchQuery.addActionListener(sorter);
        songTable = new JTable(sorter) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer,
                    int rowIndex, int vColIndex) {
                Component c = super.prepareRenderer(renderer, rowIndex,
                        vColIndex);
                if (rowIndex % 2 == 0 && !isCellSelected(rowIndex, vColIndex)) {
                    c.setBackground(new Color(220, 220, 220));
                } else {
                    if (isCellSelected(rowIndex, vColIndex)) {
                        c.setBackground(Color.blue);
                    } else {
                        // If not shaded, match the table's background
                        c.setBackground(getBackground());
                    }
                }
                return c;
            }
        };

        songTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    downloadSong();
                }
            }
        });
        songTable.addMouseListener(new PopupListener());

        songTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent e) {
                        int[] rows = songTable.getSelectedRows();
                        if (rows == null || rows.length == 0) {
                            songDl.setEnabled(false);
                            songPl.setEnabled(!playstop);
                        } else {
                            songDl.setEnabled(true);
                            songPl
                                    .setEnabled(!playstop
                                            || (rows.length >= 1 && (System
                                                    .getProperty("system.player") != null || sorter
                                                    .getFormatAt(rows[0])
                                                    .equals("mp3"))));
                            System.out.println("format="
                                    + sorter.getFormatAt(rows[0]));
                        }
                    }
                });

        sorter.addMouseListenerToHeaderInTable(songTable);
        songTable
                .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        songTable.setPreferredScrollableViewportSize(new Dimension(1500, 1600));

        for (int i = 0; i < songTable.getColumnCount(); i++) {
            TableColumn column = songTable.getColumnModel().getColumn(i);
            debugPrint("col:" + i);
            if (i == SongTableModel.TITLE_COLUMN) {
                column.setPreferredWidth(One2OhMyGod.TITLE_COL_WIDTH);
            } else if (i == SongTableModel.HOST_COLUMN
                    || i == SongTableModel.ARTIST_COLUMN
                    || i == SongTableModel.ALBUM_COLUMN) {
                column.setPreferredWidth(One2OhMyGod.TEXT_COL_WIDTH);
            } else {
                column.setPreferredWidth(One2OhMyGod.OTHER_COL_WIDTH);
            }

            if (i == SongTableModel.TIME_COLUMN) {
                column.setCellRenderer(new TimeRenderer());
            }
            if (i == SongTableModel.SIZE_COLUMN) {
                column.setCellRenderer(new SizeRenderer());
            }

        }
        songTable.setShowHorizontalLines(false);
        songTable.setGridColor(new Color(200, 200, 200));
        // songTable.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        songTable.setSelectionForeground(Color.white);
        songTable.setSelectionBackground(Color.blue);

        JScrollPane scroller = new JScrollPane(songTable);
        return scroller;
    }

    public One2OhMyGod() {
        playingRow = -1;
        playstop = true;
        songModel = new SongTableModel();
        sorter = null;
        songTable = null;
        knownIPs = new ArrayList();
        popupMenu = new CustomPopupMenu();
        if (songQueue == null)
            songQueue = new SongBuffer();

        getParameters();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the top-level container and add contents to it.
        frame = new JFrame(programName);

        JTabbedPane main_tab_pane = new JTabbedPane();
        frame.getContentPane().add(main_tab_pane, BorderLayout.CENTER);

        Component contents = createSongTableComponents();
        main_tab_pane.add(contents, "Search");

        Component queueContents = createSongQueueComponents();
        main_tab_pane.add(queueContents, "Queue");

        frame.setJMenuBar(buildMenus());

        Component hostQueueContents = createHostQueueComponents();
        main_tab_pane.add(hostQueueContents, "Hosts");

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                doExit();
            }
        });
        frame.pack();

        frame.setLocation(100, 100);
        frame
                .setSize(One2OhMyGod.OT_WINDOW_WIDTH,
                        One2OhMyGod.OT_WINDOW_HEIGHT);
        frame.setVisible(true);

        seenhosts = Collections.synchronizedList(new ArrayList());

        if (connectworker == null) {
            connectworker = new ConnectWorker(this);
            connectworker.start();
        }

        try {
            Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) netInterfaces
                        .nextElement();
                System.out.println(ni.getName());
                Enumeration addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress ip = (InetAddress) addresses.nextElement();
                    // !ip.isSiteLocalAddress() &&
                    if (!ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(":") == -1) {
                        System.out
                                .println("Interface "
                                        + ni.getName()
                                        + " seems to be InternetInterface. I'll take it...");
                        r = new Rendezvous(ip);
                        r.addServiceListener(iTunesService, this);
                    } else {
                        ip = null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        One2OhMyGod app = new One2OhMyGod();
    }

    public static void debugPrint(String str) {
        if (debug)
            System.out.println(str);
    }

    protected void fixSaveFile() {
        boolean isWindows = System.getProperty("os.name").toLowerCase()
                .indexOf("windows") != -1;
        if (isWindows) {
            String filesep = System.getProperty("file.separator");
            String folder = System.getProperty("user.home");

            // boolean success = (new File()).mkdirs();
            new File(folder + filesep + "Application Data" + filesep
                    + "ourTunes").mkdirs();

            propertiesFilename = "Application Data" + filesep + "ourTunes"
                    + filesep + "ourTunes.ini";
        } else {
            propertiesFilename = ".ourTunes";
        }
    }

    protected void getParameters() {
        fixSaveFile();

        Properties defaults = new Properties();
        FileInputStream in = null;

        setDefaults(defaults);

        properties = new Properties(defaults);

        try {
            String folder = System.getProperty("user.home");
            String filesep = System.getProperty("file.separator");
            in = new FileInputStream(folder + filesep + propertiesFilename);
            properties.load(in);

        } catch (Exception e) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (java.io.IOException e) {
                }
                in = null;
            }
        }

        updateSettingsFromProperties();

    }

    protected void saveParameters() {

        updatePropertiesFromSettings();

        FileOutputStream out = null;

        try {
            String folder = System.getProperty("user.home");
            String filesep = System.getProperty("file.separator");

            out = new FileOutputStream(folder + filesep + propertiesFilename);

            debugPrint(folder + filesep + propertiesFilename);

            properties.store(out, "ourTunes properties");
        } catch (java.io.IOException e) {
            // System.out.println("Can't save properties. ");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (java.io.IOException e) {
                }
                out = null;
            }
        }
    }

    private static String PROPERTY_SAVE_DIRECTORY = "saveDirectory";
    private static String PROPERTY_FILE_FORMAT = "file_format";

    private void updateSettingsFromProperties() {
        if (properties.getProperty(PROPERTY_SAVE_DIRECTORY) != null)
            currDirectory = new File(properties
                    .getProperty(PROPERTY_SAVE_DIRECTORY));
        // File currDirectory
        formatString = properties.getProperty(PROPERTY_FILE_FORMAT);

    }

    private void updatePropertiesFromSettings() {
        if (currDirectory != null)
            properties.setProperty(PROPERTY_SAVE_DIRECTORY, currDirectory
                    .getAbsolutePath());
        // File currDirectory
        properties.setProperty(PROPERTY_FILE_FORMAT, formatString);
    }

    private void setDefaults(Properties p) {
        String filesep = System.getProperty("file.separator");
        p.setProperty(PROPERTY_FILE_FORMAT, "%A" + "-" + "%a" + "-"
                + "%0 - %t.%f");
    }

    private String sanitize(String filename) {
        filename = filename.replace('/', '_');
        filename = filename.replace('\\', '_');
        filename = filename.replace(':', '_');
        filename = filename.replace('?', '_');
        filename = filename.replace('\"', '_');
        filename = filename.replace('*', '_');
        filename = filename.replace('>', '_');
        filename = filename.replace('<', '_');
        filename = filename.replace('|', '_');
        filename = filename.replace('.', '_');
        return filename;
    }

    /*
     * %A - artist %a - album %n - tracknum %0 - 0 padded track num %t - track
     * title %% - literal %
     */

    public String createFilename(String options, TableSorter sorter,
            int selection) {
        StringBuffer sb = new StringBuffer();
        String s;
        // break up the options string and create the string buffer
        String[] result = options.split("");

        for (int i = 0; i < result.length; i++) {
            s = result[i];
            if (s.compareTo("%") == 0) {
                s = result[++i];
                if (s.compareTo("%") == 0) {
                    sb.append(s); // the literal %
                } else if (s.equals("n")) {
                    sb.append(sanitize(sorter.getTrackNumAt(selection)
                            .toString()));
                } else if (s.equals("0")) {
                    Integer num = sorter.getTrackNumAt(selection);
                    if (num.intValue() > 0 && num.intValue() < 10) {
                        sb.append("0");
                    }
                    sb.append(sanitize(num.toString()));
                } else if (s.equals("a")) {
                    sb.append(sanitize(sorter.getAlbumAt(selection)));
                } else if (s.equals("A")) {
                    sb.append(sanitize(sorter.getArtistAt(selection)));
                } else if (s.equals("t")) {
                    sb.append(sanitize(sorter.getSongAt(selection)));
                } else if (s.equals("f")) {
                    sb.append(sanitize(sorter.getFormatAt(selection)));
                } else {
                    sb.append(s); // otherwise it was a literal that goes in the
                }
            } else {
                sb.append(s);
            }
        }
        // scan the filename for illegal characters.
        // these are illegal under windoze. Obviously not right for all
        // computers
        // Where do we get this????
        for (int i = 0; i < sb.length(); i++) {
            char c = sb.charAt(i);
            if (c == ':' || c == '?' || c == '"' || c == '<' || c == '>'
                    || c == '|') {
                sb.replace(i, i + 1, ".");
            }
        }
        return sb.toString();
    }

    private void setFieldToFile(JTextField dlDirField, File f) {
        if (f != null) {
            try {
                dlDirField.setText(f.getCanonicalPath());
            } catch (Exception e) {
            }
        }
    }

    private void createSettingsWindow() {
        final JDialog settingsWindow = new JDialog(frame, "ourTunes Settings");
        Border blackline = BorderFactory.createLineBorder(Color.black);

        Container container = settingsWindow.getContentPane();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        // download directory panel

        JPanel dlDirPanel = new JPanel();
        dlDirPanel.setLayout(new BoxLayout(dlDirPanel, BoxLayout.X_AXIS));
        dlDirPanel.setBorder(blackline);

        final JTextField dlDirField = new JTextField();
        setFieldToFile(dlDirField, currDirectory);

        JLabel dlDir = new JLabel("Download Directory:");
        JButton dlDirButton = new JButton("Browse");
        dlDirPanel.add(dlDir);
        dlDirPanel.add(dlDirField);
        dlDirPanel.add(dlDirButton);

        dlDirButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File chosen = createDLDirWindow(new File(dlDirField.getText()));
                setFieldToFile(dlDirField, chosen);
            }
        });

        container.add(dlDirPanel);

        // file-format controls
        JPanel fileFormatPanel = new JPanel();
        fileFormatPanel.setLayout(new BoxLayout(fileFormatPanel,
                BoxLayout.Y_AXIS));
        fileFormatPanel.setBorder(blackline);

        JPanel fileFormatEntryPanel = new JPanel();
        fileFormatEntryPanel.setLayout(new BoxLayout(fileFormatEntryPanel,
                BoxLayout.X_AXIS));
        final JTextField formatField = new JTextField(formatString, 25);
        JLabel format = new JLabel("File Format:");
        fileFormatEntryPanel.add(format);
        fileFormatEntryPanel.add(formatField);
        fileFormatPanel.add(fileFormatEntryPanel);

        /*
         * String formatDescText = "%A \t - \t artist\n" + "%a \t - \t album\n"
         * + "%t \t - \t track name\n" + "%n \t - \t track number\n" +
         * "%0 \t - \t 0-padded track number\n" + "%% \t - \t literal %\n" +
         * "%f \t - \t file extension\n";
         */

        String formatDescText = "<html><table>"
                + "<tr><td>%A</td><td>artist</td></tr>\n"
                + "<tr><td>%a</td><td>album</td></tr>\n"
                + "<tr><td>%t</td><td>track name</td></tr>\n"
                + "<tr><td>%n</td><td>track number</td></tr>\n"
                + "<tr><td>%0</td><td>track number (0 padded)</td></tr>\n"
                + "<tr><td>%f</td><td>file extension (mp3, m4a)<td></tr>\n"
                + "<tr><td>%%</td><td>literal %</td></tr>\n"
                + "</table></html>";
        JLabel formatDescLabel = new JLabel(formatDescText);
        fileFormatPanel.add(formatDescLabel);

        container.add(fileFormatPanel);

        // buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(Box.createHorizontalGlue());
        JButton ok_button = new JButton("OK");
        ok_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // sync controls to variables

                currDirectory = new File(dlDirField.getText());
                if (!currDirectory.isDirectory()) {
                    if (!currDirectory.mkdirs())
                        currDirectory = null;
                }

                formatString = formatField.getText();

                settingsWindow.dispose();

            }
        });
        buttonPanel.add(ok_button);

        JButton cancel_button = new JButton("Cancel");
        cancel_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                settingsWindow.dispose();
            }
        });
        buttonPanel.add(cancel_button);
        buttonPanel.add(Box.createHorizontalGlue());

        container.add(buttonPanel);

        container.add(new JLabel(
                "Note: Settings will not affect files already in the queue"));

        settingsWindow.pack();

        Center(settingsWindow, frame);
        settingsWindow.show();

    }

    private class CustomPopupMenu extends JPopupMenu {
        private final JMenuItem downloadButton;
        private final JMenuItem previewButton;

        public CustomPopupMenu() {
            super();
            downloadButton = new JMenuItem("Download");
            downloadButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    downloadSong();
                }
            });
            previewButton = new JMenuItem("Preview");
            previewButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    playSong();
                }
            });
            this.add(downloadButton);
            this.add(previewButton);
        }

        @Override
        public void show(Component arg0, int x, int y) {
            final int[] selections = songTable.getSelectedRows();
            if (selections.length == 0) {
                previewButton.setEnabled(false);
                downloadButton.setEnabled(false);
            } else {
                previewButton.setEnabled(selections.length == 1
                        && sorter.getFormatAt(selections[0]).equals("mp3"));
                downloadButton.setEnabled(true);
            }
            super.show(arg0, x, y);
        }
    }

    private class PopupListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    public void doStatusUpdate(String s) {
        new StatusUpdate(s).start();
    }

    class StatusUpdate extends SwingWorker {
        public String s;

        public StatusUpdate(String s) {
            this.s = s;
        }

        @Override
        public Object construct() {
            return null;
        }

        @Override
        public void finished() {
            statusL.setText(s);
        }
    };

    public class ProgressRenderer extends DefaultTableCellRenderer {
        IndicatorCellRenderer progressRend;

        public ProgressRenderer() {
            progressRend = new IndicatorCellRenderer(this.getBackground(), this
                    .getForeground());
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            super.getTableCellRendererComponent(table, value, isSelected,
                    hasFocus, row, column);
            if (One2OhMyGod.songQueue.getStatusAt(row, column) == SongBuffer.DOWNLOADING) {
                // System.out.println("doing the progress thing instead");
                return progressRend.getTableCellRendererComponent(table,
                        new Integer(One2OhMyGod.songQueue
                                .getProgressAsPercentageAt(row, column)),
                        isSelected, hasFocus, row, column);
            }
            return this;

        }
    }

    public class TimeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {

            super.getTableCellRendererComponent(table, value, isSelected,
                    hasFocus, row, column);

            int millisecs = ((Integer) value).intValue();
            millisecs /= 1000;
            int min = millisecs / 60;
            int secs = millisecs % 60;
            String timestr = new String(min + ":");
            if (secs < 10) {
                timestr = timestr + "0";
            }
            timestr = timestr + secs;
            setText(timestr);
            return this;
        }
    }

    public class SizeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {

            super.getTableCellRendererComponent(table, value, isSelected,
                    hasFocus, row, column);

            int bytes = ((Integer) value).intValue();
            int kilobytes = bytes / 1024;
            if (bytes < 1000) {
                setText(bytes + "B");
            }
            if (kilobytes < 1000) {
                setText(kilobytes + "K");
            } else {
                double megabytes = (kilobytes * 1.0) / 1024.0;
                DecimalFormat form = new DecimalFormat("#.0");
                setText(form.format(megabytes) + "M");
            }

            return this;
        }
    }
}
