document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('postForm');
  const submitBtn = document.querySelector('.submit-btn');

  $('#summernote').summernote({
    placeholder: '내용을 입력해주세요',
    height: 300,
    lang: 'ko-KR',
    toolbar: [
      ['style', ['bold', 'italic', 'underline', 'clear']],
      ['font', ['strikethrough', 'superscript', 'subscript']],
      ['fontsize', ['fontsize']],
      ['para', ['ul', 'ol', 'paragraph']],
      ['table', ['table']],
      ['insert', ['picture']],
      ['view', ['help']]
    ],
    callbacks: {
      onImageUpload: function (files) {
        for (let i = 0; i < files.length; i++) {
          uploadImage(files[i]);
        }
      }
    }
  });

  function uploadImage(file) {
    const formData = new FormData();
    formData.append('image', file);

    fetch('/uploadImage', {
      method: 'POST',
      body: formData
    })
      .then(res => res.text()) // 서버에서 이미지 URL 문자열 반환
      .then(imageUrl => {
        $('#summernote').summernote('insertImage', imageUrl);
      })
      .catch(err => {
        console.error('이미지 업로드 실패', err);
        alert('이미지 업로드 중 오류 발생');
      });
  }

});