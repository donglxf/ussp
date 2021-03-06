

layui.config({
    base: '/plugins/layui/extend/modules/',
    version: false
}).use(['app', 'message', 'ht_ajax', 'ht_cookie', 'ht_auth'],function () {
    var $ = layui.jquery
        , form = layui.form
        , config = layui.ht_config
        ,cookie = layui.ht_cookie
        , table = layui.table
        , active = {
                search: function () {
                 //执行重载
                	refreshTable($("#userrole_roleDialog_search_keyword").val());
                }
            };
        
    var roleListByPageUrl=config.basePath +"role/userRolelist"; //列出所有角色记录列表信息  
    var addUserRoleListUrl=config.basePath + 'userrole/add'; //禁用/启用用户角色 /stop/{id}/{status}
    
  //渲染用户数据表格
    table.render({
        id: 'userrole_roleDialog_data'
        , elem: '#userrole_roleDialog_data'
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
        , where: {
	        	query: {
                 userId:parent.userrole_userId
            }
	        }
        , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
        , cols: [[
        	 {type:'checkbox'}
            , {field: 'roleCode', width: 150, title: '角色编号'}
            , {field: 'roleNameCn',  title: '角色名称'}
            //, {field: 'app',  title: '所属系统'}
            , {field: 'status', width: 100,templet: '#statusTpl', title: '状态'}
            , {field: 'createOperator', width: 150, title: '创建人'}
            , {field: 'createdDatetime', width: 200,templet: '#createTimeTpl', title: '创建时间'}
        ]]
    });
    var $keywordInput = $("#userrole_roleDialog_search_keyword");
    $keywordInput.keydown(function (e) {
        if (e.keyCode == 13) {
            var keyWord = $keywordInput.val();
            refreshTable(keyWord);
        }
    });
    var refreshTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
    	table.reload('userrole_roleDialog_data', {
        	height: 'full-200'
            , page: {
                curr: 1 //重新从第 1 页开始
            }
            , where: {
            	 query: {
            		 keyWord: keyword
                 }
            }
        });
    };
  //监听工具栏
    $('#userrole_roleDialog_table_tools .layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
   
    $("#a_close").on('click',function() {
		var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
		parent.layer.close(index); //再执行关闭
	});
    
    $("#a_check").on('click',function() {
    	 var checkStatus = table.checkStatus('userrole_roleDialog_data');
    	 var roledata = checkStatus.data;
    	 if(roledata.length>0){
    			 $.each(roledata, function (name, value) {
                     $("#roleCode").val(value.roleCode);
            		 $("#userId").val(parent.userrole_userId);
            		 form.render(null, "filter_add_userrole_roleDialog_form");
            		 form.on('submit(filter_add_userrole_roleDialog_form)', function (data) {
                         $.ajax({
                             type: "POST",
                             url: addUserRoleListUrl,
                             data: JSON.stringify(data.field),
                             contentType: "application/json; charset=utf-8",
                             success: function (message) {
                            	 parent.refreshUserRoleTable();
                            	 var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                         		 parent.layer.close(index); //再执行关闭
                             },
                             error: function (message) {
                                 layer.msg("分配角色发生异常，请联系管理员。");
                             }
                         });
                         return false;
                     });
            		 var $submitBtn = $("button[lay-filter=filter_add_userrole_roleDialog_form]");
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