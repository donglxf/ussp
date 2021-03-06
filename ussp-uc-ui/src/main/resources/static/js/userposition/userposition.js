var userpositionUserId = "";
var refreshPositionTable;
layui.use(['form', 'ztree', 'table','ht_config', 'ht_auth'], function () {
    var $ = layui.jquery
        , form = layui.form
        , table = layui.table
        , ht_auth = layui.ht_auth
        , config = layui.ht_config
        , addDialog = 0 //新增弹出框的ID
        , viewDialog = 0 //查询弹出框的ID
        , editDialog = 0 //修改弹出框的ID
        , userposiOrgTree //组织机构树控件
        , active = {
    		search: function () { 
            	//执行重载
            	refreshUserTable($("#userposition_user_search_keyword").val());
            },
            searchuserposition: function () { 
            	//执行重载
            	refreshuserpositionTable($("#userposition_position_search_keyword").val());
            },
            loadPositionList:function(){
            	if(userpositionUserId==""){
            		layer.msg("请先选择用户！");
            		return;
            	}
            	 layer.close(addDialog);
            	 positionDialog = layer.open({
                     type: 2,
                     area: ['70%', '80%'],
                     maxmin: true,
                     shadeClose: true,
                     title: "分配岗位",
                     content: "/html/userposition/positionDialog.html",
                     success: function (layero, index) {
                    	 /* 渲染表单 */
                         form.render();
                     }
                 })
                 

            },
    };
    
    var loadUserListUrl=config.basePath + 'user/loadListByPage'; //列出所有用户信息
    var loadUserPositionListUrl=config.basePath + 'userposition/listUserPositionByPage'; //列出用户所有岗位列表
    var delUserPositionListUrl=config.basePath + 'userposition/delete'; //删除用户岗位 /delete/{id}
    var stopUserPositionListUrl=config.basePath + 'userposition/stop'; //禁用/启用用户岗位 /stop/{id}/{status}
    var orgTreeUrl = config.basePath +"org/tree"; //机构列表 
    
    var refreshUserTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
        var selectNodes = userposiOrgTree.getSelectedNodes();
        if (selectNodes && selectNodes.length == 1) {
        	 table.reload('userposition_user_datatable', {
                 page: {
                     curr: 1 //重新从第 1 页开始
                 }
                 , where: {
                     keyWord: keyword,
                     orgCode: selectNodes[0]["orgCode"]
                 }
             });
        	 userpositionUserId="";
        }else{
        	table.reload('userposition_user_datatable', {
                page: {
                    curr: 1 //重新从第 1 页开始
                }
                , where: {
                    keyWord: keyword,
                }
            });
        }
    };
    var refreshuserpositionTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
        table.reload('userposition_position_datatable', {
   		 height: 'full-600',
   	        page: {
   	            curr: 1 //重新从第 1 页开始
   	        }
   	        , where: {
   	        	query: {
             		     keyWord: keyword,
                       //orgCode: selectNodes[0]["orgCode"],
                       userId:userpositionUserId
                  }
   	        }
   	   });
    };
    
    refreshPositionTable = function (sucess) {
    	if(sucess==1){
        	layer.msg("分配岗位成功");
        }else{
        	return;
        }
        var selectNodes = userposiOrgTree.getSelectedNodes();
        if (selectNodes && selectNodes.length == 1) {
        	 table.reload('userposition_position_datatable', {
        		 height: 'full-600',
        	        page: {
        	            curr: 1 //重新从第 1 页开始
        	        }
        	        , where: {
        	        	query: {
                            userId:userpositionUserId
                       }
        	        }
        	   });
        }
    };
   
    //渲染组织机构树
    userposiOrgTree = $.fn.zTree.init($('#userposition_org_ztree_left'), {
            view: {
                showIcon: false
                , selectedMulti: false
                , fontCss: function (treeId, treeNode) {
                    return (!!treeNode.highlight) ? {color: "#A60000", "font-weight": "bold"} : {
                        color: "#333",
                        "font-weight": "normal"
                    };
                }
            }
            , async: {
                enable: true,
                url: orgTreeUrl,
                dataFilter: function (treeId, parentNode, childNodes) {
                    if (!childNodes) return null;
                    for (var i = 0, l = childNodes.length; i < l; i++) {
                      //  childNodes[i].open = true;
                        childNodes[i].name = childNodes[i]["orgNameCn"].replace(/\.n/g, '.');
                    }
                    return childNodes;
                }
            }
            , callback: {
                onClick: function (event, treeId, treeNode, clickFlag) {
                    //执行重载
                	refreshUserTable();
                	refreshuserpositionTable();
                },
                onAsyncSuccess: function (event, treeId, treeNode, msgString) {
                    var node = userposiOrgTree.getNodeByParam("level ", "0");
                    if (node) {
                        userposiOrgTree.selectNode(node);
                    }
                }
            },
            data: {
                simpleData: {
                    enable: true
                    , idKey: "orgCode"
                    , pIdKey: "parentOrgCode"
                }
            }
        }
    );
    //渲染岗位数据表格
    table.render({
        id: 'userposition_user_datatable'
        , elem: '#userposition_user_datatable'
        , url: loadUserListUrl
        , method: 'post' //如果无需自定义HTTP类型，可不加该参数
        , response: {
            statusName: 'returnCode' //数据状态的字段名称，默认：code
            , statusCode: "0000" //成功的状态码，默认：0
            , msgName: 'msg' //状态信息的字段名称，默认：msg
            , countName: 'count' //数据总数的字段名称，默认：count
            , dataName: 'data' //数据列表的字段名称，默认：data
        } //如果无需自定义数据响应名称，可不加该参数
        , page: true
        , height: '310'
        , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
        , cols: [[
            {type: 'numbers'}
            , {field: 'jobNumber', width: 100, title: '工号',event:'getposition'}
            , {field: 'userName', width: 180,   title: '用户名',event:'getposition'}
            , {field: 'mobile', width: 120, title: '手机',event:'getposition'}
            , {field: 'email', width: 180, title: '邮箱',event:'getposition'}
            , {field: 'idNo', width: 180, title: '身份证',event:'getposition'}
            , {field: 'orgName', width: 130, title: '所属机构',event:'getposition'}
            , {field: 'status', width: 60, title: '状态', templet: "#userposition_user_status_laytpl",event:'getposition'}
            , {field: 'updateOperator', width: 100, title: '更新人',event:'getposition'}
            , {field: 'lastModifiedDatetime', width: 150, title: '更新时间',event:'getposition'}
        ]]
    });
    table.render({
        id: 'userposition_position_datatable'
        , elem: '#userposition_position_datatable'
        , url: loadUserPositionListUrl
        , method: 'post' //如果无需自定义HTTP类型，可不加该参数
        , response: {
            statusName: 'returnCode' //数据状态的字段名称，默认：code
            , statusCode: "0000" //成功的状态码，默认：0
            , msgName: 'msg' //状态信息的字段名称，默认：msg
            , countName: 'count' //数据总数的字段名称，默认：count
            , dataName: 'data' //数据列表的字段名称，默认：data
        } //如果无需自定义数据响应名称，可不加该参数
        , page: true
        ,  height: 'full-600'
        , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
        , cols: [[
            {type: 'numbers'}
            , {field: 'positionCode',   title: '岗位编号'}
            , {field: 'positionNameCn',   title: '岗位名称'}
            //, {field: 'porgNameCn',   title: '所属机构'}
            , {field: 'delFlag', width: 100, title: '状态' ,templet: '#userposition_statusTpl'}
            //, {field: 'createOperator', width: 100, title: '创建人'}
            //, {field: 'createdDatetime', width: 200,templet: '#createTimeTpl', title: '创建时间'}
            , {fixed: 'right', width: 178, title: '操作', align: 'center', toolbar: '#userposition_position_datatable_bar'}
        ]]
    });
    
    //监听操作栏
    table.on('tool(filter_userposition_user_datatable)', function (obj) {
        var data = obj.data;
        userpositionUserId = data.userId;
        if (obj.event === 'getposition') {
        	refreshuserpositionTable();
        }  
    });
    table.on('tool(filter_userposition_position_datatable)', function (obj) {
        var data = obj.data;
        if (obj.event === 'startOrStop') {
        	if(data.delFlag==0){//启用状态，是否需要禁用
        		layer.confirm('是否禁用岗位？', function (index) {
                  	 $.post(stopUserPositionListUrl+"?id=" + data.id+"&status=1", null, function (result) {
                           if (result["returnCode"] == "0000") {
                        	   refreshuserpositionTable();
                               layer.close(index);
                               layer.msg("禁用岗位成功");
                           } else {
                               layer.msg(result.codeDesc);
                           }
                       });
                  });
        	}else{
        		layer.confirm('是否启用岗位？', function (index) {
                  	 $.post(stopUserPositionListUrl+"?id=" + data.id+"&status=0", null, function (result) {
                           if (result["returnCode"] == "0000") {
                        	   refreshuserpositionTable();
                               layer.close(index);
                               layer.msg("启用岗位成功");
                           } else {
                               layer.msg(result.codeDesc);
                           }
                       });
                  });
        	}
        } else if (obj.event === 'del') {
        	 layer.confirm('是否确认删除用户岗位？', function (index) {
             	 $.post(delUserPositionListUrl+"?id=" + data.id, null, function (result) {
                      if (result["returnCode"] == "0000") {
                    	  obj.del();
                          layer.close(index);
                          layer.msg("删除用户岗位成功");
                          refreshuserpositionTable();
                      } else {
                          layer.msg(result.codeDesc);
                      }
                  });
             });
        } 
    });
    
    table.on('renderComplete(filter_userposition_user_datatable)', function (obj) {
    	ht_auth.render("userposition_auth");
    });
    table.on('renderComplete(filter_userposition_position_datatable)', function (obj) {
    	ht_auth.render("userposition_auth");
    });
    
    //监听工具栏
    $('#userposition_user_table_tools .layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    
    $('#userposition_position_table_tools .layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    //刷新树的数据
    /*$('#userposition_btn_refresh_tree').on('click', function (e) {
        if (userposiOrgTree) {
            userposiOrgTree.reAsyncChildNodes(null, "refresh");
        }
    });*/
    $('#userposition_btn_tree .btn').on('click', function () {
        var type = $(this).data('type');
        switch (type) {
            case "refresh":
                if (userposiOrgTree) {
                	userposiOrgTree.reAsyncChildNodes(null, "refresh");
                }
                break;
            case "expandAll":
                if (userposiOrgTree) {
                	userposiOrgTree.expandAll(true);
                }
                break;
            case "collapseAll":
                if (userposiOrgTree) {
                	userposiOrgTree.expandAll(false);
                }
                break;
        }
    });
    var nodeList = [];
    //搜索树的数据
    $('#userposition_search_tree_org').bind('input', function (e) {
        if (userposiOrgTree && $(this).val() != "") {
            nodeList = userposiOrgTree.getNodesByParamFuzzy("name", $(this).val());
            updateNodes(true);
        } else {
            updateNodes(false);
        }
    });

    //刷新树节点
    function updateNodes(highlight) {
        for (var i = 0, l = nodeList.length; i < l; i++) {
            nodeList[i].highlight = highlight;
            userposiOrgTree.updateNode(nodeList[i]);
            if (highlight) {
                userposiOrgTree.expandNode(userposiOrgTree.getNodeByParam("orgCode", nodeList[i]["parentOrgCode"]), true, false, null, null);
            }
        }
    }
    ht_auth.render("userposition_auth");
})