package com.ecomindo.onboarding.springjpawebf.data.jpa.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import com.ecomindo.onboarding.springjpawebf.data.jpa.domain.Hotel;
import com.ecomindo.onboarding.springjpawebf.data.jpa.domain.Review;

interface ReviewRepository extends Repository<Review, Long> {

	Page<Review> findByHotel(Hotel hotel, Pageable pageable);

	Review findByHotelAndIndex(Hotel hotel, int index);

	Review save(Review review);

}
