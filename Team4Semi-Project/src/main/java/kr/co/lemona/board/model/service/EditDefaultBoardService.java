package kr.co.lemona.board.model.service;

import java.util.Map;

import kr.co.lemona.board.model.dto.Board;

public interface EditDefaultBoardService {

	/** 게시글 작성 서비스
	 * @param inputBoard
	 * @return
	 * @throws Exception
	 * @author 민장
	 */
	int defaultBoardInsert(Board inputBoard) throws Exception;

	/** 게시글 삭제 서비스
	 * @param map
	 * @return
	 * @author 민장
	 */
	int boardDelete(Map<String, Integer> map);

}
