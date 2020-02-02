package dev.tomek.benchmarksocket.pitcher;

import dev.tomek.benchmarksocket.pitcher.transport.Transport;
import lombok.RequiredArgsConstructor;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class Pitcher implements SmartLifecycle {

    private final Collection<Transport> transports;

    @Override
    public void start() {
        transports.forEach(transport -> {
            transport.run();

            int a = 123;

        });
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }
}
