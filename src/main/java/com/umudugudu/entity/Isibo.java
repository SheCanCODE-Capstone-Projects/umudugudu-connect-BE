package com.umudugudu.entity;

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
    private Village village;

    @OneToOne
    @JoinColumn(name = "isibo_leader_id")
    private User isiboLeader;

    @OneToMany(mappedBy = "isibo", fetch = FetchType.LAZY)
    private List<User> citizens;
}