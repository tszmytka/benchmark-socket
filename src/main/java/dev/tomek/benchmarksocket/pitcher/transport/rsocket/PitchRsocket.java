package dev.tomek.benchmarksocket.pitcher.transport.rsocket;

import dev.tomek.benchmarksocket.Command;
import dev.tomek.benchmarksocket.pitcher.msgprovider.MsgProvider;
import dev.tomek.benchmarksocket.pitcher.transport.PitchTransport;
import dev.tomek.benchmarksocket.pitcher.transport.PitchTransportAbstract;
import io.micrometer.core.instrument.Counter;
import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

import static dev.tomek.benchmarksocket.config.CommonConfig.PARAM_TRANSPORT;
import static dev.tomek.benchmarksocket.config.CommonConfig.TRANSPORT_RSOCKET;

@Log4j2
@Component
@ConditionalOnProperty(name = PARAM_TRANSPORT, havingValue = TRANSPORT_RSOCKET)
public class PitchRsocket extends PitchTransportAbstract implements PitchTransport {

    public PitchRsocket(
        @Qualifier("counterMessagesRsocket") Counter counter,
        @Value("${benchmark.duration}") Duration duration,
        @Value("${transport.rsocket.port}") int port,
        MsgProvider msgProvider
    ) {
        super(counter, duration, port, msgProvider);
    }

    @Override
    public void run() {
        RSocketFactory.receive()
            .acceptor((setup, sendingSocket) -> Mono.just(new PitchingSocket(msgProvider)))
            .transport(TcpServerTransport.create(port))
            .start()
            .block();
    }

    class PitchingSocket extends AbstractRSocket {
        private final Flux<String> msgFlux;

        public PitchingSocket(MsgProvider msgProvider) {
            msgFlux = Flux.fromStream(msgProvider.provide());
        }

        @Override
        public Flux<Payload> requestStream(Payload payload) {
            Command command = Command.valueOf(payload.getDataUtf8());
            switch (command) {
                case START:
                    LOGGER.info(String.format("Received command '%s'. Begin sending messages", command));
                    markSendStart();
                    return msgFlux.takeWhile(s -> shouldSend())
                        .map(DefaultPayload::create)
                        .doOnEach(signal -> Optional.ofNullable(signal.get()).ifPresent(p -> markMessageSent(p.getDataUtf8())))
                        .doOnComplete(PitchRsocket.this::markSendFinish);
                case STOP:
                    stopSending();
                    break;
            }
            throw new IllegalArgumentException("Unknown command");
        }
    }
}
