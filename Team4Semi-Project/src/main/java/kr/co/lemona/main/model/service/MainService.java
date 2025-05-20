package kr.co.lemona.main.model.service;

import java.util.Map;

public interface MainService {

	/** 메인페이지에서 최근 레시피 조회하기
	 * @return
	 * @author jihyun
	 */
	Map<String, Object> mainRecipeBoardList(int cp);

	/** 전체 게시판 통합 검색하기
	 * @param paramMap
	 * @return
	 */
	Map<String, Object> AllsearchList(Map<String, Object> paramMap, int cp);

	Map<String, Object> selectRecipeBoardList(int categoryNo, int cp);

	Map<String, Object> selectPopularBoardList(int cp);

}
