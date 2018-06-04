layui.use(['form',   'table', 'ht_config','ht_auth' ], function () {
    var $ = layui.jquery
        , form = layui.form
        , table = layui.table
        , config = layui.ht_config
        , ht_auth = layui.ht_auth
        , addDialog = 0 //新增弹出框的ID
        , viewDialog = 0 //查询弹出框的ID
        , editDialog = 0 //修改弹出框的ID
        , active = {
	        search: function () {
	            //执行重载
	        	refreshSynDdUserTable($("#dduser_search_keyword").val());
	        }
    	};
    
    var dduserListByPageUrl=config.basePath +"syndata/loadSynUserListByPage"; //列出所有系统记录列表信息  
    var adddduserUrl=config.basePath +"syndata/synUserDataToUc"; //添加系统信息
    
    var refreshSynDdUserTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
        table.reload('dduser_datatable', {
            page: {
                curr: 1 //重新从第 1 页开始
            }
            , where: {
            	query:{
            		keyWord: keyword
            	}
            }
        });
    };
    //渲染用户数据表格
    table.render({
        id: 'dduser_datatable'
        , elem: '#dduser_datatable'
        , url: dduserListByPageUrl
        , method: 'post' //如果无需自定义HTTP类型，可不加该参数
        , page: true
        , height: 'full-200'
        , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
        , cols: [[
            {type: 'numbers'}
            , {field: 'userId',  title: '钉钉用户id'}
            , {field: 'userName',   title: '用户名称'}
            , {field: 'mobile',     title: '手机号码'}
            , {field: 'email',   title: '邮箱'}
            , {field: 'jobNumber',   title: '工号'}
            , {field: 'deptId',    title: '所属机构'}
            , {field: 'position',  title: '岗位'}
            , {field: 'operatorType', templet: '#operatorTpl',   title: '操作'}
            , {field: 'status',  templet: '#synStatusTpl', title: '状态'}
            , {fixed: 'right',   title: '操作',   toolbar: '#dduser_datatable_bar'}
        ]]
    });
    //监听操作栏
    table.on('tool(filter_dduser_datatable)', function (obj) {
        var data = obj.data;
        if (obj.event === 'syndduser') {
        	$.post(adddduserUrl+"?id=" + data.id, null, function (result) {
                if (result["returnCode"] == "0000") {
              	    refreshSynDdUserTable();
                    layer.msg("同步成功");
                } else {
                    layer.msg(result.codeDesc);
                }
            });
        }   
    });
    table.on('renderComplete(filter_dduser_datatable)', function (obj) {
    	ht_auth.render("dduser_auth");
    });
    //监听工具栏
    $('#dduser_table_tools .layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    var $keywordInput = $("#dduser_search_keyword");
    $keywordInput.keydown(function (e) {
        if (e.keyCode == 13) {
            var keyWord = $keywordInput.val();
            refreshSynDdUserTable(keyWord);
        }
    });
    
    ht_auth.render("dduser_auth");

})