package com.project.enquete.core.enquete_platform.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Entity
@Table(name = "options")
@Data
@RequiredArgsConstructor
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @OneToMany(mappedBy = "option", cascade = CascadeType.ALL)
    private List<Vote> votes;

    @Override
    public String toString() {
        return "Option{" +
                "optionId=" + id +
                ", text='" + text + '\'' +
                '}';
    }
}