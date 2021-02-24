package dev.tomek.benchmarksocket.catcher;

import dev.tomek.benchmarksocket.config.CommonConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(CommonConfig.PARAM_TRANSPORT + "=" + CommonConfig.TRANSPORT_SOCKET_PLAIN)
class CatcherTest {

    @Test
    void canLoadContext() {
    }
}
