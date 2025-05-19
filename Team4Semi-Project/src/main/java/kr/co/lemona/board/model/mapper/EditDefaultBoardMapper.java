package kr.co.lemona.board.model.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.lemona.board.model.dto.Board;

@Mapper
public interface EditDefaultBoardMapper {

	/** 게시글 작성
	 * @param inputBoard
	 * @return
	 */
	int defaultBoardInsert(Board inputBoard);

	/** 게시글 삭제
	 * @param map
	 * @return
	 */
	int defaultBoardDelete(Map<String, Integer> map);

}
