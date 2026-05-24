package com.LoQueHay.project.service.reports.specifications;

import com.LoQueHay.project.model.InventoryMovement;
import com.LoQueHay.project.model.MovementType;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InventoryMovementSpecs {

    public static Specification<InventoryMovement> filterPurchases(
            Long ownerId,
            Long warehouseId,
            Long categoryId,
            MovementType type,
            LocalDateTime dateFrom,
            LocalDateTime dateTo
    ) {
        return filterPurchases(ownerId, warehouseId, categoryId, null, type, dateFrom, dateTo);
    }

    public static Specification<InventoryMovement> filterPurchases(
            Long ownerId,
            Long warehouseId,
            Long categoryId,
            Long productId,
            MovementType type,
            LocalDateTime dateFrom,
            LocalDateTime dateTo
    ) {
        return (Root<InventoryMovement> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

            var details = root.join("details");
            var product = details.join("product");

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("owner").get("id"), ownerId));
            predicates.add(cb.equal(root.get("movementType"), type));
            predicates.add(cb.between(root.get("createdAt"), dateFrom, dateTo));

            if (warehouseId != null) {
                predicates.add(cb.equal(root.get("warehouse").get("id"), warehouseId));
            }

            if (categoryId != null) {
                predicates.add(cb.equal(product.get("category").get("id"), categoryId));
            }

            if (productId != null) {
                predicates.add(cb.equal(product.get("id"), productId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
