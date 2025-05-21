package kr.co.lemona.main.model.service;

import java.util.Map;

public interface MainService {

	/** 전체 게시판 통합 검색하기
	 * @param paramMap
	 * @return
	 */
	Map<String, Object> AllsearchList(Map<String, Object> paramMap, int cp);

	Map<String, Object> selectRecipeBoardList(int categoryNo, int cp);

	Map<String, Object> selectPopularBoardList(int cp);

}
