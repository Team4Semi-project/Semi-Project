package kr.co.lemona.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("footer")
public class FooterController {
	
	@RequestMapping("private")
	public String PrivatePage() {

		return "common/footer-private";
	}
	
	@RequestMapping("center")
	public String CenterPage() {
		
		return "common/footer-center";
	}
	
	@RequestMapping("use")
	public String UsePage() {
		
		return "common/footer-use";
	}
	
	@RequestMapping("sns")
	public String SNSPage() {
		
		return "common/footer-sns";
	}

}
