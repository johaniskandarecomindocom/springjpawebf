package com.ecomindo.onboarding.springjpawebf.job;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.ecomindo.onboarding.springjpawebf.service.CityService;

public class JobInsertDataService extends QuartzJobBean {
	private static final Logger logger = LoggerFactory.getLogger(JobInsertDataService.class);

	@Autowired
	CityService cityService;

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {

			logger.info("Executing Job with key {}", jobExecutionContext.getJobDetail().getKey());

			JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
			String name = jobDataMap.getString("name");
			String country = jobDataMap.getString("country");
			String state = jobDataMap.getString("state");
			String map = jobDataMap.getString("map");

			cityService.insert(name, country, state, map);
		} catch (Exception e) {
			logger.error("Failed to send insert.", e);
			throw e;
		}
	}

}
