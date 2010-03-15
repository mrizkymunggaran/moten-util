package moten.david.util.expression;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketAvailable implements BooleanExpression {

	private final String host;
	private final int port;

	public SocketAvailable(String host, int port, long timeout) {
		this.host = host;
		this.port = port;
	}

	public SocketAvailable(String host, int port) {
		this(host, port, 500);
	}

	@Override
	public boolean evaluate() {
		Socket socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(host, port), 500);
			socket.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

}
