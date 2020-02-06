package dev.tomek.benchmarksocket.pitcher;

import dev.tomek.benchmarksocket.pitcher.transport.PitchTransport;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class Pitcher {

    private final Collection<PitchTransport> pitchTransports;

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        pitchTransports.forEach(Runnable::run);
    }
}
