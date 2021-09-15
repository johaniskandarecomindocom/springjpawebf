package com.ecomindo.onboarding.springjpawebf.controller;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ecomindo.onboarding.springjpawebf.dto.JobCityRequestDTO;
import com.ecomindo.onboarding.springjpawebf.dto.ResponseDTO;
import com.ecomindo.onboarding.springjpawebf.job.JobInsertDataService;

@CrossOrigin(methods = { RequestMethod.POST, RequestMethod.GET })
@RestController
public class JobController {
	private static final Logger logger = LoggerFactory.getLogger(JobController.class);

	@Autowired
	private Scheduler scheduler;

	@PostMapping("/scheduleInsert")
	public ResponseDTO scheduleInsert(@RequestBody JobCityRequestDTO request) {
		ResponseDTO response = new ResponseDTO();
		try {

			JobDetail jobDetail = null;
			Trigger trigger = null;
			ZonedDateTime dateTime = null;
			
			if (request.getJob().getScheduleType().equals("ONCE")) {
				dateTime = ZonedDateTime.of(request.getJob().getDateTime(),
						request.getJob().getTimeZone());
				if (dateTime.isBefore(ZonedDateTime.now())) {
					response.setCode("500");
					response.setMessage("dateTime must be after current time");
					return response;
				}
			} 

			jobDetail = buildJobDetail(request);
			trigger = buildJobTrigger(jobDetail, request.getJob().getScheduleType(), dateTime);
			scheduler.scheduleJob(jobDetail, trigger);

			response.setCode("200");
			response.setMessage("Job Scheduled Successfully!");
			response.setData(jobDetail.getKey().getName());
			return response;

		} catch (SchedulerException ex) {
			logger.error("Error scheduling", ex);

			response.setCode("500");
			response.setMessage("Error scheduling. Please try later!");
			response.setData(ex);
			return response;
		}
	}

	private JobDetail buildJobDetail(JobCityRequestDTO request) {

		JobDataMap jobDataMap = new JobDataMap();

		jobDataMap.put("name", request.getData().getName());
		jobDataMap.put("country", request.getData().getCountry());
		jobDataMap.put("state", request.getData().getState());
		jobDataMap.put("map", request.getData().getMap());

		return JobBuilder.newJob(JobInsertDataService.class).withIdentity(UUID.randomUUID().toString(), "insert-jobs")
				.withDescription("Insert cities data").usingJobData(jobDataMap).storeDurably().build();
	}

	private Trigger buildJobTrigger(JobDetail jobDetail, String schedulerType, ZonedDateTime startAt) {
		Trigger trigger = null;
		if (schedulerType.equals("ONCE")) {
			trigger = TriggerBuilder.newTrigger().forJob(jobDetail)
					.withIdentity(jobDetail.getKey().getName(), "insert-triggers-once").withDescription("Insert Trigger Once")
					.startAt(Date.from(startAt.toInstant()))
					.withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
					.build();

		} else {

			trigger = TriggerBuilder.newTrigger().forJob(jobDetail)
					.withIdentity(jobDetail.getKey().getName(), "insert-triggers-repeat").withDescription("Insert Trigger Repeat")
					.startNow()
					.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(5).repeatForever()).build();
		}
		return trigger;

	}
}
