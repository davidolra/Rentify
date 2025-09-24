package com.rentify.propertyservice.repository;

import com.rentify.propertyservice.model.Foto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FotoRepository extends JpaRepository<Foto, Long> {
    List<Foto> findByPropertyId(Long propertyId);
}
