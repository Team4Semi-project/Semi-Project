package kr.co.lemona.member.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 회원 정보를 담는 DTO 클래스
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

	private int memberNo; 		   // 회원번호(PK)
	private String memberId;	   // 회원 아이디
	private String memberName;	   // 회원 이름
	private String memberNickname; // 회원 닉네임
	private String memberEmail;	   // 회원 이메일
	private String memberPw; 	   // 회원 비밀번호
	private String profileImg; 	   // 프로필 이미지 경로
	private String enrollDate; 	   // 가입일
	private String memberDelFl;    // 회원탈퇴여부 ('N' 또는 'Y')
}