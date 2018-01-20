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
					setCookie("token",token,12,"/");
					setCookie("refreshToken",refreshToken,700,"/");
//					document.cookie = "token=" + token;
//					document.cookie = "refreshToken=" + refreshToken;
					location.href = '/';
				},
				error : function(xhr, exception, errorThrown) {
					if (xhr.status == '401' || xhr.status == '403'
							|| xhr.status == '0') {
						
						alert("用户名或密码错误，请重新登录");
						
					}
					if(xhr.status=='500'){
						alert("尚未分配权限，请联系管理员");
					}
					$(':input','#loginForm').not(':button,:submit,:reset,:hidden').val('').removeAttr('checked').removeAttr('checked');
				}

			});

	return true;

}


function logout(){
	deleteCookie("token","/");
	deleteCookie("refreshToken","/");
	window.location.href="/login.html?backurl="+window.location.href; 
	
}

//新建cookie。 
//hours为空字符串时,cookie的生存期至浏览器会话结束。hours为数字0时,建立的是一
//个失效的cookie,这个cookie会覆盖已经建立过的同名、同path的cookie（如果这个
//cookie存在）。 
function setCookie(name,value,times,path){
 var name = escape(name);
 var value = escape(value);
 var expires = new Date(); 
 expires.setMinutes(expires.getMinutes() + times);
 path = path == "" ? "" : ";path=" + path; 
 _expires = (typeof times) == "string" ? "" : ";expires=" + expires.toUTCString();
 document.cookie = name + "=" + value + _expires + path;
} 
//获取cookie值 
function getCookieValue(name){
 var name = escape(name); 
 //读cookie属性，这将返回文档的所有cookie
 var allcookies = document.cookie;      
 //查找名为name的cookie的开始位置
 name += "="; 
 var pos = allcookies.indexOf(name);    
 //如果找到了具有该名字的cookie，那么提取并使用它的值 
 //如果pos值为-1则说明搜索"version="失败
 if (pos != -1){    
		   //cookie值开始的位置    
		   var start = pos + name.length;  
		   //从cookie值开始的位置起搜索第一个";"的位置,即cookie值结尾的位置 	   
		   var end = allcookies.indexOf(";",start); 
		   //如果end值为-1说明cookie列表里只有一个cookie        
		   if (end == -1) end = allcookies.length;   
		   //提取cookie的值	   
		   var value = allcookies.substring(start,end);  
		   //对它解码   
		   return unescape(value);                            
     }   
 else return "";                                             
} 
//删除cookie 
function deleteCookie(name,path){
 var name = escape(name);
 var expires = new Date(0); 
 path = path == "" ? "" : ";path=" + path; 
 document.cookie = name + "="+ ";expires=" + expires.toUTCString() + path;
} 
  
    