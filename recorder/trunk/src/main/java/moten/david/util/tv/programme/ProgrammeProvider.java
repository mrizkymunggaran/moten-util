package moten.david.util.tv.programme;

import java.util.Date;

import moten.david.util.tv.Channel;

public interface ProgrammeProvider {

	Channel[] getStations();

	Programme getProgramme(Channel station, Date date);

}
