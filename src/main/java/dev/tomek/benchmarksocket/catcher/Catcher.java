package dev.tomek.benchmarksocket.catcher;

import dev.tomek.benchmarksocket.pitcher.transport.PitchTransport;
import lombok.RequiredArgsConstructor;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class Catcher implements SmartLifecycle {
    private final Collection<PitchTransport> pitchTransports;

    @Override
    public void start() {
        pitchTransports.forEach(PitchTransport::run);
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }
}
