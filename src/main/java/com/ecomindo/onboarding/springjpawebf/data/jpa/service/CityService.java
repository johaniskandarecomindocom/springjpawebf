package com.ecomindo.onboarding.springjpawebf.data.jpa.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ecomindo.onboarding.springjpawebf.data.jpa.domain.City;
import com.ecomindo.onboarding.springjpawebf.data.jpa.domain.HotelSummary;

public interface CityService {

	Page<City> findCities(CitySearchCriteria criteria, Pageable pageable);

	City getCity(String name, String country);

	Page<HotelSummary> getHotels(City city, Pageable pageable);
	
	City insert(String name, String country, String state);	

}
