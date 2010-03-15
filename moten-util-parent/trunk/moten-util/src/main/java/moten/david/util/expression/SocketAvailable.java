package moten.david.util.expression;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import moten.david.util.guice.ConstantProvider;

import com.google.inject.Provider;

public class SocketAvailable implements BooleanExpression, Provided<String> {

    private final Provider<String> hostPortProvider;

    public SocketAvailable(String host, int port, long timeout) {
        this(new ConstantProvider<String>(host + ":" + port));
    }

    public SocketAvailable(Provider<String> hostPortProvider) {
        this.hostPortProvider = hostPortProvider;
    }

    public SocketAvailable(String host, int port) {
        this(host, port, 500);
    }

    @Override
    public boolean evaluate() {
        String hostPort = hostPortProvider.get();
        String[] items = hostPort.split(":");
        String host = items[0];
        int port = Integer.parseInt(items[1]);

        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port), 500);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public Provider<String> getProvider() {
        return hostPortProvider;
    }

}
