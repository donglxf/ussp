function login() {
	var userName = $.trim($("#userName").val());
	var app = $.trim($("#app").val());
	var password = $.trim($("#password").val());
	userName = app + ";" + userName;

	var formdata = {
		"userName" : userName,
		"password" : password
	};
	$
			.ajax({
				type : "POST",
				url : "http://localhost:9000/api/auth/login",
				contentType : "application/json; charset=utf-8",
				data : JSON.stringify(formdata),
				async : false,
				dataType : "json",
				beforeSend : function(xhr) {
					xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
				},

				success : function(data) {
					var token = data.token;
					var refreshToken = data.refreshToken;
					document.cookie = "token=" + token;
					document.cookie = "refreshToken=" + refreshToken;
					location.href = '/';
				},
				error : function(xhr, exception, errorThrown) {
					if (xhr.status == '401' || xhr.status == '403'
							|| xhr.status == '0') {
						
						alert("用户名或密码错误，请重新登录");
						$(':input','#loginForm').not(':button,:submit,:reset,:hidden').val('').removeAttr('checked').removeAttr('checked')
					}
				}

			});

	return true;

}