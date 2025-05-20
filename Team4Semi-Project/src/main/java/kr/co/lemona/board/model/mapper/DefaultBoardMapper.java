package kr.co.lemona.board.model.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.lemona.board.model.dto.Board;

@Mapper
public interface DefaultBoardMapper {

	/** 게시글 상세 조회
	 * @param map
	 * @return
	 */
	Board selectOne(Map<String, Integer> map);

}
