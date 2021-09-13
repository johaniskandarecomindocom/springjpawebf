package com.ecomindo.onboarding.springjpawebf.data.jpa.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ecomindo.onboarding.springjpawebf.data.jpa.domain.City;

interface CityRepository extends JpaRepository<City, Long> {

//	Page<City> findAll(Pageable pageable);

	Page<City> findByNameContainingAndCountryContainingAllIgnoringCase(String name,
			String country, Pageable pageable);

	City findByNameAndCountryAllIgnoringCase(String name, String country);

}
