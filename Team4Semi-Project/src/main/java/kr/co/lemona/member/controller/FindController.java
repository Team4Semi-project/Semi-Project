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
    // 아이디 찾기
    @PostMapping("/findid")
    public Map<String, Object> findId(@RequestBody Map<String, String> params) {
        String name = params.get("name");
        String email = params.get("email");
        String foundId = memberService.findIdByNameAndEmail(params);

        Map<String, Object> result = new HashMap<>();
        result.put("foundId", foundId);
        return result;
    }
    // 비밀번호 찾기
    @PostMapping("/findpassword")
    public Map<String, Object> findPassword(@RequestBody Map<String, String> params) {
        String id = params.get("id");
        String name = params.get("name");
        String email = params.get("email");

        Map<String, Object> result = new HashMap<>();

        if (id == null || name == null || email == null) {
            result.put("success", false);
            result.put("message", "모든 정보를 입력해주세요.");
            return result;
        }

        String matchedId = memberService.findPassword(id, name, email); // 서비스에 맞게 수정

        if (matchedId != null) {
            result.put("success", true);
            result.put("redirectUrl", "/reset/findpw-update.html?userId=" + id);
        } else {
            result.put("success", false);
            result.put("message", "입력한 값이 일치하지 않습니다.");
        }

        return result;
    }
 
}