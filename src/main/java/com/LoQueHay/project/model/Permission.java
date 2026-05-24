package com.LoQueHay.project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Permission {
    @Id
    private String code;// Ej: "product:create", "product:delete"

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
