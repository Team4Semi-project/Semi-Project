/* 필드에 있는 요소 */
// 탈퇴 약관
const secessionTerms = document.querySelector(".secession-terms")
// 동의 체크 박스
const agreeCheckBox = document.querySelector("#agree");
// 비밀번호 입력창
const passwordInput = document.querySelector("#memberPw");
// 탈퇴 버튼
const submitBtn = document.querySelector("#submitBtn");

const checkObj = {
    // 약관을 읽었는가?
    isRead: false,
    // 동의(체크박스에 체크)를 하였는가?
    isAgree: false,
    // 비밀번호를 입력했는가?
    isInput: false
};

// 약관을 읽었는가 요소체크
secessionTerms.addEventListener("scroll", () => {
    const scrollTop = secessionTerms.scrollTop;       // 현재 스크롤 위치
    const scrollHeight = secessionTerms.scrollHeight; // 전체 스크롤 높이
    const clientHeight = secessionTerms.clientHeight; // 보이는 영역의 높이

    if (scrollTop + clientHeight >= scrollHeight) {
        checkObj.isRead = true;
        console.log("약관을 모두 읽었음");
    };
});

// 동의를 했는가 요소체크
agreeCheckBox.addEventListener("change", (e) => {
    if (!checkObj.isRead) {
        alert("약관을 모두 읽어주세요.");
        e.preventDefault();
        agreeCheckBox.checked = false;
    };
    checkObj.isAgree = agreeCheckBox.checked;
    checkSubmitAvailable();
});

// 비밀번호를 입력했는가 요소체크
passwordInput.addEventListener("input", () => {
    if (passwordInput.value.trim().length > 0) {
        checkObj.isInput = true;
    } else {
        checkObj.isInput = false;
    };
    checkSubmitAvailable();
});

// 각 요소의 확인 여부에 따른 버튼 활성화/비활성화
const checkSubmitAvailable = () => {
    if (checkObj.isAgree && checkObj.isInput) {
        submitBtn.disabled = false;
    } else {
        submitBtn.disabled = true;
    }
}

// 탈퇴 요청
submitBtn.addEventListener("submit", e => {

    // - 비밀번호 입력 되었는지 확인
    if (!checkObj.isInput) {
        alert("비밀번호를 입력해주세요.");
        e.preventDefault(); // 제출막기
        return;
    }

    // 약관 동의 체크 확인
    if (!checkObj.isAgree) {
        alert("약관에 동의해주세요");
        e.preventDefault();
        return;
    }

    // 탈퇴하기 물어보기
    if (!confirm("정말 탈퇴 하시겠습니까?")) {
        alert("취소 되었습니다.");
        e.preventDefault();
        return;
    }
});