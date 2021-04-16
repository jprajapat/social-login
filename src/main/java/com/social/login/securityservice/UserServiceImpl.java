package com.social.login.securityservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.social.login.model.UserInfo;
import com.social.login.repository.UserRepository;
import com.social.login.util.PasswordUtil;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserInfo save(UserInfo userInfo) {
		userInfo.setEnabled(true);
		userInfo.setRole("ADMIN");
		if(StringUtils.hasText(userInfo.getPassword())) {
			userInfo.setPassword(PasswordUtil.getEncoderPassword(userInfo.getPassword()));
		}
		return userRepository.save(userInfo);
	}

	@Override
	public UserInfo findByEmail(String email) {
		
		return userRepository.findByEmail(email);
	}

	@Override
	public void update(UserInfo dbUser) {
		userRepository.save(dbUser);
	}

}
