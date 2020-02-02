package dev.tomek.benchmarksocket.catcher.transport;

import dev.tomek.benchmarksocket.Command;
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

@Log
@Component
public class CatchRsocket implements CatchTransport {

    private final RSocketFactory.Start<RSocket> transport;

    private final Counter counter;

    public CatchRsocket(@Value("${transports.rsocket.port}") int port, @Qualifier("counterMessagesRsocket") Counter counter) {
        transport = RSocketFactory.connect()
            .transport(TcpClientTransport.create(port));
        this.counter = counter;
    }

    @Override
    public void run() {
        transport.start()
            .flatMapMany(rSocket -> rSocket.requestStream(DefaultPayload.create(Command.START.toString())).map(Payload::getDataUtf8))
            .doOnEach(s -> counter.increment())
            .blockLast();
        LOGGER.info("Total messages count: " + counter.count());
    }
}
