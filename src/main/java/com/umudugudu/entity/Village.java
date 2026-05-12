package com.umudugudu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "villages")
public class Village {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToOne
    @JoinColumn(name = "village_leader_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "isibo", "village"})
    private User villageLeader;

    @OneToMany(mappedBy = "village", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Isibo> isibos;
}