package fr.terrier.apiterriercrm.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Configuration
public class SchedulersConfiguration {
    @Value("${schedulers.datasource.pool-size}")
    private int connectionPoolSize;

    @Value("${schedulers.datasource.queue-task-cap}")
    private int datasourceSchedulerQueueSize;

    @Bean
    public Scheduler datasourceScheduler() {
        return Schedulers.newBoundedElastic(connectionPoolSize, datasourceSchedulerQueueSize, "datasourceScheduler");
    }
}
