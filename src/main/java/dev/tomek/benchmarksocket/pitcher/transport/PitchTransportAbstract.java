package dev.tomek.benchmarksocket.pitcher.transport;

import dev.tomek.benchmarksocket.pitcher.msgprovider.MsgProvider;
import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Log4j2
@RequiredArgsConstructor
public abstract class PitchTransportAbstract {
    private final Counter counter;
    private final Duration duration;
    protected final int port;
    protected final MsgProvider msgProvider;

    /**
     * This can influence results if a large number of messages is sent
     */
    private final boolean storeMsgs = false;
    private final Collection<String> msgsSent = new ArrayList<>();

    private long endMillis;
    private boolean forceStop;


    protected void markSendStart() {
        LOGGER.info("Start sending messages.");
        endMillis = ZonedDateTime.now().plus(duration).toInstant().toEpochMilli();
    }

    protected boolean shouldSend() {
        return !forceStop && System.currentTimeMillis() <= endMillis;
    }

    protected void markMessageSent(String msg) {
        if (storeMsgs) {
            msgsSent.add(msg);
        }
        counter.increment();
    }

    protected void markSendFinish() {
        if (storeMsgs) {
            LOGGER.info(String.format("Messages sent: %d. Hash: %s", msgsSent.size(), msgsSent.hashCode()));
        }
        LOGGER.info("Finished sending messages.");
    }

    public void setForceStop(boolean forceStop) {
        this.forceStop = forceStop;
    }
}
