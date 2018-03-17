package com.agrawal.rajeshwar.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Friends {

    @ManyToOne
    @JoinColumn(name = "id", nullable = false)
    private User user;

    @Id
    @Column
    private int friendId;

}