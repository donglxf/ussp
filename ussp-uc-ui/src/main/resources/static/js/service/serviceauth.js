layui.use(['form', 'ztree', 'table','ht_config', 'ht_auth'], function () {
    var $ = layui.jquery
        , form = layui.form
        , table = layui.table
        , config = layui.ht_config
        , ht_auth = layui.ht_auth
        , addDialog = 0 //新增弹出框的ID
        , viewDialog = 0 //查询弹出框的ID
        , editDialog = 0 //修改弹出框的ID
        , serviceAuthAppTree //组织机构树控件
        , active = {
    		 addService: function () { //弹出用户新增弹出框
            	 var nodes = serviceAuthAppTree.getSelectedNodes();
                 if (nodes.length == 0) {
                     layer.alert("请先选择主微服务");
                     return false;
                 }
                layer.close(addDialog);
                addDialog = layer.open({
                    type: 1,
                    area: ['600px', '500px'],
                    maxmin: true,
                    shadeClose: true,
                    title: "新增角色",
                    content: $("#role_add_role_div").html(),
                    btn: ['保存', '取消'],
                    yes: function (index, layero) {
                        var $submitBtn = $("button[lay-filter=filter_add_role_form]", layero);
                        if ($submitBtn) {
                            $submitBtn.click();
                        } else {
                            throw new Error("没有找到submit按钮。");
                        }
                    },
                    btn2: function () {
                        layer.closeAll('tips');
                    },
                    success: function (layero, index) {
                    	//初始化
                    	$("input[name=appNameCn]", layero).val(nodes[0]["name"]);
                        $("input[name=app]", layero).val(nodes[0]["id"]);
                    	
                        //填充选中的组织机构
                        form.render(null, "filter_add_role_form");
                        form.on('submit(filter_add_role_form)', function (data) {
                            $.ajax({
                                type: "POST",
                                url: addRoleUrl,
                                data: JSON.stringify(data.field),
                                contentType: "application/json; charset=utf-8",
                                success: function (message) {
                                    layer.close(addDialog);
                                    if (message.returnCode == '0000') {
                                        table.reload('role_datatable', {
                                            page: {
                                                curr: 1 //重新从第 1 页开始
                                            }
                                        });
                                        layer.alert("角色新增成功");
                                    }
                                },
                                error: function (message) {
                                    layer.msg("角色新增发生异常，请联系管理员。");
                                    console.error(message);
                                }
                            });
                            return false;
                        });
                    }
                })
            },
    		search: function () { 
            	//执行重载
            	refreshUserTable($("#servcieauth_service_search_keyword").val());
            },
            searchservcieauth: function () { 
            	//执行重载
            	refreshservcieauthTable($("#servcieauth_api_search_keyword").val());
            },
            loadServiceCalledList:function(){
            	if(positionCode==""){
            		layer.msg("请先选择系统微服务！");
            		return;
            	}
            	 layer.close(addDialog);
            	 roleDialog = layer.open({
                     type: 2,
                     area: ['70%', '80%'],
                     maxmin: true,
                     shadeClose: true,
                     title: "分配角色",
                     content: "/html/servcieauth/roleDialog.html",
                     success: function (layero, index) {
                    	 /* 渲染表单 */
                         form.render();
                     }
                 })
            },
    };
    var listCalledServiceUrl=config.basePath +"authservice/listCalledService"; //列出对应授权的微服务
    var addCalledServiceUrl=config.basePath + 'authservice/add'; //添加微服务间的授权管理
    var serviceListByPageUrl=config.basePath + 'authservice/serviceListByPage'; //所有微服务列表
    var listServiceAPIUrl=config.basePath +"authservice/listServiceAPI"; //列出对应授权的微服务
    
   // var appListByPageUrl=config.basePath +"userapp/listAppByPage"; //系统列表
    var appListByPageUrl=config.basePath +"authservice/app/getAppServiceTree"; //系统列表
    
    var refreshServiceServiceTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
        var selectNodes = serviceAuthAppTree.getSelectedNodes();
        if (selectNodes && selectNodes.length == 1) {
        	 table.reload('servcieauth_service_datatable', {
                 page: {
                     curr: 1 //重新从第 1 页开始
                 }
                 , where: {
                     query: {
               		     keyWord: keyword,
               		     serviceCode: selectNodes[0]["serviceCode"]
                    }
                 }
             });
        }
    };
    var refreshServiceApiTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
        var selectNodes = serviceAuthAppTree.getSelectedNodes();
        if (selectNodes && selectNodes.length == 1) {
        	 table.reload('servcieauth_api_datatable', {
        		  height: 'full-600',
        	        page: {
        	            curr: 1 //重新从第 1 页开始
        	        }
        	        , where: {
        	        	query: {
                  		    keyWord: keyword,
                  		   serviceCode: selectNodes[0]["serviceCode"],
                       }
        	        }
        	   });
        }
    };
   
    //渲染组织机构树
    serviceAuthAppTree = $.fn.zTree.init($('#servcieauth_org_ztree_left'), { 
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
            otherParam: ["page", "1", "limit", "1000"],
            url: appListByPageUrl,
            // url: config.basePath + "role/res/app/load",
            dataFilter: function (treeId, parentNode, childNodes) {
                if (!childNodes) return null;
                var appData = childNodes;
            	for (var i=0, l=appData.length; i<l; i++) {
            		/*appData[i].name = appData[i].mainService;
            		appData[i].id = appData[i].app;
            		appData[i].name = appData[i].name.replace(/\.n/g, '.');*/
            		if(childNodes[i]["mainService"]){
            			childNodes[i].name = childNodes[i]["mainService"];
            		}
            	}
            	return appData;
            }
        }
        , callback: {
        	onClick: function (event, treeId, treeNode, clickFlag) {
        		refreshServiceServiceTable();
            },
            onAsyncSuccess: function (event, treeId, treeNode, msgString) {
                var node = serviceAuthAppTree.getNodeByParam("level ", "0");
                if (node) {
                	serviceAuthAppTree.selectNode(node);
                }
            }
        },
        data: {
            simpleData: {
                enable: true
                , idKey: "serviceCode"
                , pIdKey: "parentCode"
            }
        }
    }
    );
    //渲染岗位数据表格
    table.render({
        id: 'servcieauth_service_datatable'
        , elem: '#servcieauth_service_datatable'
        , url: listCalledServiceUrl
        , method: 'post' //如果无需自定义HTTP类型，可不加该参数
        , page: true
        , height: '310'
        , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
        , cols: [[
            {type: 'numbers'}
            , {field: 'mainServiceCode', width: 120, title: '主微服务',event:'getRole'}
            , {field: 'callService',   title: '可调用微服务',event:'getRole'}
            , {field: 'app', width: 220, title: '所属系统',event:'getRole'}
            , {field: 'status', width: 100, title: '状态',event:'getRole'}
            , {field: 'createOperator', width: 100, title: '创建人',event:'getRole'}
            , {field: 'createdDatetime', width: 250, title: '创建时间',templet: '#createTimeTpl',event:'getRole'}
        ]]
    });
    table.render({
        id: 'servcieauth_api_datatable'
        , elem: '#servcieauth_api_datatable'
        , url: listServiceAPIUrl
        , method: 'post' //如果无需自定义HTTP类型，可不加该参数
        , page: true
        , height: 'full-600'
        , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
        , cols: [[
            {type: 'numbers'}
            , {field: 'callServiceCode', width: 120, title: '授权code',event:'getRole'}
            , {field: 'apiContent',   title: '可调用微服务',event:'getRole'}
            , {field: 'apiDesc',   title: '可调用微服务',event:'getRole'}
            , {field: 'app', width: 220, title: '所属系统',event:'getRole'}
            , {field: 'status', width: 100, title: '状态',event:'getRole'}
            , {field: 'createOperator', width: 100, title: '创建人',event:'getRole'}
            , {field: 'createdDatetime', width: 250, title: '创建时间',templet: '#createTimeTpl',event:'getRole'}
        ]]
    });
    //监听操作栏
    table.on('tool(filter_servcieauth_service_datatable)', function (obj) {
        var data = obj.data;
        if (obj.event === 'getRole') {
        	refreshservcieauthTable();
        }  
    });
    table.on('tool(filter_servcieauth_api_datatable)', function (obj) {
        var data = obj.data;
        if (obj.event === 'startOrStop') {
        	if(data.delFlag==0){//启用状态，是否需要禁用
        		layer.confirm('是否禁用岗位角色？', function (index) {
                  	 $.post(stopservcieauthListUrl+"?id=" + data.id+"&status=1", null, function (result) {
                           if (result["returnCode"] == "0000") {
                        	   refreshservcieauthTable();
                               layer.close(index);
                               layer.msg("禁用岗位角色成功");
                           } else {
                               layer.msg(result.codeDesc);
                           }
                       });
                  });
        	}else{
        		layer.confirm('是否启用岗位角色？', function (index) {
                  	 $.post(stopservcieauthListUrl+"?id=" + data.id+"&status=1", null, function (result) {
                           if (result["returnCode"] == "0000") {
                        	   refreshservcieauthTable();
                               layer.close(index);
                               layer.msg("启用岗位角色成功");
                           } else {
                               layer.msg(result.codeDesc);
                           }
                       });
                  });
        	}
        } else if (obj.event === 'del') {
        	 layer.confirm('是否确认删除岗位角色？', function (index) {
             	 $.post(delservcieauthListUrl+"?id=" + data.id, null, function (result) {
                      if (result["returnCode"] == "0000") {
                    	  obj.del();
                    	  refreshservcieauthTable();
                          layer.close(index);
                          layer.msg("删除岗位角色成功");
                      } else {
                          layer.msg(result.codeDesc);
                      }
                  });
             });
        } 
    });
    
    table.on('renderComplete(filter_servcieauth_service_datatable)', function (obj) {
    	ht_auth.render("servcieauth_auth");
    });
    table.on('renderComplete(filter_servcieauth_api_datatable)', function (obj) {
    	ht_auth.render("servcieauth_auth");
    });
    
    //监听工具栏
    $('#servcieauth_service_table_tools .layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    
    $('#servcieauth_api_table_tools .layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    
    $('#servcieauth_btn_tree .btn').on('click', function () {
        var type = $(this).data('type');
        switch (type) {
            case "refresh":
                if (serviceAuthAppTree) {
                	serviceAuthAppTree.reAsyncChildNodes(null, "refresh");
                }
                break;
            case "expandAll":
                if (serviceAuthAppTree) {
                	serviceAuthAppTree.expandAll(true);
                }
                break;
            case "collapseAll":
                if (serviceAuthAppTree) {
                	serviceAuthAppTree.expandAll(false);
                }
                break;
        }
    });
    
    var nodeList = [];
   
    //搜索树的数据
    $('#servcieauth_search_tree_org').bind('input', function (e) {
        if (serviceAuthAppTree && $(this).val() != "") {
            nodeList = serviceAuthAppTree.getNodesByParamFuzzy("name", $(this).val());
            updateNodes(true);
        } else {
            updateNodes(false);
        }
    });

    //刷新树节点
    function updateNodes(highlight) {
        for (var i = 0, l = nodeList.length; i < l; i++) {
            nodeList[i].highlight = highlight;
            serviceAuthAppTree.updateNode(nodeList[i]);
            if (highlight) {
                serviceAuthAppTree.expandNode(serviceAuthAppTree.getNodeByParam("orgCode", nodeList[i]["parentOrgCode"]), true, false, null, null);
            }
        }
    }
    ht_auth.render("servcieauth_auth");
})