package com.social.login.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.linkedin.api.LinkedIn;
import org.springframework.social.linkedin.api.LinkedInProfileFull;
import org.springframework.social.linkedin.api.impl.LinkedInTemplate;
import org.springframework.social.linkedin.connect.LinkedInConnectionFactory;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Service;

@Service
public class LinkedinServiceImpl implements LinkedinService {

	@Value("${spring.social.linkedin.app-id}")
	private String linkedinId;
	@Value("${spring.social.linkedin.app-secret}")
	private String linkedinSecret;

	private LinkedInConnectionFactory createLinkedInConnection() {
		return new LinkedInConnectionFactory(linkedinId, linkedinSecret);
	}

	@Override
	public String linkedinlogin() {
		OAuth2Parameters parameters = new OAuth2Parameters();
		parameters.setRedirectUri("http://localhost:8085/linkedin");
		parameters.setScope("r_basicprofile,r_emailaddress");
		return createLinkedInConnection().getOAuthOperations().buildAuthenticateUrl(parameters);
	}

	@Override
	public String getLinkedInAccessToken(String code) {
		return createLinkedInConnection().getOAuthOperations()
				.exchangeForAccess(code, "http://localhost:8085/linkedin", null).getAccessToken();
	}

	@Override
	public LinkedInProfileFull getLinkedInUserProfile(String accessToken) {
		LinkedIn linkedIn = new LinkedInTemplate(accessToken);
		LinkedInProfileFull linkedInProfileFull = linkedIn.profileOperations().getUserProfileFull();
		return linkedInProfileFull;
	}

}
