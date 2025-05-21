document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("resetPwForm");
  const newPassword = document.getElementById("newPassword");
  const confirmPassword = document.getElementById("confirmPassword");
  const errorDiv = document.getElementById("pwError");

  form.addEventListener("submit", (e) => {
    if (newPassword.value !== confirmPassword.value) {
      e.preventDefault();
      errorDiv.textContent = "비밀번호가 일치하지 않습니다.";
    } else if (newPassword.value.length < 6) {
      e.preventDefault();
      errorDiv.textContent = "비밀번호는 6자 이상이어야 합니다.";
    } else {
      errorDiv.textContent = "";
    }
  });
});