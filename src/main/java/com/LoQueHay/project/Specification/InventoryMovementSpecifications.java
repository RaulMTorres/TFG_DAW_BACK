package com.LoQueHay.project.Specification;

import com.LoQueHay.project.model.InventoryMovement;
import com.LoQueHay.project.model.InventoryMovementDetail;
import com.LoQueHay.project.model.MovementType;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
public class InventoryMovementSpecifications {

    public static Specification<InventoryMovement> belongsToOwner(Long ownerId) {
        return (root, query, cb) -> cb.equal(root.get("owner").get("id"), ownerId);
    }

    public static Specification<InventoryMovement> referenceContains(String reference) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("referenceDocument")), "%" + reference.toLowerCase() + "%");
    }

    public static Specification<InventoryMovement> movementTypeEquals(String movementType) {
        return (root, query, cb) ->
                cb.equal(root.get("movementType"), MovementType.valueOf(movementType));
    }

    public static Specification<InventoryMovement> warehouseId(Long warehouseId) {
        return (root, query, cb) ->
                cb.equal(root.get("warehouse").get("id"), warehouseId);
    }

    public static Specification<InventoryMovement> hasProduct(Long productId) {
        return (root, query, cb) -> {
            Join<InventoryMovement, InventoryMovementDetail> details = root.join("details");
            return cb.equal(details.get("product").get("id"), productId);
        };
    }

    public static Specification<InventoryMovement> quantityEquals(Integer quantity) {
        return (root, query, cb) -> {
            Join<InventoryMovement, InventoryMovementDetail> details = root.join("details");
            return cb.equal(details.get("quantity"), quantity);
        };
    }

    public static Specification<InventoryMovement> unitCostEquals(Double unitCost) {
        return (root, query, cb) -> {
            Join<InventoryMovement, InventoryMovementDetail> details = root.join("details");
            return cb.equal(details.get("unitCost"), unitCost);
        };
    }
}


