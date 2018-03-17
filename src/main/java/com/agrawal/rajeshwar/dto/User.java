package com.agrawal.rajeshwar.dto;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.springframework.util.CollectionUtils;

import com.google.common.collect.Sets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Override
    public String toString() {
	return "User [email=" + this.email + "] friends are "
		+ Optional.ofNullable(this.friends)
			  .orElse(Sets.newHashSet())
			  .stream()
			  .filter(Objects::nonNull)
			  .map(User::getEmail)
			  .collect(Collectors.toList());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(unique = true, length = 500, nullable = false)
    private String email;

    // defaults to false
    @Builder.Default
    @Column(nullable = false)
    private boolean isDelete = false;

    @ManyToMany
    @JoinTable(name = "friends", joinColumns = @JoinColumn(name = "id"), inverseJoinColumns = @JoinColumn(name = "friendId"))
    private Set<User> friends;

    @ManyToMany
    @JoinTable(name = "friends", joinColumns = @JoinColumn(name = "friendId"), inverseJoinColumns = @JoinColumn(name = "id"))
    private Set<User> friendOf;

    public void addFriend(User user) {
	if (CollectionUtils.isEmpty(this.friends)) {
	    this.friends = Sets.newHashSet();
	}
	this.friends.add(user);
    }

}