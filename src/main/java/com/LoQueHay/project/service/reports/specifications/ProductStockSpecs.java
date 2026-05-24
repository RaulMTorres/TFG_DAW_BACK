package com.LoQueHay.project.service.reports.specifications;

import com.LoQueHay.project.model.ProductStock;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ProductStockSpecs {

    /**
     * @param ownerId      obligatorio
     * @param warehouseId  opcional — filtra por almacén
     * @param categoryId   opcional — filtra por categoría
     * @param productId    opcional — filtra por producto específico
     * @param expiringOnly si true filtra solo stocks con fecha de expiración
     * @param dateTo       si expiringOnly=true, límite superior de la fecha de expiración (null = sin límite superior)
     */
    public static Specification<ProductStock> filterStocks(
            Long ownerId,
            Long warehouseId,
            Long categoryId,
            Long productId,
            boolean expiringOnly,
            LocalDate dateTo
    ) {
        return (root, query, cb) -> {
            Join<Object, Object> product = root.join("product", JoinType.INNER);
            Join<Object, Object> category = product.join("category", JoinType.INNER);

            Predicate predicate = cb.conjunction();

            // Owner (obligatorio)
            predicate = cb.and(predicate, cb.equal(product.get("owner").get("id"), ownerId));

            // Solo stock disponible (cantidad > 0)
            predicate = cb.and(predicate, cb.gt(root.get("quantity"), 0));

            if (warehouseId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("warehouse").get("id"), warehouseId));
            }

            if (categoryId != null) {
                predicate = cb.and(predicate, cb.equal(category.get("id"), categoryId));
            }

            if (productId != null) {
                predicate = cb.and(predicate, cb.equal(product.get("id"), productId));
            }

            if (expiringOnly) {
                // Tiene fecha de expiración
                predicate = cb.and(predicate, cb.isNotNull(root.get("expirationDate")));
                // Desde hoy
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("expirationDate"), LocalDate.now()));
                // Hasta la fecha límite (si se pasa)
                if (dateTo != null) {
                    predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("expirationDate"), dateTo));
                }
            }

            return predicate;
        };
    }

    // Compatibilidad con llamadas existentes (sin productId ni dateTo)
    public static Specification<ProductStock> filterStocks(
            Long ownerId, Long warehouseId, Long categoryId, boolean expiringOnly
    ) {
        LocalDate expiryLimit = expiringOnly ? LocalDate.now().plusDays(30) : null;
        return filterStocks(ownerId, warehouseId, categoryId, null, expiringOnly, expiryLimit);
    }
}
