package dev.tomek.benchmarksocket.catcher;

import dev.tomek.benchmarksocket.catcher.transport.CatchTransport;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
@Log
public class Catcher implements SmartLifecycle {
    private final Collection<CatchTransport> catchTransports;

    @Override
    public void start() {
        catchTransports.forEach(catchTransport -> {
            String msg = "receiving messages with " + catchTransport.getClass().getSimpleName();
            LOGGER.info("Start " + msg);
            catchTransport.run();
            LOGGER.info("Finished " + msg);
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
