package au.edu.anu.delibdem.qsort;

public class DataCombination {

	private String participantType;
	private String stage;

	public DataCombination(String participantType, String stage) {
		super();
		this.participantType = participantType;
		this.stage = stage;
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

	@Override
	public String toString() {
		return ("all".equalsIgnoreCase(participantType) ? "All Participants"
				: participantType)
				+ " " + ("all".equalsIgnoreCase(stage) ? "All Stages" : stage);
	}

}
