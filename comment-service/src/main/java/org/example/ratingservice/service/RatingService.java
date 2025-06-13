package org.example.ratingservice.service;

import org.example.ratingservice.dto.CreateRatingRequest;
import org.example.ratingservice.dto.RatingDto;
import org.example.ratingservice.dto.RatingSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RatingService {
    RatingDto createRating(String userId, CreateRatingRequest request);
    RatingDto updateRating(String userId, String ratingId, Integer rating, String comment);
    void deleteRating(String userId, String ratingId);
    Page<RatingDto> getRatingsByProduct(String productId, Pageable pageable);
    Page<RatingDto> getRatingsByUser(String userId, Pageable pageable);
    RatingSummaryDto getProductRatingSummary(String productId);
}