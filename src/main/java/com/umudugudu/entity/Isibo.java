package com.umudugudu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "isibos")
public class Isibo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "village_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "isibos", "villageLeader"})
    private Village village;

    @OneToOne
    @JoinColumn(name = "isibo_leader_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "isibo", "village"})
    private User isiboLeader;

    @OneToMany(mappedBy = "isibo", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<User> citizens;
}