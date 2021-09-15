package com.ecomindo.onboarding.springjpawebf.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ecomindo.onboarding.springjpawebf.model.City;
import com.ecomindo.onboarding.springjpawebf.model.Hotel;
import com.ecomindo.onboarding.springjpawebf.model.Review;
import com.ecomindo.onboarding.springjpawebf.model.ReviewDetails;

public interface HotelService {

	Hotel getHotel(City city, String name);

	Page<Review> getReviews(Hotel hotel, Pageable pageable);

	Review getReview(Hotel hotel, int index);

	Review addReview(Hotel hotel, ReviewDetails details);

	ReviewsSummary getReviewSummary(Hotel hotel);

}
