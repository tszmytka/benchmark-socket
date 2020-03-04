package dev.tomek.benchmarksocket.pitcher;

import dev.tomek.benchmarksocket.pitcher.transport.PitchTransport;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;

@Log4j2
@RequiredArgsConstructor
@SpringBootApplication
public class Pitcher {
    private final PitchTransport pitchTransport;

    private final ConfigurableApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(Pitcher.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        LOGGER.info("Running pitcher: " + pitchTransport.getClass().getSimpleName());
        pitchTransport.run();
        context.close();
    }
}
