document.addEventListener("DOMContentLoaded", function () {
  // 요소 참조
  const recipeStepsContainer = document.getElementById("recipeStepsContainer");
  const addStepBtn = document.getElementById("addStepBtn");
  const hashTagInput = document.getElementById("hashTagInput");
  const hashTagContainer = document.getElementById("hashTagContainer");
  const submitBtn = document.getElementById("submitBtn");

  // 게시글 번호 수집
  const pathname = window.location.pathname;
  const segments = pathname.split("/");
  const boardNo = segments[4];

  // 스텝 카운터
  let stepCounter = 1;

  // 해시태그 배열
  let hashTags = [];

  // 요리과정 추가 버튼 이벤트
  addStepBtn.addEventListener("click", function () {
    addNewStep();
  });

  document.querySelectorAll("#hashTagContainer .hashtag span:first-child").forEach((span) => {
    const text = span.textContent.replace(/^#/, "");
    hashTags.push(text);
  });

  // 해시태그 입력 이벤트
  hashTagInput.addEventListener("keypress", function (e) {
    if (e.key === "Enter") {
      e.preventDefault();
      addHashTag();
    }
  });

  // 글 수정 완료 버튼 이벤트
  submitBtn.addEventListener("click", function (e) {
    if (!confirm("레시피를 수정하시겠습니까?")) {
      alert("수정이 취소되었습니다.");

      location.href = `/board/1/${categoryNo}/${boardNo}`;
    }
    submitRecipe(e);
  });

  // 초기 스텝 설정
  updateStepButtons();
  bindStepEvents();

  /**
   * 새 요리과정 스텝 추가
   */
  function addNewStep() {
    // 최대 10개 제한
    if (stepCounter >= 10) {
      alert("요리과정은 최대 10개까지만 추가 가능합니다.");
      return;
    }

    stepCounter++;

    const newStep = document.createElement("div");
    newStep.className = "recipe-step";
    newStep.dataset.step = stepCounter;

    newStep.innerHTML = `
            <div class="step-buttons">
                <button type="button" class="step-btn step-up" title="위로 이동"><span>↑</span></button>
                <button type="button" class="step-btn step-delete" title="삭제"><span>×</span></button>
                <button type="button" class="step-btn step-down" title="아래로 이동"><span>↓</span></button>
            </div>
            <div class="step-content">
                <textarea name="recipeStep" placeholder="요리 과정을 입력하세요..."></textarea>
                <div class="step-image-area">
                    <div class="image-upload">
                        <label for="stepImage${stepCounter}">
                            <div class="upload-icon">+</div>
                            <span>사진 추가</span>
                        </label>
                        <input type="file" id="stepImage${stepCounter}" class="step-image-input" accept="image/*">
                    </div>
                    <div class="image-preview-container" style="display: none;">
                        <div class="image-preview"></div>
                        <div class="image-controls">
                            <div class="thumbnail-control">
                                <input type="radio" name="thumbnailStep" value="${stepCounter}" id="thumbnail${stepCounter}">
                                <label for="thumbnail${stepCounter}">대표 이미지로 설정</label>
                            </div>
                            <button type="button" class="image-cancel-btn">선택 취소</button>
                        </div>
                    </div>
                </div>
            </div>
        `;

    recipeStepsContainer.appendChild(newStep);
    updateStepButtons();
    bindStepEvents();
  }

  /**
   * 요리과정 스텝 이벤트 바인딩
   */
  function bindStepEvents() {
    // 이미지 업로드 이벤트
    document.querySelectorAll(".step-image-input").forEach((input) => {
      if (!input.hasListener) {
        input.addEventListener("change", handleImageUpload);
        input.hasListener = true;
      }
    });

    // 이미지 취소 버튼 이벤트
    document.querySelectorAll(".image-cancel-btn").forEach((btn) => {
      if (!btn.hasListener) {
        btn.addEventListener("click", function () {
          const stepElement = this.closest(".recipe-step");
          const imageInput = stepElement.querySelector(".step-image-input");
          const imageUpload = stepElement.querySelector(".image-upload");
          const previewContainer = stepElement.querySelector(
            ".image-preview-container"
          );
          const thumbnailRadio = stepElement.querySelector(
            'input[type="radio"]'
          );

          // 이미지 입력 초기화
          imageInput.value = "";

          // 미리보기 숨기기
          previewContainer.style.display = "none";
          imageUpload.style.display = "flex";

          // 썸네일 체크 해제
          if (thumbnailRadio) {
            thumbnailRadio.checked = false;
          }
        });
        btn.hasListener = true;
      }
    });

    // 스텝 순서 변경 버튼 이벤트
    document.querySelectorAll(".step-up").forEach((btn) => {
      if (!btn.hasListener) {
        btn.addEventListener("click", function () {
          moveStepUp(this.closest(".recipe-step"));
        });
        btn.hasListener = true;
      }
    });

    document.querySelectorAll(".step-down").forEach((btn) => {
      if (!btn.hasListener) {
        btn.addEventListener("click", function () {
          moveStepDown(this.closest(".recipe-step"));
        });
        btn.hasListener = true;
      }
    });

    // 스텝 삭제 버튼 이벤트
    document.querySelectorAll(".step-delete").forEach((btn) => {
      if (!btn.hasListener) {
        btn.addEventListener("click", function () {
          deleteStep(this.closest(".recipe-step"));
        });
        btn.hasListener = true;
      }
    });
  }

  /**
   * 이미지 업로드 처리
   */
  function handleImageUpload(e) {
    const file = e.target.files[0];
    if (!file) return;

    const stepElement = this.closest(".recipe-step");
    const imageUpload = stepElement.querySelector(".image-upload");
    const previewContainer = stepElement.querySelector(
      ".image-preview-container"
    );
    const preview = stepElement.querySelector(".image-preview");

    // 미리보기 생성
    const reader = new FileReader();
    reader.onload = function (e) {
      preview.innerHTML = `<img src="${e.target.result}" alt="레시피 이미지">`;
      imageUpload.style.display = "none";
      previewContainer.style.display = "block";
    };

    // 업도드된 이미지가 마지막 사진일때 썸내일로 사용
    const steps = document.querySelectorAll(".recipe-step");
    const isLastStep = stepElement === steps[steps.length - 1];

    if (isLastStep) {
      const thumbnailRadio = stepElement.querySelector('input[type="radio"]');
      if (thumbnailRadio) {
        thumbnailRadio.checked = true;
      }
    }

    reader.readAsDataURL(file);
  }

  /**
   * 스텝 순서 위로 이동
   */
  function moveStepUp(stepElement) {
    const prevStep = stepElement.previousElementSibling;
    if (prevStep) {
      recipeStepsContainer.insertBefore(stepElement, prevStep);
      updateStepNumbers();
      updateStepButtons();
    }
  }

  /**
   * 스텝 순서 아래로 이동
   */
  function moveStepDown(stepElement) {
    const nextStep = stepElement.nextElementSibling;
    if (nextStep) {
      recipeStepsContainer.insertBefore(nextStep, stepElement);
      updateStepNumbers();
      updateStepButtons();
    }
  }

  /**
   * 스텝 삭제
   */
  function deleteStep(stepElement) {
    // 스텝이 2개 이상일 때만 삭제 가능
    const steps = document.querySelectorAll(".recipe-step");
    if (steps.length < 2) {
      return;
    }

    recipeStepsContainer.removeChild(stepElement);
    updateStepNumbers();
    updateStepButtons();
  }

  /**
   * 스텝 번호 업데이트
   */
  function updateStepNumbers() {
    const steps = document.querySelectorAll(".recipe-step");
    steps.forEach((step, index) => {
      const stepNumber = index + 1;
      step.dataset.step = stepNumber;

      // 이미지 업로드 input ID 업데이트
      const imageInput = step.querySelector(".step-image-input");
      const imageLabel = step.querySelector(".image-upload label");
      const thumbnailRadio = step.querySelector('input[type="radio"]');
      const thumbnailLabel = step.querySelector(".thumbnail-control label");

      if (imageInput) {
        const newId = `stepImage${stepNumber}`;
        imageInput.id = newId;
        if (imageLabel) {
          imageLabel.setAttribute("for", newId);
        }
      }

      if (thumbnailRadio) {
        const newId = `thumbnail${stepNumber}`;
        thumbnailRadio.id = newId;
        thumbnailRadio.value = stepNumber;
        if (thumbnailLabel) {
          thumbnailLabel.setAttribute("for", newId);
        }
      }
    });
  }

  /**
   * 스텝 버튼 상태 업데이트
   */
  function updateStepButtons() {
    const steps = document.querySelectorAll(".recipe-step");

    // 스텝이 1개일 때는 버튼 비활성화
    if (steps.length === 1) {
      steps[0].querySelectorAll(".step-btn").forEach((btn) => {
        btn.disabled = true;
        btn.classList.add("disabled");
      });
      return;
    }

    // 모든 버튼 활성화
    steps.forEach((step) => {
      step.querySelectorAll(".step-btn").forEach((btn) => {
        btn.disabled = false;
        btn.classList.remove("disabled");
      });
    });

    // 첫 번째 스텝의 위로 버튼 비활성화
    steps[0].querySelector(".step-up").disabled = true;
    steps[0].querySelector(".step-up").classList.add("disabled");

    // 마지막 스텝의 아래로 버튼 비활성화
    const lastStep = steps[steps.length - 1];
    lastStep.querySelector(".step-down").disabled = true;
    lastStep.querySelector(".step-down").classList.add("disabled");
  }

  /**
   * 해시태그 추가
   */
  function addHashTag() {
    const tagText = hashTagInput.value.trim();

    // 빈 값 체크
    if (!tagText) return;

    // # 제거하고 저장
    let cleanTag = tagText;
    if (cleanTag.startsWith("#")) {
      cleanTag = cleanTag.substring(1);
    }

    // 중복 체크
    if (hashTags.includes(cleanTag)) {
      hashTagInput.value = "";
      return;
    }

    // 해시태그 추가
    hashTags.push(cleanTag);

    // 해시태그 엘리먼트 생성
    const tagElement = document.createElement("div");
    tagElement.className = "hashtag";
    tagElement.innerHTML = `
            <span>#${cleanTag}</span>
            <span class="delete-hashtag">×</span>
        `;

    // 삭제 버튼 이벤트
    tagElement
      .querySelector(".delete-hashtag")
      .addEventListener("click", function () {
        hashTagContainer.removeChild(tagElement);
        hashTags = hashTags.filter((tag) => tag !== cleanTag);
      });

    hashTagContainer.appendChild(tagElement);
    hashTagInput.value = "";
  }

  /**
   * 레시피 작성 완료
   */
  function submitRecipe(e) {
    // 폼 데이터 수집 및 제출 로직
    const formData = new FormData();

    // 카테고리 수집
    const categoryNo = document.getElementById("categorySelect").value;
    if (!categoryNo) { // 카테고리가 없다면
      alert("레시피 카테고리를 지정해주세요!")
      e.preventDefault(); // 제출 취소
      return;
    }
    formData.append("categoryNo", categoryNo);
    formData.append("boardNo", boardNo);

    // 제목 수집
    const boardTitle = document.getElementById("boardTitle").value;
    if (!boardTitle) { // 제목이 없다면
      alert("레시피 제목을 작성해주세요!");
      document.getElementById("boardTitle").focus();
      e.preventDefault(); // 제출 취소
      return;
    }
    formData.append("boardTitle", boardTitle);

    // 대표 이미지 선택 여부
    let isThumbnailSelected = false;
    let isEmptyStepContent = false;
    // 요리과정 수집
    const steps = document.querySelectorAll(".recipe-step");
    steps.forEach((step, index) => {
      const stepContent = step.querySelector("textarea").value;
      if (!stepContent) { // 설명이 비어있는 단계가 있다면
        isEmptyStepContent = true;
        step.querySelector("textarea").focus();
      } else {
        formData.append("stepContents", stepContent);
      }

      // 이미지 수집
      const imageInput = step.querySelector(".step-image-input");
      if (imageInput.files.length > 0) {
        formData.append("images", imageInput.files[0]);
      } else { // 이미지가 없다면
        formData.append("images", new File([""], ""));
      }

      // 썸네일 설정 확인
      const isThumbnail = step.querySelector('input[type="radio"]').checked;
      if (isThumbnail) {
        formData.append("thumbnailNo", index + 1);
        isThumbnailSelected = true;
      }
    });

    // 설명이 비어있는 단계가 있다면
    if (isEmptyStepContent) {
      alert("작성되지 않은 레시피 과정이 있어요!");
      e.preventDefault(); // 제출 취소
      return;
    }

    // 대표 이미지가 선택되지 않았다면
    if (!isThumbnailSelected) {
      alert("대표 이미지를 선택해주세요!");
      e.preventDefault(); // 제출 취소
      return;
    }

    // 해시태그 수집
    if (hashTags.length != 0) {
      hashTags.forEach((tag, index) => {
        formData.append("hashTags", tag);
      });
    }

    // 레시피 제출
    // 제출할 데이터를 formData에 업로드
    formData.forEach((value, key) => {
      console.log(key, value);
    });

    fetch("/board/1/update", {
      method: "POST",
      // ✅ AJAX 요청임을 명시!
      headers: { "X-Requested-With": "XMLHttpRequest" },
      body: formData,
    })
      .then(async (response) => {
        if (!response.ok) {
          // 상태 코드에 따라 다르게 처리
          switch (response.status) {
            case 404:
              alert("404 ERROR: 페이지를 찾을 수 없습니다.");
              break;
            case 500:
              alert("500 ERROR: 서버 내부 오류입니다.");
              break;
            default:
              alert(`${response.status} ERROR`);
          }

          throw new Error(`HTTP 오류: ${response.status}`);
        }

        // 정상 응답일 경우
        return response.text();
      })
      .then((result) => {
        console.log("응답 성공:", result);
        alert("레시피를 수정했습니다!");
        location.href = result;
      })
      .catch((error) => {
        console.error("에러 발생:", error);
        alert("레시피 수정에 실패했습니다.");
      });
  }
});
