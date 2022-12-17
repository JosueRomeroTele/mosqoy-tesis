package com.example.sw2.config;

import com.example.sw2.job.MonthlyExpirationAlertJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigureMonthlyAlertJob {

    @Bean
    public JobDetail jobADetails() {
        return JobBuilder.newJob(MonthlyExpirationAlertJob.class).withIdentity("monthly-alert-products")
                .storeDurably().build();
    }

    @Bean
    public Trigger jobATrigger(JobDetail jobADetails) {

        return TriggerBuilder.newTrigger().forJob(jobADetails)
                .withIdentity("TriggerA")
                //.withSchedule(CronScheduleBuilder.cronSchedule("0 0/5 * 1/1 * ? *")) // Prueba cada 5 min
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 1 1/1 ? *")) // cada primero del mes
                .build();
    }
}
