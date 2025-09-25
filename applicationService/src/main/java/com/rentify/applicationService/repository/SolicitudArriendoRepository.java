package com.rentify.applicationService.repository;

import com.rentify.applicationService.model.SolicitudArriendo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitudArriendoRepository extends JpaRepository<SolicitudArriendo, Long> {
    List<SolicitudArriendo> findByUsuarioId(Long usuarioId);
    List<SolicitudArriendo> findByPropiedadId(Long propiedadId);
}
