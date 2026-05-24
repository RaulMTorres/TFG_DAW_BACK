package com.LoQueHay.project.dto.auth_dtos;

import java.util.Set;

public class MyUserEntityDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private boolean enabled;
    private boolean locked;
    private long created_by;
    private long updated_by;

    // 🔹 NUEVO: permisos efectivos
    private Set<String> effectivePermissions;

    // Getters y Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }

    public long getCreated_by() { return created_by; }
    public void setCreated_by(long created_by) { this.created_by = created_by; }

    public long getUpdated_by() { return updated_by; }
    public void setUpdated_by(long updated_by) { this.updated_by = updated_by; }

    public Set<String> getEffectivePermissions() {
        return effectivePermissions;
    }

    public void setEffectivePermissions(Set<String> effectivePermissions) {
        this.effectivePermissions = effectivePermissions;
    }
}
