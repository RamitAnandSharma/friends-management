package com.agrawal.rajeshwar.utils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class EmailValidator {
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
}
