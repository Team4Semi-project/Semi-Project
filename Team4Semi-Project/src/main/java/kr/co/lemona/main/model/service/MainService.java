package kr.co.lemona.main.model.service;

import java.util.Map;

public interface MainService {

	/** 메인페이지에서 최근 레시피 조회하기
	 * @return
	 * @author jihyun
	 */
	Map<String, Object> mainRecipeBoardList();

}
