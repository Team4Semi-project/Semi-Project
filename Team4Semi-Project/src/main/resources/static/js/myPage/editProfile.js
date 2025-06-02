document.addEventListener("DOMContentLoaded", () => {
  const profileImageInput = document.getElementById("profileImageInput");
  const profileImagePreview = document.getElementById("profileImagePreview");
  const uploadImageBtn = document.getElementById("uploadImageBtn");
  const removeImageBtn = document.querySelector(".remove-image-btn");
  const form = document.getElementById("editProfileForm");
  const memberName = document.getElementById("memberName");
  const memberNickname = document.getElementById("memberNickname");
  const submitBtn = document.querySelector(".submit-btn");

  const DEFAULT_PROFILE_IMAGE = "/images/user.png";  

  // 이미지 선택 시 미리보기
  profileImageInput.addEventListener("change", (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (event) => {
        profileImagePreview.src = event.target.result;
        uploadImageBtn.style.display = "inline-block";
      };
      reader.readAsDataURL(file);
    }
  });

  // 이미지 업로드 버튼 클릭 시
  uploadImageBtn.addEventListener("click", () => {
    const file = profileImageInput.files[0];
    if (!file) {
      alert("이미지를 선택해주세요.");
      return;
    }

    const formData = new FormData();
    formData.append("profileImg", file);

    fetch("/mypage/uploadProfileImage", {
      method: "POST",
      body: formData,
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        return response.json();
      })
      .then((result) => {
        if (result.status === "success") {
          alert("이미지 업로드 성공!");
          profileImagePreview.src = result.imagePath; // 서버에서 반환된 경로로 미리보기 업데이트
          uploadImageBtn.style.display = "none"; // 업로드 후 버튼 숨김
        } else {
          alert(result.message || "이미지 업로드 실패");
        }
      })
      .catch((error) => {
        console.error("Error:", error);
        alert("이미지가 변경되었습니다");
      });
  });

  // 이미지 제거
  function removeProfileImage() {
    profileImagePreview.src = "/images/default-profile.png";
    profileImageInput.value = ""; // 파일 입력 초기화
    uploadImageBtn.style.display = "none"; // "수정하기" 버튼 숨김

    // 서버에 이미지 제거 요청
    fetch("/mypage/removeProfileImage", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ memberId: document.getElementById("memberId").value }),
    })
      .then((response) => response.text())
      .then((message) => {
        console.log(message);
        alert(message);
        profileImagePreview.src = DEFAULT_PROFILE_IMAGE;
      })
      .catch((error) => {
        console.error("Error:", error);
        alert("이미지 제거 중 오류가 발생했습니다.");
      });
  }
  removeImageBtn.addEventListener("click", removeProfileImage); 

  // 폼 제출 이벤트
  form.addEventListener("submit", (e) => {
    e.preventDefault();

    if (!memberName.value.trim()) {
      alert("이름을 입력해주세요.");
      memberName.focus();
      return;
    }
    if (!memberNickname.value.trim()) {
      alert("닉네임을 입력해주세요.");
      memberNickname.focus();
      return;
    }

    submitBtn.classList.add("loading");
    submitBtn.disabled = true;

    const formData = new FormData(form);
    if (profileImageInput.files[0]) {
      formData.append("profileImage", profileImageInput.files[0]);
    }

    fetch("/mypage/editProfile", {
      method: "POST",
      body: formData,
    })
      .then((response) => response.text())
      .then((message) => {
        alert("변경 성공!");
        window.location.href = "/mypage/editProfile";
      })
      .catch((error) => {
        console.error("Error:", error);
        alert("프로필 수정 중 오류가 발생했습니다.");
      })
      .finally(() => {
        submitBtn.classList.remove("loading");
        submitBtn.disabled = false;
      });
  });
});