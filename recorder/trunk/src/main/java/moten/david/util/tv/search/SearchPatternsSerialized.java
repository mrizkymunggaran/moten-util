package moten.david.util.tv.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import moten.david.util.tv.Configuration;

import com.google.inject.Inject;

public class SearchPatternsSerialized implements SearchPatterns {

	private final File file;

	@Inject
	public SearchPatternsSerialized(Configuration configuration) {
		file = configuration.getSearchPatternsFilename();
	}

	@Override
	public String[] getSearchPatterns() {
		try {
			if (!file.exists())
				return new String[] { "(?i)^.*CYCLING.*$", "(?i)^.*MOVIE.*$",
						"(?i)^.*COOKING.*$", "(?i)^.*JAZZ.*$" };
			else {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(file)));
				List<String> list = new ArrayList<String>();
				String line;
				while ((line = br.readLine()) != null)
					list.add(line);
				br.close();
				return list.toArray(new String[] {});
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void setSearchPatterns(String[] patterns) {
		PrintStream out;
		try {
			out = new PrintStream(file);
			for (String pattern : patterns)
				out.println(pattern);
			out.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}
