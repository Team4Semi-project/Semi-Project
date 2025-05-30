package kr.co.lemona.myPage.model.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.co.lemona.board.model.dto.Board;
import kr.co.lemona.board.model.dto.Pagination;
import kr.co.lemona.member.model.dto.Member;
import kr.co.lemona.myPage.model.mapper.MyPageMapper;
import kr.co.lemona.recipeBoard.model.dto.RecipeBoard;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@PropertySource("classpath:/config.properties")
@Slf4j
public class MyPageServiceImpl implements MyPageService {

	@Autowired
	private MyPageMapper mapper;

	// Bcrypt 암호화 객체 의존성 주입(SecurityConfig 참고)
	@Autowired
	private BCryptPasswordEncoder bcrypt;

	@Value("${my.profile.web-path}")
	private String profileWebPath; // /myPage/profile/

	@Value("${my.profile.folder-path}")
	private String profileFolderPath; // C:/uploadFiles/profile/


	// 비밀번호 변경 서비스
	@Override
	public int changePw(Map<String, String> paramMap, int memberNo) {

		// 1. 현재 비밀번호가 일치하는지 확인하기
		// - 현재 로그인한 회원의 암호화된 비밀번호를 DB에서 조회
		String originPw = mapper.selectPw(memberNo);

		// 입력받은 현재 비밀번호와(평문)
		// DB에서 조회한 비밀번호(암호화)를 비교
		// -> bcrypt.matches(평문, 암호화비번) 사용

		// 다를 경우
		if (!bcrypt.matches(paramMap.get("currentPw"), originPw)) {
			return 0;
		}

		// 2. 같을 경우

		// 새 비밀번호를 암호화 (bcrypt.encode(평문))
		String encPw = bcrypt.encode(paramMap.get("newPw"));

		// DB에 업데이트
		// SQL 전달 해야하는 데이터 2개 (암호화한 새 비번 encPw , 회원번호 memberNo)
		// -> mapper에 전달할수 있는 전달인자는 단 1개!
		// -> 묶어서 전달 (paramMap 재활용)

		paramMap.put("encPw", encPw);
		paramMap.put("memberNo", memberNo + "");

		return mapper.changePw(paramMap);
	}

	// 회원 탈퇴 서비스
	@Override
	public int secession(String memberPw, int memberNo) {

		// 현재 로그인한 회원의 암호화된 비밀번호 DB 조회
		String originPw = mapper.selectPw(memberNo);

		// 다를 경우
		if (!bcrypt.matches(memberPw, originPw)) {
			return 0;
		}

		// 같은 경우
		return mapper.secession(memberNo);
	}

	
	@Override
	public int updateInfo(Member inputMember) {
		// TODO Auto-generated method stub
		return mapper.updateInfo(inputMember);
	}
	
	@Override
	public int profile(MultipartFile profileImg, Member loginMember) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/** 사용자 조회 / 특정 사용자가 쓴 글 목록
	 *
	 * @author miae
	 */
	@Override
	public Map<String, Object> selectMemberInfo(Map<String, Object> inputMap) {
		int memberNo = (int) inputMap.get("memberNo");
		int cp = (int) inputMap.get("cp");
		int listCount = 0;
		
		Map<String, Object> map = new HashMap<>();
		
		// 사용자 조회
		Member member = mapper.selectMember(memberNo);
		log.info("member 들어오는지 확인하기 : " + member.toString());
		
		// 특정 사용자가 쓴 글 목록
		List<RecipeBoard> recipeBoardList = null;
		
		
		

		// 1. 특정 사용자가 작성한 레시피 중 삭제 되지 않은 게시글 수를 조회
		listCount = mapper.getMemberRecipeListCount(memberNo);


		// 2. 1번의 결과 + cp 를 이용해서 Pagination 객체를 생성
		Pagination pagination = new Pagination(cp, listCount);

		// 3. 특정 게시판의 지정된 페이지 목록 조회
		int limit = pagination.getLimit(); // 한 페이지 내에 보여줄 게시글 수
		int offset = (cp - 1) * limit; // 보여줄 페이지의 앞에 건너뛸 게시글 개수
		RowBounds rowBounds = new RowBounds(offset, limit);


		// 조회 결과를 리스트에 저장
		recipeBoardList = mapper.selectMemberRecipeList(memberNo, rowBounds);


		// 해시태그 받아오는 부분
		for (RecipeBoard recipeBoard : recipeBoardList) {
			String tags = recipeBoard.getTags();
			if (tags != null && !tags.isEmpty()) {
				List<String> tagList = Arrays.stream(tags.split(",")).map(String::trim).collect(Collectors.toList());
				recipeBoard.setHashTagList(tagList);
			}
		}

		// 4. 목록 조회 결과 + Pagination 객체를 Map 으로 묶어서 반환
		map.put("member", member);
		map.put("pagination", pagination);
		map.put("recipeBoardList", recipeBoardList);
		
		
		
		return map;
	}

	/** 사용자 조회 / 특정 사용자가 쓴 일반 게시글 목록
	 * @author jihyun / miae
	 */
	@Override
	public Map<String, Object> selectMemberBoardList(Map<String, Object> inputMap) {
		int memberNo = (int) inputMap.get("memberNo");
		int cp = (int) inputMap.get("cp");
		int listCount = 0;
		
		Map<String, Object> map = new HashMap<>();
		
		// 사용자 조회
		Member member = mapper.selectMember(memberNo);
		log.info("member 들어오는지 확인하기 : " + member.toString());
		
		// 특정 사용자가 쓴 글 목록
		List<Board> boardList = null;
		
		
		

		// 1. 특정 사용자가 작성한 글 중 삭제 되지 않은 게시글 수를 조회
		listCount = mapper.getMemberDefaultListCount(memberNo);


		// 2. 1번의 결과 + cp 를 이용해서 Pagination 객체를 생성
		Pagination pagination = new Pagination(cp, listCount);

		// 3. 특정 게시판의 지정된 페이지 목록 조회
		int limit = pagination.getLimit(); // 한 페이지 내에 보여줄 게시글 수
		int offset = (cp - 1) * limit; // 보여줄 페이지의 앞에 건너뛸 게시글 개수
		RowBounds rowBounds = new RowBounds(offset, limit);


		// 조회 결과를 리스트에 저장
		boardList = mapper.selectMemberBoardList(memberNo, rowBounds);


		// 해시태그 받아오는 부분
		for (Board list : boardList) {
			String tags = list.getTags();
			if (tags != null && !tags.isEmpty()) {
				List<String> tagList = Arrays.stream(tags.split(",")).map(String::trim).collect(Collectors.toList());
				list.setHashTagList(tagList);
			}
		}

		// 4. 목록 조회 결과 + Pagination 객체를 Map 으로 묶어서 반환
		map.put("member", member);
		map.put("pagination", pagination);
		map.put("boardList", boardList);
		
		
		
		return map;
	}



}
