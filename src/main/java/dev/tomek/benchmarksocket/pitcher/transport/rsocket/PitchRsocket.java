package dev.tomek.benchmarksocket.pitcher.transport.rsocket;

import dev.tomek.benchmarksocket.Command;
import dev.tomek.benchmarksocket.pitcher.msgprovider.MsgProvider;
import dev.tomek.benchmarksocket.pitcher.transport.PitchTransport;
import io.micrometer.core.instrument.Counter;
import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocket;
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
import reactor.core.publisher.Signal;
import reactor.core.publisher.SignalType;

import java.util.HashSet;
import java.util.Set;

@Component
@Log
public class PitchRsocket implements PitchTransport {
    private final RSocketFactory.Start<CloseableChannel> transport;

    public PitchRsocket(@Value("${transports.rsocket.port}") int port, RSocket pitchingSocket) {
        this.transport = RSocketFactory.receive()
            .acceptor((setup, sendingSocket) -> Mono.just(pitchingSocket))
            .transport(TcpServerTransport.create(port));
    }

    @Override
    public void run() {
        transport.start().block();
    }

    @Component
    static class PitchingSocket extends AbstractRSocket {
        private final Flux<String> msgFlux;
        private final Counter counter;
        private final Set<String> msgsSent = new HashSet<>();

        public PitchingSocket(MsgProvider msgProvider, @Qualifier("counterMessagesRsocket") Counter counter) {
            msgFlux = Flux.fromStream(msgProvider.provide());
            this.counter = counter;
        }

        @Override
        public Flux<Payload> requestStream(Payload payload) {
            Command command = Command.valueOf(payload.getDataUtf8());
            switch (command) {
                case START:
                    LOGGER.info(String.format("Received command '%s'. Begin sending messages", command));
                    return msgFlux.doOnEach(this::processMsg).map(DefaultPayload::create).doFinally(this::onFinally);
                case STOP:
                    throw new IllegalArgumentException("Not yet implemented");
            }
            throw new IllegalArgumentException("Unknown command");
        }

        private void processMsg(Signal<String> msg) {
            msgsSent.add(msg.get());
            counter.increment();
        }

        private void onFinally(SignalType signalType) {
            LOGGER.info(String.format("Messages sent: %d. Hash: %s", msgsSent.size(), msgsSent.hashCode()));
            LOGGER.info(String.format("Messages sent: %s", counter.count()));
            dispose();
        }
    }
}
