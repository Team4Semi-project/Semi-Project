package kr.co.lemona.member.model.service;

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

	/**
	 * 회원 가입 서비스
	 * @param inputMember
	 * @param memberAddress
	 * @return
	 */
	int register(Member inputMember);

}
