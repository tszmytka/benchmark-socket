package dev.tomek.benchmarksocket.catcher.transport;

import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.util.HashSet;
import java.util.Set;

@Log
@RequiredArgsConstructor
public class CatchTransportAbstract {
    protected final Counter counter;
    private final Set<String> msgsReceived = new HashSet<>();

    protected void onEachMessage(String msg) {
        msgsReceived.add(msg);
        counter.increment();
    }

    protected void onFinally() {
        LOGGER.info(String.format("Messages received: %d. Hash: %s", msgsReceived.size(), msgsReceived.hashCode()));
        LOGGER.info(String.format("Messages received: %s", counter.count()));
    }
}
