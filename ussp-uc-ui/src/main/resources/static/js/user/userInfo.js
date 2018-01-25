
var userId=10001; 
layui.use(['form',   'table' , 'ht_config'], function () {
    var $ = layui.jquery
        , form = layui.form
        , config = layui.ht_config
        , table = layui.table
        , viewDialog = 0 //查询弹出框的ID
        , editDialog = 0 //修改弹出框的ID
        , active = {
	        userInfoDetail:function(){
	        	 $.get(userBaseInfoUrl + "/" + userId, null, function (result) {
	        		 //表单数据填充
                     $.each(result.result, function (name, value) {
                         var $input = $("input[name=" + name + "]");
                         if ($input && $input.length == 1) {
                             $input.val(value);
                         }
                     });
	        	 });
	        }
    };
    var userBaseInfoUrl=config.basePath +"user/in/selfinfo"; //查看登录用户信息
    var editUserBaseInfoUrl=config.basePath +"user/in/selfinfo/set"; //查看登录用户信息
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