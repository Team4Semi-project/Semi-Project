package kr.co.lemona.recipeBoard.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.lemona.recipeBoard.model.dto.RecipeComment;
import kr.co.lemona.recipeBoard.model.mapper.RecipeCommentMapper;

@Service
@Transactional(rollbackFor = Exception.class)
@PropertySource("classpath:/config.properties")
public class RecipeCommentServiceImpl implements RecipeCommentService {

	@Autowired
	private RecipeCommentMapper mapper;

	/**
	 * 댓글 수 조회 서비스
	 * 
	 * @author 민장
	 */
	@Override
	public int count(int boardNo) {
		return mapper.count(boardNo);
	}

	/**
	 * 댓글 목록 조회 서비스
	 * 
	 * @author 민장
	 */
	@Override
	public List<RecipeComment> select(int boardNo, int memberNo) {
	    List<RecipeComment> list = mapper.select(boardNo);

	    for (RecipeComment comment : list) {
	        int likeCount = mapper.countLike(comment.getCommentNo());
	        comment.setLikeCount(likeCount);

	        // 로그인한 사용자가 있는 경우에만 likeCheck 설정
	        if (memberNo != 0) {
	    		Map<String, Object> map = new HashMap<>();
	    		map.put("commentNo", comment.getCommentNo());
	    		map.put("memberNo", memberNo);
	            int likeCheck = mapper.checkLike(map);
	            comment.setLikeCheck(likeCheck > 0 ? 1 : 0);
	        }
	    }

	    return list;
	}

	/**
	 * 댓글/답글 등록 서비스
	 * 
	 * @param comment
	 * @return
	 * @author 민장
	 */
	@Override
	public int insert(RecipeComment comment) {
		return mapper.insert(comment);
	}

	/**
	 * 댓글/답글 삭제 서비스
	 * 
	 * @author 민장
	 */
	@Override
	public int delete(int commentNo) {
		return mapper.delete(commentNo);
	}

	/**
	 * 댓글 수정 서비스
	 * 
	 * @param comment
	 * @return
	 * @author 민장
	 */
	@Override
	public int update(RecipeComment comment) {
		return mapper.update(comment);
	}

	/**
	 * 댓글 좋아요 서비스
	 *
	 */
	@Override
	public int like(int commentNo, int memberNo) {
		Map<String, Object> map = new HashMap<>();
		map.put("commentNo", commentNo);
		map.put("memberNo", memberNo);

		int result = 0;

		if (mapper.checkLike(map) > 0) {
			result = mapper.deleteLike(map); // 좋아요 취소됨
		} else {
			result = mapper.insertLike(map); // 좋아요 추가됨
		}

		if(result > 0) {
			// 좋아요 개수 갱신 반환
			return mapper.countLike(commentNo);
		}
		return -1;
	}
}
