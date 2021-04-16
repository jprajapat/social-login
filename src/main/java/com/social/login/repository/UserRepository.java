package com.social.login.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.social.login.model.UserInfo;

@Repository
public interface UserRepository extends CrudRepository<UserInfo, Long> {

	public UserInfo findByEmailAndEnabled(String email, boolean enabled);

	public UserInfo findByEmail(String email);

}
