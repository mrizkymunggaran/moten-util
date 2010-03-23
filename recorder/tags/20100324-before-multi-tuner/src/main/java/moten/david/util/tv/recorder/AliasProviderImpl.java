package moten.david.util.tv.recorder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class AliasProviderImpl implements AliasProvider {

	private final HashMap<String, String> mplayerChannels;

	public AliasProviderImpl() {
		mplayerChannels = new HashMap<String, String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(getClass()
				.getResourceAsStream("/channels.txt")));
		String line;
		try {
			while ((line = br.readLine()) != null) {
				if (line.trim().length() > 0) {
					String[] items = line.split("\t");
					mplayerChannels.put(items[0], items[1]);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getAlias(String channelId) {
		return mplayerChannels.get(channelId);
	}

}
