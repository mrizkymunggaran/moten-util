package moten.david.markup;

import moten.david.markup.xml.study.Study;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class CurrentStudy implements Provider<Study> {

	private final Study study;

	@Inject
	public CurrentStudy(StudyFolder studyFolder) {
		study = studyFolder.getStudy("src/test/resources/study1/markup");
	}

	@Override
	public Study get() {
		return study;
	}

}
