package kr.co.lemona.board.model.service;

import java.util.Map;

import kr.co.lemona.board.model.dto.Board;

public interface DefaultBoardService {

	/** 게시글 리스트 조회
	 * @param cp
	 * @return
	 * @author 민장
	 * @param sort 
	 */
	Map<String, Object> selectBoardList(Map<String, Object> inputMap);
	
	/** 게시글 상세 조회 서비스
	 * @param map
	 * @return
	 * @author 민장
	 */
	Map<String, Object> selectOne(Map<String, Integer> map);

	/** 해당 게시판 검색 조회 서비스
	 * @param paramMap
	 * @param cp
	 * @return
	 * @author jihyun
	 */
	Map<String, Object> serchList(Map<String, Object> paramMap, int cp);

	/** 게시글 조회수 증가
	 * @param boardNo
	 * @return
	 */
	int updateReadCount(int boardNo);
	
	/** 좋아요 기능
	 * @return
	 * @author 재호
	 */
	int updateLikeCount(Map<String, Integer> map);

}
