document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("resetPwForm");             // 폼 요소
  const newPassword = document.getElementById("newPassword");      // 새 비밀번호 입력란
  const confirmPassword = document.getElementById("confirmPassword");  // 비밀번호 확인 입력란
  const errorDiv = document.getElementById("pwError");             // 오류 메시지를 표시할 div

  form.addEventListener("submit", (e) => {
    // 비밀번호와 확인 비밀번호가 다를 경우
    if (newPassword.value !== confirmPassword.value) {
      e.preventDefault(); // 제출 막기
      errorDiv.textContent = "비밀번호가 일치하지 않습니다.";
    
    // 비밀번호 길이가 6자 미만인 경우
    } else if (newPassword.value.length < 6) {
      e.preventDefault();
      errorDiv.textContent = "비밀번호는 6자 이상이어야 합니다.";
    
    // 조건을 모두 통과했을 경우
    } else {
      errorDiv.textContent = "";
    }
  });
});