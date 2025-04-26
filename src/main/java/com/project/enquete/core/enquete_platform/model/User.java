package com.project.enquete.core.enquete_platform.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 70)
    private String email;

    @Column(nullable = false, length = 70)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Transient
    private List<Vote> votes;
}
