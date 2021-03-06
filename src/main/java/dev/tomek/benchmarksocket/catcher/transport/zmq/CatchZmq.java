package dev.tomek.benchmarksocket.catcher.transport.zmq;

import dev.tomek.benchmarksocket.catcher.transport.CatchTransport;
import dev.tomek.benchmarksocket.catcher.transport.CatchTransportAbstract;
import io.micrometer.core.instrument.Counter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import static dev.tomek.benchmarksocket.config.CommonConfig.PARAM_TRANSPORT;
import static dev.tomek.benchmarksocket.config.CommonConfig.TRANSPORT_ZMQ;

@Component
@ConditionalOnProperty(name = PARAM_TRANSPORT, havingValue = TRANSPORT_ZMQ)
public class CatchZmq extends CatchTransportAbstract implements CatchTransport {
    private final ZMQ.Socket socket;

    public CatchZmq(
        @Qualifier("counterMessagesZmq") Counter counter,
        @Value("${transport.zmq.port}") int port,
        ZContext zContext
    ) {
        super(counter);
        socket = zContext.createSocket(SocketType.PULL);
        socket.connect("tcp://127.0.0.1:" + port);
    }

    @Override
    public void run() {
        byte[] data;
        while ((data = socket.recv()) != null) {
            onEachMessage(new String(data));
        }
        onFinally();
    }
}
