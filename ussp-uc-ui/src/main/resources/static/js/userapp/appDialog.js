var appListByPageUrl=basepath +"userapp/listAppByPage"; //列出所有角色记录列表信息  
var addUserAppListUrl=basepath + 'userapp/add'; //禁用/启用用户角色 /stop/{id}/{status}

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
        	 table.reload('userapp_add_data', {
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
        id: 'userapp_add_data'
        , elem: '#userapp_add_data'
        , url: appListByPageUrl
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
        	 , {field: 'app', width: 120, title: '系统编号'}
             , {field: 'nameCn', title: '系统名称'}
             , {field: 'delFlag', width: 100, title: '状态' ,templet: '#userapp_statusTpl'}
             , {field: 'createOperator', width: 100, title: '创建人'}
             , {field: 'createdDatetime', width: 200,templet: '#createTimeTpl', title: '创建时间'}
        ]]
    });
   
    $("#a_close").on('click',function() {
		var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
		parent.layer.close(index); //再执行关闭
	});
    
    $("#a_check").on('click',function() {
    	 var checkStatus = table.checkStatus('userapp_add_data');
    	 var roledata = checkStatus.data;
    	 if(roledata.length>0){
    			 $.each(roledata, function (name, value) {
                     $("#app").val(value.app);
            		 $("#userId").val(parent.userId);
            		 form.render(null, "filter_add_app_form");
            		 form.on('submit(filter_add_app_form)', function (data) {
                         $.ajax({
                             type: "POST",
                             url: addUserAppListUrl,
                             data: JSON.stringify(data.field),
                             contentType: "application/json; charset=utf-8",
                             success: function (message) {
                            	 parent.refreshAppTable(1);
                            	 var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                         		 parent.layer.close(index); //再执行关闭
                             },
                             error: function (message) {
                                 layer.msg("关联系统发生异常，请联系管理员。");
                             }
                         });
                         return false;
                     });
            		 var $submitBtn = $("button[lay-filter=filter_add_app_form]");
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