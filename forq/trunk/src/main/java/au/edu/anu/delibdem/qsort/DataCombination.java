package au.edu.anu.delibdem.qsort;

public class DataCombination {

	private String participantType;
	private String stage;
	private boolean forced;

	public DataCombination(String participantType, String stage, boolean forced) {
		super();
		this.participantType = participantType;
		this.stage = stage;
		this.forced = forced;
	}

	public String getParticipantType() {
		return participantType;
	}

	public void setParticipantType(String participantType) {
		this.participantType = participantType;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public boolean getForced() {
		return forced;
	}

	public void setForced(boolean forced) {
		this.forced = forced;
	}

	public String toString() {
		return ("all".equalsIgnoreCase(participantType) ? "All Participants"
				: participantType)
				+ " "
				+ ("all".equalsIgnoreCase(stage) ? "All Stages" : stage)
				+ " " + (forced ? "Forced" : "Unforced");
	}

}
