package dev.tomek.benchmarksocket.pitcher.transport.socket;

import dev.tomek.benchmarksocket.Command;
import dev.tomek.benchmarksocket.pitcher.msgprovider.MsgProvider;
import dev.tomek.benchmarksocket.pitcher.transport.PitchTransport;
import dev.tomek.benchmarksocket.pitcher.transport.PitchTransportAbstract;
import io.micrometer.core.instrument.Counter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;

import static dev.tomek.benchmarksocket.config.CommonConfig.PARAM_TRANSPORT;
import static dev.tomek.benchmarksocket.config.CommonConfig.TRANSPORT_SOCKET;

@Log4j2
@Component
@ConditionalOnProperty(name = PARAM_TRANSPORT, havingValue = TRANSPORT_SOCKET)
public class PitchSocket extends PitchTransportAbstract implements PitchTransport {

    public PitchSocket(
        @Qualifier("counterMessagesSocket") Counter counter,
        @Value("${benchmark.duration}") Duration duration,
        @Value("${transport.socket.port}") int port,
        MsgProvider msgProvider
    ) {
        super(counter, duration, port, msgProvider);
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOGGER.info("ServerSocket accepting connection...");
            final Socket socket = serverSocket.accept();
            try (BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                LOGGER.info("Connection accepted. Waiting for commands...");
                String line;
                while ((line = socketReader.readLine()) != null) {
                    final Command command = Command.valueOf(line);
                    LOGGER.info("Received command " + command);
                    switch (command) {
                        case START:
                            sendMessages(socket);
                            break;
                        case STOP:
                            setForceStop(true);
                            break;
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Connection error", e);
        }
    }

    private void sendMessages(Socket socket) {
        markSendStart();
        try (PrintWriter socketWriter = new PrintWriter(socket.getOutputStream(), true)) {
            msgProvider.provide().takeWhile(s -> shouldSend()).forEach(msg -> {
                markMessageSent(msg);
                socketWriter.println(msg);
            });
        } catch (IOException e) {
            LOGGER.error("Cannot write to socket", e);
        } finally {
            markSendFinish();
        }
    }
}
