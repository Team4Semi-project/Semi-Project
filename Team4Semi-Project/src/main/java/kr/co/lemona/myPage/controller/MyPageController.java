package kr.co.lemona.myPage.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import kr.co.lemona.board.model.dto.Board;
import kr.co.lemona.member.model.dto.Member;
import kr.co.lemona.myPage.model.service.MyPageService;
import kr.co.lemona.recipeBoard.model.dto.RecipeBoard;
import lombok.extern.slf4j.Slf4j;

@SessionAttributes({ "loginMember" })
@Controller
@RequestMapping("mypage")
@Slf4j
public class MyPageController {

    @Autowired
    private MyPageService service;

    // 프로필 수정 페이지 이동
    @GetMapping("editProfile")
    public String editProfilePage(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember != null) {
            model.addAttribute("member", loginMember);
            return "myPage/editProfile";
        } else {
            return "redirect:/login";
        }
    }

    // 비밀번호 변경 화면 이동
    @GetMapping("changePw")
    public String changePw() {
        return "myPage/mypage-changePw";
    }

    // 회원 탈퇴 화면 이동
    @GetMapping("secession")
    public String secession() {
        return "myPage/mypage-secession";
    }

    // 사용자 정보 및 게시글 조회
    @GetMapping("userProfile")
    public String selectMemberInfo(@SessionAttribute("loginMember") Member loginMember,
            @RequestParam(value = "type", required = false, defaultValue = "recipe") String type,
            @RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
            Model model, RedirectAttributes ra) {

        Map<String, Object> inputMap = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>();
        String path = null;
        String message = null;
        int memberNo = 0;

        if (loginMember == null) {
            message = "로그인 멤버 없으면 나중에 처리할게용.";
            path = "redirect:/";
        } else {
            memberNo = loginMember.getMemberNo();
        }

        inputMap.put("memberNo", memberNo);
        inputMap.put("cp", cp);

        if (type.equals("recipe")) {
            resultMap = service.selectMemberInfo(inputMap);
        } else {
            resultMap = service.selectMemberBoardList(inputMap);
        }

        if (resultMap == null) {
            message = "사용자 조회 에러발생. 관리자에게 문의해주세요.";
            path = "redirect:/";
        } else {
            Member member = (Member) resultMap.get("member");
            log.info("조회가 되긴해~~~~~~~~~~~~~~~~~~");
            model.addAttribute("member", member);

            if (type.equals("recipe")) {
                List<RecipeBoard> recipeBoardList = (List<RecipeBoard>) resultMap.get("recipeBoardList");
                model.addAttribute("boardList", recipeBoardList);
                model.addAttribute("pagination", resultMap.get("pagination"));
            } else {
                List<Board> boardList = (List<Board>) resultMap.get("boardList");
                model.addAttribute("boardList", boardList);
                model.addAttribute("pagination", resultMap.get("pagination"));
            }
        }
        path = "mypage/myPage-userProfile";
        ra.addFlashAttribute("message", message);
        return path;
    }

    // 회원 정보 수정
    @PostMapping("info")
    public String updateInfo(Member inputMember, @SessionAttribute("loginMember") Member loginMember,
            RedirectAttributes ra) {

        inputMember.setMemberNo(loginMember.getMemberNo());
        int result = service.updateInfo(inputMember);

        String message = null;

        if (result > 0) {
            loginMember.setMemberNickname(inputMember.getMemberNickname());
            message = "회원 정보 수정 성공!!";
        } else {
            message = "회원 정보 수정 실패..";
        }

        ra.addFlashAttribute("message", message);
        return "redirect:info";
    }

    // 프로필 이미지 및 회원 정보 업데이트
    @PostMapping("editProfile")
    public String updateProfile(
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam(value = "deleteImage", required = false) String deleteImage,
            @RequestParam("memberName") String memberName,
            @RequestParam("memberNickname") String memberNickname,
            @SessionAttribute("loginMember") Member loginMember,
            RedirectAttributes ra) throws IOException {

        Member member = loginMember;
        String profileImagePath = member.getProfileImg();

        if ("true".equals(deleteImage)) {
            profileImagePath = "/images/default-profile.png"; // 기본 이미지로 변경
        } else if (profileImage != null && !profileImage.isEmpty()) {
            String uploadDir = "src/main/resources/static/images/profiles/";
            String fileName = UUID.randomUUID().toString() + "_" + profileImage.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, profileImage.getBytes());
            profileImagePath = "/images/profiles/" + fileName;
        }

        member.setMemberName(memberName);
        member.setMemberNickname(memberNickname);
        if (profileImagePath != null) {
            member.setProfileImg(profileImagePath);
        }

        // DB 업데이트 (서비스 호출 필요)
        int result = service.updateProfile(member); // service 메서드 추가 필요

        String message = result > 0 ? "프로필이 성공적으로 수정되었습니다." : "프로필 수정에 실패했습니다.";
        ra.addFlashAttribute("message", message);
        return "redirect:/mypage/editProfile";
    }
}