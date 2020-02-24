package dev.tomek.benchmarksocket.pitcher;

import dev.tomek.benchmarksocket.pitcher.transport.PitchTransport;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.Collection;

@RequiredArgsConstructor
@SpringBootApplication
public class Pitcher {
    private final Collection<PitchTransport> pitchTransports;

    public static void main(String[] args) {
        SpringApplication.run(Pitcher.class);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        pitchTransports.forEach(Runnable::run);
    }
}
