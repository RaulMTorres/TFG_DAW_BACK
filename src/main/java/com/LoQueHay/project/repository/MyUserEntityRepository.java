package com.LoQueHay.project.repository;

import com.LoQueHay.project.model.MyUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MyUserEntityRepository extends JpaRepository<MyUserEntity, Long> {

    Optional<MyUserEntity> findByEmail(String email);
    Optional<MyUserEntity> findByUsername(String username);

}
