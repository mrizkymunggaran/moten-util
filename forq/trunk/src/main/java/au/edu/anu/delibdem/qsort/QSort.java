package au.edu.anu.delibdem.qsort;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QSort implements Serializable {
	private static final long serialVersionUID = -7796050822543154325L;
	private String stage;
	private Integer participantId;
	private String participantType;
	private List<Double> rankings = new ArrayList<Double>();
	private List<Double> rankingsForced = new ArrayList<Double>();
	private List<Double> qResults = new ArrayList<Double>();
	private List<Double> qResultsForced = new ArrayList<Double>();
	private List<Double> metaconsensus = new ArrayList<Double>();

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("stage=" + stage);
		s.append(",participantId=" + participantId);
		s.append(",type=" + participantType);
		s.append(",rankingsForced=" + rankingsForced);
		s.append(",qResultsForced=" + qResultsForced);
		s.append(",metaconsensus=" + metaconsensus);
		s.append(",rankingsUnforced=" + rankings);
		s.append(",qResultsUnforced=" + qResults);
		return s.toString();
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public Integer getParticipantId() {
		return participantId;
	}

	public void setParticipantId(Integer participantId) {
		this.participantId = participantId;
	}

	public String getParticipantType() {
		return participantType;
	}

	public void setParticipantType(String participantType) {
		this.participantType = participantType;
	}

	public List<Double> getRankings() {
		return rankings;
	}

	public void setRankings(List<Double> rankings) {
		this.rankings = rankings;
	}

	public List<Double> getRankingsForced() {
		return rankingsForced;
	}

	public List<Double> getRankings(boolean forced) {
		if (forced)
			return getRankingsForced();
		else
			return getRankings();
	}

	public void setRankingsForced(List<Double> rankingsForced) {
		this.rankingsForced = rankingsForced;
	}

	public List<Double> getQResults() {
		return qResults;
	}

	public List<Double> getQResults(boolean forced) {
		if (forced)
			return getQResultsForced();
		else
			return getQResults();
	}

	public void setQResults(List<Double> results) {
		qResults = results;
	}

	public List<Double> getQResultsForced() {
		return qResultsForced;
	}

	public void setQResultsForced(List<Double> resultsForced) {
		qResultsForced = resultsForced;
	}

	public List<Double> getMetaconsensus() {
		return metaconsensus;
	}

	public void setMetaconsensus(List<Double> metaconsensus) {
		this.metaconsensus = metaconsensus;
	}

	public QSort copy() {
		QSort q = new QSort();
		q.setMetaconsensus(new ArrayList<Double>(getMetaconsensus()));
		q.setParticipantId(participantId);
		q.setParticipantType(participantType);
		q.setQResults(new ArrayList<Double>(this.qResults));
		q.setQResultsForced(new ArrayList<Double>(this.qResultsForced));
		q.setRankings(new ArrayList<Double>(rankings));
		q.setRankingsForced(new ArrayList<Double>(rankingsForced));
		q.setStage(stage);
		return q;
	}
}
