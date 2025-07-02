document.addEventListener("DOMContentLoaded", () => {
  const loginForm = document.getElementById("loginForm");
  const username = document.getElementById("username");
  const password = document.getElementById("password");

  // 로그인 유효성 검사
  loginForm.addEventListener("submit", function (e) {
    if (username.value.trim() === "") {
      alert("아이디를 입력하세요.");
      username.focus();
      e.preventDefault();
      return;
    }

    if (password.value.trim() === "") {
      alert("비밀번호를 입력하세요.");
      password.focus();
      e.preventDefault();
      return;
    }
  });

  // 회원가입 버튼 클릭 시 /register로 이동
  const registerBtn = document.getElementById("registerBtn");
  registerBtn.addEventListener("click", () => {
    window.location.href = "/register";
  });
});
