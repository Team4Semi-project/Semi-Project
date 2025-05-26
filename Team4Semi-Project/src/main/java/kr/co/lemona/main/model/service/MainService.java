package kr.co.lemona.main.model.service;

import java.util.Map;

public interface MainService {
	
	/** 메인화면에 출력될 최근 인기 게시글 4개 조회 서비스
	 * @param cp
	 * @return
	 * @author jihyun
	 */
	Map<String, Object> selectPopularBoardList(int cp);

	/** 메인화면에 출력될 최근 레시피 게시글 4개 조회 서비스
	 * @param categoryNo
	 * @param cp
	 * @return
	 * @author jihyun
	 */
	Map<String, Object> selectRecipeBoardList(int categoryNo, int cp);

	/** 전체 게시글 통합 검색 서비스
	 * @param paramMap
	 * @param cp
	 * @return
	 * @author jihyun
	 */
	Map<String, Object> AllsearchList(Map<String, Object> paramMap, int cp, String sort);
}
