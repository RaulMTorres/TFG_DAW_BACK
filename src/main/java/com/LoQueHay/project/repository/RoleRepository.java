package com.LoQueHay.project.repository;

import com.LoQueHay.project.model.MyUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import com.LoQueHay.project.model.Role;

import java.util.Optional;


public interface RoleRepository extends JpaRepository<Role, String>{

    Optional<Role> findByName(String name);
}
