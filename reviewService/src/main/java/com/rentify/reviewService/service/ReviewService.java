package com.rentify.reviewService.service;

import com.rentify.reviewService.model.Review;
import java.util.List;

public interface ReviewService {
    Review createReview(Review review);
    List<Review> getReviewsByUsuario(Long usuarioId);
    List<Review> getReviewsByProperty(Long propertyId);
    List<Review> getAllReviews();
    void deleteReview(Long id);
}
