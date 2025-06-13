package org.example.ratingservice.repository;

import org.example.ratingservice.entity.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, String> {

    Page<Rating> findByProductId(String productId, Pageable pageable);

    Page<Rating> findByUserId(String userId, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.productId = ?1")
    Double getAverageRatingByProductId(String productId);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.productId = ?1")
    Long getRatingCountByProductId(String productId);
}