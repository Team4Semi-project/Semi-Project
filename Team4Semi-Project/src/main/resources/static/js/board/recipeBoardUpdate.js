document.addEventListener("DOMContentLoaded", function () {
  // 요소 참조
  const recipeStepsContainer = document.getElementById("recipeStepsContainer");
  const addStepBtn = document.getElementById("addStepBtn");
  const hashTagInput = document.getElementById("hashTagInput");
  const hashTagContainer = document.getElementById("hashTagContainer");
  const submitBtn = document.getElementById("submitBtn");

  // 제목 길이 제한
  const titleInput = document.querySelector("#boardTitle");
  titleInput.addEventListener("input", e => {
    if (e.target.value.length > 40) {
      alert("메뉴 이름의 길이는 40자를 넘을 수 없습니다.")
      e.target.value = e.target.value.slice(0, 40); // 40자까지만 유지
    }
  })

  // 스텝 내용 길이 제한
  const contentInput = document.querySelectorAll("[name='recipeStep']");
  contentInput.forEach(input => {
    input.addEventListener("input", e => {
      if (e.target.value.length > 390) {
        alert("각 조리과정의 길이는 390자를 넘을 수 없습니다.");
        e.target.value = e.target.value.slice(0, 390); // 390자까지만 유지
      }
    });
  });

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

  // 해시태그 입력 이벤트
  hashTagInput.addEventListener("keypress", function (e) {
    if (e.key === "Enter") {
      e.preventDefault();
      addHashTag();
    }
  });

  // 글 작성 완료 버튼 이벤트
  submitBtn.addEventListener("click", function (e) {
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
    // newStep.dataset.step = stepCounter;

    newStep.innerHTML = `
            <div class="step-buttons">
                <button type="button" class="step-btn step-up" title="위로 이동">
                  <span class="material-symbols-outlined">
                    keyboard_arrow_up
                  </span>
                </button>
                <button type="button" class="step-btn step-delete" title="삭제">
                  <span class="material-symbols-outlined">
                    close
                  </span>
                </button>
                <button type="button" class="step-btn step-down" title="아래로 이동">
                  <span class="material-symbols-outlined">
                    keyboard_arrow_down
                  </span>
                </button>
            </div>
            <div class="step-content">
                <textarea name="recipeStep" placeholder="요리 과정을 알려주세요, 셰프!"></textarea>
                <div class="step-image-area">
                    <div class="image-upload">
                        <label for="stepImage${stepCounter}">
                          <span class="material-symbols-outlined">
                            image
                          </span>
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
                    <input type="hidden" value="0" class="step-no">
                </div>
            </div>
        `;

    recipeStepsContainer.appendChild(newStep);

    const lastStep = recipeStepsContainer.querySelector(".recipe-step:last-child");
    bindStepEvents(lastStep); // ← 확실한 참조
    updateStepButtons();

    updateStepNumbers();
  }

  /**
   * 요리과정 스텝 이벤트 바인딩
   */
  function bindStepEvents(stepElement = document) {
    // 이미지 업로드 이벤트
    stepElement.querySelectorAll(".step-image-input").forEach((input) => {
      input.removeEventListener("change", handleImageUpload); // 기존 리스너 제거
      input.addEventListener("change", handleImageUpload);
    });

    // 이미지 취소 버튼 이벤트
    stepElement.querySelectorAll(".image-cancel-btn").forEach((btn) => {
      btn.addEventListener("click", function () {
        // 기존 취소 로직 복사
      });
    });

    // 스텝 삭제 버튼 이벤트
    stepElement.querySelectorAll(".step-delete").forEach((btn) => {
      btn.addEventListener("click", function () {
        deleteStep(this.closest(".recipe-step"));
      });
    });

    // 위로 이동 버튼 이벤트
    stepElement.querySelectorAll(".step-up").forEach((btn) => {
      btn.addEventListener("click", function () {
        moveStepUp(this.closest(".recipe-step"));
      });
    });

    // 아래로 이동 버튼 이벤트
    stepElement.querySelectorAll(".step-down").forEach((btn) => {
      btn.addEventListener("click", function () {
        moveStepDown(this.closest(".recipe-step"));
      });
    });

    // 이미지 변경 시 클릭 이벤트
    stepElement.querySelectorAll(".image-preview").forEach((previewDiv) => {
      previewDiv.addEventListener("click", function (e) {
        const img = e.target;
        if (img.tagName !== "IMG") return;
        const stepDiv = previewDiv.closest(".step-image-area");
        const fileInput = stepDiv.querySelector(".step-image-input");
        if (fileInput) fileInput.click();
      });
    });
  }


  /**
   * 이미지 업로드 처리
   */
  function handleImageUpload(e) {
    const file = e.target.files[0];
    const maxSize = 1024 * 1024 * 10; // 10MB
    const allowedTypes = ["image/jpeg", "image/png", "image/gif"];

    const input = e.target;
    const stepElement = input.closest(".recipe-step");
    const imageUpload = stepElement.querySelector(".image-upload");
    const previewContainer = stepElement.querySelector(".image-preview-container");
    const preview = stepElement.querySelector(".image-preview");

    console.log("🔥 실제 클릭된 input ID:", input.id);
    console.log("🔥 감지된 step 번호:", stepElement.dataset.step);

    // 기존 이미지 태그
    const prevImg = preview.querySelector("img");
    // 기존 이미지 src 기억
    const previousImageSrc = prevImg?.getAttribute("src");

    // 사용자가 파일 선택을 취소했을 때
    if (!file) {
      // 기존 이미지 복원
      if (previousImageSrc) {
        preview.innerHTML = `<img src="${previousImageSrc}" alt="레시피 이미지">`;
        imageUpload.style.display = "none";
        previewContainer.style.display = "flex";
      }
      return;
    }

    // 업로드 파일 조건 검사 (예: 확장자, 용량 제한 등)
    if (!allowedTypes.includes(file.type)) {
      alert("JPG, PNG, GIF 형식의 이미지만 업로드 가능합니다.");
      // 기존 이미지 복원
      if (previousImageSrc) {
        preview.innerHTML = `<img src="${previousImageSrc}" alt="레시피 이미지">`;
        imageUpload.style.display = "none";
        previewContainer.style.display = "flex";
      }
      return;
    }

    // 용량 제한 조건 적용
    if (file.size > maxSize) {
      alert("10MB 이하의 이미지만 업로드 가능합니다.");
      // 기존 이미지 복원
      if (previousImageSrc) {
        preview.innerHTML = `<img src="${previousImageSrc}" alt="레시피 이미지">`;
        imageUpload.style.display = "none";
        previewContainer.style.display = "flex";
      }
      return;
    }

    // 미리보기 생성
    const reader = new FileReader();
    reader.onload = function (e) {
      preview.innerHTML = `<img src="${e.target.result}" alt="레시피 이미지">`;
      imageUpload.style.display = "none";
      previewContainer.style.display = "flex";

      // 업로드된 이미지가 마지막 사진일 때 썸네일로 사용
      const steps = document.querySelectorAll(".recipe-step");
      const isLastStep = stepElement === steps[steps.length - 1];
      if (isLastStep) {
        const thumbnailRadio = stepElement.querySelector('input[type="radio"]');
        if (thumbnailRadio) {
          thumbnailRadio.checked = true;
        }
      }
    };

    stepElement.querySelector(".step-no").value = "0";

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
    console.log("🛠️ updateStepNumbers 실행됨");

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

  // 1. 기존 렌더된 해시태그를 hashTags에 추가하고 삭제 이벤트 연결
  document.querySelectorAll("#hashTagContainer .hashtag").forEach((tagElement) => {
    const tagText = tagElement.querySelector("span:first-child").textContent.replace(/^#/, "");

    hashTags.push(tagText); // 배열에 추가

    // 삭제 이벤트 바인딩
    tagElement.querySelector(".delete-hashtag").addEventListener("click", function () {
      tagElement.remove();
      hashTags = hashTags.filter((tag) => tag !== tagText);
    });
  });

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

    // 해시태그 정규식 검사
    const tagPattern = /^[a-zA-Z가-힣0-9]+$/;
    if (!tagPattern.test(cleanTag)) {
      alert("해시태그에는 특수문자나 공백을 사용할 수 없습니다.");
      hashTagInput.value = "";
      return;
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
            <span class="delete-hashtag material-symbols-outlined">
              close
            </span>
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

    // 스텝 정보 수집
    const steps = document.querySelectorAll(".recipe-step");
    for (let index = 0; index < steps.length; index++) {
      const step = steps[index];

      // 스텝 설명 수집
      const stepContent = step.querySelector("textarea").value;
      // 작성되지 않은 스텝 설명이 있을때
      if (!stepContent.trim()) {
        alert("작성되지 않은 레시피 과정이 있어요!");
        step.querySelector("textarea").focus();
        e.preventDefault();
        return;
      }
      // 작성되지 않은 스텝 설명이 없을때, 스텝 설명을 수집
      formData.append("stepContents", stepContent);

      // 스텝 이미지 수집
      const imageInput = step.querySelector(".step-image-input");
      // 신규 이미지가 있을 때
      if (imageInput && imageInput.files && imageInput.files.length > 0) {
        formData.append("images", imageInput.files[0]);
        formData.append("imageExists", true);
      } else {
        formData.append("imageExists", false);
      }

      // 썸내일 정보 수집
      const thumbnailRadio = step.querySelector('input[type="radio"]');
      // 썸내일 체크박스가 있고, 체크 요소도 있을때
      if (thumbnailRadio && thumbnailRadio.checked) {
        // 썸내일 정보 수집
        formData.append("thumbnailNo", index + 1);
        isThumbnailSelected = true;
      }

      // 순서 변경 및 기존 이미지에 대한 처리사항 수집
      const stepNoInput = step.querySelector(".step-no");
      const stepNo = stepNoInput ? stepNoInput.value : "0";
      formData.append("stepNoList", stepNo);
    }

    // 이미지는 있는데 썸내일이 없다면
    const hasAnyImage = Array.from(steps).some(step => {
      const img = step.querySelector(".image-preview img");
      const src = img?.getAttribute("src");
      return img && src && !["", "null", "/images/recipeBoard/null"].includes(src.trim());
    });

    if (hasAnyImage && !isThumbnailSelected) {
      alert("대표 이미지를 선택해주세요!");
      e.preventDefault();
      return;
    }

    // 썸네일 라디오 버튼 존재 여부 확인
    const hasThumbnailRadio = Array.from(steps).some(step => step.querySelector('input[type="radio"]'));


    // 썸네일 라디오 버튼은 있지만 이미지가 전혀 없는 경우 → 빈값 처리
    if (!hasAnyImage && hasThumbnailRadio && !isThumbnailSelected) {
      formData.delete("thumbnailNo");
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
