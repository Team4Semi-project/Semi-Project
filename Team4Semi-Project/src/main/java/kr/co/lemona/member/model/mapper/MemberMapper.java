package kr.co.lemona.member.model.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.lemona.member.model.dto.Member;

@Mapper
public interface MemberMapper {
   

	/** 로그인 SQL 실행
	 * @param memberEmail
	 * @return
	 */
	Member login(String memberId);

	/** 이메일 중복검사
	 * @param memberEmail
	 * @return
	 */
	int checkEmail(String memberEmail);

	/** 닉네임 중복검사
	 * @param memberNickname
	 * @return
	 */
	int checkNickname(String memberNickname);
	
	/** 아이디 중복 체크
	 * @param memberId
	 * @return
	 */
	int checkId(String memberId);

	/** 회원 가입
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
	 * Map 으로 받아서 params 로 받아야 함
	 */
	Map<String, String> findUserByIdNameEmail(Member member);


	/** 비밀번호 재설정
	 * @param params
	 * @return
	 */
	int updatePassword(Map<String, Object> params);

	/** 회원정보 삭제 메서드
	 * @return
	 */
	int deleteMembers();


}
