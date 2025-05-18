package kr.co.lemona.board.model.service;

import kr.co.lemona.board.model.dto.Board;

public interface EditDefaultBoardService {

	/** 게시글 작성 서비스
	 * @param inputBoard
	 * @return
	 * @throws Exception
	 */
	int defaultBoardInsert(Board inputBoard) throws Exception;

}
