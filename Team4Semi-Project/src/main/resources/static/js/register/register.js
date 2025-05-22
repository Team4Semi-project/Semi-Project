document.addEventListener("DOMContentLoaded", () => {
  console.log("DOM fully loaded and parsed");

  const form = document.getElementById("registerForm");
  if (!form) {
    console.error("Form element not found!");
    return;
  }
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
  const confirmPasswordMessage = document.getElementById(
    "confirmPasswordMessage"
  );

  const memberNameInput = document.querySelector('input[name="memberName"]');
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
    fetch(`/member/checkId?memberId=${id}`,  {
		method : "POST",
					headers : {
						"Content-Type" : "application/json"
					}
					, body : id
				}
		)
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

  // 이메일 유효성 검사 및 인증번호 전송
  sendCodeBtn.addEventListener("click", () => {
    const email = emailInput.value.trim();
    const reg = /^[\w\.-]+@[a-zA-Z\d\.-]+\.[a-zA-Z]{2,6}$/;
    if (!email) {
      emailMessage.textContent = "이메일을 입력해주세요.";
      emailMessage.className = "message error";
      return;
    }
    if (!reg.test(email)) {
      emailMessage.textContent = "올바른 이메일 형식을 입력해주세요.";
      emailMessage.className = "message error";
      return;
    }
    emailMessage.textContent =
      "인증번호가 전송되었습니다. 메일함을 확인해주세요.";
    emailMessage.className = "message confirm";
    // 서버로 인증번호 전송 로직 추가
  });

  // 인증번호 확인
  verifyCodeBtn.addEventListener("click", () => {
    const code = emailCodeInput.value.trim();
    if (!code) {
      authCodeMessage.textContent = "인증번호를 입력해주세요.";
      authCodeMessage.className = "message error";
      return;
    }
    if (code === "123456") {
      authCodeMessage.textContent = "인증에 성공했습니다.";
      authCodeMessage.className = "message confirm";
    } else {
      authCodeMessage.textContent = "인증번호가 일치하지 않습니다.";
      authCodeMessage.className = "message error";
    }
  });

  // 비밀번호 실시간 확인 함수
  const checkPasswords = () => {
    const password = passwordInput.value.trim();
    const confirmPassword = confirmPasswordInput.value.trim();

    if (!password) {
      confirmPasswordMessage.textContent = "비밀번호를 입력해주세요.";
      confirmPasswordMessage.className = "message error";
    } else if (password !== confirmPassword) {
      confirmPasswordMessage.textContent = "비밀번호가 일치하지 않습니다.";
      confirmPasswordMessage.className = "message error";
    } else {
      confirmPasswordMessage.textContent = "비밀번호가 일치합니다.";
      confirmPasswordMessage.className = "message confirm";
    }
  };

  // 비밀번호 입력 시 실시간 확인
  passwordInput.addEventListener("input", checkPasswords);
  confirmPasswordInput.addEventListener("input", checkPasswords);

  // 닉네임 중복 체크
  nicknameCheckBtn.addEventListener("click", () => {
    const nickname = nicknameInput.value.trim();
    if (!nickname) {
      nicknameMessage.textContent = "닉네임을 입력해주세요.";
      nicknameMessage.className = "message error";
      return;
    }
    if (nickname.length < 2) {
      nicknameMessage.textContent = "닉네임은 2자 이상이어야 합니다.";
      nicknameMessage.className = "message error";
      return;
    }
    fetch(`/member/checkNickname?memberNickname=${nickname}`,
		{
			method : "POST",
			headers : {
				"Content-Type" : "application/json"
			}
			, body : nickname
		}
		
	)
      .then((resp) => resp.text())
      .then((count) => {
        if (count == 1) {
          nicknameMessage.textContent = "이미 사용 중인 닉네임입니다.";
          nicknameMessage.className = "message error";
        } else {
          nicknameMessage.textContent = "사용 가능한 닉네임입니다.";
          nicknameMessage.className = "message confirm";
        }
      })
      .catch(() => {
        nicknameMessage.textContent = "서버와의 통신에 실패했습니다.";
        nicknameMessage.className = "message error";
      });
  });

  // 폼 제출 처리
  form.addEventListener("submit", (e) => {
    e.preventDefault(); // 기본 폼 제출 방지
    console.log("Form submission triggered");

    // 아이디 검증
    const id = memberId.value.trim();
    console.log("Member ID:", id);
    if (!id) {
      idCheckMessage.textContent = "아이디를 입력해주세요.";
      idCheckMessage.className = "message error";
      console.log("ID validation failed");
      return;
    }
    if (idCheckMessage.textContent !== "사용 가능한 아이디입니다.") {
      idCheckMessage.textContent = "아이디 중복 체크를 완료해주세요.";
      idCheckMessage.className = "message error";
      console.log("ID check validation failed");
      return;
    }

    // 이메일 검증
    const email = emailInput.value.trim();
    console.log("Email:", email);
    const reg = /^[\w\.-]+@[a-zA-Z\d\.-]+\.[a-zA-Z]{2,6}$/;
    if (!email) {
      emailMessage.textContent = "이메일을 입력해주세요.";
      emailMessage.className = "message error";
      console.log("Email validation failed");
      return;
    }
    if (!reg.test(email)) {
      emailMessage.textContent = "올바른 이메일 형식을 입력해주세요.";
      emailMessage.className = "message error";
      console.log("Email format validation failed");
      return;
    }
    if (
      emailMessage.textContent !==
      "인증번호가 전송되었습니다. 메일함을 확인해주세요."
    ) {
      emailMessage.textContent = "이메일 인증을 완료해주세요.";
      emailMessage.className = "message error";
      console.log("Email verification validation failed");
      return;
    }

    // 인증번호 검증
    const emailCode = emailCodeInput.value.trim();
    console.log("Email Code:", emailCode);
    if (!emailCode) {
      authCodeMessage.textContent = "인증번호를 입력해주세요.";
      authCodeMessage.className = "message error";
      console.log("Email code validation failed");
      return;
    }
    if (authCodeMessage.textContent !== "인증에 성공했습니다.") {
      authCodeMessage.textContent = "인증번호 확인을 완료해주세요.";
      authCodeMessage.className = "message error";
      console.log("Email code verification failed");
      return;
    }

    // 비밀번호 검증
    const password = passwordInput.value.trim();
    const confirmPassword = confirmPasswordInput.value.trim();
    console.log("Password:", password, "Confirm Password:", confirmPassword);
    if (!password) {
      confirmPasswordMessage.textContent = "비밀번호를 입력해주세요.";
      confirmPasswordMessage.className = "message error";
      console.log("Password validation failed");
      return;
    }
    if (password !== confirmPassword) {
      confirmPasswordMessage.textContent = "비밀번호가 일치하지 않습니다.";
      confirmPasswordMessage.className = "message error";
      console.log("Password match validation failed");
      return;
    }

    // 사용자 이름 검증
    const memberName = memberNameInput.value.trim();
    console.log("Member Name:", memberName);
    if (!memberName) {
      confirmPasswordMessage.textContent = "사용자 이름을 입력해주세요.";
      confirmPasswordMessage.className = "message error";
      console.log("Member name validation failed");
      return;
    }

    // 닉네임 검증
    const nickname = nicknameInput.value.trim();
    console.log("Nickname:", nickname);
    if (!nickname) {
      nicknameMessage.textContent = "닉네임을 입력해주세요.";
      nicknameMessage.className = "message error";
      console.log("Nickname validation failed");
      return;
    }
    if (nickname.length < 2) {
      nicknameMessage.textContent = "닉네임은 2자 이상이어야 합니다.";
      nicknameMessage.className = "message error";
      console.log("Nickname length validation failed");
      return;
    }
    if (nicknameMessage.textContent !== "사용 가능한 닉네임입니다.") {
      nicknameMessage.textContent = "닉네임 중복 체크를 완료해주세요.";
      nicknameMessage.className = "message error";
      console.log("Nickname check validation failed");
      return;
    }

    // 모든 검증 통과 시 서버에 POST 요청
    const formData = new FormData(form);
    console.log("Submitting form data:", Array.from(formData.entries()));
    console.log("Sending POST request to /member/register");

    fetch("/member/register", {
      method: "POST",
      body: formData,
    })
      .then((response) => {
        console.log("Response status:", response.status);
        if (!response.ok) {
          return response.text().then((text) => {
            throw new Error(
              `HTTP error! status: ${response.status}, body: ${text}`
            );
          });
        }
        return response.text();
      })
      .then((message) => {
        console.log("Received message:", message);
        if (confirm(message)) {
          window.location.href = "/login";
        }
      })
      .catch((error) => {
        console.error("Fetch error:", error);
        confirmPasswordMessage.textContent =
          "회원가입 중 오류가 발생했습니다: " + error.message;
        confirmPasswordMessage.className = "message error";
      });
  });

  // DOM 요소가 제대로 로드되었는지 확인
  console.log("Form:", form);
  console.log("Member ID:", memberId);
  console.log("Email Input:", emailInput);
  console.log("Email Code Input:", emailCodeInput);
  console.log("Password Input:", passwordInput);
  console.log("Confirm Password Input:", confirmPasswordInput);
  console.log("Member Name Input:", memberNameInput);
  console.log("Nickname Input:", nicknameInput);
});
