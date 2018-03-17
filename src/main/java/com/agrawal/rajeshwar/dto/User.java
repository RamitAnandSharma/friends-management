package com.agrawal.rajeshwar.dto;

import java.io.Serializable;
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

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class User implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 542041268714856030L;

    @Override
    public String toString() {
	StringBuilder s = new StringBuilder("User [email=" + this.email + "] friends are "
		+ Optional.ofNullable(this.friends)
			  .orElse(Sets.newHashSet())
			  .stream()
			  .filter(Objects::nonNull)
			  .map(User::getEmail)
			  .collect(Collectors.toList()));
	s.append("\n isFollowingUsers are " + Optional.ofNullable(this.isFollowingUsers)
						      .orElse(Sets.newHashSet())
						      .stream()
						      .filter(Objects::nonNull)
						      .map(User::getEmail)
						      .collect(Collectors.toList()));

	s.append("\n isFollowedByUsers are " + Optional.ofNullable(this.isFollowedByUsers)
						       .orElse(Sets.newHashSet())
						       .stream()
						       .filter(Objects::nonNull)
						       .map(User::getEmail)
						       .collect(Collectors.toList()));

	s.append("\n hasBlockedUsers are " + Optional.ofNullable(this.hasBlockedUsers)
						     .orElse(Sets.newHashSet())
						     .stream()
						     .filter(Objects::nonNull)
						     .map(User::getEmail)
						     .collect(Collectors.toList()));

	s.append("\n isBlockedByUsers are " + Optional.ofNullable(this.isBlockedByUsers)
						      .orElse(Sets.newHashSet())
						      .stream()
						      .filter(Objects::nonNull)
						      .map(User::getEmail)
						      .collect(Collectors.toList()));
	return s.toString();
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

    @ManyToMany
    @JoinTable(name = "subscriber", joinColumns = @JoinColumn(name = "id"), inverseJoinColumns = @JoinColumn(name = "targetUserId"))
    private Set<User> isFollowingUsers;

    @ManyToMany
    @JoinTable(name = "subscriber", joinColumns = @JoinColumn(name = "targetUserId"), inverseJoinColumns = @JoinColumn(name = "id"))
    private Set<User> isFollowedByUsers;

    @ManyToMany
    @JoinTable(name = "blockers", joinColumns = @JoinColumn(name = "id"), inverseJoinColumns = @JoinColumn(name = "targetUserId"))
    private Set<User> hasBlockedUsers;

    @ManyToMany
    @JoinTable(name = "blockers", joinColumns = @JoinColumn(name = "targetUserId"), inverseJoinColumns = @JoinColumn(name = "id"))
    private Set<User> isBlockedByUsers;

    public void addFriend(User user) {
	if (CollectionUtils.isEmpty(this.friends)) {
	    this.friends = Sets.newHashSet();
	}
	this.friends.add(user);
    }

    public void followAnotherUser(User user) {
	if (CollectionUtils.isEmpty(this.isFollowingUsers)) {
	    this.isFollowingUsers = Sets.newHashSet();
	}
	this.isFollowingUsers.add(user);
    }

    public void hasBlockedTheUser(User user) {
	if (CollectionUtils.isEmpty(this.hasBlockedUsers)) {
	    this.hasBlockedUsers = Sets.newHashSet();
	}
	this.hasBlockedUsers.add(user);
    }

    public Set<User> getAllFriends() {
	// get all friends
	Set<User> friends = Optional.ofNullable(this.getFriends()).orElse(Sets.newHashSet());
	// get friends on other side
	friends.addAll(Optional.ofNullable(this.getFriendOf()).orElse(Sets.newHashSet()));
	return friends;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + this.id;
	return result;
    }

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
	User other = (User) obj;
	if (this.id != other.id) {
	    return false;
	}
	return true;
    }

}