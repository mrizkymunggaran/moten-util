package moten.david.util.tv;

public class Util {
	public static Channel getChannel(String channelName, Channel... allChannels) {
		for (Channel channel : allChannels)
			if (channel.getId().equals(channelName))
				return channel;
		throw new RuntimeException("channel not found " + channelName);
	}

}
