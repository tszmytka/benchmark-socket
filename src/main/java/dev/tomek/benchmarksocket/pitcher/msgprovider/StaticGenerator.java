package dev.tomek.benchmarksocket.pitcher.msgprovider;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.stream.Stream;

@Component
public class StaticGenerator implements MsgProvider {
    @Override
    public Stream<String> provide() {
        return infiniteStaticString();
    }

    private Stream<String> infiniteStaticString() {
        return Stream.generate(() -> "Message static");
    }

    private Stream<String> infiniteStrings() {
        return Stream.generate(() -> "Message at " + Instant.now());
    }

    private Stream<String> infiniteSequentialInts() {
        return Stream.iterate(0, i -> i + 1).map(i -> "Message #" + i + " at " + System.currentTimeMillis());
    }
}
