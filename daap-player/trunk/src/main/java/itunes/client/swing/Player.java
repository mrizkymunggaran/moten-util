/*
 * Created on May 17, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
Copyright 2003 Joseph Barnett

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
import java.io.FileInputStream;
import java.io.InputStream;
import javax.swing.JOptionPane;

import itunes.client.swing.One2OhMyGod;
/**
 * @author jbarnett
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Player extends Thread {
	private javazoom.jl.player.Player p;
	private One2OhMyGod prog;
	
	public Player(One2OhMyGod prog, String fileName) {
		this.prog = prog;
		FileInputStream f=null;
		try {
			f = new FileInputStream(fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		newPlayer(f);
	}
	
	private void newPlayer(InputStream f) {
		if (p != null)
			this.stopMusic();
		try{
			p = new javazoom.jl.player.Player(f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Player(One2OhMyGod prog, InputStream f) {
		this.prog = prog;
		newPlayer(f);
	}
	
	public void run() {
		try {
			p.play();
		} catch (javazoom.jl.decoder.BitstreamException be) {
			JOptionPane.showMessageDialog(prog.frame,"Unsupported file format!\n"+be.getLocalizedMessage());
			prog.stopPlaying();
		}catch (Exception e) {
			e.printStackTrace();
			prog.stopPlaying();
		}
		if (p.isComplete()) {
		    prog.songPl.setText("Play Selected");
		    prog.playstop = true;
			//prog.playNext();
		}
	}
	
	public int getPosition() {
		return p.getPosition();
	}
	
	public void stopMusic() {
		p.close();
	}
}
