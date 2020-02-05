package dev.tomek.benchmarksocket.pitcher.transport.zmq;

import dev.tomek.benchmarksocket.Command;
import dev.tomek.benchmarksocket.pitcher.msgprovider.MsgProvider;
import dev.tomek.benchmarksocket.pitcher.transport.PitchTransport;
import dev.tomek.benchmarksocket.pitcher.transport.PitchTransportAbstract;
import io.micrometer.core.instrument.Counter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.concurrent.atomic.AtomicBoolean;

@Log
@Component
public class PitchZmq extends PitchTransportAbstract implements PitchTransport {
    private final ZMQ.Socket socket;
    private final MsgProvider msgProvider;

    public PitchZmq(
        @Qualifier("counterMessagesRsocket") Counter counter,
        @Value("${transports.zmq.port}") int port,
        MsgProvider msgProvider,
        ZContext zContext
    ) {
        super(counter);
        this.msgProvider = msgProvider;
        socket = zContext.createSocket(SocketType.PUB);
        socket.bind("tcp://*:" + port);
    }

    @Override
    public void run() {
        AtomicBoolean shouldSend = new AtomicBoolean();
        // PUB socket is ONLY for send()
//        Command command = Command.valueOf(new String(socket.recv()));
        Command command = Command.START;
        switch (command) {
            case START:
                shouldSend.set(true);
                msgProvider.provide().takeWhile(s -> shouldSend.get()).forEach(data -> {
                    onEachMessage(data);
                    socket.send(data, 1);
                });
                onFinally();
                break;
            case STOP:
                LOGGER.info("STOP received");
                shouldSend.set(false);
                break;
        }
        throw new IllegalArgumentException("Unknown command");
    }
}

