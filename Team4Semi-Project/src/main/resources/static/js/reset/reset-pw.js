document.addEventListener("DOMContentLoaded", () => {
  const newPwInput = document.getElementById("newPassword");
  const newPwConfirmInput = document.getElementById("confirmPassword");
  const resetBtn = document.querySelector(".find-pw-btn");

  resetBtn.addEventListener("click", async () => {
    const newPw = newPwInput.value.trim();
    const newPwConfirm = newPwConfirmInput.value.trim();

    // 1️⃣ 입력 유효성 검사
    if (!newPw) {
      alert("새 비밀번호를 입력해주세요.");
      newPwInput.focus();
      return;
    }

    if (!newPwConfirm) {
      alert("새 비밀번호 확인을 입력해주세요.");
      newPwConfirmInput.focus();
      return;
    }

    if (newPw !== newPwConfirm) {
      alert("비밀번호가 일치하지 않습니다.");
      return;
    }

    // 2️⃣ 비밀번호 형식 검사 (예: 8~20자, 영문/숫자/특수문자 포함)
    const pwRegex =
      /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*()_+])[A-Za-z\d!@#$%^&*()_+]{8,20}$/;
    if (!pwRegex.test(newPw)) {
      alert("비밀번호는 8~20자, 영문/숫자/특수문자를 포함해야 합니다.");
      return;
    }

    // 3️⃣ 서버에 비밀번호 업데이트 요청 보내기
    const response = await fetch("/find/findpw-update", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ newPw }),
    });

    const result = await response.json();
    if (result.success) {
      alert("비밀번호가 성공적으로 변경되었습니다. 다시 로그인해주세요.");
      window.location.href = "/login"; // 로그인 페이지로 이동
    } else {
      alert(result.message || "비밀번호 변경에 실패했습니다.");
    }
  });
});
