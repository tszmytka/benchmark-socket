package dev.tomek.benchmarksocket.pitcher.transport.socket;

import dev.tomek.benchmarksocket.pitcher.msgprovider.MsgProvider;
import io.micrometer.core.instrument.Counter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Duration;

import static dev.tomek.benchmarksocket.config.CommonConfig.PARAM_TRANSPORT;
import static dev.tomek.benchmarksocket.config.CommonConfig.TRANSPORT_SOCKET_PLAIN;

@Log4j2
@Component
@ConditionalOnProperty(name = PARAM_TRANSPORT, havingValue = TRANSPORT_SOCKET_PLAIN)
public class PitchSocketPlain extends PitchSocketAbstract {

    public PitchSocketPlain(
        @Qualifier("counterMessagesSocket") Counter counter,
        @Value("${benchmark.duration}") Duration duration,
        @Value("${transport.socket.port}") int port,
        MsgProvider msgProvider
    ) {
        super(counter, duration, port, msgProvider);
    }

    @Override
    protected void sendMessages(Socket socket) {
        markSendStart();
        try {
            PrintWriter socketWriter = new PrintWriter(socket.getOutputStream(), true);
            msgProvider.provide().takeWhile(s -> shouldSend()).forEach(msg -> {
                socketWriter.println(msg);
                markMessageSent(msg);
            });
        } catch (IOException e) {
            LOGGER.error("Cannot write to socket", e);
        } finally {
            markSendFinish();
        }
    }
}
