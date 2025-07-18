package kr.co.lemona.member.model.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import kr.co.lemona.member.model.dto.Member;
import kr.co.lemona.member.model.mapper.MemberMapper;
import lombok.extern.slf4j.Slf4j;

@Transactional(rollbackFor = Exception.class)
@Service // 비즈니스로직 처리 역할 명시 + Bean 등록
@Slf4j
public class MemberServiceImpl implements MemberService {

	// 등록된 Bean 중에서 MemberMapper와 같은 타입 or 상속관계인 Bean을 찾아
	@Autowired // 의존성 주입(DI)
	private MemberMapper mapper;

	// Bcrypt 암호화 객체 의존성 주입(DI) - SecurityConfig 참고
	@Autowired
	private BCryptPasswordEncoder bcrypt;

	// 로그인 서비스
	@Override
	public Member login(Member inputMember) {

		// 암호화 진행

		// bcrypt.encode(문자열) : 문자열을 암호화하여 반환
		String bcryptPassword = bcrypt.encode(inputMember.getMemberPw());
		log.debug("bcryptPassword : " + bcryptPassword);

		// bcrypt.matches(평문, 암호화): 평문과 암호화가 일치하면 true, 아니면 false 반환

		// 1. 아이디가 일치하면서 탈퇴하지 않은 회원 조회
		Member loginMember = mapper.login(inputMember.getMemberId());

		// 2. 만약에 일치하는 아이디 없어서 조회 결과가 null 인 경우
		if (loginMember == null)
			return null;

		// 3. 입력받은 비밀번호(평문 :inputMember.getMemberPw()) 와
		// 암호화된 비밀번호(loginMember.getMemberPw())
		// 두 비밀번호가 일치하는지 확인 (bcrypt.matches(평문, 암호화))
		// 일치하지 않으면
		log.debug("loginMember : {} ", loginMember);

		if (!bcrypt.matches(inputMember.getMemberPw(), loginMember.getMemberPw())) {
			log.debug("????");

			return null;
		}

		// 로그인 결과에서 비밀번호 제거
		loginMember.setMemberPw(null);

		return loginMember;
	}

	// 이메일 중복 검사 서비스
	@Override
	public int checkEmail(String memberEmail) {
		return mapper.checkEmail(memberEmail);
	}

	// 닉네임 중복 검사 서비스
	@Override
	public int checkNickname(String memberNickname) {
		return mapper.checkNickname(memberNickname);
	}

	// 회원가입 서비스
	@Override
	public int register(Member inputMember) {

		// 비밀번호 암호화 진행

		// inputMember 안의 memberPw -> 평문
		// 비밀번호를 암호화하여 inputMember 세팅
		String encPw = bcrypt.encode(inputMember.getMemberPw());
		inputMember.setMemberPw(encPw);

		// 회원 가입 mapper 메서드 호출
		return mapper.register(inputMember);

	}

	// 아이디 중복 체크 검사
	@Override
	public int checkId(String memberId) {
		return mapper.checkId(memberId);
	}

	// 아이디 찾기
	@Override
	public String findIdByNameAndEmail(Map<String, String> params) {
		log.debug("입력 name: " + params.get("name"));
		log.debug("입력 email: " + params.get("email"));

		String result = mapper.findIdByNameAndEmail(params);

		log.debug("찾은 ID: " + result);

		return result;
	}

	// 비밀번호 찾기
	@Override
	public Map<String, String> findUserByIdNameEmail(Member member) {
		return mapper.findUserByIdNameEmail(member);
	}

	// 비밀번호 재설정
	@Override
	public int updatePassword(String memberId, String memberPw) {
		Map<String, Object> map = new HashMap<>();
		map.put("memberId", memberId);
		map.put("memberPw", memberPw);
		return mapper.updatePassword(map);
	}
	
	// 회원 정보 삭제
	@Override
	public int deleteMembers() {
		return mapper.deleteMembers();
	}

}
