package dev.tomek.benchmarksocket.catcher.transport.rsocket;

import dev.tomek.benchmarksocket.Command;
import dev.tomek.benchmarksocket.catcher.transport.CatchTransport;
import io.micrometer.core.instrument.Counter;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Log
@Component
public class CatchRsocket implements CatchTransport {
    private final RSocketFactory.Start<RSocket> transport;
    private final Counter counter;
    private final Duration duration;

    public CatchRsocket(@Value("${transports.rsocket.port}") int port, @Qualifier("counterMessagesRsocket") Counter counter, @Value("${duration-per-transport}") Duration duration) {
        transport = RSocketFactory.connect()
            .transport(TcpClientTransport.create(port));
        this.counter = counter;
        this.duration = duration;
    }

    @Override
    public void run() {
        transport.start()
            .take(duration)
            .flatMapMany(rSocket -> rSocket.requestStream(DefaultPayload.create(Command.START.toString())).map(Payload::getDataUtf8))
            .doOnEach(s -> counter.increment())
            .blockLast();
        LOGGER.info("Total messages received count: " + counter.count());
    }
}
