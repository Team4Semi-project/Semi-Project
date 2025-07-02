package kr.co.lemona.recipeBoard.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//레시피 게시글에 달린 댓글의 정보를 담는 DTO 클래스
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeComment {
	
    private int commentNo;			 // 댓글 번호(PK)
    private String commentContent;	 // 댓글 내용
    private String commentWriteDate; // 댓글 작성일
    private String commentDelFl;	 // 댓글 삭제 여부
    private int boardNo;			 // 댓글이 달린 게시글 번호(FK)
    private int parentCommentNo;	 // 댓글 부모댓글 번호(자기참조)
    private int commentCount; 		 // 댓글 수

	// 작성자 정보
	private int memberNo;			 // 댓글 작성자 번호
	// BOARD+MEMBER
	private String memberNickname;   // 댓글 작성자 닉네임
	private String profileImg;		 // 댓글 작성자 프로필 이미지
	
	// DEFAULT_COMMENT(+)DEFAULT_COMMENT_LIKE
	private int likeCount;			 // 댓글 좋아요 수
	// 좋아요 Y/N == 1/0
	private int likeCheck;			 // 댓글 좋아요 여부
    
}
