package com.social.login.controller;

import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.social.login.model.UserInfo;
import com.social.login.securityservice.SecurityService;
import com.social.login.securityservice.UserService;

@Controller
public class RegistrationController {

	@Autowired
	private UserService userService;

	@Autowired
	private SecurityService securityService;

	@PostMapping(value = "/register")
	public String registration(@ModelAttribute UserInfo userInfo, HttpServletRequest request, Model model) {
		String password = userInfo.getPassword();
		UserInfo dbUser = userService.save(userInfo);
		securityService.autoLogin(dbUser.getEmail(), password, dbUser.getRole(), request);
		model.addAttribute("user", dbUser);

		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		Collection<? extends GrantedAuthority> grantedAuthorities = SecurityContextHolder.getContext()
				.getAuthentication().getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = grantedAuthorities.iterator();
		while (iterator.hasNext()) {
			System.out.println(iterator.next());
		}
		System.out.println("name " + email);

		return "view/userprofile";
	}

}
