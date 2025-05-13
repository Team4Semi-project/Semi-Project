package kr.co.lemona.board.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeBoard {

	private int boardNo; 			// 게시글 번호 (PK)
	private String boardTitle; 		// 게시글 제목
	private int readCount; 			// 조회수
	private String boardDelFl; 		// 삭제여부 ('N' or 'Y')
	private String boardCreateDate; // 작성일
	private String boardUpdateDate; // 수정일
	private int memberNo; 			// 작성자 회원번호 (FK)
	private int boardCode; 			// 게시판 코드 (FK)
	private int categoryNo; 		// 카테고리 번호
}