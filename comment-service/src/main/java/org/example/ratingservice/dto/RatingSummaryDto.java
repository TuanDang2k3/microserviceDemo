package org.example.ratingservice.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingSummaryDto {
    private String productId;
    private Double averageRating;
    private Long totalRatings;
}