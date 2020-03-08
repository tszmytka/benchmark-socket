package dev.tomek.benchmarksocket.pitcher.transport;

public interface PitchTransport extends Runnable {
    void setDoAfterMessagesSent(Runnable task);
}
