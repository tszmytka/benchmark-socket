package dev.tomek.benchmarksocket.catcher.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeterConfig extends dev.tomek.benchmarksocket.config.MeterConfig {

    @Bean
    public Counter counterMessagesSocket(MeterRegistry meterRegistry) {
        // todo use a BeanFactory instead of having n bean methods?
        return meterRegistry.counter("messages.received", "transport", "socket");
    }

    @Bean
    public Counter counterMessagesZmq(MeterRegistry meterRegistry) {
        return meterRegistry.counter("messages.received", "transport", "zmq");
    }

    @Bean
    public Counter counterMessagesRsocket(MeterRegistry meterRegistry) {
        return meterRegistry.counter("messages.received", "transport", "rsocket");
    }
}
