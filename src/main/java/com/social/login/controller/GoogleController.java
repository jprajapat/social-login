package com.social.login.controller;

import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.google.api.plus.Person;
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
import com.social.login.service.GoogleService;

@Controller
public class GoogleController {

	@Autowired
	private GoogleService googleService;

	@Autowired
	private UserService userService;

	@Autowired
	private SecurityService securityService;

	@RequestMapping(value = "/googlelogin")
	public RedirectView googlelogin() {
		RedirectView redirectView = new RedirectView();
		String url = googleService.googlelogin();
		System.out.println(url);
		redirectView.setUrl(url);
		return redirectView;
	}

	@GetMapping(value = "/google")
	public String google(@RequestParam("code") String code) {
		String accessToken = googleService.getGoogleAccessToken(code);
		return "redirect:/googleprofiledata/" + accessToken;
	}

	@RequestMapping(value = "/googleprofiledata/{accessToken:.+}")
	public String googleprofiledata(@PathVariable String accessToken, Model model, HttpServletRequest request) {
		Person user = googleService.getGoogleUserProfile(accessToken);

		UserInfo dbUser = userService.findByEmail(user.getAccountEmail());
		String role = "USER";
		if (dbUser != null) {
			dbUser.setFirstName(user.getGivenName());
			dbUser.setLastName(user.getFamilyName());
			userService.update(dbUser);
			role = dbUser.getRole();
			model.addAttribute("user", dbUser);
		} else {
			UserInfo userinfo = new UserInfo(user.getGivenName(), user.getFamilyName());
			userinfo.setEmail(user.getAccountEmail());
			userinfo.setEnabled(true);
			userinfo.setRole("USER");
			userService.update(userinfo);
			model.addAttribute("user", userinfo);
		}

		securityService.autoLogin(user.getAccountEmail(), null, role, request);

		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		Collection<? extends GrantedAuthority> grantedAuthorities = SecurityContextHolder.getContext()
				.getAuthentication().getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = grantedAuthorities.iterator();
		while (iterator.hasNext()) {
			System.out.println(iterator.next());
		}
		System.out.println("name " + name);

		return "view/userprofile";
	}
}
