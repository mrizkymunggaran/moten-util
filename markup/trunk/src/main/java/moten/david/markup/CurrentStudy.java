package moten.david.markup;

import moten.david.markup.xml.study.Study;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class CurrentStudy implements Provider<Study> {

	private static final String STUDY_PATH = "src/test/resources/study1/markup";
	private final Study study;
	private final StudyFolder studyFolder;

	@Inject
	public CurrentStudy(StudyFolder studyFolder) {
		this.studyFolder = studyFolder;
		study = studyFolder.getStudy(STUDY_PATH);
	}

	@Override
	public Study get() {
		return study;
	}

	public void save() {
		studyFolder.writeStudy(study, STUDY_PATH);
	}

}
