layui.use(['form', 'ztree', 'table','ht_config'], function () {
    var $ = layui.jquery
        , form = layui.form
        , table = layui.table
        , config = layui.ht_config
        , addDialog = 0 //新增弹出框的ID
        , viewDialog = 0 //查询弹出框的ID
        , editDialog = 0 //修改弹出框的ID
        , orgTree //组织机构树控件
        , active = {
    		search: function () { 
            	//执行重载
            	refreshUserTable($("#userposition_user_search_keyword").val());
            },
            searchuserposition: function () { 
            	//执行重载
            	refreshuserpositionTable($("#userposition_role_search_keyword").val());
            },
            loadPositionList:function(){
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
    var userId = "";
    var refreshPositionTable;
    
    var refreshUserTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
        var selectNodes = orgTree.getSelectedNodes();
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
        }
    };
    var refreshuserpositionTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
        var selectNodes = orgTree.getSelectedNodes();
        if (selectNodes && selectNodes.length == 1) {
        	 table.reload('userposition_role_datatable', {
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
    
    refreshPositionTable = function (sucess) {
    	if(sucess==1){
        	layer.msg("分配角色成功");
        }else{
        	return;
        }
        var selectNodes = orgTree.getSelectedNodes();
        if (selectNodes && selectNodes.length == 1) {
        	 table.reload('userposition_role_datatable', {
        	        page: {
        	            curr: 1 //重新从第 1 页开始
        	        }
        	        , where: {
        	        	query: {
                            userId:userId
                       }
        	        }
        	   });
        }
    };
   
    //渲染组织机构树
    orgTree = $.fn.zTree.init($('#userposition_org_ztree_left'), {
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
                	refreshuserpositionTable();
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
            , {field: 'jobNumber', width: 100, title: '工号',event:'getRole'}
            , {field: 'userName', width: 180,   title: '用户名',event:'getRole'}
            , {field: 'mobile', width: 120, title: '手机',event:'getRole'}
            , {field: 'email', width: 180, title: '邮箱',event:'getRole'}
            , {field: 'idNo', width: 180, title: '身份证',event:'getRole'}
            , {field: 'orgName', width: 130, title: '所属机构',event:'getRole'}
            , {field: 'status', width: 60, title: '状态', templet: "#userposition_user_status_laytpl",event:'getRole'}
            , {field: 'updateOperator', width: 100, title: '更新人',event:'getRole'}
            , {field: 'lastModifiedDatetime', width: 150, title: '更新时间',event:'getRole'}
        ]]
    });
    table.render({
        id: 'userposition_role_datatable'
        , elem: '#userposition_role_datatable'
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
        , height: '300'
        , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
        , cols: [[
            {type: 'numbers'}
            , {field: 'positionCode',   title: '岗位编号'}
            , {field: 'positionNameCn',   title: '岗位名称'}
            , {field: 'porgNameCn',   title: '所属机构'}
            , {field: 'delFlag', width: 100, title: '状态' ,templet: '#userposition_statusTpl'}
            , {field: 'createOperator', width: 100, title: '创建人'}
            , {field: 'createdDatetime', width: 200,templet: '#createTimeTpl', title: '创建时间'}
            , {fixed: 'right', width: 178, title: '操作', align: 'center', toolbar: '#userposition_role_datatable_bar'}
        ]]
    });
    //监听操作栏
    table.on('tool(filter_userposition_user_datatable)', function (obj) {
        var data = obj.data;
        userId = data.userId;
        if (obj.event === 'getRole') {
        	refreshuserpositionTable();
        }  
    });
    table.on('tool(filter_userposition_role_datatable)', function (obj) {
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
             	obj.del();
             	 $.post(delUserPositionListUrl+"?id=" + data.id, null, function (result) {
                      if (result["returnCode"] == "0000") {
                    	  refreshuserpositionTable();
                          layer.close(index);
                          layer.msg("删除用户岗位成功");
                      } else {
                          layer.msg(result.codeDesc);
                      }
                  });
             });
        } 
    });
    //监听工具栏
    $('#userposition_user_table_tools .layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    
    $('#userposition_role_table_tools .layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    //刷新树的数据
    $('#userposition_btn_refresh_tree').on('click', function (e) {
        if (orgTree) {
            orgTree.reAsyncChildNodes(null, "refresh");
        }
    });
    var nodeList = [];
    //搜索树的数据
    $('#userposition_search_tree_org').bind('input', function (e) {
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