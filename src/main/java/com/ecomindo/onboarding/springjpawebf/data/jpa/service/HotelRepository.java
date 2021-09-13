package com.ecomindo.onboarding.springjpawebf.data.jpa.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.ecomindo.onboarding.springjpawebf.data.jpa.domain.City;
import com.ecomindo.onboarding.springjpawebf.data.jpa.domain.Hotel;
import com.ecomindo.onboarding.springjpawebf.data.jpa.domain.HotelSummary;
import com.ecomindo.onboarding.springjpawebf.data.jpa.domain.RatingCount;

interface HotelRepository extends Repository<Hotel, Long> {

	Hotel findByCityAndName(City city, String name);

	@Query("select new com.ecomindo.onboarding.springjpawebf.data.jpa.domain.HotelSummary(h.city, h.name, avg(r.rating)) "
			+ "from Hotel h left outer join h.reviews r where h.city = ?1 group by h")
	Page<HotelSummary> findByCity(City city, Pageable pageable);

	@Query("select new com.ecomindo.onboarding.springjpawebf.data.jpa.domain.RatingCount(r.rating, count(r)) "
			+ "from Review r where r.hotel = ?1 group by r.rating order by r.rating DESC")
	List<RatingCount> findRatingCounts(Hotel hotel);
}
