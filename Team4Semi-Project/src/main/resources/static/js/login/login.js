document.getElementById('loginForm').addEventListener('submit', function(e) {
  e.preventDefault();
  // 여기에 로그인 로직을 추가할 수 있습니다.
  // 테스트를 위해 로그인 오류 메시지를 보여줍니다.
  document.getElementById('loginError').style.display = 'block';
});