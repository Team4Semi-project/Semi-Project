/**
 * 레모나 프로젝트 - 메인 JavaScript 파일
 * 주요 기능:
 * 1. 메뉴 호버 기능
 * 2. 뷰 토글 (아젠다/코지)
 * 3. 드롭다운 기능
 * 4. 페이지네이션 핸들링
 * 5. 게시글 정렬 기능
 */

document.addEventListener("DOMContentLoaded", function () {
  // 전역 변수
  const recipeMenu = document.querySelector(".recipe-menu");
  const viewToggles = document.querySelectorAll(".view-toggle");
  const recipeContainer = document.querySelector(".recipe-container");
  const sortSelect = document.getElementById("sortSelect");
  const dropdownButtons = document.querySelectorAll(".dropdown-button");
  const searchInput = document.querySelector(".search-input");
  const searchButton = document.querySelector(".search-button");
  const cozy = document.querySelector("#cozy");
  const agenda = document.querySelector("#agenda");

  // 메뉴 호버 기능
  initMenuHover();

  // 드롭다운 기능
  initDropdowns();

  // 검색 기능
  initSearch();

  /**
   * 메뉴 호버 기능 초기화
   */
  function initMenuHover() {
    if (recipeMenu) {
      // 모바일 환경에서는 클릭 이벤트로 서브메뉴 표시
      if (window.innerWidth <= 768) {
        recipeMenu.addEventListener("click", function (e) {
          // 이미 서브메뉴가 열려있는 경우, 링크 동작 허용
          if (e.target.tagName === "A" && !e.target.closest(".submenu")) {
            e.preventDefault();
            const submenu = this.querySelector(".submenu");
            if (submenu.style.display === "block") {
              submenu.style.display = "none";
              // 링크 이동 허용
              window.location.href = e.target.href;
            } else {
              submenu.style.display = "block";
            }
          }
        });

        // 서브메뉴 외부 클릭 시 닫기
        document.addEventListener("click", function (e) {
          if (!e.target.closest(".recipe-menu")) {
            const submenu = recipeMenu.querySelector(".submenu");
            if (submenu) {
              submenu.style.display = "none";
            }
          }
        });
      }
    }
  }


  /* 좋아요 기능 */
  const likes = document.querySelectorAll(".likes");
  if (likes) {

    // obj 객체를 서버로 비동기 요청
    likes.forEach((likesBtn) => {
      likesBtn.addEventListener("click", function (e) {
        const loginMemberNo = this.dataset.loginMemberNo;
        const boardNo = this.dataset.boardNo;
        const boardCode = this.dataset.boardCode;
        let likeCK = Number(this.dataset.likeCheck);

        console.log(loginMemberNo, boardNo, likeCK, `${boardCode}`);

        if (!loginMemberNo || loginMemberNo === "null") {
          alert("로그인 후 이용해주세요");
          return;
        }

        const obj = {
          memberNo: loginMemberNo,
          boardNo: boardNo,
          likeCheck: likeCK
        };

        fetch(`/board/${boardCode}/like`, {
          method: "post",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(obj)
        })
          .then(resp => resp.text())
          .then(count => {

            if (count == -1) {
              console.log("좋아요 처리 실패");
              return;
            }

            // 상태 토글 및 반영
            likeCK = likeCK === 0 ? 1 : 0;
            this.dataset.likeCheck = likeCK;

            // 아이콘 클래스 토글
            const icon = this.querySelector("i");
            if (icon) {
              icon.classList.toggle("fa-regular");
              icon.classList.toggle("fa-solid");
            }

            // 좋아요 수 변경
            const countSpan = this.querySelector("span");
            if (countSpan) {
              countSpan.innerText = count;
            }
          })
          .catch(err => {
            console.error("좋아요 처리 중 오류:", err);
          });
      });
    });
  };

 
  /**
   * 드롭다운 초기화
   */
  function initDropdowns() {
    dropdownButtons.forEach((button) => {
      const dropdown = button.nextElementSibling;

      // 드롭다운 토글
      button.addEventListener("click", function (e) {
        e.stopPropagation();
        const isVisible = dropdown.style.display === "block";

        // 모든 드롭다운 닫기
        document.querySelectorAll(".dropdown-content").forEach((d) => {
          d.style.display = "none";
        });

        // 현재 드롭다운 토글
        if (!isVisible) {
          dropdown.style.display = "block";
        }
      });

      // 드롭다운 항목 클릭 시
      if (dropdown) {
        dropdown.querySelectorAll("a").forEach((item) => {
          item.addEventListener("click", function (e) {
            e.preventDefault();

            // 버튼 텍스트 업데이트
            button.querySelector("span").textContent = this.textContent;

            // 데이터 속성 저장
            if (this.dataset.type) {
              button.dataset.type = this.dataset.type;
            }

            // 드롭다운 닫기
            dropdown.style.display = "none";
          });
        });
      }
    });

    // 외부 클릭 시 드롭다운 닫기
    document.addEventListener("click", function () {
      document.querySelectorAll(".dropdown-content").forEach((dropdown) => {
        dropdown.style.display = "none";
      });
    });
  }


  /**
   * 검색 기능 초기화
   */
  function initSearch() {
    if (searchButton && searchInput) {
      searchButton.addEventListener("click", function () {
        performSearch();
      });

      // 엔터 키 입력 시 검색
      searchInput.addEventListener("keypress", function (e) {
        if (e.key === "Enter") {
          performSearch();
        }
      });
    }
  }

  /**
   * 검색 실행
   */
  function performSearch() {
    const searchType =
      document.querySelector(".search-dropdown .dropdown-button").dataset
        .type || "all";
    const searchKeyword = searchInput.value.trim();

    if (searchKeyword) {
      // 현재 URL 정보 가져오기
      const url = new URL(window.location.href);

      // 검색 파라미터 설정
      url.searchParams.set("searchType", searchType);
      url.searchParams.set("keyword", searchKeyword);

      // 페이지 파라미터 초기화
      url.searchParams.set("page", "1");

      // 페이지 이동
      window.location.href = url.toString();
    }
  }

  /**
   * 반응형 디자인 처리
   */
  function handleResponsive() {
    const width = window.innerWidth;

    // 모바일 환경 처리
    if (width <= 768) {
      if (recipeContainer && recipeContainer.classList.contains("cozy-view")) {
        // 레시피 그리드 조정
        const recipeGrid = document.querySelector(".recipe-grid");
        if (recipeGrid) {
          recipeGrid.style.gridTemplateColumns = "repeat(1, 1fr)";
        }
      }
    } else if (width <= 1024) {
      // 태블릿 환경 처리
      if (recipeContainer && recipeContainer.classList.contains("cozy-view")) {
        const recipeGrid = document.querySelector(".recipe-grid");
        if (recipeGrid) {
          recipeGrid.style.gridTemplateColumns = "repeat(2, 1fr)";
        }
      }
    } else {
      // 데스크톱 환경 처리
      if (recipeContainer && recipeContainer.classList.contains("cozy-view")) {
        const recipeGrid = document.querySelector(".recipe-grid");
        if (recipeGrid) {
          recipeGrid.style.gridTemplateColumns = "repeat(3, 1fr)";
        }
      }
    }
  }

  // 초기 반응형 처리 실행
  handleResponsive();

  // 윈도우 크기 변경 시 반응형 처리
  window.addEventListener("resize", handleResponsive);

  /**
   * 페이지 로드 시 이미지 지연 로딩
   */
  function lazyLoadImages() {
    const lazyImages = document.querySelectorAll("img[data-src]");

    if (lazyImages.length > 0) {
      // IntersectionObserver 지원 확인
      if ("IntersectionObserver" in window) {
        const imageObserver = new IntersectionObserver(function (entries) {
          entries.forEach(function (entry) {
            if (entry.isIntersecting) {
              const lazyImage = entry.target;
              lazyImage.src = lazyImage.dataset.src;
              lazyImage.removeAttribute("data-src");
              imageObserver.unobserve(lazyImage);
            }
          });
        });

        lazyImages.forEach(function (lazyImage) {
          imageObserver.observe(lazyImage);
        });
      } else {
        // 대체 방법으로 스크롤 이벤트 사용
        let lazyLoadThrottleTimeout;

        function lazyLoad() {
          if (lazyLoadThrottleTimeout) {
            clearTimeout(lazyLoadThrottleTimeout);
          }

          lazyLoadThrottleTimeout = setTimeout(function () {
            const scrollTop = window.scrollY;

            lazyImages.forEach(function (lazyImage) {
              if (lazyImage.offsetTop < window.innerHeight + scrollTop) {
                lazyImage.src = lazyImage.dataset.src;
                lazyImage.removeAttribute("data-src");
              }
            });

            if (lazyImages.length === 0) {
              document.removeEventListener("scroll", lazyLoad);
              window.removeEventListener("resize", lazyLoad);
              window.removeEventListener("orientationChange", lazyLoad);
            }
          }, 20);
        }

        document.addEventListener("scroll", lazyLoad);
        window.addEventListener("resize", lazyLoad);
        window.addEventListener("orientationChange", lazyLoad);
      }
    }
  }

  // 이미지 지연 로딩 초기화
  lazyLoadImages();
});