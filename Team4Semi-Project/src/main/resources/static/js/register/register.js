// HTML 문서가 모두 로드된 후 실행
document.addEventListener("DOMContentLoaded", () => {
  // 아이디 입력창 요소 선택
  const memberId = document.getElementById("memberId");

  // 아이디 중복 검사 버튼 요소 선택
  const idCheckBtn = document.querySelector(".btn-sub");

  // 아이디 중복 결과 메시지를 표시할 요소 선택
  const idCheckMessage = document.getElementById("idCheckMessage");

  // 아이디 중복 검사 통과 여부를 저장할 변수
  let isIdChecked = false;

  // 아이디 중복 검사 버튼 클릭 이벤트 리스너
  idCheckBtn.addEventListener("click", () => {
    console.log("------------");
    // 입력된 아이디 값 앞뒤 공백 제거
    const idValue = memberId.value.trim();
    console.log(idValue.length);

    // 아이디가 입력되지 않았을 경우 메시지 출력 후 종료
    if (idValue.length === 0) {
      idCheckMessage.innerText = "아이디를 입력해주세요.";
      idCheckMessage.classList.add("error"); // 에러 스타일 추가
      idCheckMessage.classList.remove("confirm"); // 성공 스타일 제거
      isIdChecked = false; // 체크 실패 상태로 설정
      return;
    }

    // 아이디 형식 정규식: 영문 또는 숫자, 4~12자
    const regExp = /^[a-zA-Z0-9]{4,12}$/;

    // 형식이 맞지 않으면 메시지 출력 후 종료
    if (!regExp.test(idValue)) {
      idCheckMessage.innerText = "아이디는 4~12자의 영문 또는 숫자여야 합니다.";
      idCheckMessage.classList.add("error");
      idCheckMessage.classList.remove("confirm");
      isIdChecked = false;
      return;
    }

    // 서버에 아이디 중복 검사 요청 (GET 방식)
    fetch(`/member/checkId?memberId=${idValue}`)
      .then((resp) => resp.text()) // 응답을 텍스트로 변환
      .then((count) => {
        // 서버 응답이 1이면 이미 사용 중인 아이디
        if (count == 1) {
          idCheckMessage.innerText = "이미 사용중인 아이디입니다.";
          idCheckMessage.classList.add("error");
          idCheckMessage.classList.remove("confirm");
          isIdChecked = false;
          return;
        }

        // 사용 가능한 아이디
        idCheckMessage.innerText = "사용 가능한 아이디입니다.";
        idCheckMessage.classList.add("confirm");
        idCheckMessage.classList.remove("error");
        isIdChecked = true;
      })
      .catch(() => {
        // 통신 오류 시 메시지 출력
        idCheckMessage.innerText = "서버와 통신 중 오류가 발생했습니다.";
        idCheckMessage.classList.add("error");
        idCheckMessage.classList.remove("confirm");
        isIdChecked = false;
      });
  });

  // 가입 폼 제출 시 아이디 중복 검사가 완료되었는지 확인
  const registerForm = document.getElementById("registerForm");
  registerForm.addEventListener("submit", (e) => {
    // 중복 체크가 되지 않았으면 제출 막기
    if (!isIdChecked) {
      alert("아이디 중복 체크를 해주세요.");
      e.preventDefault(); // 폼 제출 막기
      return;
    }

    // 그 외에도 비밀번호 등 추가 검증을 여기에 넣으면 됨
  });
});
