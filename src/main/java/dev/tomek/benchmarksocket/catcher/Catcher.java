package dev.tomek.benchmarksocket.catcher;

import dev.tomek.benchmarksocket.catcher.transport.CatchTransport;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.Collection;

@SpringBootApplication
@RequiredArgsConstructor
@Log4j2
public class Catcher {
    private final Collection<CatchTransport> catchTransports;

    public static void main(String[] args) {
        SpringApplication.run(Catcher.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        catchTransports.forEach(catchTransport -> {
            String msg = "receiving messages with " + catchTransport.getClass().getSimpleName();
            LOGGER.info("Start " + msg);
            catchTransport.run();
            LOGGER.info("Finished " + msg);
        });
    }
}
