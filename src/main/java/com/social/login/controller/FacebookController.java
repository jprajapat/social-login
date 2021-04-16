package com.social.login.controller;

import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.facebook.api.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import com.social.login.model.UserInfo;
import com.social.login.securityservice.SecurityService;
import com.social.login.securityservice.UserService;
import com.social.login.service.FacebookService;

@Controller
public class FacebookController {

	@Autowired
	private FacebookService facebookService;

	@Autowired
	private UserService userService;

	@Autowired
	private SecurityService securityService;

	@RequestMapping(value = "/facebooklogin")
	public RedirectView facebooklogin() {
		RedirectView redirectView = new RedirectView();
		String url = facebookService.facebooklogin();
		System.out.println("url " + url);
		redirectView.setUrl(url);
		return redirectView;
	}

	@GetMapping(value = "/facebook")
	public String facebook(@RequestParam("code") String code) {
		String accessToken = facebookService.getFacebookAccessToken(code);
		return "redirect:/facebookprofiledata/" + accessToken;
	}

	@RequestMapping(value = "/facebookprofiledata/{accessToken:.+}")
	public String facebookprofiledata(@PathVariable String accessToken, Model model, HttpServletRequest request) {
		User user = facebookService.getFacebookUserProfile(accessToken);

		UserInfo dbUser = userService.findByEmail(user.getEmail());
		String role = "USER";
		if (dbUser != null) {
			dbUser.setFirstName(user.getFirstName());
			dbUser.setLastName(user.getLastName());
			dbUser.setEmail(user.getEmail());
			userService.update(dbUser);
			role = dbUser.getRole();
			model.addAttribute("user", dbUser);
		} else {
			UserInfo userinfo = new UserInfo(user.getFirstName(), user.getLastName());
			userinfo.setEmail(user.getEmail());
			userinfo.setEnabled(true);
			userinfo.setRole("USER");
			userService.update(userinfo);
			model.addAttribute("user", userinfo);
		}

		securityService.autoLogin(user.getEmail(), null, role, request);

		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		Collection<? extends GrantedAuthority> grantedAuthorities = SecurityContextHolder.getContext()
				.getAuthentication().getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = grantedAuthorities.iterator();
		while(iterator.hasNext()) {
			System.out.println(iterator.next());
		}
		System.out.println("email "+email);
		

		return "view/userprofile";
	}

}
