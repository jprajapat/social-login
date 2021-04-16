package com.social.login.securityservice;

import com.social.login.model.UserInfo;

public interface UserService {

	public UserInfo save(UserInfo userInfo);

	public UserInfo findByEmail(String email);

	public void update(UserInfo dbUser);

}
