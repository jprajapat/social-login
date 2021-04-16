package com.social.login.securityservice;

import javax.servlet.http.HttpServletRequest;

public interface SecurityService {

	public void autoLogin(String email, String password, String role, HttpServletRequest request);

}
