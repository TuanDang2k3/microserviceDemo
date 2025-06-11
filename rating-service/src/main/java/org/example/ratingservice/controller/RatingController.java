package org.example.ratingservice.controller;

import org.example.ratingservice.dto.*;
import org.example.ratingservice.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
@RestController
@RequestMapping("/ratings")
@RequiredArgsConstructor
@Slf4j
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<ApiResponse<RatingDto>> createRating(
            @Valid @RequestBody CreateRatingRequest request) {

        log.info("Creating rating for product: {} by user: {}", request.getProductId(), request.getUserId());
        RatingDto createdRating = ratingService.createRating(request.getUserId(), request);

        ApiResponse<RatingDto> response = ApiResponse.<RatingDto>builder()
                .code(0)
                .message("Rating created successfully")
                .result(createdRating)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RatingDto>> updateRating(
            @PathVariable String id,
            @RequestBody UpdateRatingRequest request) {

        log.info("Updating rating: {} by user: {}", id, request.getUserId());
        RatingDto updatedRating = ratingService.updateRating(request.getUserId(), id, request.getRating(), request.getComment());

        ApiResponse<RatingDto> response = ApiResponse.<RatingDto>builder()
                .code(0)
                .message("Rating updated successfully")
                .result(updatedRating)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRating(
            @PathVariable String id,
            @RequestBody DeleteRatingRequest request) {

        log.info("Deleting rating: {} by user: {}", id, request.getUserId());
        ratingService.deleteRating(request.getUserId(), id);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(0)
                .message("Rating deleted successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<Page<RatingDto>>> getProductRatings(
            @PathVariable String productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Fetching ratings for product: {}, page: {}, size: {}", productId, page, size);
        Page<RatingDto> ratings = ratingService.getRatingsByProduct(productId, PageRequest.of(page, size));

        ApiResponse<Page<RatingDto>> response = ApiResponse.<Page<RatingDto>>builder()
                .code(0)
                .message("Ratings retrieved successfully")
                .result(ratings)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<Page<RatingDto>>> getUserRatings(
            @RequestParam String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Fetching ratings for user: {}, page: {}, size: {}", userId, page, size);
        Page<RatingDto> ratings = ratingService.getRatingsByUser(userId, PageRequest.of(page, size));

        ApiResponse<Page<RatingDto>> response = ApiResponse.<Page<RatingDto>>builder()
                .code(0)
                .message("Ratings retrieved successfully")
                .result(ratings)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary/{productId}")
    public ResponseEntity<ApiResponse<RatingSummaryDto>> getProductRatingSummary(
            @PathVariable String productId) {

        log.info("Fetching rating summary for product: {}", productId);
        RatingSummaryDto summary = ratingService.getProductRatingSummary(productId);

        ApiResponse<RatingSummaryDto> response = ApiResponse.<RatingSummaryDto>builder()
                .code(0)
                .message("Rating summary retrieved successfully")
                .result(summary)
                .build();

        return ResponseEntity.ok(response);
    }
}