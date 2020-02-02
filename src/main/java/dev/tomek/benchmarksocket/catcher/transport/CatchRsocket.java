package dev.tomek.benchmarksocket.catcher.transport;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Log
@Component
public class CatchRsocket implements CatchTransport {

    private final RSocketFactory.Start<RSocket> transport;

    public CatchRsocket(@Value("${transports.rsocket.port}") int port) {
        transport = RSocketFactory.connect()
            .transport(TcpClientTransport.create(port));
    }

    @Override
    public void run() {
        transport.start()
            .flatMapMany(rSocket -> rSocket.requestStream(DefaultPayload.create("START")).map(Payload::getDataUtf8))
            .subscribe(s -> LOGGER.info("Received " + s));
    }
}
