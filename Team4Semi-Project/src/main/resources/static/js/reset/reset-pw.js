document.addEventListener("DOMContentLoaded", function () {
  const newPasswordInput = document.getElementById("newPassword");
  const confirmPasswordInput = document.getElementById("confirmPassword");
  const resetButton = document.getElementById("resetButton");
  const form = document.getElementById("resetForm");

  // 비밀번호 정규식 (8자 이상, 영문/숫자/특수문자 포함)
  const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*()_+=-]).{8,}$/;

  // 비밀번호 유효성 및 일치 여부 확인
  function checkPasswords() {
    const newPassword = newPasswordInput.value;
    const confirmPassword = confirmPasswordInput.value;

    if (newPassword && confirmPassword) {
      if (!passwordRegex.test(newPassword)) {
        resetButton.disabled = true;
        newPasswordInput.style.borderColor = "red";
        confirmPasswordInput.style.borderColor = ""; // 초기화
      } else if (newPassword !== confirmPassword) {
        resetButton.disabled = true;
        newPasswordInput.style.borderColor = "green";
        confirmPasswordInput.style.borderColor = "red";
      } else {
        resetButton.disabled = false;
        newPasswordInput.style.borderColor = "green";
        confirmPasswordInput.style.borderColor = "green";
      }
    } else {
      resetButton.disabled = true;
      newPasswordInput.style.borderColor = "";
      confirmPasswordInput.style.borderColor = "";
    }
  }

  // 입력값 변경 시 실시간 확인
  newPasswordInput.addEventListener("input", checkPasswords);
  confirmPasswordInput.addEventListener("input", checkPasswords);

  // 폼 제출 시 서버 요청 처리
  form.addEventListener("submit", function (event) {
    event.preventDefault(); // 기본 폼 제출 방지

    const newPassword = newPasswordInput.value;
    const confirmPassword = confirmPasswordInput.value;

    // 최종 비밀번호 유효성 검사
    if (!passwordRegex.test(newPassword)) {
      alert("비밀번호는 8자 이상이며, 영문, 숫자, 특수문자를 포함해야 합니다.");
      newPasswordInput.focus();
      return;
    }

    if (newPassword !== confirmPassword) {
      alert("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
      confirmPasswordInput.focus();
      return;
    }

    // fetch 요청
    fetch(form.action, {
      method: "POST",
      body: new FormData(form),
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("서버 응답 오류");
        }
        return response.json(); // JSON 응답으로 변경
      })
      .then((data) => {
        if (data.status === "success") {
          alert("비밀번호 재설정 완료");
          window.location.href = "/member/login";
        } else {
          alert(data.message || "비밀번호 재설정에 실패했습니다.");
          location.reload();
        }
      })
      .catch((error) => {
        console.error("Error:", error);
        alert("오류가 발생했습니다: " + error.message);
        location.reload();
      });
  });
});
