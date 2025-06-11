package org.example.ratingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ratingservice.dto.CreateRatingRequest;
import org.example.ratingservice.dto.RatingDto;
import org.example.ratingservice.dto.RatingSummaryDto;
import org.example.ratingservice.entity.Rating;
import org.example.ratingservice.exception.ResourceNotFoundException;
import org.example.ratingservice.exception.UnauthorizedException;
import org.example.ratingservice.mapper.RatingMapper;
import org.example.ratingservice.repository.RatingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductRatingService implements RatingService {

    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;

    @Override
    @Transactional
    public RatingDto createRating(String userId, CreateRatingRequest request) {
        Rating rating = Rating.builder()
                .userId(userId)
                .productId(request.getProductId())
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Rating savedRating = ratingRepository.save(rating);
        log.info("Rating created for product: {} by user: {}", request.getProductId(), userId);

        return ratingMapper.toDto(savedRating);
    }

    @Override
    @Transactional
    public RatingDto updateRating(String userId, String ratingId, Integer rating, String comment) {
        Rating existingRating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found"));

        if (!existingRating.getUserId().equals(userId)) {
            throw new UnauthorizedException("You can only update your own ratings");
        }

        existingRating.setRating(rating);
        existingRating.setComment(comment);
        existingRating.setUpdatedAt(LocalDateTime.now());

        Rating updatedRating = ratingRepository.save(existingRating);
        log.info("Rating updated: {}", ratingId);

        return ratingMapper.toDto(updatedRating);
    }

    @Override
    @Transactional
    public void deleteRating(String userId, String ratingId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found"));

        if (!rating.getUserId().equals(userId)) {
            throw new UnauthorizedException("You can only delete your own ratings");
        }

        ratingRepository.delete(rating);
        log.info("Rating deleted: {}", ratingId);
    }

    @Override
    public Page<RatingDto> getRatingsByProduct(String productId, Pageable pageable) {
        Page<Rating> ratings = ratingRepository.findByProductId(productId, pageable);
        return ratings.map(ratingMapper::toDto);
    }

    @Override
    public Page<RatingDto> getRatingsByUser(String userId, Pageable pageable) {
        Page<Rating> ratings = ratingRepository.findByUserId(userId, pageable);
        return ratings.map(ratingMapper::toDto);
    }

    @Override
    public RatingSummaryDto getProductRatingSummary(String productId) {
        Double avgRating = ratingRepository.getAverageRatingByProductId(productId);
        Long totalRatings = ratingRepository.getRatingCountByProductId(productId);

        return RatingSummaryDto.builder()
                .productId(productId)
                .averageRating(avgRating != null ? avgRating : 0.0)
                .totalRatings(totalRatings != null ? totalRatings : 0L)
                .build();
    }
}