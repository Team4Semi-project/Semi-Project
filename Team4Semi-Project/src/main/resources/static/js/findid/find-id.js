document.addEventListener("DOMContentLoaded", () => {
  const findIdForm = document.getElementById("findIdForm");
  const nameInput = document.getElementById("nameInput");
  const emailInput = document.getElementById("emailInput");
  const foundId = document.getElementById("foundId");

  findIdForm.addEventListener("submit", async (e) => {
    e.preventDefault(); // 기본 form 제출 막기

    const name = nameInput.value.trim();
    const email = emailInput.value.trim();

    // 입력 유효성 검사
    if (!name) {
      alert("이름을 입력해주세요.");
      nameInput.focus();
      return;
    }

    if (!email) {
      alert("이메일을 입력해주세요.");
      emailInput.focus();
      return;
    }

    try {
      const response = await fetch("/findId", {
        // 대소문자 주의!
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ name, email }),
      });

      const data = await response.json();
      console.log("응답 내용:", data);

      if (data.foundId) {
        foundId.value = data.foundId;
        alert("아이디를 찾았습니다!");
      } else {
        alert("입력한 정보와 일치하는 아이디가 없습니다. 다시 입력해주세요.");
        foundId.value = "";
      }
    } catch (error) {
      console.error("에러:", error);
      alert("아이디 찾기 중 오류가 발생했습니다.");
    }
  });
});
