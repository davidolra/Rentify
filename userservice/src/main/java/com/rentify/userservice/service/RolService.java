package com.rentify.userservice.service;

import com.rentify.userservice.model.Rol;
import com.rentify.userservice.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;

    public Rol crearRol(Rol rol) {
        return rolRepository.save(rol);
    }

    public List<Rol> obtenerRoles() {
        return rolRepository.findAll();
    }

    public Optional<Rol> obtenerRolPorId(Long id) {
        return rolRepository.findById(id);
    }
}
