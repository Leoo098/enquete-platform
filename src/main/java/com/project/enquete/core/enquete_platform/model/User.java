package com.project.enquete.core.enquete_platform.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role = UserRole.USER;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Transient
    private List<Vote> votes;

    @Column(name = "social_login")
    private Boolean socialLogin = false;

    private Boolean demoUser = false;

    @CreationTimestamp
    private Instant createdAt;

    public List<String> getRoles(){
        return List.of(role.name());
    }
}
