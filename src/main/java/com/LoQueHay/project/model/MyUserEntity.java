package com.LoQueHay.project.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class MyUserEntity {

    // ---------- Identidad ----------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;

    private boolean enabled;
    private boolean locked;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    private long created_by;
    private long updated_by;

    // ---------- Relaciones de roles y permisos ----------
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_name"))
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_additional_permissions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_code"))
    private Set<Permission> additionalPermissions = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_revoked_permissions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_code"))
    private Set<Permission> revokedPermissions = new HashSet<>();

    // ---------- Relaciones con negocios ----------
    @ManyToMany
    @JoinTable(name = "user_business",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "business_id"))
    private Set<Business> businesses = new HashSet<>();

    // 🔗 Usuario creador (OWNER de este usuario)
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private MyUserEntity owner;

    // ---------- Getters y Setters ----------

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

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setAdditionalPermissions(Set<Permission> additionalPermissions) {
        this.additionalPermissions = additionalPermissions;
    }

    public void setRevokedPermissions(Set<Permission> revokedPermissions) {
        this.revokedPermissions = revokedPermissions;
    }

    public void setBusinesses(Set<Business> businesses) {
        this.businesses = businesses;
    }

    public long getCreated_by() { return created_by; }
    public long getUpdated_by() { return updated_by; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }

    public Set<Permission> getAdditionalPermissions() { return additionalPermissions; }
    public Set<Permission> getRevokedPermissions() { return revokedPermissions; }

    public Set<Business> getBusinesses() { return businesses; }

    public MyUserEntity getOwner() { return owner; }
    public void setOwner(MyUserEntity owner) { this.owner = owner; }

    public void setCreated_by(long created_by) {
        this.created_by = created_by;
    }

    public void setUpdated_by(long updated_by) {
        this.updated_by = updated_by;
    }
}
