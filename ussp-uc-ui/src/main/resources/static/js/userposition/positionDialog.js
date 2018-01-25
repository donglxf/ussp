
layui.config({
    base: '/js/modules/',
    version: false
}).use(['app', 'message', 'ht_ajax', 'ht_cookie', 'ht_auth'],function () {
    var $ = layui.jquery
        , form = layui.form
        , config = layui.ht_config
        ,cookie = layui.ht_cookie
        , table = layui.table;
    
    var positionListByPageUrl=config.basePath  +"position/in/list.json"; //列出所有岗位记录列表信息  
    var addUserPositionListUrl=config.basePath  + 'userposition/add'; //禁用/启用用户角色 /stop/{id}/{status}
    
    var loadPositionListTable = function (keyword) {
        var selectNodes = orgTree.getSelectedNodes();
        if (selectNodes && selectNodes.length == 1) {
        	 table.reload('userposition_add_data', {
        	        page: {
        	            curr: 1 //重新从第 1 页开始
        	        }
        	        , where: {
        	        	query: {
                   		  keyWord: keyword,
                          orgCode: 'HT'
                        }
        	        }
        	   });
        }
    };
  //渲染用户数据表格
    table.render({
        id: 'userposition_add_data'
        , elem: '#userposition_add_data'
        , url: positionListByPageUrl
        , method: 'post' //如果无需自定义HTTP类型，可不加该参数
        	, where: {
	        	query: {
                  orgCode: 'HT'
                }
	        }
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
        	 , {field: 'positionCode', width: 120, title: '岗位编号'}
             , {field: 'positionNameCn',   title: '岗位名称'}
             , {field: 'porgNameCn', width: 220, title: '所属机构'}
             , {field: 'delFlag', width: 100, title: '状态' ,templet: '#statusTpl'}
             , {field: 'createOperator', width: 100, title: '创建人'}
             , {field: 'createdDatetime', width: 200,templet: '#createTimeTpl', title: '创建时间'}
        ]]
    });
   
    $("#a_close").on('click',function() {
		var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
		parent.layer.close(index); //再执行关闭
	});
    
    $("#a_check").on('click',function() {
    	 var checkStatus = table.checkStatus('userposition_add_data');
    	 var postiondata = checkStatus.data;
    	 if(postiondata.length>0){
    		 $.each(postiondata, function (name, value) {
                 $("#positionCode").val(value.positionCode);
        		 $("#userId").val(parent.userId);
        		 form.render(null, "filter_add_position_form");
        		 form.on('submit(filter_add_position_form)', function (data) {
                     $.ajax({
                         type: "POST",
                         url: addUserPositionListUrl,
                         data: JSON.stringify(data.field),
                         contentType: "application/json; charset=utf-8",
                         success: function (message) {
                        	 parent.refreshPositionTable(1);
                        	 var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                     		 parent.layer.close(index); //再执行关闭
                         },
                         error: function (message) {
                             layer.msg("分配岗位发生异常，请联系管理员。");
                         }
                     });
                     return false;
                 });
        		 var $submitBtn = $("button[lay-filter=filter_add_position_form]");
                 if ($submitBtn) {
                     $submitBtn.click();
                 } else {
                     throw new Error("没有找到submit按钮。");
                 }
             });
     	 }else{
    		 layer.msg("请选中分配的岗位");
    	 }
         
	});

})