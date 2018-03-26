
var userrole_userId = "";
var refreshRoleTable;
layui.use(['form', 'ztree', 'table','ht_config', 'ht_auth'], function () {
    var $ = layui.jquery
        , form = layui.form
        , ht_auth = layui.ht_auth
        , table = layui.table
        , config = layui.ht_config
        , addDialog = 0 //新增弹出框的ID
        , viewDialog = 0 //查询弹出框的ID
        , editDialog = 0 //修改弹出框的ID
        , userRoleOrgTree //组织机构树控件
        , active = {
    		search: function () { 
            	//执行重载
            	refreshUserTable($("#userrole_user_search_keyword").val());
            },
            searchUserRole: function () { 
            	//执行重载
            	refreshUserRoleTable($("#userrole_role_search_keyword").val());
            },
            loadRoleList:function(){
            	if(userrole_userId==""){
            		layer.msg("请先选择用户！");
            		return;
            	}
            	 layer.close(addDialog);
            	 roleDialog = layer.open({
                     type: 2,
                     area: ['70%', '80%'],
                     maxmin: true,
                     shadeClose: true,
                     title: "分配角色",
                     content: "/html/userrole/roleDialog.html",
                     success: function (layero, index) {
                    	 /* 渲染表单 */
                         form.render();
                     }
                 })
            },
    };
    var loadUserListUrl=config.basePath + 'user/loadListByPage'; //列出所有用户信息
    var loadUserRoleListUrl=config.basePath + 'userrole/listUserRoleByPage'; //列出用户所有角色列表
    var delUserRoleListUrl=config.basePath + 'userrole/delete'; //删除用户角色 /delete/{id}
    var stopUserRoleListUrl=config.basePath + 'userrole/stop'; //禁用/启用用户角色 /stop/{id}/{status}
    var roleListByPageUrl=config.basePath +"role/in/list"; //列出所有角色记录列表信息  
    var orgTreeUrl = config.basePath +"org/tree"; //机构列表 
    
    var refreshUserTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
        var selectNodes = userRoleOrgTree.getSelectedNodes();
        if (selectNodes && selectNodes.length == 1) {
        	 table.reload('userrole_user_datatable', {
                 page: {
                     curr: 1 //重新从第 1 页开始
                 }
                 , where: {
                     keyWord: keyword,
                     orgCode: selectNodes[0]["orgCode"]
                 }
             });
        	 userrole_userId = "";
        }else{
        	table.reload('userrole_user_datatable', {
                page: {
                    curr: 1 //重新从第 1 页开始
                }
                , where: {
                    keyWord: keyword,
                }
            });
        }
    };
    var refreshUserRoleTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
        table.reload('userrole_role_datatable', {
  		  height: 'full-600',
  	        page: {
  	            curr: 1 //重新从第 1 页开始
  	        }
  	        , where: {
  	        	query: {
            		    keyWord: keyword,
                      userId:userrole_userId
                 }
  	        }
  	   });
       /* var selectNodes = userRoleOrgTree.getSelectedNodes();
        if (selectNodes && selectNodes.length == 1) {
        	 table.reload('userrole_role_datatable', {
        		  height: 'full-600',
        	        page: {
        	            curr: 1 //重新从第 1 页开始
        	        }
        	        , where: {
        	        	query: {
                  		     keyWord: keyword,
                            orgCode: selectNodes[0]["orgCode"],
                            userId:userrole_userId
                       }
        	        }
        	   });
        }*/
    };
    refreshRoleTable = function (sucess) {
        if(sucess==1){
        	layer.msg("分配角色成功");
        }else{
        	return;
        }
        var selectNodes = userRoleOrgTree.getSelectedNodes();
        if (selectNodes && selectNodes.length == 1) {
        	 table.reload('userrole_role_datatable', {
        		  height: 'full-600',
        	        page: {
        	            curr: 1 //重新从第 1 页开始
        	        }
        	        , where: {
        	        	query: {
                            orgCode: selectNodes[0]["orgCode"],
                            userId:userrole_userId
                       }
        	        }
        	   });
        }
    };
   
    //渲染组织机构树
    userRoleOrgTree = $.fn.zTree.init($('#userrole_org_ztree_left'), {
            view: {
            	height: "full-183",
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
                	refreshUserRoleTable();
                },
                onAsyncSuccess: function (event, treeId, treeNode, msgString) {
                    var node = userRoleOrgTree.getNodeByParam("level ", "0");
                    if (node) {
                        userRoleOrgTree.selectNode(node);
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
        id: 'userrole_user_datatable'
        , elem: '#userrole_user_datatable'
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
            , {field: 'jobNumber', width: 100, title: '工号',event:'getRole'}
            , {field: 'userName', width: 180,   title: '用户名',event:'getRole'}
            , {field: 'mobile', width: 120, title: '手机',event:'getRole'}
            , {field: 'email', width: 180, title: '邮箱',event:'getRole'}
            , {field: 'idNo', width: 180, title: '身份证',event:'getRole'}
            , {field: 'orgName', width: 130, title: '所属机构',event:'getRole'}
            , {field: 'status', width: 60, title: '状态', templet: "#userrole_user_status_laytpl",event:'getRole'}
            , {field: 'updateOperator', width: 100, title: '更新人',event:'getRole'}
            , {field: 'lastModifiedDatetime', width: 150, title: '更新时间',event:'getRole'}
        ]]
    });
    table.render({
        id: 'userrole_role_datatable'
        , elem: '#userrole_role_datatable'
        , url: loadUserRoleListUrl
        , method: 'post' //如果无需自定义HTTP类型，可不加该参数
        , response: {
            statusName: 'returnCode' //数据状态的字段名称，默认：code
            , statusCode: "0000" //成功的状态码，默认：0
            , msgName: 'msg' //状态信息的字段名称，默认：msg
            , countName: 'count' //数据总数的字段名称，默认：count
            , dataName: 'data' //数据列表的字段名称，默认：data
        } //如果无需自定义数据响应名称，可不加该参数
        , page: true
        , height: 'full-600'
        	 , where: {
 	        	query: {
                     userId:userrole_userId
                }
 	        }
        , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
        , cols: [[
            {type: 'numbers'}
            , {field: 'roleCode',   title: '角色编号'}
            , {field: 'roleNameCn',   title: '角色名称'}
            , {field: 'status', width: 100,templet: '#userrole_statusTpl', title: '状态'}
           // , {field: 'createOperator', width: 150, title: '创建人'}
           // , {field: 'createdDatetime', width: 200,templet: '#createTimeTpl', title: '创建时间'}
            , {fixed: 'right', width: 178, title: '操作', align: 'center', toolbar: '#userrole_role_datatable_bar'}
        ]]
    });
    
    //监听操作栏
    table.on('tool(filter_userrole_user_datatable)', function (obj) {
        var data = obj.data;
        userrole_userId = data.userId;
        if (obj.event === 'getRole') {
        	refreshUserRoleTable();
        }  
    });
    table.on('tool(filter_userrole_role_datatable)', function (obj) {
        var data = obj.data;
        if (obj.event === 'startOrStop') {
        	if(data.delFlag==0){//启用状态，是否需要禁用
        		layer.confirm('是否禁用角色？', function (index) {
                  	 $.post(stopUserRoleListUrl+"?id=" + data.id+"&status=0", null, function (result) {
                           if (result["returnCode"] == "0000") {
                        	   refreshUserRoleTable();
                               layer.close(index);
                               layer.msg("禁用角色成功");
                           } else {
                               layer.msg(result.codeDesc);
                           }
                       });
                  });
        	}else{
        		layer.confirm('是否启用角色？', function (index) {
                  	 $.post(stopUserRoleListUrl+"?id=" + data.id+"&status=0", null, function (result) {
                           if (result["returnCode"] == "0000") {
                        	   refreshUserRoleTable();
                               layer.close(index);
                               layer.msg("启用角色成功");
                           } else {
                               layer.msg(result.codeDesc);
                           }
                       });
                  });
        	}
        } else if (obj.event === 'del') {
        	 layer.confirm('是否确认删除用户角色？', function (index) {
             	 $.post(delUserRoleListUrl+"?id=" + data.id, null, function (result) {
                      if (result["returnCode"] == "0000") {
                    	  obj.del();
                    	  refreshUserRoleTable();
                          layer.close(index);
                          layer.msg("删除用户角色成功");
                      } else {
                          layer.msg(result.codeDesc);
                      }
                  });
             });
        } 
    });
    
    table.on('renderComplete(filter_userrole_user_datatable)', function (obj) {
    	ht_auth.render("userrole_auth");
    });
    table.on('renderComplete(filter_userrole_role_datatable)', function (obj) {
    	ht_auth.render("userrole_auth");
    });
    
    //监听工具栏
    $('#userrole_user_table_tools .layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    
    $('#userrole_role_table_tools .layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    //刷新树的数据
    /*$('#userrole_btn_refresh_tree').on('click', function (e) {
        if (userRoleOrgTree) {
            userRoleOrgTree.reAsyncChildNodes(null, "refresh");
        }
    });*/
    $('#userrole_btn_tree .btn').on('click', function () {
        var type = $(this).data('type');
        switch (type) {
            case "refresh":
                if (userRoleOrgTree) {
                	userRoleOrgTree.reAsyncChildNodes(null, "refresh");
                }
                break;
            case "expandAll":
                if (userRoleOrgTree) {
                	userRoleOrgTree.expandAll(true);
                }
                break;
            case "collapseAll":
                if (userRoleOrgTree) {
                	userRoleOrgTree.expandAll(false);
                }
                break;
        }
    });
    var nodeList = [];
    //搜索树的数据
    $('#userrole_search_tree_org').bind('input', function (e) {
        if (userRoleOrgTree && $(this).val() != "") {
            nodeList = userRoleOrgTree.getNodesByParamFuzzy("name", $(this).val());
            updateNodes(true);
        } else {
            updateNodes(false);
        }
    });

    //刷新树节点
    function updateNodes(highlight) {
        for (var i = 0, l = nodeList.length; i < l; i++) {
            nodeList[i].highlight = highlight;
            userRoleOrgTree.updateNode(nodeList[i]);
            if (highlight) {
                userRoleOrgTree.expandNode(userRoleOrgTree.getNodeByParam("orgCode", nodeList[i]["parentOrgCode"]), true, false, null, null);
            }
        }
    }
    ht_auth.render("userrole_auth");
})