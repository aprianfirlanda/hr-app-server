package com.aprianfirlanda.hrappserver.repositories;

import com.aprianfirlanda.hrappserver.domain.models.ERole;
import com.aprianfirlanda.hrappserver.domain.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(ERole name);
}