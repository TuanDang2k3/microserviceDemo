package org.example.ratingservice.mapper;

import org.example.ratingservice.dto.RatingDto;
import org.example.ratingservice.entity.Rating;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RatingMapper {
    RatingDto toDto(Rating rating);
}