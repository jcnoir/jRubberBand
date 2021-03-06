package org.black.jtranscribe;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

@Configuration
@ComponentScan
public class JRubberBandConfiguration {
    @Bean
    public EventBus eventBus() {
        return new AsyncEventBus(Executors.newFixedThreadPool(3));
    }

}