package com.rentify.reviewService.controller;

import com.rentify.reviewService.model.Review;
import com.rentify.reviewService.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // Crear reseña
    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        return ResponseEntity.ok(reviewService.createReview(review));
    }

    // Obtener todas las reseñas
    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    // Obtener reseñas por usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Review>> getReviewsByUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(reviewService.getReviewsByUsuario(usuarioId));
    }

    // Obtener reseñas por propiedad
    @GetMapping("/propiedad/{propertyId}")
    public ResponseEntity<List<Review>> getReviewsByProperty(@PathVariable Long propertyId) {
        return ResponseEntity.ok(reviewService.getReviewsByProperty(propertyId));
    }

    // Eliminar reseña
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
