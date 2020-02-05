package dev.tomek.benchmarksocket.pitcher.transport;

import io.micrometer.core.instrument.Counter;
import io.rsocket.AbstractRSocket;
import lombok.extern.java.Log;
import reactor.core.publisher.Signal;
import reactor.core.publisher.SignalType;

import java.util.HashSet;
import java.util.Set;

@Log
public abstract class PitchTransportAbstract extends AbstractRSocket {
    private final Counter counter;
    private final Set<String> msgsSent = new HashSet<>();

    public PitchTransportAbstract(Counter counter) {
        this.counter = counter;
    }

    protected void onEachMessage(String msg) {
        msgsSent.add(msg);
        counter.increment();
    }

    protected void onFinally() {
        LOGGER.info(String.format("Messages sent: %d. Hash: %s", msgsSent.size(), msgsSent.hashCode()));
        LOGGER.info(String.format("Messages sent: %s", counter.count()));
        dispose();
    }
}
