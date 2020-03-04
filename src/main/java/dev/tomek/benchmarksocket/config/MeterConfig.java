package dev.tomek.benchmarksocket.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import io.micrometer.core.instrument.logging.LoggingRegistryConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

@Log4j2
public class MeterConfig {
    @Bean
    public MeterRegistry loggingMeterRegistry() {
        // todo build the registry in a different way - this one inherits from StepMeterRegistry which builds counters returning total amount from last x seconds
        return LoggingMeterRegistry.builder(new LoggingMeterRegConfig()).loggingSink(LOGGER::info).build();
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
