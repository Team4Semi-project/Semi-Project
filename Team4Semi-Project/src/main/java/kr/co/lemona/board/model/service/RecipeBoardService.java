package kr.co.lemona.board.model.service;

import java.util.Map;

public interface RecipeBoardService {

	/** 레시피 게시판/카테고리 별 페이지 요청 서비스
	 * @param categoryNo
	 * @param cp
	 * @return
	 * @author 재호
	 */
	Map<String, Object> selectRecipeBoardList(int categoryNo, int cp);

}
