package com.LoQueHay.project.Specification;

import org.springframework.data.jpa.domain.Specification;
import com.LoQueHay.project.model.Product;

public class ProductSpecifications {

    public static Specification<Product> belongsToOwner(Long ownerId) {
        return (root, query, cb) -> cb.equal(root.get("owner").get("id"), ownerId);
    }

    public static Specification<Product> nameContains(String search) {
        return (root, query, cb) ->
                (search == null || search.isEmpty())
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%");
    }

    public static Specification<Product> skuContains(String sku) {
        return (root, query, cb) ->
                (sku == null || sku.isEmpty())
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("sku")), "%" + sku.toLowerCase() + "%");
    }

    public static Specification<Product> categoryId(Long categoryId) {
        return (root, query, cb) ->
                (categoryId == null)
                        ? cb.conjunction()
                        : cb.equal(root.get("category").get("id"), categoryId);
    }


}
