package com.rentify.propertyservice.repository;

import com.rentify.propertyservice.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    List<Region> findByPropertyId(Long propertyId);
}
