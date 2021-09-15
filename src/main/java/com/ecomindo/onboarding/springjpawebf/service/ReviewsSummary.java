package com.ecomindo.onboarding.springjpawebf.service;

import com.ecomindo.onboarding.springjpawebf.model.Rating;

public interface ReviewsSummary {

	long getNumberOfReviewsWithRating(Rating rating);

}
