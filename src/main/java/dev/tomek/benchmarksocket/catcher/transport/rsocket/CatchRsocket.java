package dev.tomek.benchmarksocket.catcher.transport.rsocket;

import dev.tomek.benchmarksocket.Command;
import dev.tomek.benchmarksocket.catcher.transport.CatchTransport;
import dev.tomek.benchmarksocket.catcher.transport.CatchTransportAbstract;
import io.micrometer.core.instrument.Counter;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Log4j2
//@Component
public class CatchRsocket extends CatchTransportAbstract implements CatchTransport {
    private final RSocketFactory.Start<RSocket> transport;
    private final Duration duration;

    public CatchRsocket(
        @Qualifier("counterMessagesRsocket") Counter counter,
        @Value("${transport.rsocket.port}") int port,
        @Value("${benchmark.duration}") Duration duration
    ) {
        super(counter);
        transport = RSocketFactory.connect()
            .transport(TcpClientTransport.create(port));
        this.duration = duration;
    }

    @Override
    public void run() {
        transport.start()
            .flatMapMany(rSocket -> rSocket.requestStream(DefaultPayload.create(Command.START.toString())).map(Payload::getDataUtf8))
            .doOnEach(s -> onEachMessage(s.get()))
            .take(duration)
            .blockLast();
        onFinally();
    }
}
