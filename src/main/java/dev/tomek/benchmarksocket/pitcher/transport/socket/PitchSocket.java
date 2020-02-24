package dev.tomek.benchmarksocket.pitcher.transport.socket;

import dev.tomek.benchmarksocket.Command;
import dev.tomek.benchmarksocket.pitcher.msgprovider.MsgProvider;
import dev.tomek.benchmarksocket.pitcher.transport.PitchTransport;
import dev.tomek.benchmarksocket.pitcher.transport.PitchTransportAbstract;
import io.micrometer.core.instrument.Counter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
@Component
public class PitchSocket extends PitchTransportAbstract implements PitchTransport {
    private final MsgProvider msgProvider;
    private final int port;

    public PitchSocket(
        @Qualifier("counterMessagesSocket") Counter counter,
        @Value("${transports.socket.port}") int port,
        MsgProvider msgProvider
    ) {
        super(counter);
        this.msgProvider = msgProvider;
        this.port = port;
    }

    @Override
    public void run() {
        final AtomicBoolean shouldSend = new AtomicBoolean();
        try {
            final ServerSocket serverSocket = new ServerSocket(port);
            LOGGER.info("Pitcher ready. Accepting connection...");
            final Socket socket = serverSocket.accept();
            // todo close streams
            final BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketReader.close();
            String line;
            LOGGER.info("Connection accepted. Waiting for commands...");
            while ((line = socketReader.readLine()) != null) {
                final Command command = Command.valueOf(line);
                switch (command) {
                    case START:
                        shouldSend.set(true);
                        try (PrintWriter socketWriter = new PrintWriter(socket.getOutputStream(), true)) {
                            msgProvider.provide().takeWhile(s -> shouldSend.get()).forEach(msg -> {
                                onEachMessage(msg);
                                socketWriter.println(msg);
                            });
                        }
                        onFinally();
                        break;
                    case STOP:
                        shouldSend.set(false);
                        break;
                }
                throw new IllegalArgumentException("Unknown command");
            }
        } catch (IOException e) {

        }
    }
}
