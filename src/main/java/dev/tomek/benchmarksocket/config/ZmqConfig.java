package dev.tomek.benchmarksocket.config;

import org.springframework.context.annotation.Bean;
import org.zeromq.ZContext;

public class ZmqConfig {
    @Bean
    public ZContext zContext() {
        return new ZContext();
    }
}
