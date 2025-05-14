// 탭 전환
document.getElementById('findIdTab').addEventListener('click', function() {
  window.location.href = 'find-id.html';
});

document.getElementById('findPwTab').addEventListener('click', function() {
  window.location.href = 'find-password.html';
});

// 사용자 확인 폼 제출
document.getElementById('verifyUserForm').addEventListener('submit', function(e) {
  e.preventDefault();
  // 여기서 사용자 정보 검증 로직이 들어갑니다
  // 검증 성공 시 비밀번호 재설정 폼으로 전환
  document.getElementById('verifyUserForm').style.display = 'none';
  document.getElementById('resetPasswordForm').style.display = 'block';
});

// 비밀번호 재설정 폼 제출
document.getElementById('resetPasswordForm').addEventListener('submit', function(e) {
  e.preventDefault();
  
  const newPassword = document.querySelectorAll('#resetPasswordForm input[type="password"]')[0].value;
  const confirmPassword = document.querySelectorAll('#resetPasswordForm input[type="password"]')[1].value;
  
  if (newPassword !== confirmPassword) {
    document.getElementById('passwordError').style.display = 'block';
    return;
  }
  
  // 비밀번호 변경 로직
  // 성공 시 로그인 페이지로 이동
  window.location.href = 'login.html';
});

// 비밀번호 일치 체크
const passwordInputs = document.querySelectorAll('#resetPasswordForm input[type="password"]');
const passwordError = document.getElementById('passwordError');

if (passwordInputs.length >= 2) {
  passwordInputs[1].addEventListener('input', function() {
    if (passwordInputs[0].value !== passwordInputs[1].value) {
      passwordError.style.display = 'block';
    } else {
      passwordError.style.display = 'none';
    }
  });
}