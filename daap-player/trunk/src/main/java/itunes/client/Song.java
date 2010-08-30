/*
 * Created on May 7, 2003
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
package itunes.client;

/**
 * @author jbarnett
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Song implements Comparable{
	public String name;
	public int id;
	public int time;
	public String album;
	public String artist;
	public int track;
	public String genre;
	public int rating;
	public String format;
	public int size;
	public int bitrate;
	
	public Song() {
		name="";
		id=0;
		album = "";
		artist = "";
		track = 0;
		genre = "";
		rating = 0;
		format = "";
		size = 0;
		bitrate = 0;
	}
	
	public int compareTo(Object o) {
		if (Song.class.isInstance(o)) {
			if (this.artist.compareTo(((Song)o).artist) != 0) {
				if (this.artist.matches(" *"))
					return 1;
				else if (((Song)o).artist.matches(" *"))
					return -1;
				return this.artist.compareTo(((Song)o).artist);
			}else if (this.album.compareTo(((Song)o).album) != 0) {
				if (this.album.matches(" *"))
					return 1;
				else if (((Song)o).album.matches(" *"))
					return -1;
				return this.album.compareTo(((Song)o).album);
			}
			else if (new Integer(this.track).compareTo(new Integer(((Song)o).track)) != 0)
				return (new Integer(this.track).compareTo(new Integer(((Song)o).track)));
			else
				return this.name.compareTo(((Song)o).name);
		} else throw new ClassCastException();
	}
	
	public String toString() {
		String ret=artist + (artist.length()>0?" - ":"") + album+(album.length()>0?" ":"")+(track>0?" (Track":"")+(track>0?Integer.toString(track):"")+(track>0?") - ":"")+name +(genre.length()>0?" [":"")+genre+(genre.length()>0?"]":"");
		for (int i = 0; i < rating/20; i++) {
			ret += " *";
		}
		return ret;
	}
	/**
	 * @return
	 */
	public String getAlbum() {
		return album;
	}

	/**
	 * @return
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * @return
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @return
	 */
	public String getGenre() {
		return genre;
	}

	/**
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public int getRating() {
		return rating;
	}

	/**
	 * @return
	 */
	public int getTrack() {
		return track;
	}

	/**
	 * @return
	 */
	public int getTime() {
		return time;
	}
	
	public int getSize() {
		return size;
	}
	
	public int getRate() {
		return bitrate;
	}

	/**
	 * @param i
	 */
	public void setTime(int i) {
		time = i;
	}

}

