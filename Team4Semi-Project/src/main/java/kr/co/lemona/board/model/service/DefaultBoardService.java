package kr.co.lemona.board.model.service;

import java.util.Map;

import kr.co.lemona.board.model.dto.Board;

public interface DefaultBoardService {

	/** 게시글 상세 조회 서비스
	 * @param map
	 * @return
	 */
	Board selectOne(Map<String, Integer> map);

}
