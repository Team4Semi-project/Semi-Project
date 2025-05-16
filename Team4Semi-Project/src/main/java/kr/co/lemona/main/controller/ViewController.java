package kr.co.lemona.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

	@GetMapping("/register")
	public String goRegister() {
		return "register/register"; // templates/register/register.html
	}

	@GetMapping("/login")
	public String loginPage() {

		return "login/login"; // templates/login/login.html 반환
	}

	@GetMapping("/findid")
	public String findIdPage() {
	    return "findid/find-id";
	}

	@GetMapping("/findpw")
	public String findPasswordPage() {
	    return "findpw/find-password";
	}
}
