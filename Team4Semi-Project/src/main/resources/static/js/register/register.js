document.addEventListener("DOMContentLoaded", () => {
  const memberId = document.getElementById("memberId");
  const idCheckBtn = document.getElementById("idCheckBtn");
  const idCheckMessage = document.getElementById("idCheckMessage");
  
  // 체크 상태 저장
  let isIdChecked = false;

  idCheckBtn.addEventListener("click", () => {
    const idValue = memberId.value.trim();
    if (idValue.length === 0) {
      idCheckMessage.innerText = "아이디를 입력해주세요.";
      idCheckMessage.classList.add("error");
      idCheckMessage.classList.remove("confirm");
      isIdChecked = false;
      return;
    }
    
    // 아이디 형식 체크 (예: 4~12자 영문, 숫자)
    const regExp = /^[a-zA-Z0-9]{4,12}$/;
    if (!regExp.test(idValue)) {
      idCheckMessage.innerText = "아이디는 4~12자의 영문 또는 숫자여야 합니다.";
      idCheckMessage.classList.add("error");
      idCheckMessage.classList.remove("confirm");
      isIdChecked = false;
      return;
    }
    
    // 중복 검사 요청 (서버에 맞게 URL 조정)
    fetch(`/member/checkId?memberId=${idValue}`)
      .then(resp => resp.text())
      .then(count => {
        if (count == 1) {
          idCheckMessage.innerText = "이미 사용중인 아이디입니다.";
          idCheckMessage.classList.add("error");
          idCheckMessage.classList.remove("confirm");
          isIdChecked = false;
          return;
        }
        idCheckMessage.innerText = "사용 가능한 아이디입니다.";
        idCheckMessage.classList.add("confirm");
        idCheckMessage.classList.remove("error");
        isIdChecked = true;
      })
      .catch(() => {
        idCheckMessage.innerText = "서버와 통신 중 오류가 발생했습니다.";
        idCheckMessage.classList.add("error");
        idCheckMessage.classList.remove("confirm");
        isIdChecked = false;
      });
  });

  // 가입 버튼 클릭 시 아이디 중복 체크 확인
  const registerForm = document.getElementById("registerForm");
  registerForm.addEventListener("submit", (e) => {
    if (!isIdChecked) {
      alert("아이디 중복 체크를 해주세요.");
      e.preventDefault();
      return;
    }

    // 기존 checkObj 검사 등 추가 검증 코드를 넣어주면 좋음
  });
});
