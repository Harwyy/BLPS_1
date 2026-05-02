package com.blps.blps.cfg;

import com.blps.blps.job.OldOrdersCancelJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail oldOrdersCancelJobDetail() {
        return JobBuilder.newJob(OldOrdersCancelJob.class)
                .withIdentity("oldOrdersCancelJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger oldOrdersCancelTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(oldOrdersCancelJobDetail())
                .withIdentity("oldOrdersCancelTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 * * * ?"))
                .build();
    }
}