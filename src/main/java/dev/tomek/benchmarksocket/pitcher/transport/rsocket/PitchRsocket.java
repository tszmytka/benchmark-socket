package dev.tomek.benchmarksocket.pitcher.transport.rsocket;

import dev.tomek.benchmarksocket.Command;
import dev.tomek.benchmarksocket.pitcher.msgprovider.MsgProvider;
import dev.tomek.benchmarksocket.pitcher.transport.PitchTransport;
import dev.tomek.benchmarksocket.pitcher.transport.PitchTransportAbstract;
import io.micrometer.core.instrument.Counter;
import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log
@Component
public class PitchRsocket extends PitchTransportAbstract implements PitchTransport {
    private final RSocketFactory.Start<CloseableChannel> transport;

    public PitchRsocket(
        @Qualifier("counterMessagesRsocket") Counter counter,
        @Value("${transports.rsocket.port}") int port,
        MsgProvider msgProvider
    ) {
        super(counter);
        this.transport = RSocketFactory.receive()
            .acceptor((setup, sendingSocket) -> Mono.just(new PitchingSocket(msgProvider)))
            .transport(TcpServerTransport.create(port));
    }

    @Override
    public void run() {
        transport.start().block();
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
                    return msgFlux.doOnEach(s -> onEachMessage(s.get())).map(DefaultPayload::create).doFinally(s -> onFinally());
                case STOP:
                    throw new IllegalArgumentException("Not yet implemented");
            }
            throw new IllegalArgumentException("Unknown command");
        }
    }
}
