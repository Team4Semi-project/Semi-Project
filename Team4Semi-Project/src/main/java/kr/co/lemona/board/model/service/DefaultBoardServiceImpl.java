package kr.co.lemona.board.model.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.lemona.board.model.dto.Board;
import kr.co.lemona.board.model.mapper.DefaultBoardMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class DefaultBoardServiceImpl implements DefaultBoardService{

	@Autowired
	private DefaultBoardMapper mapper;
	
	/** 게시글 상세 조회
	 *
	 */
	@Override
	public Board selectOne(Map<String, Integer> map) {
		// 
		return mapper.selectOne(map);
	}

}
