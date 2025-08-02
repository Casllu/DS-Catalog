package com.lucasalmeida.dscatalog.repository;

import com.lucasalmeida.dscatalog.entities.PasswordRecover;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordRevocerRepository extends JpaRepository<PasswordRecover, Long> {
}
