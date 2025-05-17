package kr.co.lemona.board.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.lemona.board.model.dto.Board;
import kr.co.lemona.board.model.mapper.EditDefaultBoardMapper;

@Service
@Transactional(rollbackFor = Exception.class)
@PropertySource("classpath:/config.properties")
public class EditDefaultBoardServiceImpl implements EditDefaultBoardService {

	@Autowired
	private EditDefaultBoardMapper mapper;

	@Override
	public int defaultBoardInsert(Board inputBoard) throws Exception {

		int result = mapper.defaultBoardInsert(inputBoard);

		// 삽입 실패 시
		if (result == 0) {
			return 0;
		}

		// 삽입 성공 시
		// 삽입된 게시글의 번호를 변수로 저장 후 리턴
		int boardNo = inputBoard.getBoardNo();
		return boardNo;
	}

}
