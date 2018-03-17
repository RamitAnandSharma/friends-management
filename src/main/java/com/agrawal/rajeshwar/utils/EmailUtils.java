package com.agrawal.rajeshwar.utils;

import java.util.Optional;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang3.StringUtils;

import com.agrawal.rajeshwar.exceptions.InvalidUserException;

public class EmailUtils {
    public static boolean isValidMail(String str) {
	boolean result = true;
	try {
	    InternetAddress emailAddr = new InternetAddress(str);
	    emailAddr.validate();
	} catch (AddressException ex) {
	    result = false;
	}
	return result;
    }

    public static String sanitizeEmail(String email) {
	return Optional.ofNullable(email).orElse("").trim().toLowerCase();
    }

    public static void validateEmailOrThrowException(String email) throws InvalidUserException {
	if (StringUtils.isEmpty(email)) {
	    throw new InvalidUserException("User Email cannot be null");
	}
	if (!EmailUtils.isValidMail(email)) {
	    throw new InvalidUserException("Invalid Email " + email);
	}
    }
}
