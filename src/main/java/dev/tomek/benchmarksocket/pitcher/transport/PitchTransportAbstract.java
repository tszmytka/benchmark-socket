package dev.tomek.benchmarksocket.pitcher.transport;

import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.util.HashSet;
import java.util.Set;

@Log
@RequiredArgsConstructor
public abstract class PitchTransportAbstract {
    private final Counter counter;
    private final Set<String> msgsSent = new HashSet<>();

    protected void onEachMessage(String msg) {
        msgsSent.add(msg);
        counter.increment();
    }

    protected void onFinally() {
        LOGGER.info(String.format("Messages sent: %d. Hash: %s", msgsSent.size(), msgsSent.hashCode()));
        LOGGER.info(String.format("Messages sent: %s", counter.count()));
    }
}
