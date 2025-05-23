// /js/login/login.js
document.addEventListener("DOMContentLoaded", () => {
  const loginForm = document.getElementById("loginForm");
  const username = document.getElementById("username");
  const password = document.getElementById("password");

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
});
