package dev.tomek.benchmarksocket.catcher.transport.zmq;

import dev.tomek.benchmarksocket.Command;
import dev.tomek.benchmarksocket.catcher.transport.CatchTransport;
import io.micrometer.core.instrument.Counter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.time.Duration;
import java.time.ZonedDateTime;

@Log
@Component
public class CatchZmq implements CatchTransport {
    private final ZMQ.Socket socket;
    private final Counter counter;
    private final Duration duration;

    public CatchZmq(@Value("${transports.zmq.port}") int port, ZContext zContext, @Qualifier("counterMessagesZmq") Counter counter, @Value("${duration-per-transport}") Duration duration) {
        socket = zContext.createSocket(SocketType.SUB);
        socket.connect("tcp://127.0.0.1:" + port);
        this.counter = counter;
        this.duration = duration;
    }

    @Override
    public void run() {
        // todo Is there a way of easily telling the producer to stop?
        long endMillis = ZonedDateTime.now().plus(duration).toInstant().toEpochMilli();
        // SUB socket is ONLY for recv()
//        socket.send(Command.START.toString());
        socket.subscribe("");
        while (System.currentTimeMillis() <= endMillis) {
            String msg = new String(socket.recv());
            // todo store msg and after all is done calculate hash to see if all messages have been received
            counter.increment();
        }
        socket.send(Command.STOP.toString());
        LOGGER.info("Total messages received count: " + counter.count());
    }
}
