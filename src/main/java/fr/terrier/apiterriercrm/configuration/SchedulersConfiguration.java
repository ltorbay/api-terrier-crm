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

    @Value("${schedulers.payment.pool-size}")
    private int paymentSchedulerPoolSize;

    @Value("${schedulers.datasource.queue-task-cap}")
    private int paymentSchedulerQueueSize;

    @Bean
    public Scheduler datasourceScheduler() {
        return Schedulers.newBoundedElastic(connectionPoolSize, datasourceSchedulerQueueSize, "datasourceScheduler");
    }
    
    @Bean
    public Scheduler paymentScheduler() {
        return Schedulers.newBoundedElastic(paymentSchedulerPoolSize, paymentSchedulerQueueSize, "paymentScheduler");
    }
}
