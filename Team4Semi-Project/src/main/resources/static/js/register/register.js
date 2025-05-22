document.addEventListener("DOMContentLoaded", () => {
  const memberId = document.getElementById("memberId");
  const idCheckBtn = document.getElementById("idCheckBtn");
  const idCheckMessage = document.getElementById("idCheckMessage");

  const emailInput = document.getElementById("emailId");
  const sendCodeBtn = document.getElementById("sendCodeBtn");
  const emailMessage = document.getElementById("emailMessage");

  const emailCodeInput = document.getElementById("emailCodeInput");
  const verifyCodeBtn = document.getElementById("verifyCodeBtn");
  const authCodeMessage = document.getElementById("authCodeMessage");

  const passwordInput = document.getElementById("passwordInput");
  const confirmPasswordInput = document.getElementById("confirmPasswordInput");
  const passwordMessage = document.getElementById("passwordMessage");
  const confirmPasswordMessage = document.getElementById("confirmPasswordMessage");

  const nicknameInput = document.getElementById("nicknameInput");
  const nicknameCheckBtn = document.getElementById("nicknameCheckBtn");
  const nicknameMessage = document.getElementById("nicknameMessage");

  // 아이디 중복 체크
  idCheckBtn.addEventListener("click", () => {
    const id = memberId.value.trim();
    if (id.length === 0) {
      idCheckMessage.textContent = "아이디를 입력해주세요.";
      idCheckMessage.className = "message error";
      return;
    }
    //  아이디 중복 확인 서버 요청
    fetch(`/member/checkId?memberId=${id}`)
      .then((resp) => resp.text())
      .then((count) => {
        if (count == 1) {
          idCheckMessage.textContent = "이미 사용 중인 아이디입니다.";
          idCheckMessage.className = "message error";
        } else {
          idCheckMessage.textContent = "사용 가능한 아이디입니다.";
          idCheckMessage.className = "message confirm";
        }
      })
      .catch(() => {
        idCheckMessage.textContent = "서버와의 통신에 실패했습니다.";
        idCheckMessage.className = "message error";
      });
  });

  // 이메일 유효성 검사
  sendCodeBtn.addEventListener("click", () => {
    const email = emailInput.value.trim();
    const reg = /^[\w\.-]+@[a-zA-Z\d\.-]+\.[a-zA-Z]{2,6}$/;
    if (!reg.test(email)) {
      emailMessage.textContent = "올바른 이메일 형식을 입력해주세요.";
      emailMessage.className = "message error";
      return;
    }
    emailMessage.textContent =
      "인증번호가 전송되었습니다. 메일함을 확인해주세요.";
    emailMessage.className = "message confirm";
    // 서버 전송 로직 추가
  });

  // 인증번호 확인
  verifyCodeBtn.addEventListener("click", () => {
    const code = emailCodeInput.value.trim();
    if (code === "123456") {
      authCodeMessage.textContent = "인증에 성공했습니다.";
      authCodeMessage.className = "message confirm";
    } else {
      authCodeMessage.textContent = "인증번호가 일치하지 않습니다.";
      authCodeMessage.className = "message error";
    }
  });

  // 비밀번호 확인
  confirmPasswordInput.addEventListener("blur", () => {
    if (passwordInput.value !== confirmPasswordInput.value) {
      confirmPasswordMessage.textContent = "비밀번호가 일치하지 않습니다.";
      confirmPasswordMessage.className = "message error";
    } else {
      confirmPasswordMessage.textContent = "비밀번호가 일치합니다.";
      confirmPasswordMessage.className = "message confirm";
    }
	// const regExp = /^[a-zA-Z0-9!@#_-]{6,20}$/; 
  });

  // 닉네임 중복 체크 (예시)
  nicknameCheckBtn.addEventListener("click", () => {
    const nickname = nicknameInput.value.trim();
    if (nickname.length < 2) {
      nicknameMessage.textContent = "닉네임은 2자 이상이어야 합니다.";
      nicknameMessage.className = "message error";
    } else {
      nicknameMessage.textContent = "사용 가능한 닉네임입니다.";
      nicknameMessage.className = "message confirm";
    }
  });
});
