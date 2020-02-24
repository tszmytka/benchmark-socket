package dev.tomek.benchmarksocket.catcher.transport.zmq;

import dev.tomek.benchmarksocket.catcher.transport.CatchTransport;
import dev.tomek.benchmarksocket.catcher.transport.CatchTransportAbstract;
import io.micrometer.core.instrument.Counter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.time.Duration;
import java.time.ZonedDateTime;

@Component
public class CatchZmq extends CatchTransportAbstract implements CatchTransport {
    private final ZMQ.Socket socket;
    private final Duration duration;

    public CatchZmq(
        @Qualifier("counterMessagesZmq") Counter counter,
        @Value("${transports.zmq.port}") int port,
        @Value("${duration-per-transport}") Duration duration,
        ZContext zContext
    ) {
        super(counter);
        socket = zContext.createSocket(SocketType.PULL);
        socket.connect("tcp://127.0.0.1:" + port);
        this.duration = duration;
    }

    @Override
    public void run() {
        long endMillis = ZonedDateTime.now().plus(duration).toInstant().toEpochMilli();
        while (System.currentTimeMillis() <= endMillis) {
            onEachMessage(new String(socket.recv()));
        }
        onFinally();
    }
}
