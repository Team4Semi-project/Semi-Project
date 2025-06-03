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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

	// 프로필 이미지 변경 화면 이동
	@GetMapping("profile") // /myPage/profile GET 요청 매핑
	public String profile() {
		return "myPage/myPage-profile";
	}

	// 비밀번호 변경 화면 이동
	@GetMapping("changePw") // /myPage/changePw GET 요청 매핑
	public String changePw() {
		return "myPage/myPage-changePw";
	}

	// 회원 탈퇴 화면 이동
	@GetMapping("secession") // /myPage/secession GET 요청 매핑
	public String secession() {
		return "myPage/myPage-secession";
	}

	/**
	 * 회원 탈퇴
	 * 
	 */
	@PostMapping("secession")
	public String secession(@SessionAttribute("loginMember") Member loginMember,
			@RequestParam("memberPw") String memberPw, RedirectAttributes ra, SessionStatus status) {

		// 로그인한 회원의 회원번호 꺼내기
		int memberNo = loginMember.getMemberNo();

		// 서비스 호출 (입력받은 비밀번호, 로그인한 회원번호)
		int result = service.secession(memberPw, memberNo);

		String message = null;
		String path = null;

		if (result > 0) {
			message = "탈퇴 성공";
			path = "/";
			status.setComplete();

		} else {
			message = "비밀번호 불일치";
			path = "secession";
		}

		ra.addFlashAttribute("message", message);

		return "redirect:" + path;

	}

	/**
	 * 사용자 정보 조회 및 해당 사용자가 쓴 레시피/게시글 조회
	 * 
	 * @param loginMember
	 * @param cp
	 * @param model
	 * @param ra
	 * @return
	 * @author miae
	 */
	@GetMapping("userProfile")
	public String selectMemberInfo(@RequestParam("memberNickname") String memberNickname,
			@RequestParam(value = "type", required = false, defaultValue = "recipe") String type,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp, Model model,
			RedirectAttributes ra, @SessionAttribute(value = "loginMember", required = false) Member loginMember) {

		Map<String, Object> inputMap = new HashMap<>();
		Map<String, Object> recipeMap = new HashMap<>();
		Map<String, Object> defaultMap = new HashMap<>();
		String path = null;
		String message = null;

		inputMap.put("memberNickname", memberNickname);

		inputMap.put("cp", cp);
		if (loginMember != null)
			inputMap.put("memberNo", loginMember.getMemberNo());

		// 사용자 정보, 사용자가 쓴 레시피 게시글
		recipeMap = service.selectMemberInfo(inputMap);

		// 사용자 정보, 사용자가 쓴 게시글
		defaultMap = service.selectMemberBoardList(inputMap);

		if (recipeMap == null || defaultMap == null) {
			// 결과가 null 일 경우 메시지 보내기
			message = "사용자 조회 에러발생. 관리자에게 문의해주세요.";
			path = "redirect:/";
		} else {

			// map 에서 값 꺼내오기
			Member member = (Member) recipeMap.get("member");

			log.info("조회가 되긴해~~~~~~~~~~~~~~~~~~");
			model.addAttribute("member", member);

			// 레시피 게시판 글 model에 담아서 보냄
			List<RecipeBoard> recipeBoardList = (List<RecipeBoard>) recipeMap.get("recipeBoardList");
			int recipeListCount = (int) recipeMap.get("listCount");
			int recipeCommentCount = (int) recipeMap.get("recipeCommentCount");
			model.addAttribute("recipeBoardList", recipeBoardList);
			model.addAttribute("recipePagination", recipeMap.get("pagination"));

			// 일반 게시판 글 model에 담아서 보냄
			List<Board> boardList = (List<Board>) defaultMap.get("boardList");
			int defaultListCount = (int) defaultMap.get("listCount");
			int commentCount = (int) defaultMap.get("commentCount");
			model.addAttribute("boardList", boardList);
			model.addAttribute("defaultPagination", defaultMap.get("pagination"));

			model.addAttribute("writtenCount", recipeListCount + defaultListCount);
			model.addAttribute("commentCount", recipeCommentCount + commentCount);
			model.addAttribute("type", type);

		}
		path = "mypage/myPage-userProfile";
		ra.addFlashAttribute("message", message);
		return path;
	}

	/**
	 * 회원 정보 수정
	 * 
	 * @param inputMember   : 커맨드 객체(@ModelAttribute가 생략된 상태) 제출된 수정된 회원 닉네임, 전화번호,
	 *                      주소
	 * @param loginMember   : 로그인한 회원 정보 (회원 번호 사용할 예정)
	 * @param memberAddress : 주소만 따로 받은 String[] 구분자 ^^^ 변경 예정
	 * @param ra            :
	 * @return
	 */
	@PostMapping("info")
	public String updateInfo(Member inputMember, @SessionAttribute("loginMember") Member loginMember,
			RedirectAttributes ra) {

		// inputMember에 로그인한 회원 번호 추가
		inputMember.setMemberNo(loginMember.getMemberNo());
		// inputMember : 회원 번호, 회원 닉네임, 전화번호, 주소

		// 회원 정보 수정 서비스 호출
		int result = service.updateInfo(inputMember);

		String message = null;

		if (result > 0) { // 회원 정보 수정 성공

			// loginMember 새로 세팅
			// 우리가 방금 바꾼 값으로 세팅

			// loginMember는 세션에 저장된 로그인한 회원 정보가
			// 저장된 객체를 참조하고있다!

			// -> loginMember를 수정하면
			// 세션에 저장된 로그인한 회원 정보가 수정된다
			// == 세션 데이터와 DB 데이터를 동기화

			loginMember.setMemberNickname(inputMember.getMemberNickname());

			message = "회원 정보 수정 성공!!";

		} else {

			message = "회원 정보 수정 실패..";
		}

		ra.addFlashAttribute("message", message);

		return "redirect:info";
	}

	@PostMapping("uploadProfileImage")
	public String profile(@RequestParam("profileImg") MultipartFile profileImg,
			@SessionAttribute("loginMember") Member loginMember, RedirectAttributes ra) throws Exception {

		// 업로드된 파일 정보를 DB에 INSERT 후 결과 행의 갯수 반환 받을 예정
		int result = service.profile(profileImg, loginMember);

		String message = null;

		if (result > 0)
			message = "변경 성공!";
		else
			message = "변경 실패";

		ra.addFlashAttribute("message", message);

		return "redirect:profile";
	}


	
	@PostMapping("/editProfile")
	@ResponseBody
	public Map<String, Object> updateProfile(
	        @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
	        @RequestParam(value = "deleteImage", required = false) String deleteImage,
	        Member member,
	        @SessionAttribute("loginMember") Member loginMember) throws IOException {

	    member.setMemberNo(loginMember.getMemberNo());

	    // 기존 이미지 경로 유지
	    String profileImagePath = loginMember.getProfileImg();

	    if ("true".equals(deleteImage)) {
	        // 기본 이미지로 변경
	        profileImagePath = "/images/default-profile.png";
	    } else if (profileImage != null && !profileImage.isEmpty()) {
	        // 저장 경로와 웹 접근 경로
	        String folderPath = "C:/lemonaFiles/profile/";
	        String webPath = "/myPage/profile/";

	        // 파일명 변경
	        String fileName = fileRename(profileImage.getOriginalFilename());

	        // 저장 경로에 파일 저장
	        Path filePath = Paths.get(folderPath, fileName);
	        Files.createDirectories(filePath.getParent());
	        Files.write(filePath, profileImage.getBytes());

	        // DB에 저장할 웹 접근 경로 설정
	        profileImagePath = webPath + fileName;
	    }

	    if (profileImagePath != null) {
	        member.setProfileImg(profileImagePath);
	    }

	    // DB 업데이트
	    int result = service.updateProfile(member);

	    // 세션 동기화
	    if (result > 0) {
	        loginMember.setMemberName(member.getMemberName());
	        loginMember.setMemberNickname(member.getMemberNickname());
	        loginMember.setProfileImg(profileImagePath);
	    }

	    // 응답 구성
	    Map<String, Object> response = new HashMap<>();
	    response.put("success", result > 0);
	    response.put("message", result > 0 ? "변경 성공!" : "중복된 닉네임입니다!");
	    response.put("length", member.getMemberNickname().length() > 10 ? "10자 내로 설정해주세요!" : "변경 성공!");

	    return response;
	}


	private String fileRename(String originalFilename) {
		// TODO Auto-generated method stub
		return null;
	}

	// editProfile 페이지 보여주기
	@GetMapping("/editProfile")
	public String showEditProfilePage(@SessionAttribute("loginMember") Member loginMember, Model model) {
		log.info("loginMember: " + loginMember);

		// 번호로 사용자 정보 조회
		Member member = service.selectMember(loginMember.getMemberNo());

		model.addAttribute("member", member);
		return "mypage/editProfile";
	}

	

	// 프로필 이미지 삭제
	@PostMapping("removeProfileImage")
	@ResponseBody
	public String removeProfileImage(@SessionAttribute("loginMember") Member loginMember) {
		int memberNo = loginMember.getMemberNo();
		int result = service.removeProfileImage(memberNo);

		if (result > 0) {
			String defaultProfileImg = "/images/user.png";
			loginMember.setProfileImg(defaultProfileImg); // 세션 동기화
			// loginMember.setProfileImg("/images/profiles/" + fileName); // 세션 동기화
			// ra.addFlashAttribute("message", "이미지 업로드 성공!");

			return "이미지 삭제 성공!";
		} else {
			// ra.addFlashAttribute("message", "이미지 업로드 실패");
			return "이미지 삭제 실패";
		}

	}

	/**
	 * 비밀번호 변경
	 * 
	 * @param paramMap    : 모든 파라미터(요청 데이터)를 맵으로 저장
	 * @param loginMember : 세션에 등록된 현재 로그인한 회원 정보
	 * @param ra
	 * @return
	 * @author jihyun
	 */
	@PostMapping("changePw") // /myPage/changePw POST 요청 매핑
	public String changePw(@RequestParam Map<String, String> paramMap,
			@SessionAttribute("loginMember") Member loginMember, RedirectAttributes ra) {
		// paramMap = {currentPw=asd123, newPw=pass02!, newPwConfirm=pass02!}
		// debug.log("paramMap : " + paramMap);

		// 로그인한 회원 번호
		int memberNo = loginMember.getMemberNo();

		// 현재 + 새 비번 + 회원번호를 서비스로 전달
		int result = service.changePw(paramMap, memberNo);

		String path = null;
		String message = null;

		if (result > 0) {
			// 변경 성공 시
			message = "비밀번호가 변경되었습니다!";
			path = "/";

		} else {
			// 변경 실패 시
			message = "현재 비밀번호가 일치하지 않습니다.";
			path = "/myPage/changePw";
		}

		ra.addFlashAttribute("message", message);

		return "redirect:" + path;
	}
}