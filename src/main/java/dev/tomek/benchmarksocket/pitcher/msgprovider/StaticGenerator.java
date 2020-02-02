package dev.tomek.benchmarksocket.pitcher.msgprovider;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.stream.Stream;

@Component
public class StaticGenerator implements MsgProvider {
    @Override
    public Stream<String> provide() {
        return Stream.generate(() -> "Message at " + Instant.now());
    }
}
