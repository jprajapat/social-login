package com.social.login.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {
	static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	public static String getEncoderPassword(String password) {
		return passwordEncoder.encode(password);
	}

}
