// WarehouseMapper.java
package com.LoQueHay.project.mappers;

import com.LoQueHay.project.dto.warehouse_dtos.WarehouseRequestDTO;
import com.LoQueHay.project.dto.warehouse_dtos.WarehouseResponseDTO;
import com.LoQueHay.project.model.Warehouse;
import com.LoQueHay.project.model.MyUserEntity;

public class WarehouseMapper {

    public static Warehouse toEntity(WarehouseRequestDTO dto, MyUserEntity user) {
        Warehouse w = new Warehouse();
        w.setName(dto.getName());
        w.setLocation(dto.getLocation());
        w.setDescription(dto.getDescription());
        w.setOwner(user);
        w.setCreatedBy(user);
        return w;
    }

    public static WarehouseResponseDTO toDTO(Warehouse w) {
        WarehouseResponseDTO dto = new WarehouseResponseDTO();
        dto.setId(w.getId());
        dto.setName(w.getName());
        dto.setLocation(w.getLocation());
        dto.setDescription(w.getDescription());
        dto.setOwnerUsername(w.getOwner().getUsername());
        dto.setCreatedByUsername(w.getCreatedBy().getUsername());
        dto.setCreatedAt(w.getCreatedAt());
        return dto;
    }
}
