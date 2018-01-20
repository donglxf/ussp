var roleListByPageUrl=basepath +"role/in/list"; //列出所有角色记录列表信息  
var addUserRoleListUrl=basepath + 'userrole/add'; //禁用/启用用户角色 /stop/{id}/{status}
var addPositionRoleListUrl=basepath + 'positionrole/add'; //禁用/启用用户角色 /stop/{id}/{status}

layui.use(['form', 'table' ], function () {
    var $ = layui.jquery
        , form = layui.form
        , table = layui.table;
    
    var loadRoleListTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
        var selectNodes = orgTree.getSelectedNodes();
        if (selectNodes && selectNodes.length == 1) {
        	 table.reload('userrole_add_data', {
        	        page: {
        	            curr: 1 //重新从第 1 页开始
        	        }
        	        , where: {
        	        	query: {
                  		    keyWord: keyword,
                            orgCode: selectNodes[0]["orgCode"],
                            userId:userId
                       }
        	        }
        	   });
        }
    };
  //渲染用户数据表格
    table.render({
        id: 'userrole_add_data'
        , elem: '#userrole_add_data'
        , url: roleListByPageUrl
        , method: 'post' //如果无需自定义HTTP类型，可不加该参数
        , response: {
            statusName: 'returnCode' //数据状态的字段名称，默认：code
            , statusCode: "0000" //成功的状态码，默认：0
            , msgName: 'msg' //状态信息的字段名称，默认：msg
            , countName: 'count' //数据总数的字段名称，默认：count
            , dataName: 'data' //数据列表的字段名称，默认：data
        } //如果无需自定义数据响应名称，可不加该参数
        , page: true
        , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
        , cols: [[
        	 {type:'checkbox'}
            , {field: 'roleCode', width: 150, title: '角色编号'}
            , {field: 'roleNameCn',  title: '角色名称'}
            , {field: 'status', width: 100,templet: '#statusTpl', title: '状态'}
            , {field: 'createOperator', width: 150, title: '创建人'}
            , {field: 'createdDatetime', width: 200,templet: '#createTimeTpl', title: '创建时间'}
        ]]
    });
   
    $("#a_close").on('click',function() {
		var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
		parent.layer.close(index); //再执行关闭
	});
    
    $("#a_check").on('click',function() {
    	 var checkStatus = table.checkStatus('userrole_add_data');
    	 var roledata = checkStatus.data;
    	 if(roledata.length>0){
    			 $.each(roledata, function (name, value) {
                     $("#roleCode").val(value.roleCode);
            		 $("#userId").val(parent.userId);
            		 form.render(null, "filter_add_role_form");
            		 form.on('submit(filter_add_role_form)', function (data) {
                         $.ajax({
                             type: "POST",
                             url: addUserRoleListUrl,
                             data: JSON.stringify(data.field),
                             contentType: "application/json; charset=utf-8",
                             success: function (message) {
                            	 parent.refreshRoleTable(1);
                            	 var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                         		 parent.layer.close(index); //再执行关闭
                             },
                             error: function (message) {
                                 layer.msg("分配角色发生异常，请联系管理员。");
                             }
                         });
                         return false;
                     });
            		 var $submitBtn = $("button[lay-filter=filter_add_role_form]");
                     if ($submitBtn) {
                         $submitBtn.click();
                     } else {
                         throw new Error("没有找到submit按钮。");
                     }
                 });
    		
     	 }else{
    		 layer.msg("请选中分配的角色");
    	 }
         
	});

})