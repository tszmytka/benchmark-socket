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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.util.retry.Retry;

import java.time.Duration;

import static dev.tomek.benchmarksocket.config.CommonConfig.PARAM_TRANSPORT;
import static dev.tomek.benchmarksocket.config.CommonConfig.TRANSPORT_RSOCKET;

@Log4j2
@Component
@ConditionalOnProperty(name = PARAM_TRANSPORT, havingValue = TRANSPORT_RSOCKET)
public class CatchRsocket extends CatchTransportAbstract implements CatchTransport {
    private final RSocketFactory.Start<RSocket> transport;

    public CatchRsocket(
        @Qualifier("counterMessagesRsocket") Counter counter,
        @Value("${transport.rsocket.port}") int port
    ) {
        super(counter);
        transport = RSocketFactory.connect()
            .transport(TcpClientTransport.create(port));
    }

    @Override
    public void run() {
        transport.start()
            .flatMapMany(rSocket -> rSocket.requestStream(DefaultPayload.create(Command.START.toString())).map(Payload::getDataUtf8))
            .doOnEach(s -> onEachMessage(s.get()))
            .retryWhen(Retry.backoff(CONNECTION_ATTEMPTS_MAX, Duration.ofSeconds(5)))
            .blockLast();
    }
}
