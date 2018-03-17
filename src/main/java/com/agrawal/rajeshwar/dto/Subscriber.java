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
public class Subscriber {

    @ManyToOne
    @JoinColumn(name = "id", nullable = false)
    private User user;

    @Id
    @Column
    private int targetUserId;

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (this.getClass() != obj.getClass()) {
	    return false;
	}
	Subscriber other = (Subscriber) obj;
	if (this.targetUserId != other.targetUserId) {
	    return false;
	}
	return true;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + this.targetUserId;
	return result;
    }

}