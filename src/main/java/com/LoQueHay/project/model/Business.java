package com.LoQueHay.project.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToOne
    @JoinColumn(name = "owner_id")
    private MyUserEntity owner;

    @ManyToMany(mappedBy = "businesses")
    private Set<MyUserEntity> workers = new HashSet<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MyUserEntity getOwner() {
        return owner;
    }

    public void setOwner(MyUserEntity owner) {
        this.owner = owner;
    }

    public Set<MyUserEntity> getWorkers() {
        return workers;
    }

    public void setWorkers(Set<MyUserEntity> workers) {
        this.workers = workers;
    }
}
