package com.rentify.reviewService.repository;

import com.rentify.reviewService.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUsuarioId(Long usuarioId);
    List<Review> findByPropertyId(Long propertyId);
}
