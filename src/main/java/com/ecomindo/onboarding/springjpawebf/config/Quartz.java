package com.ecomindo.onboarding.springjpawebf.config;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ecomindo.onboarding.springjpawebf.job.JobInsertData2Service;

@Configuration
public class Quartz {
	@Bean
	public JobDetail jobAutoInsertDetails() {
		return JobBuilder.newJob(JobInsertData2Service.class).withIdentity("JobAutoInsert", "insert-city-second")
				.storeDurably()
				.build();
	}

	@Bean
	public Trigger jobAutoInsertTrigger(JobDetail jobAutoInsertDetails) {
		return TriggerBuilder.newTrigger().forJob(jobAutoInsertDetails)
				.withIdentity("trigger-from-main", "insert-city-second")
				.startNow()
				.withSchedule(CronScheduleBuilder.cronSchedule("0/20 * * ? * * *")).build();
	}
	
}
