package com.ecomindo.onboarding.springjpawebf.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.ecomindo.onboarding.springjpawebf.dao.CityRepository;
import com.ecomindo.onboarding.springjpawebf.dao.HotelRepository;
import com.ecomindo.onboarding.springjpawebf.model.City;
import com.ecomindo.onboarding.springjpawebf.model.HotelSummary;

@Component("cityService")
@Transactional
class CityServiceImpl implements CityService {

	private final CityRepository cityRepository;

	private final HotelRepository hotelRepository;

    private ExecutorService executor = Executors.newFixedThreadPool(2);
    
    @Autowired
	public CityServiceImpl(CityRepository cityRepository, HotelRepository hotelRepository) {
		this.cityRepository = cityRepository;
		this.hotelRepository = hotelRepository;
	}

	@Override
	public Page<City> findCities(CitySearchCriteria criteria, Pageable pageable) {

		Assert.notNull(criteria, "Criteria must not be null");
		String name = criteria.getName();

		if (!StringUtils.hasLength(name)) {
			return this.cityRepository.findAll((Pageable)null);
		}

		String country = "";
		int splitPos = name.lastIndexOf(",");

		if (splitPos >= 0) {
			country = name.substring(splitPos + 1);
			name = name.substring(0, splitPos);
		}

		return this.cityRepository
				.findByNameContainingAndCountryContainingAllIgnoringCase(name.trim(),
						country.trim(), pageable);
	}

	@Override
	public City getCity(String name, String country) {
		Assert.notNull(name, "Name must not be null");
		Assert.notNull(country, "Country must not be null");
		return this.cityRepository.findByNameAndCountryAllIgnoringCase(name, country);
	}

	@Override
	public Page<HotelSummary> getHotels(City city, Pageable pageable) {
		Assert.notNull(city, "City must not be null");
		return this.hotelRepository.findByCity(city, pageable);
	}
	
	@Override
	public City insert(String name, String country, String state, String map) {
		try {
			City city = this.cityRepository.save(new City(name, country, state, map));
			return city;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public Future<City> insertLongTime(String name, String country, String state, String map) {
		return executor.submit(() -> {
			Thread.sleep(10000);
			return insert(name, country, state, map);
		});
	}
}
