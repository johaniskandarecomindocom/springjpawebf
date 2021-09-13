package com.ecomindo.onboarding.springjpawebf.data.jpa.service;

import com.ecomindo.onboarding.springjpawebf.data.jpa.domain.Rating;

public interface ReviewsSummary {

	long getNumberOfReviewsWithRating(Rating rating);

}
