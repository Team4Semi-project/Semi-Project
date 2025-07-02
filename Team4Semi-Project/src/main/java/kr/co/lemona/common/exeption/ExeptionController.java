package kr.co.lemona.common.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

/*
 * - 스프링 예외 처리 방법 (우선 순위별로 작성
 * 
 * 컨트롤러 클래스에서 클래스 단위로 모아서 처리
 * (@ExceptionHandler 어노테이션을 지닌 메서드 작성)
 * 
 */

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice // 전역적 예외처리 활성화 어노테이션
public class ExeptionController {
	
	// @ExceptionHandler(예외 종류) : 어떤 예외를 다룰 건지 작성

	// SQLException.class - SQL 관련 예외만 처리
	// IOException.class - 입출력 관련 예외만 처리
	// NoResourceFoundException.class - 요청한 주소를 찾을 수 없을 때 예외처리
	
	@ExceptionHandler(NoResourceFoundException.class)
	public String notFound() {
		return "error/404";
	}
	
	// 프로젝트에서 발생하는 모든 종류의 예외를 잡아 처리하는 메서드
	@ExceptionHandler(Exception.class)
	public Object allExceptionHandler(Exception e, HttpServletRequest request, Model model) {
	    e.printStackTrace();

	    boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

	    if (isAjax) {
	        return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("500 ERROR: AJAX 요청 중 서버 오류");
	    }

	    model.addAttribute("e", e);
	    return "error/500"; // 브라우저에서 이동한 경우는 그대로 HTML 응답
	}
	
	
	/*HTTP 응답 상태 코드
	 * 
	 * 400 : 잘못된 요청(Bad Request)
	 * 
	 * 403 : 서버에서 외부 접근 거부 (Forbidden)
	 * 
	 * 404 : 요청 주소를 찾을 수 없음 (Not Found)
	 * 
	 * 405 : 허용되지 않는 메서드(요청방식) (Method Not Allowed)
	 * 
	 * 500 : 서버 내부 오류(Internal Server Error) (코드->콘솔창보기)
	 * 
	 */

}
