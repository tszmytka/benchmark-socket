package dev.tomek.benchmarksocket.pitcher.transport.zmq;

import dev.tomek.benchmarksocket.Command;
import dev.tomek.benchmarksocket.config.CommonConfig;
import dev.tomek.benchmarksocket.pitcher.Pitcher;
import dev.tomek.benchmarksocket.pitcher.msgprovider.MsgProvider;
import dev.tomek.benchmarksocket.pitcher.transport.PitchTransport;
import dev.tomek.benchmarksocket.pitcher.transport.PitchTransportAbstract;
import io.micrometer.core.instrument.Counter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.time.Duration;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.tomek.benchmarksocket.config.CommonConfig.PARAM_TRANSPORT;
import static dev.tomek.benchmarksocket.config.CommonConfig.TRANSPORT_ZMQ;

@Log4j2
@Component
@ConditionalOnProperty(name = PARAM_TRANSPORT, havingValue = TRANSPORT_ZMQ)
public class PitchZmq extends PitchTransportAbstract implements PitchTransport {
    private final ZContext zContext;

    public PitchZmq(
        @Qualifier("counterMessagesZmq") Counter counter,
        @Value("${benchmark.duration}") Duration duration,
        @Value("${transport.zmq.port}") int port,
        MsgProvider msgProvider,
        ZContext zContext
    ) {
        super(counter, duration, port, msgProvider);
        this.zContext = zContext;
    }

    @Override
    public void run() {
        try (ZMQ.Socket socket = zContext.createSocket(SocketType.PUSH)) {
            socket.bind("tcp://*:" + port);
            // zmq push socket blocks automatically until pull socket starts receiving
            socket.send("DUMMY");
            markSendStart();
            msgProvider.provide().takeWhile(s -> shouldSend()).forEach(msg -> {
                socket.send(msg);
                markMessageSent(msg);
            });
        } finally {
            markSendFinish();
        }
    }
}
