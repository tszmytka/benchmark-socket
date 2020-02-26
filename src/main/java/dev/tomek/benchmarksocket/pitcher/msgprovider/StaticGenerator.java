package dev.tomek.benchmarksocket.pitcher.msgprovider;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Stream;

@Log4j2
@Component
public class StaticGenerator implements MsgProvider {
    private static final int MSG_PER_SEC = 50_000;

    private final Duration duration;

    private final String[] messages;

    public StaticGenerator(
        @Value("${benchmark.duration}") Duration duration
    ) {
        this.duration = duration;
        if (duration.toSeconds() > Integer.MAX_VALUE) {
            throw new IllegalStateException("Will not be able to pre-build so many messages");
        }
        LOGGER.info("Begin pre-building messages");
        final int amount = MSG_PER_SEC * (int) duration.toSeconds();
        messages = new String[amount];
        prepareFiniteStrings(amount);
        LOGGER.info("Finished pre-building messages. Amount: " + amount);
    }

    private void prepareFiniteStrings(int amount) {
        for (int i = 0; i < amount; i++) {
            messages[i] = "Message " + i + " @ " + System.currentTimeMillis();
        }
    }

    @Override
    public Stream<String> provide() {
        return Arrays.stream(messages);
    }

    private Stream<String> infiniteStaticString() {
        return Stream.generate(() -> "Message static");
    }

    private Stream<String> infiniteSequentialInts() {
        return Stream.iterate(0, i -> i + 1).map(i -> "Message #" + i + " at " + System.currentTimeMillis());
    }
}
