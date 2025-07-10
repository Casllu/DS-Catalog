package com.lucasalmeida.dscatalog.repository;

import com.lucasalmeida.dscatalog.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
