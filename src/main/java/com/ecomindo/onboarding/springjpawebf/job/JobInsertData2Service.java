package com.ecomindo.onboarding.springjpawebf.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ecomindo.onboarding.springjpawebf.service.CityService;

@Component
public class JobInsertData2Service implements Job {
	private static final Logger logger = LoggerFactory.getLogger(JobInsertData2Service.class);

	@Autowired
	CityService cityService;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
//			logger.info("Executing Job with key {}", jobExecutionContext.getJobDetail().getKey());
			
			String name = String.format("name1");
			String country = String.format("country1");
			String state = String.format("state1");
			String map = String.format("map1");

//			cityService.insert(name, country, state, map);
		} catch (Exception e) {
			logger.error("Failed to send insert.", e);
			throw e;
		}
	}


}
