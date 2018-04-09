

layui.config({
    base: '/plugins/layui/extend/modules/',
    version: false
}).use(['app', 'message', 'ht_ajax', 'ht_cookie', 'ht_auth'],function () {
	var token = getvl("token");
	var url = getvl("url");
    var $ = layui.jquery
        , form = layui.form
        , config = layui.ht_config
        , table = layui.table
        , viewDialog = 0 //查询弹出框的ID
        , editDialog = 0 //修改弹出框的ID
        , active = {
	        userInfoDetail:function(){
	        	$.ajax({
			        cache : true,
			        type : "POST",
			        url :userBaseInfoUrl+"?token="+token,
			        contentType: 'application/json;charset=utf-8',
			       // data: {"token":token},    
			        async : false,
			        success : function(result) {
			        	 //表单数据填充
	                     $.each(result.data, function (name, value) {
	                         var $input = $("input[name=" + name + "]");
	                         if ($input) {
	                             $input.val(value);
	                         }
	                     });
			        }
			    });
	        }
    };
    
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
	 
    var userBaseInfoUrl=config.basePath +"user/getUserForOther"; //查看登录用户信息
    var editUserBaseInfoUrl=config.basePath +"user/in/selfinfo/set"; //查看登录用户信息
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
 // 监听提交
	form.on('submit(userInfo_filter_modify_form)', function(data) {
		$.ajax({
			cache : true,
			type : "POST",
			url : editUserBaseInfoUrl,
			data: JSON.stringify(data.field),
            contentType: "application/json; charset=utf-8",
			async : false,
			success : function(result) {
				 $.each(result.result, function (name, value) {
                     var $input = $("input[name=" + name + "]");
                     if ($input && $input.length == 1) {
                         $input.val(value);
                     }
                 });
				 layer.msg("保存成功");
			}
		});
		return false;
	});
    active.userInfoDetail();
})