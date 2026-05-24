package com.LoQueHay.project.service.reports.specifications;

import com.LoQueHay.project.model.Product;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecs {

    public static Specification<Product> filterInventorySummary(
            Long ownerId,
            Long warehouseId,
            Long categoryId
    ) {
        return (root, query, cb) -> {
            // JOIN con categoría y stock
            Join<Object, Object> category = root.join("category", JoinType.INNER);
            Join<Object, Object> stock = root.join("stock", JoinType.LEFT); // LEFT para incluir productos sin stock

            // Predicado inicial
            Predicate predicate = cb.conjunction();

            // Filtrar por owner (obligatorio)
            predicate = cb.and(predicate, cb.equal(root.get("owner").get("id"), ownerId));

            // Filtrar por categoría si se pasa
            if (categoryId != null) {
                predicate = cb.and(predicate, cb.equal(category.get("id"), categoryId));
            }

            // Filtrar por warehouse si se pasa
            if (warehouseId != null) {
                predicate = cb.and(predicate, cb.equal(stock.get("warehouse").get("id"), warehouseId));
            }

            return predicate;
        };
    }
}
