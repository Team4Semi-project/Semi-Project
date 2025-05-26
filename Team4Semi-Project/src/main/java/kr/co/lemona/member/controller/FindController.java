package kr.co.lemona.member.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.co.lemona.member.model.service.MemberService;

@RestController
public class FindController {
	
    @Autowired
    private MemberService memberService;
    
    /**
     * @param params
     * @return
     * Map으로 받기 떄문에 params 하나만 받아야 한다.
     */ 
    @PostMapping("/findid")
    public Map<String, Object> findId(@RequestBody Map<String, String> params) {
        String name = params.get("name");
        String email = params.get("email");
        String foundId = memberService.findIdByNameAndEmail(params);

        Map<String, Object> result = new HashMap<>();
        result.put("foundId", foundId);
        return result;
    }
    
 
}