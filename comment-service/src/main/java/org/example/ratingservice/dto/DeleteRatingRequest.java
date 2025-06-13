package org.example.ratingservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteRatingRequest {
    @NotBlank(message = "User ID is required")
    private String userId;
}