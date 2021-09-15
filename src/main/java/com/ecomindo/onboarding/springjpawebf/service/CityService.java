package com.ecomindo.onboarding.springjpawebf.service;

import java.util.concurrent.Future;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ecomindo.onboarding.springjpawebf.model.City;
import com.ecomindo.onboarding.springjpawebf.model.HotelSummary;

public interface CityService {

	Page<City> findCities(CitySearchCriteria criteria, Pageable pageable);

	City getCity(String name, String country);

	Page<HotelSummary> getHotels(City city, Pageable pageable);

	City insert(String name, String country, String state, String map);	

	Future<City> insertLongTime(String name, String country, String state, String map);
	
}
