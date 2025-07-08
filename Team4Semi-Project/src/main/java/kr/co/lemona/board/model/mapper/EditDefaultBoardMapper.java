package kr.co.lemona.board.model.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.lemona.board.model.dto.Board;

@Mapper
public interface EditDefaultBoardMapper {

	/** 게시글 작성
	 * @param inputBoard
	 * @return
	 * @author 민장
	 */
	int defaultBoardInsert(Board inputBoard);

	/** 게시글 삭제
	 * @param map
	 * @return
	 * @author 민장
	 */
	int defaultBoardDelete(Map<String, Integer> map);

	/** 게시글 수정
	 * @param inputBoard
	 * @return
	 */
	int defaultBoardUpdate(Board board);

}
