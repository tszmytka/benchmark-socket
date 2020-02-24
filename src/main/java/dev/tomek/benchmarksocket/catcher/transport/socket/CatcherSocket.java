package dev.tomek.benchmarksocket.catcher.transport.socket;

import dev.tomek.benchmarksocket.catcher.transport.CatchTransport;
import dev.tomek.benchmarksocket.catcher.transport.CatchTransportAbstract;
import io.micrometer.core.instrument.Counter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.time.Duration;
import java.time.ZonedDateTime;

@Log
@Component
public class CatcherSocket extends CatchTransportAbstract implements CatchTransport {
    private final int port;
    private final Duration duration;

    public CatcherSocket(
        @Qualifier("counterMessagesSocket") Counter counter,
        @Value("${transports.socket.port}") int port,
        @Value("${duration-per-transport}") Duration duration
    ) {
        super(counter);
        this.port = port;
        this.duration = duration;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket("localhost", port)) {
            long endMillis = ZonedDateTime.now().plus(duration).toInstant().toEpochMilli();
            final BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (System.currentTimeMillis() <= endMillis) {
                String message;
                while ((message = socketReader.readLine()) != null) {
                    onEachMessage(message);
                }
            }
        } catch (IOException e) {
            LOGGER.info("Error while receiving data" + e);
        }
        onFinally();
    }
}
