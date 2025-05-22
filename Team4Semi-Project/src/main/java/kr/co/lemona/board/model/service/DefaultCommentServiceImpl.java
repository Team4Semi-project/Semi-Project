package kr.co.lemona.board.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.lemona.board.model.dto.DefaultComment;
import kr.co.lemona.board.model.mapper.DefaultCommentMapper;

@Service
@Transactional(rollbackFor = Exception.class)
@PropertySource("classpath:/config.properties")
public class DefaultCommentServiceImpl implements DefaultCommentService{
	
	@Autowired
	private DefaultCommentMapper mapper;
	
	/** 댓글 수 조회 서비스
	 *	@author 민장
	 */
	@Override
	public int count(int boardNo) {
		return mapper.count(boardNo);
	}
	
	/** 댓글 목록 조회 서비스
	 * @author 민장
	 */
	@Override
	public List<DefaultComment> select(int boardNo) {
		return mapper.select(boardNo);
	}

	/** 댓글/답글 등록 서비스
	 * @param comment
	 * @return
	 * @author 민장
	 */
	@Override
	public int insert(DefaultComment comment) {
		return mapper.insert(comment);
	}

	/** 댓글/답글 삭제 서비스
	 * @author 민장
	 */
	@Override
	public int delete(int commentNo) {
		return mapper.delete(commentNo);
	}

	/** 댓글 수정 서비스
	 * @param comment
	 * @return
	 * @author 민장
	 */
	@Override
	public int update(DefaultComment comment) {
		return mapper.update(comment);
	}
}
