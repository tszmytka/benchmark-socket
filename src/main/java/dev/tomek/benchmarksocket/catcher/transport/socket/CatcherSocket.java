package dev.tomek.benchmarksocket.catcher.transport.socket;

import dev.tomek.benchmarksocket.Command;
import dev.tomek.benchmarksocket.catcher.transport.CatchTransport;
import dev.tomek.benchmarksocket.catcher.transport.CatchTransportAbstract;
import io.micrometer.core.instrument.Counter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Duration;

@Log4j2
@Component
public class CatcherSocket extends CatchTransportAbstract implements CatchTransport {
    private final int port;
    private final Duration duration;

    public CatcherSocket(
        @Qualifier("counterMessagesSocket") Counter counter,
        @Value("${transport.socket.port}") int port,
        @Value("${benchmark.duration}") Duration duration
    ) {
        super(counter);
        this.port = port;
        this.duration = duration;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket("localhost", port)) {
            final PrintWriter socketWriter = new PrintWriter(socket.getOutputStream(), true);
            socketWriter.println(Command.START.toString());
            final BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message;
            while ((message = socketReader.readLine()) != null) {
                onEachMessage(message);
            }
        } catch (IOException e) {
            LOGGER.error("Error while receiving data", e);
        }
        onFinally();
    }
}
