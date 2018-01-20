var loadUserListUrl=basepath + 'user/loadListByPage.json'; //列出所有用户信息
var loadUserAppListUrl=basepath + 'userapp/listUserAppByPage.json'; //列出用户所有系统
var delUserAppListUrl=basepath + 'userapp/delete'; //删除用户角色 /delete/{id}
var stopUserAppListUrl=basepath + 'userapp/stop'; //禁用/启用用户角色 /stop/{id}/{status}
var orgTreeUrl = basepath +"/org/tree.json"; //机构列表 
var userId = "";
var refreshAppTable = "";
layui.use(['form', 'ztree', 'table'], function () {
    var $ = layui.jquery
        , form = layui.form
        , table = layui.table
        , addDialog = 0 //新增弹出框的ID
        , viewDialog = 0 //查询弹出框的ID
        , editDialog = 0 //修改弹出框的ID
        , orgTree //组织机构树控件
        , active = {
    		search: function () { 
            	//执行重载
            	refreshUserTable($("#userapp_user_search_keyword").val());
            },
            searchuserapp: function () { 
            	//执行重载
            	refreshUserAppTable($("#userapp_app_search_keyword").val());
            },
            loadAppList:function(){
            	if(userId==""){
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
                     content: "/html/userapp/appDialog.html",
                     success: function (layero, index) {
                    	 /* 渲染表单 */
                         form.render();
                     }
                 })
            },
    };
    var refreshUserTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
        var selectNodes = orgTree.getSelectedNodes();
        if (selectNodes && selectNodes.length == 1) {
        	 table.reload('userapp_user_datatable', {
                 page: {
                     curr: 1 //重新从第 1 页开始
                 }
                 , where: {
                     keyWord: keyword,
                     orgCode: selectNodes[0]["orgCode"]
                 }
             });
        }
    };
    var refreshUserAppTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
        var selectNodes = orgTree.getSelectedNodes();
        if (selectNodes && selectNodes.length == 1) {
        	 table.reload('userapp_app_datatable', {
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
     refreshAppTable = function (sucess) {
    	if(sucess==1){
        	layer.msg("关联系统成功");
        }else{
        	return;
        }
        var selectNodes = orgTree.getSelectedNodes();
        if (selectNodes && selectNodes.length == 1) {
        	 table.reload('userapp_app_datatable', {
        	        page: {
        	            curr: 1 //重新从第 1 页开始
        	        }
        	        , where: {
        	        	query: {
                            orgCode: selectNodes[0]["orgCode"],
                            userId:userId
                       }
        	        }
        	   });
        }
    };
   
    //渲染组织机构树
    orgTree = $.fn.zTree.init($('#userapp_org_ztree_left'), {
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
                        childNodes[i].open = true;
                        childNodes[i].name = childNodes[i]["orgNameCn"].replace(/\.n/g, '.');
                    }
                    return childNodes;
                }
            }
            , callback: {
                onClick: function (event, treeId, treeNode, clickFlag) {
                    //执行重载
                	refreshUserTable();
                	refreshUserAppTable();
                },
                onAsyncSuccess: function (event, treeId, treeNode, msgString) {
                    var node = orgTree.getNodeByParam("level ", "0");
                    if (node) {
                        orgTree.selectNode(node);
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
    //渲染用户系统数据表格
    table.render({
        id: 'userapp_user_datatable'
        , elem: '#userapp_user_datatable'
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
            , {field: 'userName',   title: '用户名',event:'getRole'}
            , {field: 'mobile', width: 120, title: '手机',event:'getRole'}
            , {field: 'email', width: 200, title: '邮箱',event:'getRole'}
            , {field: 'idNo', width: 180, title: '身份证',event:'getRole'}
            , {field: 'orgName', width: 100, title: '所属机构',event:'getRole'}
            , {field: 'status', width: 60, title: '状态', templet: "#userapp_user_status_laytpl",event:'getRole'}
            , {field: 'updateOperator', width: 100, title: '更新人',event:'getRole'}
            , {field: 'lastModifiedDatetime', width: 150, title: '更新时间',event:'getRole'}
        ]]
    });
    table.render({
        id: 'userapp_app_datatable'
        , elem: '#userapp_app_datatable'
        , url: loadUserAppListUrl
        , method: 'post' //如果无需自定义HTTP类型，可不加该参数
        , response: {
            statusName: 'returnCode' //数据状态的字段名称，默认：code
            , statusCode: "0000" //成功的状态码，默认：0
            , msgName: 'msg' //状态信息的字段名称，默认：msg
            , countName: 'count' //数据总数的字段名称，默认：count
            , dataName: 'data' //数据列表的字段名称，默认：data
        } //如果无需自定义数据响应名称，可不加该参数
        , page: true
        , height: '300'
        , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
        , cols: [[
            {type: 'numbers'}
            , {field: 'app', width: 120, title: '系统编号'}
            , {field: 'nameCn', width: 200, title: '系统名称'}
            , {field: 'delFlag', width: 100, title: '状态' ,templet: '#userapp_statusTpl'}
            , {field: 'createOperator', width: 100, title: '创建人'}
            , {field: 'createdDatetime', width: 200,templet: '#createTimeTpl', title: '创建时间'}
            , {fixed: 'right', width: 178, title: '操作', align: 'center', toolbar: '#userapp_app_datatable_bar'}
        ]]
    });
    //监听操作栏
    table.on('tool(filter_userapp_user_datatable)', function (obj) {
        var data = obj.data;
        userId = data.userId;
        if (obj.event === 'getRole') {
        	refreshUserAppTable();
        }  
    });
    table.on('tool(filter_userapp_app_datatable)', function (obj) {
        var data = obj.data;
        if (obj.event === 'startOrStop') {
        	if(data.delFlag==0){//启用状态，是否需要禁用
        		layer.confirm('是否禁用用户系统？', function (index) {
                  	 $.post(stopUserAppListUrl+"?id=" + data.id+"&status=1", null, function (result) {
                           if (result["returnCode"] == "0000") {
                        	   refreshUserAppTable();
                               layer.close(index);
                               layer.msg("禁用用户系统成功");
                           } else {
                               layer.msg(result.codeDesc);
                           }
                       });
                  });
        	}else{
        		layer.confirm('是否启用用户系统？', function (index) {
                  	 $.post(stopUserAppListUrl+"?id=" + data.id+"&status=0", null, function (result) {
                           if (result["returnCode"] == "0000") {
                        	   refreshUserAppTable();
                               layer.close(index);
                               layer.msg("启用用户系统成功");
                           } else {
                               layer.msg(result.codeDesc);
                           }
                       });
                  });
        	}
        } else if (obj.event === 'del') {
        	 layer.confirm('是否确认删除用户系统？', function (index) {
             	obj.del();
             	 $.post(delUserAppListUrl+"?id=" + data.id, null, function (result) {
                      if (result["returnCode"] == "0000") {
                    	  refreshUserAppTable();
                          layer.close(index);
                          layer.msg("删除用户系统成功");
                      } else {
                          layer.msg(result.codeDesc);
                      }
                  });
             });
        } 
    });
    //监听工具栏
    $('#userapp_user_table_tools .layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    
    $('#userapp_app_table_tools .layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    //刷新树的数据
    $('#userapp_btn_refresh_tree').on('click', function (e) {
        if (orgTree) {
            orgTree.reAsyncChildNodes(null, "refresh");
        }
    });
    var nodeList = [];
    //搜索树的数据
    $('#userapp_search_tree_org').bind('input', function (e) {
        if (orgTree && $(this).val() != "") {
            nodeList = orgTree.getNodesByParamFuzzy("name", $(this).val());
            updateNodes(true);
        } else {
            updateNodes(false);
        }
    });

    //刷新树节点
    function updateNodes(highlight) {
        for (var i = 0, l = nodeList.length; i < l; i++) {
            nodeList[i].highlight = highlight;
            orgTree.updateNode(nodeList[i]);
            if (highlight) {
                orgTree.expandNode(orgTree.getNodeByParam("orgCode", nodeList[i]["parentOrgCode"]), true, false, null, null);
            }
        }
    }
})