package com.agrawal.rajeshwar.service;

import java.util.List;

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

import com.agrawal.rajeshwar.dao.UserRepository;
import com.agrawal.rajeshwar.dto.User;
import com.agrawal.rajeshwar.exceptions.InvalidUserException;
import com.agrawal.rajeshwar.service.impl.UserServiceImpl;
import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.Silent.class)
@ContextConfiguration
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User mockValidUser;

    private List<String> invalidMailList = Lists.newArrayList("plainaddress", "#@%^%#$@#$@#.com", "@example.com",
	    "Joe Smith <email@example.com>", "email.example.com", "email@example@example.com", ".email@example.com",
	    "email.@example.com", "email..email@example.com", "email@example.com (Joe Smith)", "email@-example.com",
	    "email@example.web", "email@111.222.333.44444", "email@example..com", "Abc..123@example.com");

    @Before
    public void setUp() throws Exception {
	this.mockValidUser = User.builder().email("valid@valid.com").build();
	Mockito.when(this.userRepository.findFirstByEmail(ArgumentMatchers.any(String.class)))
	       .thenReturn(this.mockValidUser);
    }

    @Test
    public void testNullMail() {
	try {
	    this.userService.validateEmailAndReturnUser(null);
	    this.userService.validateEmailAndReturnUser("");
	    Assert.fail("should return exception");
	} catch (InvalidUserException e) {
	    Assert.assertTrue(true);
	} catch (Exception e) {
	    Assert.fail(e.getMessage());
	}

    }

    @Test
    public void testNoUserFound() {
	Mockito.when(this.userRepository.findFirstByEmail(ArgumentMatchers.any(String.class))).thenReturn(null);
	try {
	    User user = this.userService.validateEmailAndReturnUser(" abc@raj.com" + "" + "  \n");
	    Assert.fail();
	} catch (InvalidUserException e) {
	    Assert.assertTrue(true);
	} catch (Exception e) {
	    Assert.fail(e.getMessage());
	}

    }

    @Test
    public void testUnSanitizedMail() {
	try {
	    User user = this.userService.validateEmailAndReturnUser(" abc@raj.com" + "" + "  \n");
	    Assert.assertEquals(user, this.mockValidUser);
	} catch (Exception e) {
	    Assert.fail();
	}

    }

    @Test
    public void testInvalidMail() {
	this.invalidMailList.forEach(mail -> {
	    try {
		this.userService.validateEmailAndReturnUser(mail);
		Assert.fail("failed for invalide mail" + mail);
	    } catch (InvalidUserException e) {
		Assert.assertTrue(true);
	    } catch (Exception e) {
		Assert.fail();
	    }
	});

    }

}
