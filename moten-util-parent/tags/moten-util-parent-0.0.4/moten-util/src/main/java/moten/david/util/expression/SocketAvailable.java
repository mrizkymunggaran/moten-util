package moten.david.util.expression;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Logger;

public class SocketAvailable implements BooleanExpression, Operation {

    private static Logger log = Logger.getLogger(SocketAvailable.class
            .getName());

    private final StringExpression host;
    private final NumericExpression port;
    private final NumericExpression timeoutMs;

    public SocketAvailable(StringExpression host, NumericExpression port,
            NumericExpression timeoutMs) {
        this.host = host;
        this.port = port;
        this.timeoutMs = timeoutMs;
    }

    public SocketAvailable(String host, int port, long timeoutMs) {
        this(new Stringy(host), new Numeric(port), new Numeric(timeoutMs));
    }

    public SocketAvailable(String host, int port) {
        this(host, port, 500);
    }

    @Override
    public boolean evaluate() {

        Socket socket = new Socket();
        try {

            String host = this.host.evaluate();
            int port = this.port.evaluate().intValue();
            int timeoutMs = this.timeoutMs.evaluate().intValue();
            log.info("connecting to socket at " + host + ":" + port
                    + " with timeout " + timeoutMs + "ms");
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            socket.close();
            return true;
        } catch (IOException e) {
            log.info(e.getMessage());
            return false;
        }
    }

    @Override
    public Expression[] getExpressions() {
        return new Expression[] { host, port, timeoutMs };
    }

}
