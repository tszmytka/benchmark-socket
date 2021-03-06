package dev.tomek.benchmarksocket.catcher.transport.socket;

import dev.tomek.benchmarksocket.Command;
import dev.tomek.benchmarksocket.catcher.transport.CatchTransport;
import dev.tomek.benchmarksocket.catcher.transport.CatchTransportAbstract;
import io.micrometer.core.instrument.Counter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

import static dev.tomek.benchmarksocket.config.CommonConfig.*;

@Log4j2
@Component
@Conditional(CatchSocket.ConditionTransportSocket.class)
public class CatchSocket extends CatchTransportAbstract implements CatchTransport {
    private final int port;

    public CatchSocket(
        @Qualifier("counterMessagesSocket") Counter counter,
        @Value("${transport.socket.port}") int port
    ) {
        super(counter);
        this.port = port;
    }

    @Override
    public void run() {
        boolean keepTrying = true;
        int failedAttempt = 0;
        while (keepTrying) {
            try (Socket socket = new Socket("localhost", port)) {
                keepTrying = false;
                final PrintWriter socketWriter = new PrintWriter(socket.getOutputStream(), true);
                socketWriter.println(Command.START.toString());
                final BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message;
                while ((message = socketReader.readLine()) != null) {
                    onEachMessage(message);
                }
            } catch (ConnectException e) {
                LOGGER.warn("Failed to connect. Retrying...");
                failedAttempt++;
                if (failedAttempt > CONNECTION_ATTEMPTS_MAX) {
                    throw new RuntimeException("Max connection attempts reached: " + CONNECTION_ATTEMPTS_MAX);
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (IOException e) {
                LOGGER.error("Error while receiving data", e);
            }
        }
        onFinally();
    }

    static class ConditionTransportSocket extends AnyNestedCondition {

        private ConditionTransportSocket() {
            super(ConfigurationPhase.PARSE_CONFIGURATION);
        }

        @ConditionalOnProperty(name = PARAM_TRANSPORT, havingValue = TRANSPORT_SOCKET_PLAIN)
        static class TransportSocketPlain {

        }

        @ConditionalOnProperty(name = PARAM_TRANSPORT, havingValue = TRANSPORT_SOCKET_REFINED)
        static class TransportSocketRefined {

        }
    }
}
