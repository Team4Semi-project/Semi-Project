package kr.co.lemona.myPage.model.service;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import kr.co.lemona.common.util.Utility;
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

	/**
	 * 프로필 이미지 변경 서비스
	 * 
	 * @author 명하
	 */
	@Override
	public int profile(MultipartFile profileImg, Member loginMember) throws Exception {
		// 프로필 이미지 경로
		String updatePath = null;

		// 변경명 저장
		String rename = null;

		// 업로드한 이미지가 있을 경우
		// - 있을 경우 : 경로 조합 (클라이언트 접근 경로 + 리네임파일명)
		if (!profileImg.isEmpty()) {
			// 1. 파일명 변경
			rename = Utility.fileRename(profileImg.getOriginalFilename());

			// 2. /myPage/profile/변경된파일명
			updatePath = profileWebPath + rename;
		}

		// 수정된 프로필 이미지 경로 + 회원 번호를 저장할 DTO 객체
		Member member = Member.builder().memberNo(loginMember.getMemberNo()).profileImg(updatePath).build();

		int result = mapper.profile(member);

		if (result > 0) {

			// 프로필 이미지를 없애는 update 를 한 경우를 제외
			// -> 업로드한 이미지가 있을 경우
			if (!profileImg.isEmpty()) {
				// 파일을 서버에 저장
				profileImg.transferTo(new File(profileFolderPath + rename));
				// C:/uploadFiles/profile/변경한이름
			}

			// 세션에 저장된 loginMember의 프로필 이미지 경로를
			// DB와 동기화
			loginMember.setProfileImg(updatePath);

		}

		return result;
	}

	/**
	 * 사용자 No 조회
	 * 
	 * @author 민장
	 */
	@Override
	public int searchMemberNo(String memberNickname) {
		return mapper.searchMemberNo(memberNickname);
	}

	/**
	 * 사용자 조회 / 특정 사용자가 쓴 글 목록
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
		log.info("member 들어오는지 확인하기 : selectMemberInfo : " + member.toString());

		// 특정 사용자가 쓴 글 목록
		List<RecipeBoard> recipeBoardList = null;

		// 1. 특정 사용자가 작성한 레시피 중 삭제 되지 않은 게시글 수를 조회
		listCount = mapper.getMemberRecipeListCount(memberNo);

		// 2. 1번의 결과 + cp 를 이용해서 Pagination 객체를 생성
		Pagination pagination = new Pagination(cp, listCount);
		log.info("pagination : " + pagination.getListCount());

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

		// 레시피 게시판 댓글 수
		int recipeCommentCount = mapper.selectRecipeCommentCount(memberNo);

		// 4. 목록 조회 결과 + Pagination 객체를 Map 으로 묶어서 반환
		map.put("member", member);
		map.put("pagination", pagination);
		map.put("recipeBoardList", recipeBoardList);
		map.put("listCount", listCount);
		map.put("recipeCommentCount", recipeCommentCount);

		return map;
	}

	/**
	 * 사용자 조회 / 특정 사용자가 쓴 일반 게시글 목록
	 * 
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

		// 썸네일 추출 추가
		for (Board board : boardList) {
			// 글 내용만 가져옴
			String content = board.getBoardContent();
			if (content != null) {
				// 글 내용에서 img 태그의 src 속성 값을 추출하는 정규식을 Patter에 정의
				// 그 정규식으로 matcher 객체 생성
				Matcher matcher = Pattern.compile("<img[^>]+src=[\"']([^\"']+)[\"']").matcher(content);
				// matcher에서 패턴에 정의 된 정규식에 맞는 첫번째 문자열을 찾음
				if (matcher.find()) {
					// 문자열을 board의 thumbnail에 세팅 // 0 : img src 태그 전체
					board.setThumbnail(matcher.group(1)); // 1 : "또는'이 나오기 전까지의 모든 문자(첫번째 ()의 내용인 [^\"']+)
					log.info("board.Thumbnail : " + board.getThumbnail());
				}
			}
		}

		// 해시태그 받아오는 부분
		for (Board list : boardList) {
			String tags = list.getTags();
			if (tags != null && !tags.isEmpty()) {
				List<String> tagList = Arrays.stream(tags.split(",")).map(String::trim).collect(Collectors.toList());
				list.setHashTagList(tagList);
			}
		}

		// 댓글 수
		int commentCount = mapper.selectCommentCount(memberNo);

		// 4. 목록 조회 결과 + Pagination 객체를 Map 으로 묶어서 반환
		map.put("member", member);
		map.put("pagination", pagination);
		map.put("boardList", boardList);
		map.put("listCount", listCount);
		map.put("commentCount", commentCount);

		return map;
	}

	@Override
	public int updateProfile(Member member) {
		// 프로필 이미지 처리 (컨트롤러에서 이미 파일 저장 로직이 포함되어 있으므로 여기서는 DB 업데이트만 처리)
        int result = mapper.updateProfile(member);

        if (result > 0) {
            // 세션 동기화
            Member loginMember = member; // 세션에서 가져온 member 객체로 동기화
            // 세션에 저장된 loginMember가 업데이트된 member로 반영되도록 설정
            // (컨트롤러에서 이미 세션에 반영되므로 별도 로직 생략 가능)
        }

        return result;
    }
	
	/** 프로필 이미지 삭제
	 * 명하
	 */
	@Override
	public int removeProfileImage(int memberNo) {
		return mapper.removeProfileImage(memberNo);
	}
	
	/** 멤버 검색
	 * 명하
	 */
	@Override
	public Member selectMember(int memberNo) {
		return mapper.selectMember(memberNo);
	}
	


//	/** 닉네임 중복 검사
//	 *
//	 */
//	@Override
//	public int checkNickname(String memberNickname) {
//		return mapper.checkNickname(memberNickname);
//	
//	}
	
}

