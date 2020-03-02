package dev.tomek.benchmarksocket.pitcher;

import dev.tomek.benchmarksocket.pitcher.transport.PitchTransport;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@RequiredArgsConstructor
@SpringBootApplication
public class Pitcher {
    private final PitchTransport pitchTransport;

    public static void main(String[] args) {
        SpringApplication.run(Pitcher.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        pitchTransport.run();
    }
}
