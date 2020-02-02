package dev.tomek.benchmarksocket.catcher.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import io.micrometer.core.instrument.logging.LoggingRegistryConfig;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Log
public class MeterConfig {

    @Bean
    public LoggingMeterRegistry loggingMeterRegistry() {
        return LoggingMeterRegistry.builder(new LoggingMeterRegConfig()).loggingSink(LOGGER::info).build();
    }

    @Bean
    public Counter counterMessagesZmq(MeterRegistry meterRegistry) {
        return meterRegistry.counter("messages.received", "transport", "zmq");
    }

    @Bean
    public Counter counterMessagesRsocket(MeterRegistry meterRegistry) {
        return meterRegistry.counter("messages.received", "transport", "rsocket");
    }

    private static class LoggingMeterRegConfig implements LoggingRegistryConfig {

        @Override
        public String get(String key) {
            return null;
        }

        @Override
        public Duration step() {
            return Duration.ofSeconds(5);
        }
    }
}
