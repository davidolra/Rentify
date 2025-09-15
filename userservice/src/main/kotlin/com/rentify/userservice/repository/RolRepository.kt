package com.rentify.userservice.repository

import com.rentify.userservice.model.Rol
import org.springframework.data.jpa.repository.JpaRepository

interface RolRepository : JpaRepository<Rol, Long>
