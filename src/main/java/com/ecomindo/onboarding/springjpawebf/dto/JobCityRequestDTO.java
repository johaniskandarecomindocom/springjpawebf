package com.ecomindo.onboarding.springjpawebf.dto;

import com.ecomindo.onboarding.springjpawebf.model.City;

public class JobCityRequestDTO {
	private City data;
	private JobRequestDTO job;
	
	public City getData() {
		return data;
	}
	public void setData(City data) {
		this.data = data;
	}
	public JobRequestDTO getJob() {
		return job;
	}
	public void setJob(JobRequestDTO job) {
		this.job = job;
	}

}
