package dev.tomek.benchmarksocket.catcher;

import dev.tomek.benchmarksocket.catcher.transport.CatchTransport;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;

@Log4j2
@SpringBootApplication
@RequiredArgsConstructor
public class Catcher {
    private final CatchTransport catchTransport;

    private final ConfigurableApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(Catcher.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        LOGGER.info("Running catcher " + catchTransport.getClass().getSimpleName());
        new Thread(catchTransport).start();
    }
}
