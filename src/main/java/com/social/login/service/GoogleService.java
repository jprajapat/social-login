package com.social.login.service;

import org.springframework.social.google.api.plus.Person;
import org.springframework.stereotype.Service;

@Service
public interface GoogleService {

	public String googlelogin();

	public String getGoogleAccessToken(String code);

	public Person getGoogleUserProfile(String accessToken);


	

}
