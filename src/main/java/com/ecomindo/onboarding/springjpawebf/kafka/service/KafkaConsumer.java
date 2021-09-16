package com.ecomindo.onboarding.springjpawebf.kafka.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ecomindo.onboarding.springjpawebf.service.CityService;

@Component
public class KafkaConsumer {

	@Autowired
	CityService cityService;

	@KafkaListener(topics = "${spring.custom.kafka.topic}", groupId = "java")
	public void listToTopicOnBoaarding(String message) {
		System.out.println("Received Message in group foo: " + message);

		String formattedDate = new SimpleDateFormat("yyyyMMdd_hh:mm").format(new Date());

		String name = String.format("Test name %s", formattedDate);
		String country = String.format("Test country %s %s", message, formattedDate);
		String state = String.format("Test state %s", formattedDate);
		String map = String.format("Test map %s", formattedDate);

		cityService.insert(name, country, state, map);

	}

}
