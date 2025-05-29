// DOM이 완전히 로드된 후 실행
document.addEventListener("DOMContentLoaded", () => {
  console.log("DOM fully loaded and parsed");

  // 필수 입력 항목의 유효성 검사 여부를 체크하기 위한 JS 객체
  const checkObj = {
    memberId: false,
    memberEmail: false,
    authKey: false,
    memberPw: false,
    memberPwConfirm: false,
    memberNickname: false,
  };

  // 아이디 관련 요소
  const memberId = document.getElementById("memberId");
  const idCheckBtn = document.getElementById("idCheckBtn");
  const idCheckMessage = document.getElementById("idCheckMessage");

  // 이메일 관련 요소
  const memberEmail = document.getElementById("emailId");
  const sendAuthKeyBtn = document.getElementById("sendCodeBtn");
  const authKey = document.getElementById("emailCodeInput");
  const checkAuthKeyBtn = document.getElementById("verifyCodeBtn");
  const emailMessage = document.getElementById("emailMessage");
  const authKeyMessage = document.getElementById("authCodeMessage");

  // 비밀번호 관련 요소
  const memberPw = document.getElementById("passwordInput");
  const memberPwConfirm = document.getElementById("confirmPasswordInput");
  const pwMessage = document.getElementById("confirmPasswordMessage");

  // 닉네임 관련 요소
  const memberNickname = document.getElementById("nicknameInput");
  const nicknameCheckBtn = document.getElementById("nicknameCheckBtn");
  const nickMessage = document.getElementById("nicknameMessage");

  // 폼 요소
  const registerForm = document.getElementById("registerForm");

  let authTimer; // 타이머 역할을 할 setInterval 함수를 저장할 변수

  const initMin = 4; // 타이머 초기값 (분)
  const initSec = 59; // 타이머 초기값 (초)
  const initTime = "05:00";

  // 실제 줄어드는 시간을 저장할 변수
  let min = initMin;
  let sec = initSec;

  // 매개변수 전달받은 숫자가 10 미만인 경우 앞에 0 붙여서 반환
  function addZero(number) {
    return number < 10 ? "0" + number : number;
  }

  // 아이디 중복 체크
  idCheckBtn.addEventListener("click", () => {
    const id = memberId.value.trim();
    if (id.length === 0) {
      idCheckMessage.textContent = "아이디를 입력해주세요.";
      idCheckMessage.className = "message error";
      return;
    }
    fetch(`/member/checkId?memberId=${id}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
    })
      .then((resp) => resp.text())
      .then((count) => {
        if (count == 1) {
          idCheckMessage.textContent = "이미 사용 중인 아이디입니다.";
          idCheckMessage.className = "message error";
          checkObj.memberId = false;
        } else {
          idCheckMessage.textContent = "사용 가능한 아이디입니다.";
          idCheckMessage.className = "message confirm";
          checkObj.memberId = true;
        }
      })
      .catch(() => {
        idCheckMessage.textContent = "서버와의 통신에 실패했습니다.";
        idCheckMessage.className = "message error";
      });
  });

 // 이메일 유효성 검사
  memberEmail.addEventListener("input", (e) => {
    const inputEmail = e.target.value.trim();

    checkObj.authKey = false; // 이메일 변경 시 인증 무효화
    authKeyMessage.innerText = "";
    clearInterval(authTimer);

    if (inputEmail.length === 0) {
      emailMessage.innerText = "메일을 받을 수 있는 이메일을 입력해주세요.";
      emailMessage.classList.remove("message", "confirm", "error");
      checkObj.memberEmail = false;
      memberEmail.value = "";
      return;
    }

    const regExp = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (!regExp.test(inputEmail)) {
      emailMessage.innerText = "알맞은 이메일 형식으로 작성해주세요.";
      emailMessage.classList.add("message", "error");
      emailMessage.classList.remove("confirm");
      checkObj.memberEmail = false;
      return;
    }

    fetch(`/member/checkEmail?memberEmail=${inputEmail}`)
      .then((resp) => resp.text())
      .then((count) => {
        if (count == 1) {
          emailMessage.innerText = "이미 사용중인 이메일입니다.";
          emailMessage.classList.add("message", "error");
          emailMessage.classList.remove("confirm");
          checkObj.memberEmail = false;
          return;
        }
        emailMessage.innerText = "사용 가능한 이메일입니다.";
        emailMessage.classList.add("message", "confirm");
        emailMessage.classList.remove("error");
        checkObj.memberEmail = true;
      })
      .catch((err) => console.log(err));
  });

  // 인증번호 받기 버튼 클릭 시
  sendAuthKeyBtn.addEventListener("click", () => {
    checkObj.authKey = false;
    authKeyMessage.innerText = "";
    clearInterval(authTimer);

    if (!checkObj.memberEmail) {
      alert("유효한 이메일을 작성 후 클릭해주세요.");
      return;
    }

    min = initMin;
    sec = initSec;

  // 이전 동작중인 인터벌 클리어(없애기)
  clearInterval(authTimer);

  // ****************************************
  // 비동기로 서버에서 메일 보내기
  fetch("/email/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email: memberEmail.value })
  })
    .then((resp) => resp.text())
    .then((result) => {
      if (result == 1) {
        console.log("인증 번호 발송 성공");
        alert("인증번호가 발송되었습니다.");
      } else {
        console.log("인증 번호 발송 실패!!!...");
      }
    });
  // ****************************************
  // 메일은 비동기로 서버에서 보내라고 놔두고
  // 화면에서는 타이머 시작하기
    authTimer = setInterval(() => {
      authKeyMessage.innerText = `${addZero(min)}:${addZero(sec)}`;

      if (min == 0 && sec == 0) {
        checkObj.authKey = false;
        clearInterval(authTimer);
        authKeyMessage.innerText = "인증번호 입력 시간이 만료되었습니다.";
        authKeyMessage.classList.add("message", "error");
        authKeyMessage.classList.remove("confirm");
        return;
      }

      if (sec == 0) {
        sec = 60;
        min--;
      }

      sec--;
    }, 1000);
  });

  // 인증번호 확인 버튼 클릭 시
  checkAuthKeyBtn.addEventListener("click", () => {
    if (min === 0 && sec === 0) {
      alert("인증번호 입력 시간이 만료되었습니다. 다시 발급해주세요.");
      return;
    }

    if (!authKey.value.trim()) {
      authKeyMessage.innerText = "인증번호를 입력해주세요.";
      authKeyMessage.classList.add("message", "error");
      return;
    }

    const obj = {
      email: memberEmail.value,
      authKey: authKey.value.trim(),
    };

    fetch("/email/checkAuthKey", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(obj),
    })
      .then((resp) => resp.text())
      .then((result) => {
        if (result == 0) {
          authKeyMessage.innerText = "인증번호가 다릅니다.";
          authKeyMessage.classList.add("message", "error");
          authKeyMessage.classList.remove("confirm");
          checkObj.authKey = false;
          return;
        }
        clearInterval(authTimer);
        authKeyMessage.innerText = "인증되었습니다.";
        authKeyMessage.classList.add("message", "confirm");
        authKeyMessage.classList.remove("error");
        checkObj.authKey = true;
      })
      .catch(() => {
        authKeyMessage.innerText = "서버와의 통신에 실패했습니다.";
        authKeyMessage.classList.add("message", "error");
      });
  });

  // 비밀번호 유효성 검사
  memberPw.addEventListener("input", (e) => {
    const inputPw = e.target.value.trim();

    if (inputPw.length === 0) {
      // pwMessage.innerText = "영어,숫자,특수문자(!,@,#,-,_) 6~20글자 사이로 입력해주세요.";
      pwMessage.classList.remove("message", "confirm", "error");
      checkObj.memberPw = false;
      memberPw.value = "";
      return;
    }

    const regExp = /^[a-zA-Z0-9!@#_-]{6,20}$/;
    if (!regExp.test(inputPw)) {
      pwMessage.innerText = "비밀번호가 유효하지 않습니다.";
      pwMessage.classList.add("message", "error");
      pwMessage.classList.remove("confirm");
      checkObj.memberPw = false;
      return;
    }

    pwMessage.innerText = "유효한 비밀번호 형식입니다.";
    pwMessage.classList.add("message", "confirm");
    pwMessage.classList.remove("error");
    checkObj.memberPw = true;

    if (memberPwConfirm.value.length > 0) {
      checkPw();
    }
  });

  // 비밀번호 확인 유효성 검사
  const checkPw = () => {
    if (memberPw.value === memberPwConfirm.value) {
      pwMessage.innerText = "비밀번호가 일치합니다.";
      pwMessage.classList.add("message", "confirm");
      pwMessage.classList.remove("error");
      checkObj.memberPwConfirm = true;
      return;
    }
    pwMessage.innerText = "비밀번호가 일치하지 않습니다.";
    pwMessage.classList.add("message", "error");
    pwMessage.classList.remove("confirm");
    checkObj.memberPwConfirm = false;
  };

  memberPwConfirm.addEventListener("input", () => {
    if (checkObj.memberPw) {
      checkPw();
      return;
    }
    checkObj.memberPwConfirm = false;
  });

  // 닉네임 유효성 검사 (입력 시 형식만 검사)
  memberNickname.addEventListener("input", (e) => {
    const inputNickname = e.target.value.trim();

    if (inputNickname.length === 0) {
      nickMessage.innerText = "한글,영어,숫자로만 2~10글자";
      nickMessage.classList.remove("message", "confirm", "error");
      checkObj.memberNickname = false;
      memberNickname.value = "";
      return;
    }

    const regExp = /^[가-힣\w\d]{2,10}$/;
    if (!regExp.test(inputNickname)) {
      nickMessage.innerText = "유효하지 않은 닉네임 형식입니다.";
      nickMessage.classList.add("message", "error");
      nickMessage.classList.remove("confirm");
      checkObj.memberNickname = false;
      return;
    }

    // 실시간 중복 체크 제거
    nickMessage.innerText = "닉네임 형식 검사 통과. 중복 체크 버튼을 눌러주세요.";
    nickMessage.classList.add("message", "confirm");
    nickMessage.classList.remove("error");
    // checkObj.memberNickname은 버튼 클릭 시에만 업데이트
  });

  // 닉네임 중복 체크 버튼 (DB와 비교)
  nicknameCheckBtn.addEventListener("click", () => {
    const nickname = memberNickname.value.trim();
    if (!nickname) {
      nickMessage.textContent = "닉네임을 입력해주세요.";
      nickMessage.className = "message error";
      checkObj.memberNickname = false;
      return;
    }
    if (nickname.length < 2) {
      nickMessage.textContent = "닉네임은 2자 이상이어야 합니다.";
      nickMessage.className = "message error";
      checkObj.memberNickname = false;
      return;
    }

    // DB와 비교
    fetch(`/member/checkNickname?memberNickname=${nickname}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
    })
      .then((resp) => resp.text())
      .then((count) => {
        if (count == 1) {
          nickMessage.textContent = "이미 사용중인 닉네임 입니다.";
          nickMessage.className = "message error";
          checkObj.memberNickname = false;
        } else {
          nickMessage.textContent = "사용 가능한 닉네임 입니다.";
          nickMessage.className = "message confirm";
          checkObj.memberNickname = true;
        }
      })
      .catch(() => {
        nickMessage.textContent = "서버와의 통신에 실패했습니다.";
        nickMessage.className = "message error";
        checkObj.memberNickname = false;
      });
  });

  // 폼 제출 시 유효성 검사
  registerForm.addEventListener("submit", (e) => {
    e.preventDefault();

    for (let key in checkObj) {
      if (!checkObj[key]) {
        let str;
        switch (key) {
          case "memberId":
            str = "아이디 중복 체크를 완료해주세요.";
            break;
          case "memberEmail":
            str = "이메일이 유효하지 않습니다.";
            break;
          case "authKey":
            str = "이메일이 인증되지 않았습니다.";
            break;
          case "memberPw":
            str = "비밀번호가 유효하지 않습니다.";
            break;
          case "memberPwConfirm":
            str = "비밀번호가 일치하지 않습니다.";
            break;
          case "memberNickname":
            str = "닉네임 중복 체크를 완료해주세요.";
            break;
        }
        alert(str);
        document.getElementById(key).focus();
        return;
      }
    }

    const formData = new FormData(registerForm);
    fetch("/member/register", {
      method: "POST",
      body: formData,
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("HTTP error! status: " + response.status);
        }
        return response.text();
      })
      .then((message) => {
        if (confirm(message)) {
          window.location.href = "/member/login";
        }
      })
      .catch((error) => {
        console.error("Fetch error:", error);
        pwMessage.textContent = "회원가입 중 오류가 발생했습니다: " + error.message;
        pwMessage.className = "message error";
      });
  });

  // DOM 요소 로깅
  console.log("Form:", registerForm);
  console.log("Member ID:", memberId);
  console.log("Email Input:", memberEmail);
  console.log("Auth Key Input:", authKey);
  console.log("Password Input:", memberPw);
  console.log("Confirm Password Input:", memberPwConfirm);
  console.log("Nickname Input:", memberNickname);
});
