document.addEventListener("DOMContentLoaded", () => {
  const findPwForm = document.getElementById("findPwForm");
  const userIdInput = document.getElementById("userId");
  const userNameInput = document.getElementById("userName");
  const userEmailInput = document.getElementById("userEmail");
  const findPwBtn = document.querySelector(".find-pw-btn");

  findPwBtn.addEventListener("click", async (e) => {
    const memberId = userIdInput.value.trim();
    const memberName = userNameInput.value.trim();
    const memberEmail = userEmailInput.value.trim();

    // 1️⃣ 입력 유효성 검사
    if (!memberId) {
      alert("아이디를 입력해주세요.");
      userIdInput.focus();
      return;
    }

    if (!memberName) {
      alert("이름을 입력해주세요.");
      userNameInput.focus();
      return;
    }

    if (!memberEmail) {
      alert("이메일을 입력해주세요.");
      userEmailInput.focus();
      return;
    }

    // 2️⃣ 서버에 POST 요청 보내기

    const response = await fetch("/find/findPw", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ memberId, memberName, memberEmail }),
    });

    const data = await response.json();
    console.log("응답 내용:", data);

    if (data.sucess) {
      // 비밀번호 재설정 페이지로 이동
      window.location.href = "/find/findpw-update?memberId="+memberId;
    } else {
      alert(data.message || "입력한 값이 일치하지 않습니다.");
    }
  });
});
