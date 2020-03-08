package dev.tomek.benchmarksocket.pitcher.transport.socket;

import dev.tomek.benchmarksocket.Command;
import dev.tomek.benchmarksocket.pitcher.msgprovider.MsgProvider;
import dev.tomek.benchmarksocket.pitcher.transport.PitchTransport;
import dev.tomek.benchmarksocket.pitcher.transport.PitchTransportAbstract;
import io.micrometer.core.instrument.Counter;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;

@Log4j2
abstract class PitchSocketAbstract extends PitchTransportAbstract implements PitchTransport {

    public PitchSocketAbstract(Counter counter, Duration duration, int port, MsgProvider msgProvider) {
        super(counter, duration, port, msgProvider);
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOGGER.info("ServerSocket accepting connection...");
            final Socket socket = serverSocket.accept();
            try (BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                LOGGER.info("Connection accepted. Waiting for commands...");
                String line = socketReader.readLine();
                final Command command = Command.valueOf(line);
                LOGGER.info("Received command " + command);
                switch (command) {
                    case START:
                        sendMessages(socket);
                        break;
                    case STOP:
                        stopSending();
                        break;
                }
            }
            LOGGER.info("Closing sockets");
            socket.close();
        } catch (IOException e) {
            LOGGER.error("Connection error", e);
        }
    }

    protected abstract void sendMessages(Socket socket);
}
