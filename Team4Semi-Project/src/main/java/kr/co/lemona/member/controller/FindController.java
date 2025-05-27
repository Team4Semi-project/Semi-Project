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
    private MemberService memberService; // MemberService에 아이디 찾기 메서드 추가해야 함

    @PostMapping("/findId")
    public Map<String, Object> findId(@RequestBody Map<String, String> paramMap) {
        String name = paramMap.get("name");
        String email = paramMap.get("email");

        String foundId = memberService.findIdByNameAndEmail(paramMap);

        Map<String, Object> result = new HashMap<>();
        result.put("foundId", foundId);

        return result;
    }
}
