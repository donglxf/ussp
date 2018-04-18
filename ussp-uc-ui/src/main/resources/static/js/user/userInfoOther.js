

layui.config({
    base: '/plugins/layui/extend/modules/',
    version: false
}).use(['app', 'message', 'ht_cookie'], function () {
	var token = getvl("token");
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
		//校验工号是否已经存在
		  checkExistJobNum : function(value) {
			  var isExist="1";
			  if(value){
					  $.ajax({
						  url : config.basePath + "user/checkUserExist?jobnum=" + value+"&userId="+$("#userId").val(),
						type : 'POST',
						async : false,
						success : function(result) {
							if (result["returnCode"] == "0000") {
								isExist="";
						    } else{
						    	isExist = "1";
						    }
						}
					});
			  }
			  if(isExist=="1"){
				  return "工号已存在或不可用";
			  } 
		  },
		//校验用户名是否已经存在
		  checkExistLoginId : function(value) {
			  var isExist="1";
			  if(value){
					  $.ajax({
						  url : config.basePath + "user/checkUserExist?loginid=" + value+"&userId="+$("#userId").val(),
						type : 'POST',
						async : false,
						success : function(result) {
							if (result["returnCode"] == "0000") {
								isExist="";
						    } else{
						    	isExist = "1";
						    }
						}
					});
			  }
			  if(isExist=="1"){
				  return "用户登录ID已存在或不可用";
			  } 
		  },
		//校验手机号是否已经存在
		  checkExistMobile : function(value) {
			  var isExist="1";
			  if(value){
					  $.ajax({
						  url : config.basePath + "user/checkUserExist?mobile=" + value+"&userId="+$("#userId").val(),
						type : 'POST',
						async : false,
						success : function(result) {
							if (result["returnCode"] == "0000") {
								isExist="";
						    } else{
						    	isExist = "1";
						    }
						}
					});
			  }
			  if(isExist=="1"){
				  return "手机号已存在或不可用";
			  } 
		  },
		//校验邮箱是否已经存在
		  checkExistEmail : function(value) {
			  var isExist="";
			  if(value){
					  $.ajax({
						url : config.basePath + "user/checkUserExist?email=" + value+"&userId="+$("#userId").val(),
						type : 'POST',
						async : false,
						success : function(result) {
							if (result["returnCode"] == "0000") {
								isExist="";
						    } else{
						    	isExist = "1";
						    }
						}
					}); 
			  }
			  if(isExist=="1"){
				  return "邮箱已存在或不可用";
			  } 
		  },
		  
	});
	 
    var userBaseInfoUrl=config.basePath +"web/getUserForOther"; //查看登录用户信息
    var editUserBaseInfoUrl=config.basePath +"web/updateUserForOther"; //查看登录用户信息

 // 监听提交
	form.on('submit(userInfo_filter_modify_form)', function(data) {
		$.ajax({
			cache : true,
			type : "POST",
			url : editUserBaseInfoUrl + "?token=" + token,
			data: JSON.stringify(data.field),
            contentType: "application/json; charset=utf-8",
			async : false,
			success : function(result) {
				if (result["returnCode"] == "0000") {
					layer.alert("保存成功");
					/*layer.confirm('保存成功', {
	             		btn: ['确认'] 
	             		}, function(index, layero){
	             			var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
	       				    parent.layer.close(index); //再执行关闭 
	             		});*/
			    } else{
			    	layer.alert(result["msg"]);
			    }
				 
			}
		});
		return false;
	});
    active.userInfoDetail();
})