package com.agrawal.rajeshwar.dto;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(unique = true, length = 500, nullable = false)
    private String email;

    // defaults to false
    @Builder.Default
    @Column(nullable = false)
    private boolean isDelete = false;

    @OneToMany(mappedBy = "user")
    private Set<Friends> friends;

}