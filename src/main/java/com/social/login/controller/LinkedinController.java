package com.social.login.controller;

import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.linkedin.api.LinkedInProfileFull;
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
import com.social.login.service.LinkedinService;

@Controller
public class LinkedinController {
	@Autowired
	private LinkedinService linkedinService;
	@Autowired
	private UserService userService;

	@Autowired
	private SecurityService securityService;

	@RequestMapping(value = "/linkedinlogin")
	public RedirectView linkedinlogin() {
		RedirectView redirectView = new RedirectView();
		String url = linkedinService.linkedinlogin();
		System.out.println(url);
		redirectView.setUrl(url);
		return redirectView;
	}

	@GetMapping(value = "/linkedin")
	public String facebook(@RequestParam("code") String code) {
		String accessToken = linkedinService.getLinkedInAccessToken(code);
		return "redirect:/linkedinprofiledata/" + accessToken;
	}

	@RequestMapping(value = "/linkedinprofiledata/{accessToken:.+}")
	public String linkedinprofiledata(@PathVariable String accessToken, Model model, HttpServletRequest request) {
		LinkedInProfileFull user = linkedinService.getLinkedInUserProfile(accessToken);

		UserInfo dbUser = userService.findByEmail(user.getEmailAddress());
		String role = "USER";
		if (dbUser != null) {
			dbUser.setFirstName(user.getFirstName());
			dbUser.setLastName(user.getLastName());
			userService.update(dbUser);
			role = dbUser.getRole();
			model.addAttribute("user", dbUser);
		} else {
			UserInfo userinfo = new UserInfo(user.getFirstName(), user.getLastName());
			userinfo.setEmail(user.getEmailAddress());
			userinfo.setEnabled(true);
			userinfo.setRole("USER");
			userService.update(userinfo);
			model.addAttribute("user", userinfo);
		}

		securityService.autoLogin(user.getEmailAddress(), null, role, request);

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
