package kr.co.lemona.board.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 자유/공지사항 게시글의 정보를 담는 DTO 클래스
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {

	private int boardNo;			// 게시글 번호 (PK)
	private String boardTitle; 		// 게시글 제목
	private String boardContent; 	// 게시글 내용
	private int readCount; 			// 조회수
	private String boardDelFl; 		// 삭제여부 ('N' or 'Y')
	private String boardCreateDate; // 작성일
	private String boardUpdateDate; // 수정일
	private int boardCode; 			// 게시판 코드 (FK)
	
	// BOARD+DEFAULT_COMMENT+DEFAULT_BOARD_LIKE
	// 각 목록 조회 결과 저장용 변수
	private int commentCount;		// 댓글 수
	private int likeCount;			// 좋아요 수
	
	// 작성자 정보
	private int memberNo; 			// 작성자 회원번호 (FK)
	// BOARD+MEMBER
	private String memberNickname;	// 작성자 닉네임
	private String profileImg;		// 작성자 프로필 이미지
	
	private String thumbnail;		// 썸내일 - summernote 최상단 이미지
	
	private List<DefaultComment> commentList;
}