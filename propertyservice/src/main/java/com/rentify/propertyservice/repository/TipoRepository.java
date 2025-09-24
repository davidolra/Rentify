package com.rentify.propertyservice.repository;

import com.rentify.propertyservice.model.Tipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TipoRepository extends JpaRepository<Tipo, Long> {
}

