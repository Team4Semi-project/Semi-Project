package kr.co.lemona.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

	@GetMapping("/login")
	public String loginPage() {
		return "login/login"; // login.html을 찾습니다
	}

	@GetMapping("/register")
	public String  goRegister() {
		return "register/register";	
	}
}
