package dev.tomek.benchmarksocket.pitcher.msgprovider;

import java.util.stream.Stream;

public interface MsgProvider {
    Stream<String> provide();
}
