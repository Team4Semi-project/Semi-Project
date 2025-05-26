package kr.co.lemona.member.model.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.lemona.member.model.dto.Member;

@Mapper
public interface MemberMapper {
   

	/**
	 * 로그인 SQL 실행
	 * @param memberEmail
	 * @return
	 */
	Member login(String memberId);

	/**
	 * 이메일 중복검사
	 * @param memberEmail
	 * @return
	 */
	int checkEmail(String memberEmail);

	/**
	 * 닉네임 중복검사
	 * @param memberNickname
	 * @return
	 */
	int checkNickname(String memberNickname);
	
	/** 아이디 체크
	 * @param memberId
	 * @return
	 */
	int checkId(String memberId); // 추가

	/**
	 * 회원 가입
	 * @param inputMember
	 * @return
	 */
	int register(Member inputMember);

	/** 아이디 찾기
	 * @param params
	 * @return
	 */
	String findIdByNameAndEmail(Map<String, String> params);



}
