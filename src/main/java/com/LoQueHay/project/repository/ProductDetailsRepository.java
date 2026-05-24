package com.LoQueHay.project.repository;

import com.LoQueHay.project.model.MyUserEntity;
import com.LoQueHay.project.model.ProductDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductDetailsRepository extends JpaRepository<ProductDetails, Long> {
    Optional<ProductDetails> findByProductId(Long id);

}
