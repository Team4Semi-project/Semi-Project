package kr.co.lemona.member.model.service;

import java.util.Map;

import kr.co.lemona.member.model.dto.Member;

public interface MemberService {
	
	/**
	 * 로그인 서비스
	 * @param inputMember
	 * @return loginMember(Member)
	 */
	Member login(Member inputMember);

	/**
	 * 이메일 중복검사 서비스
	 * @param memberEmail
	 * @return
	 */
	int checkEmail(String memberEmail);

	/**
	 * 닉네임 중복검사 서비스
	 * @param memberNickname
	 * @return
	 */	
	int checkNickname(String memberNickname);
	int checkId(String memberId); // 추가

	/**
	 * 회원 가입 서비스
	 * @param inputMember
	 * @return
	 */
	int register(Member inputMember);

	/** 아이디 찾기
	 * @param params
	 * @return
	 */
	String findIdByNameAndEmail(Map<String, String> params);

	/** 비밀번호 찾기
	 * @param member
	 * @return
	 */
	Map<String, String> findUserByIdNameEmail(Member member);
	
	/** 비밀번호 재설정
	 * @param memberId
	 * @param newPassword
	 * @return
	 */
	 boolean updatePassword(String memberId, String newPassword);

}
