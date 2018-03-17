package com.agrawal.rajeshwar.service;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;

import com.agrawal.rajeshwar.api.model.FriendsEntity;
import com.agrawal.rajeshwar.api.model.FriendsListRequestEntity;
import com.agrawal.rajeshwar.api.model.FriendsListResponseEntity;
import com.agrawal.rajeshwar.api.model.GeneralResponseEntity;
import com.agrawal.rajeshwar.dao.UserRepository;
import com.agrawal.rajeshwar.dto.User;
import com.agrawal.rajeshwar.exceptions.InvalidUserException;
import com.agrawal.rajeshwar.service.impl.FriendsServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@RunWith(MockitoJUnitRunner.Silent.class)
@ContextConfiguration
public class FriendsServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FriendsServiceImpl friendsServiceImpl;

    private String validMail1 = "valid1@gmail.com";
    private String validMail2 = "valid2@gmail.com";
    private User validUser1, validUser2;
    List<String> friends;
    FriendsEntity friendEntity;
    GeneralResponseEntity generalResponseEntity;
    FriendsListRequestEntity friendListRequestEntity;
    FriendsListResponseEntity friendListResponseEntity;

    @Before
    public void setUp() throws Exception {
	this.validUser1 = User.builder().id(1).id(1).email(this.validMail1).build();
	Mockito.when(this.userRepository.findFirstByEmail(this.validMail1)).thenReturn(this.validUser1);
	Mockito.when(this.userRepository.save(this.validUser1)).thenReturn(this.validUser1);

	this.validUser2 = User.builder().id(2).email(this.validMail2).build();
	Mockito.when(this.userRepository.findFirstByEmail(this.validMail2)).thenReturn(this.validUser2);
	Mockito.when(this.userRepository.save(this.validUser2)).thenReturn(this.validUser2);

	Mockito.when(this.userService.validateEmailAndReturnUser(this.validMail1)).thenReturn(this.validUser1);
    }

    @Test
    public void testAddFriendsInvalidEntity() {
	GeneralResponseEntity entity = this.friendsServiceImpl.addFriends(null);
	Assert.assertFalse(entity.isSuccess());

	List<String> friends = Lists.newArrayList();
	FriendsEntity friendEntity = FriendsEntity.builder().friends(friends).build();
	entity = this.friendsServiceImpl.addFriends(friendEntity);
	Assert.assertFalse(entity.isSuccess());

	// 1 friend
	friends.add("1@gmail.com");
	entity = this.friendsServiceImpl.addFriends(friendEntity);
	Assert.assertFalse(entity.isSuccess());

	// more than 2 friends
	friends.add("2@gmail.com");
	friends.add("3@gmail.com");
	entity = this.friendsServiceImpl.addFriends(friendEntity);
	Assert.assertFalse(entity.isSuccess());

	// same 2 friends
	friends.clear();
	friends.add("1@gmail.com");
	friends.add("1@gmail.com");
	entity = this.friendsServiceImpl.addFriends(friendEntity);
	Assert.assertFalse(entity.isSuccess());

    }

    @Test
    public void testAddFriendsUserDoesNotExist() {

	// both new users
	User newvalidUser1 = User.builder().email(this.validMail1).build();
	Mockito.when(this.userRepository.save(newvalidUser1)).thenReturn(newvalidUser1);
	User newvalidUser2 = User.builder().email(this.validMail2).build();
	Mockito.when(this.userRepository.save(newvalidUser2)).thenReturn(newvalidUser2);

	Mockito.when(this.userRepository.findFirstByEmail(this.validMail1)).thenReturn(null);
	Mockito.when(this.userRepository.findFirstByEmail(this.validMail2)).thenReturn(null);
	List<String> friends = Lists.newArrayList(this.validMail1, this.validMail2);
	FriendsEntity friendEntity = FriendsEntity.builder().friends(friends).build();
	GeneralResponseEntity entity = this.friendsServiceImpl.addFriends(friendEntity);
	Assert.assertTrue(entity.isSuccess());

	// any one user is new
	Mockito.when(this.userRepository.findFirstByEmail(this.validMail1)).thenReturn(this.validUser1);
	entity = this.friendsServiceImpl.addFriends(friendEntity);
	Assert.assertTrue(entity.isSuccess());

	// both users exists
	Mockito.when(this.userRepository.findFirstByEmail(this.validMail1)).thenReturn(this.validUser1);
	Mockito.when(this.userRepository.findFirstByEmail(this.validMail2)).thenReturn(this.validUser2);
    }

    @Test
    public void testAddFriendsInvalidEmail() {
	Mockito.when(this.userRepository.findFirstByEmail(ArgumentMatchers.any(String.class)))
	       .thenReturn(this.validUser1);
	Mockito.when(this.userRepository.save(this.validUser1)).thenReturn(this.validUser1);
	String invalidMail = "aa..@.com";
	this.friends = Lists.newArrayList(invalidMail, this.validMail2);
	this.friendEntity = FriendsEntity.builder().friends(this.friends).build();
	this.generalResponseEntity = this.friendsServiceImpl.addFriends(this.friendEntity);
	Assert.assertFalse(this.generalResponseEntity.isSuccess());

	this.friends = Lists.newArrayList(this.validMail2, invalidMail);
	this.friendEntity = FriendsEntity.builder().friends(this.friends).build();
	this.generalResponseEntity = this.friendsServiceImpl.addFriends(this.friendEntity);
	Assert.assertFalse(this.generalResponseEntity.isSuccess());

    }

    @Test
    public void testAddFriendsAlreadyFriends() {
	User validUser1 = User.builder().id(1).email(this.validMail1).friends(Sets.newHashSet(this.validUser2)).build();
	Mockito.when(this.userRepository.findFirstByEmail(this.validMail1)).thenReturn(validUser1);
	this.friends = Lists.newArrayList(this.validMail1, this.validMail2);
	this.friendEntity = FriendsEntity.builder().friends(this.friends).build();
	this.generalResponseEntity = this.friendsServiceImpl.addFriends(this.friendEntity);
	Assert.assertFalse(this.generalResponseEntity.isSuccess());

    }

    @Test
    public void testAddFriendsAlreadyFriendOf() {
	User validUser1 = User.builder()
			      .id(1)
			      .email(this.validMail1)
			      .friendOf(Sets.newHashSet(this.validUser2))
			      .build();
	Mockito.when(this.userRepository.findFirstByEmail(this.validMail1)).thenReturn(validUser1);
	this.friends = Lists.newArrayList(this.validMail1, this.validMail2);
	this.friendEntity = FriendsEntity.builder().friends(this.friends).build();
	this.generalResponseEntity = this.friendsServiceImpl.addFriends(this.friendEntity);
	Assert.assertFalse(this.generalResponseEntity.isSuccess());

    }

    @Test
    public void testAddFriendsBlockedUser() {
	User validUser1 = User.builder()
			      .id(1)
			      .email(this.validMail1)
			      .hasBlockedUsers(Sets.newHashSet(this.validUser2))
			      .build();
	Mockito.when(this.userRepository.findFirstByEmail(this.validMail1)).thenReturn(validUser1);
	this.friends = Lists.newArrayList(this.validMail1, this.validMail2);
	this.friendEntity = FriendsEntity.builder().friends(this.friends).build();
	this.generalResponseEntity = this.friendsServiceImpl.addFriends(this.friendEntity);
	Assert.assertFalse(this.generalResponseEntity.isSuccess());

    }

    @Test
    public void testAddFriendsBlockedByUser() {
	User blockingUser2 = User.builder()
				 .id(2)
				 .email(this.validMail2)
				 .hasBlockedUsers(Sets.newHashSet(this.validUser1))
				 .build();
	Mockito.when(this.userRepository.findFirstByEmail(this.validMail2)).thenReturn(blockingUser2);
	this.friends = Lists.newArrayList(this.validMail1, this.validMail2);
	this.friendEntity = FriendsEntity.builder().friends(this.friends).build();
	this.generalResponseEntity = this.friendsServiceImpl.addFriends(this.friendEntity);
	Assert.assertFalse(this.generalResponseEntity.isSuccess());

    }

    @Test
    public void testAddFriendsValidFriendShip() {
	this.friends = Lists.newArrayList(this.validMail1, this.validMail2);
	this.friendEntity = FriendsEntity.builder().friends(this.friends).build();
	this.generalResponseEntity = this.friendsServiceImpl.addFriends(this.friendEntity);
	Assert.assertTrue(this.generalResponseEntity.isSuccess());

    }

    @Test
    public void testvalidUsersFriendsListInvalidEntityOrUser() throws InvalidUserException {
	this.friendListResponseEntity = this.friendsServiceImpl.getFriendsList(null);
	Assert.assertFalse(this.friendListResponseEntity.isSuccess());

	// user doesnt exist
	Mockito.when(this.userService.validateEmailAndReturnUser(ArgumentMatchers.any(String.class)))
	       .thenThrow(InvalidUserException.class);
	this.friendListRequestEntity = FriendsListRequestEntity.builder().email(this.validMail1).build();
	this.friendListResponseEntity = this.friendsServiceImpl.getFriendsList(this.friendListRequestEntity);
	Assert.assertFalse(this.friendListResponseEntity.isSuccess());

    }

    @Test
    public void testGetFriendsValidFriends() throws InvalidUserException {
	// no friends
	this.friendListRequestEntity = FriendsListRequestEntity.builder().email(this.validMail1).build();
	this.friendListResponseEntity = this.friendsServiceImpl.getFriendsList(this.friendListRequestEntity);
	Assert.assertTrue(this.friendListResponseEntity.isSuccess());
	Assert.assertEquals(this.friendListResponseEntity.getCount().longValue(), 0L);
	Assert.assertEquals(this.friendListResponseEntity.getFriends().size(),
		this.friendListResponseEntity.getCount().intValue());

	// one my friend
	this.friendListRequestEntity = FriendsListRequestEntity.builder().email(this.validMail1).build();
	User testUser = User.builder().id(1).email(this.validMail1).friendOf(Sets.newHashSet(this.validUser2)).build();
	Mockito.when(this.userService.validateEmailAndReturnUser(this.validMail1)).thenReturn(testUser);
	this.friendListResponseEntity = this.friendsServiceImpl.getFriendsList(this.friendListRequestEntity);
	Assert.assertTrue(this.friendListResponseEntity.isSuccess());
	Assert.assertEquals(this.friendListResponseEntity.getCount().longValue(), 1L);
	Assert.assertEquals(this.friendListResponseEntity.getFriends().size(),
		this.friendListResponseEntity.getCount().intValue());
	Assert.assertTrue(this.friendListResponseEntity.getFriends().contains(this.validMail2));

	// I am his friend
	testUser = User.builder().id(1).email(this.validMail1).friends(Sets.newHashSet(this.validUser2)).build();
	Mockito.when(this.userService.validateEmailAndReturnUser(this.validMail1)).thenReturn(testUser);
	this.friendListResponseEntity = this.friendsServiceImpl.getFriendsList(this.friendListRequestEntity);
	Assert.assertTrue(this.friendListResponseEntity.isSuccess());
	Assert.assertEquals(this.friendListResponseEntity.getCount().longValue(), 1L);
	Assert.assertEquals(this.friendListResponseEntity.getFriends().size(),
		this.friendListResponseEntity.getCount().intValue());
	Assert.assertTrue(this.friendListResponseEntity.getFriends().contains(this.validMail2));

	// 1 friend in both friend and friendOf
	String validMail3 = "valid3@gmail.com";
	User validUser3 = User.builder().email(validMail3).id(3).build();
	testUser = User.builder()
		       .id(1)
		       .email(this.validMail1)
		       .friends(Sets.newHashSet(this.validUser2))
		       .friendOf(Sets.newHashSet(validUser3))
		       .build();
	Mockito.when(this.userService.validateEmailAndReturnUser(this.validMail1)).thenReturn(testUser);
	this.friendListResponseEntity = this.friendsServiceImpl.getFriendsList(this.friendListRequestEntity);
	Assert.assertTrue(this.friendListResponseEntity.isSuccess());
	Assert.assertEquals(this.friendListResponseEntity.getCount().longValue(), 2L);
	Assert.assertEquals(this.friendListResponseEntity.getFriends().size(),
		this.friendListResponseEntity.getCount().intValue());
	Assert.assertTrue(this.friendListResponseEntity.getFriends().contains(this.validMail2));
	Assert.assertTrue(this.friendListResponseEntity.getFriends().contains(validMail3));

    }

    @Test
    public void testGetCommonFriendsInvalidEntity() {
	this.friendListResponseEntity = this.friendsServiceImpl.getCommonFriends(null);
	Assert.assertFalse(this.friendListResponseEntity.isSuccess());

	// empty friends
	this.friends = Lists.newArrayList();
	this.friendEntity = FriendsEntity.builder().friends(this.friends).build();
	this.friendListResponseEntity = this.friendsServiceImpl.getCommonFriends(this.friendEntity);
	Assert.assertFalse(this.friendListResponseEntity.isSuccess());

	// 1 friend
	this.friends.add("1@gmail.com");
	this.friendEntity = FriendsEntity.builder().friends(this.friends).build();
	this.friendListResponseEntity = this.friendsServiceImpl.getCommonFriends(this.friendEntity);
	Assert.assertFalse(this.friendListResponseEntity.isSuccess());

	// more than 2 friends
	this.friends.add("2@gmail.com");
	this.friends.add("3@gmail.com");
	this.friendEntity = FriendsEntity.builder().friends(this.friends).build();
	this.friendListResponseEntity = this.friendsServiceImpl.getCommonFriends(this.friendEntity);
	Assert.assertFalse(this.friendListResponseEntity.isSuccess());

	// same 2 friends
	this.friends.clear();
	this.friends.add(this.validMail1);
	this.friends.add(this.validMail1);
	this.friendEntity = FriendsEntity.builder().friends(this.friends).build();
	this.friendListResponseEntity = this.friendsServiceImpl.getCommonFriends(this.friendEntity);
	Assert.assertFalse(this.friendListResponseEntity.isSuccess());
    }

    @Test
    public void testGetCommonFriendsCombinationCommonFriends() throws InvalidUserException {
	final String[] validMails = { "0@m.com", "1@m.com", "2@m.com", "3@m.com" };
	List<User> validUsers = Lists.newArrayList();
	IntStream.range(0, validMails.length).forEach(i -> {
	    User user = User.builder().id(i).email(validMails[i]).build();
	    validUsers.add(user);
	    try {
		Mockito.when(this.userService.validateEmailAndReturnUser(validMails[i])).thenReturn(user);
	    } catch (InvalidUserException e) {
		Assert.fail();
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	});

	// test uncommon friends
	this.friends = Lists.newArrayList(this.validMail1, this.validMail2);
	this.friendEntity = FriendsEntity.builder().friends(this.friends).build();
	User user1, user2;
	user1 = User.builder().id(1).email(this.validMail1).friends(Sets.newHashSet(validUsers.get(0))).build();
	user2 = User.builder().id(2).email(this.validMail2).friends(Sets.newHashSet(validUsers.get(1))).build();

	Mockito.when(this.userService.validateEmailAndReturnUser(this.validMail1)).thenReturn(user1);
	Mockito.when(this.userService.validateEmailAndReturnUser(this.validMail2)).thenReturn(user2);

	this.friendListResponseEntity = this.friendsServiceImpl.getCommonFriends(this.friendEntity);
	Assert.assertTrue(this.friendListResponseEntity.isSuccess());
	Assert.assertEquals(this.friendListResponseEntity.getCount().longValue(), 0L);
	Assert.assertEquals(this.friendListResponseEntity.getCount().intValue(),
		this.friendListResponseEntity.getFriends().size());

	// uncommon friend friendOf
	user1 = User.builder().id(1).email(this.validMail1).friends(Sets.newHashSet(validUsers.get(0))).build();
	user2 = User.builder().id(2).email(this.validMail2).friendOf(Sets.newHashSet(validUsers.get(1))).build();
	Mockito.when(this.userService.validateEmailAndReturnUser(this.validMail1)).thenReturn(user1);
	Mockito.when(this.userService.validateEmailAndReturnUser(this.validMail2)).thenReturn(user2);
	this.friendListResponseEntity = this.friendsServiceImpl.getCommonFriends(this.friendEntity);
	Assert.assertTrue(this.friendListResponseEntity.isSuccess());
	Assert.assertEquals(this.friendListResponseEntity.getCount().longValue(), 0L);
	Assert.assertEquals(this.friendListResponseEntity.getCount().intValue(),
		this.friendListResponseEntity.getFriends().size());

	// common friend friend
	user1 = User.builder().id(1).email(this.validMail1).friends(Sets.newHashSet(validUsers.get(0))).build();
	user2 = User.builder().id(2).email(this.validMail2).friends(Sets.newHashSet(validUsers.get(0))).build();
	Mockito.when(this.userService.validateEmailAndReturnUser(this.validMail1)).thenReturn(user1);
	Mockito.when(this.userService.validateEmailAndReturnUser(this.validMail2)).thenReturn(user2);
	this.friendListResponseEntity = this.friendsServiceImpl.getCommonFriends(this.friendEntity);
	Assert.assertTrue(this.friendListResponseEntity.isSuccess());
	Assert.assertEquals(this.friendListResponseEntity.getCount().longValue(), 1L);
	Assert.assertEquals(this.friendListResponseEntity.getCount().intValue(),
		this.friendListResponseEntity.getFriends().size());
	Assert.assertTrue(this.friendListResponseEntity.getFriends().contains(validMails[0]));

	// common friend friendOf
	user1 = User.builder().id(1).email(this.validMail1).friends(Sets.newHashSet(validUsers.get(0))).build();
	user2 = User.builder().id(2).email(this.validMail2).friendOf(Sets.newHashSet(validUsers.get(0))).build();
	Mockito.when(this.userService.validateEmailAndReturnUser(this.validMail1)).thenReturn(user1);
	Mockito.when(this.userService.validateEmailAndReturnUser(this.validMail2)).thenReturn(user2);
	this.friendListResponseEntity = this.friendsServiceImpl.getCommonFriends(this.friendEntity);
	Assert.assertTrue(this.friendListResponseEntity.isSuccess());
	Assert.assertEquals(this.friendListResponseEntity.getCount().longValue(), 1L);
	Assert.assertEquals(this.friendListResponseEntity.getCount().intValue(),
		this.friendListResponseEntity.getFriends().size());
	Assert.assertTrue(this.friendListResponseEntity.getFriends().contains(validMails[0]));

	// cross
	user1 = User.builder()
		    .id(1)
		    .email(this.validMail1)
		    .friends(Sets.newHashSet(validUsers.get(0)))
		    .friendOf(Sets.newHashSet(validUsers.get(1)))
		    .build();
	user2 = User.builder()
		    .id(2)
		    .email(this.validMail2)
		    .friends(Sets.newHashSet(validUsers.get(1)))
		    .friendOf(Sets.newHashSet(validUsers.get(0)))
		    .build();
	Mockito.when(this.userService.validateEmailAndReturnUser(this.validMail1)).thenReturn(user1);
	Mockito.when(this.userService.validateEmailAndReturnUser(this.validMail2)).thenReturn(user2);
	this.friendListResponseEntity = this.friendsServiceImpl.getCommonFriends(this.friendEntity);
	Assert.assertTrue(this.friendListResponseEntity.isSuccess());
	Assert.assertEquals(this.friendListResponseEntity.getCount().longValue(), 2L);
	Assert.assertEquals(this.friendListResponseEntity.getCount().intValue(),
		this.friendListResponseEntity.getFriends().size());
	Assert.assertTrue(this.friendListResponseEntity.getFriends().contains(validMails[0]));
	Assert.assertTrue(this.friendListResponseEntity.getFriends().contains(validMails[1]));

	user1 = User.builder()
		    .id(1)
		    .email(this.validMail1)
		    .friends(Sets.newHashSet(validUsers.get(0), validUsers.get(2)))
		    .friendOf(Sets.newHashSet(validUsers.get(1)))
		    .build();
	user2 = User.builder()
		    .id(2)
		    .email(this.validMail2)
		    .friends(Sets.newHashSet(validUsers.get(1)))
		    .friendOf(Sets.newHashSet(validUsers.get(0), validUsers.get(3)))
		    .build();
	Mockito.when(this.userService.validateEmailAndReturnUser(this.validMail1)).thenReturn(user1);
	Mockito.when(this.userService.validateEmailAndReturnUser(this.validMail2)).thenReturn(user2);
	this.friendListResponseEntity = this.friendsServiceImpl.getCommonFriends(this.friendEntity);
	Assert.assertTrue(this.friendListResponseEntity.isSuccess());
	Assert.assertEquals(this.friendListResponseEntity.getCount().longValue(), 2L);
	Assert.assertEquals(this.friendListResponseEntity.getCount().intValue(),
		this.friendListResponseEntity.getFriends().size());
	Assert.assertTrue(this.friendListResponseEntity.getFriends().contains(validMails[0]));
	Assert.assertTrue(this.friendListResponseEntity.getFriends().contains(validMails[1]));
	Assert.assertFalse(this.friendListResponseEntity.getFriends().contains(validMails[2]));
	Assert.assertFalse(this.friendListResponseEntity.getFriends().contains(validMails[3]));

    }

}
