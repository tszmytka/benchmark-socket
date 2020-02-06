package dev.tomek.benchmarksocket.catcher;

import dev.tomek.benchmarksocket.catcher.transport.CatchTransport;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
@Log
public class Catcher {
    private final Collection<CatchTransport> catchTransports;

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
