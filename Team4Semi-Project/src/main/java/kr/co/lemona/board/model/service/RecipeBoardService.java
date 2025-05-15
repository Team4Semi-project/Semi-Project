package kr.co.lemona.board.model.service;

import java.util.Map;

public interface RecipeBoardService {

	/** 레시피 게시판 목록 조회
	 * @param categoryNo : 카테고리 넘버
	 * @param cp : 현재 페이지
	 * @return
	 */
	Map<String, Object> selectRecipeBoardList(int categoryNo, int cp);

}
