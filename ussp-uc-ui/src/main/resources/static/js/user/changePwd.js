layui.use(['form',   'table' ,'ht_config'], function () {
    var $ = layui.jquery
        , form = layui.form
        , config = layui.ht_config
        , table = layui.table;
    //自定义验证规则
	 form.verify({
		  newPwd : function(value) {
			  if(value.length<6){
				  return "长度必须大于6";
			  }
			  var newPwd = $("#newPwd").val();
			  var oldPwd = $("#oldPwd").val();
			  if(newPwd){
				  if(oldPwd==newPwd){
					  return "新密码不能与旧密码相同";
				  }
				  var reg = /^[a-zA-Z]\w{5,17}$/;
				  if (!reg.test(newPwd)) {
					  return "以字母开头，长度在6-18之间，只能包含字符、数字和下划线";
				  }
			  }
		  },
		  confirmPwd : function(value) {
			  var newPwd = $("#newPwd").val();
			  var newPwdConfirm = $("#newPwdConfirm").val();
			  if(newPwd){
				  if(newPwdConfirm!=newPwd){
					  return "确认密码请与新密码保持一致";
				  }
			  }
		  },
	});
	 var changePwdUrl=config.basePath +"user/in/changePwd"; //修改密码

 // 监听提交
	form.on('submit(changePwd_filter_modify_form)', function(data) {
		$.ajax({
			cache : true,
			type : "POST",
			url : changePwdUrl,
			data: JSON.stringify(data.field),
            contentType: "application/json; charset=utf-8",
			async : false,
			success : function(result) {
				 if(result.status_code==500){
					 layer.msg(result.result_msg);
				 }else{
					 layer.msg("修改成功");
				 }
			}
		});
		return false;
	});
})