package dev.tomek.benchmarksocket.pitcher;

import dev.tomek.benchmarksocket.pitcher.transport.PitchTransport;
import lombok.RequiredArgsConstructor;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class Pitcher implements SmartLifecycle {

    private final Collection<PitchTransport> pitchTransports;

    @Override
    public void start() {
        pitchTransports.forEach(pitchTransport -> {
            pitchTransport.run();
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
