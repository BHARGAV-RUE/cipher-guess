package com.bhargav.crack_the_number.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class AsyncConfigTest {

    @Autowired
    private AsyncConfig asyncConfig;

    @Test
    void asyncConfig_ShouldLoadAsApplicationContext() {
        assertThat(asyncConfig).isNotNull();
    }

    @Test
    void asyncConfig_ShouldBeAnnotatedWithEnableAsync() {
        boolean hasEnableAsync = AsyncConfig.class
                .isAnnotationPresent(EnableAsync.class);
        assertThat(hasEnableAsync).isTrue();
    }
}