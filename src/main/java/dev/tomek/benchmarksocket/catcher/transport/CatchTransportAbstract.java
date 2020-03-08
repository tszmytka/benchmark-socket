package dev.tomek.benchmarksocket.catcher.transport;

import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Collection;

@Log4j2
@RequiredArgsConstructor
public class CatchTransportAbstract {
    protected static final int CONNECTION_ATTEMPTS_MAX = 10;
    private final Counter counter;

    /**
     * This can influence results if a large number of messages is sent
     */
    private final boolean storeMsgs = false;
    private final Collection<String> msgsReceived = new ArrayList<>();

    protected void onEachMessage(String msg) {
        if (storeMsgs) {
            msgsReceived.add(msg);
        }
        counter.increment();
    }

    protected void onFinally() {
        if (storeMsgs) {
            LOGGER.info(String.format("Messages received: %d. Hash: %s", msgsReceived.size(), msgsReceived.hashCode()));
        }
        LOGGER.info("Finished catching");
    }
}
