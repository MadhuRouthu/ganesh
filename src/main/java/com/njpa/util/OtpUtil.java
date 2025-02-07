package com.njpa.util;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;
@Component
public class OtpUtil {

	public String generateOtp() {
	    SecureRandom random = new SecureRandom();
	    int randomNumber = random.nextInt(999999);
	    String output = Integer.toString(randomNumber);

	    while (output.length() < 6) {
	      output = "0" + output;
	    }
	    return output;
	  }
}
