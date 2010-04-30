package moten.david.util.tv.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import moten.david.util.tv.Configuration;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.AutoCloseInputStream;

import com.google.inject.Inject;

public class SearchPatternsSerialized implements SearchPatterns {

	private final File file;

	@Inject
	public SearchPatternsSerialized(Configuration configuration) {
		file = configuration.getSearchPatternsFilename();
	}

	@Override
	public synchronized String[] getSearchPatterns() {
		try {
			if (!file.exists()) {
				OutputStream os = new FileOutputStream(file);
				IOUtils.copy(new AutoCloseInputStream(getClass()
						.getResourceAsStream("/default-search-patterns.txt")),
						os);
				os.close();
			}

			// read the patterns from the file
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));
			List<String> list = new ArrayList<String>();
			String line;
			while ((line = br.readLine()) != null)
				if (line.trim().length() > 0)
					list.add(line);
			br.close();
			return list.toArray(new String[] {});

		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	private void copyStreamAndClose(InputStream is, OutputStream os) {
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
