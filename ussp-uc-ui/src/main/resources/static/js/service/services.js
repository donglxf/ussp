
layui.use(['form', 'laytpl' , 'ztree','table','ht_config', 'ht_auth'  ], function () {
    var $ = layui.jquery
        , form = layui.form
        , table = layui.table
        , config = layui.ht_config
        ,laytpl = layui.laytpl
        , ht_auth = layui.ht_auth
        , addDialog = 0 //新增弹出框的ID
        , viewDialog = 0 //查询弹出框的ID
        , editDialog = 0 //修改弹出框的ID
        , appTree //组织机构树控件
        , active = {
        add: function () { //弹出用户新增弹出框
        	 var nodes = appTree.getSelectedNodes();
             if (nodes.length == 0) {
                 layer.alert("请先选择一个系统");
                 return false;
             }
            layer.close(addDialog);
            addDialog = layer.open({
                type: 1,
                area: ['600px', '500px'],
                maxmin: true,
                shadeClose: true,
                title: "新增微服务",
                content: $("#services_add_services_div").html(),
                btn: ['保存', '取消'],
                yes: function (index, layero) {
                    var $submitBtn = $("button[lay-filter=filter_add_services_form]", layero);
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
                	$("#appNameCn", layero).val(nodes[0]["name"]);
                    $("input[name=app]", layero).val(nodes[0]["id"]);
                	
                    //填充选中的组织机构
                    form.render(null, "filter_add_services_form");
                    form.on('submit(filter_add_services_form)', function (data) {
                        $.ajax({
                            type: "POST",
                            url: addServiceUrl,
                            data: JSON.stringify(data.field),
                            contentType: "application/json; charset=utf-8",
                            success: function (message) {
                                layer.close(addDialog);
                                if (message.returnCode == '0000') {
                                    table.reload('services_datatable', {
                                        page: {
                                            curr: 1 //重新从第 1 页开始
                                        }
                                    });
                                    layer.alert("微服务新增成功");
                                }
                            },
                            error: function (message) {
                                layer.msg("微服务新增发生异常，请联系管理员。");
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
        	refreshServieDataTable($("#role_search_keyword").val());
        },
        
    };
    
    var serviceListByPageUrl=config.basePath +"minservice/getServiceListByPage"; //列出所有微服务记录列表信息  
    var addServiceUrl=config.basePath +"minservice/add"; //添加微服务信息
    var statusServiceUrl=config.basePath +"minservice/stop"; //禁用
    var appListByPageUrl=config.basePath +"userapp/listAppByPage"; //列出所有微服务记录列表信息
    
    var refreshServieDataTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
        var selectNodes = appTree.getSelectedNodes();
        if (selectNodes && selectNodes.length == 1) {
        	table.reload('services_datatable', {
            	height: 'full-200'
                , page: {
                    curr: 1 //重新从第 1 页开始
                }
                , where: {
                    keyWord: keyword,
                    query: {
                    	app: selectNodes[0]["app"]
                    }
                }
            });
        }else{
        	table.reload('services_datatable', {
            	height: 'full-200'
                , page: {
                    curr: 1 //重新从第 1 页开始
                }
                , where: {
                    keyWord: keyword,
                }
            });
        }
    };
    
  //渲染组织机构树
    appTree = $.fn.zTree.init($('#services_app_ztree_left'), {
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
                otherParam: ["page", "1", "limit", "100"],
                url: appListByPageUrl,
                dataFilter: function (treeId, parentNode, childNodes) {
                    if (!childNodes) return null;
                    var appData = childNodes.data;
                	for (var i=0, l=appData.length; i<l; i++) {
                		appData[i].name = appData[i].nameCn;
                		appData[i].id = appData[i].app;
                		appData[i].name = appData[i].name.replace(/\.n/g, '.');
                	}
                	
                	return appData;
                }
            }
            , callback: {
            	onClick: function (event, treeId, treeNode, clickFlag) {
            		refreshServieDataTable();
                },
                onAsyncSuccess: function (event, treeId, treeNode, msgString) {
                    var node = appTree.getNodeByParam("level ", "0");
                    if (node) {
                        appTree.selectNode(node);
                    }
                }
            },
            data: {
                simpleData: {
                    enable: true
                }
            }
        }
    );
    //渲染用户数据表格
    table.render({
        id: 'services_datatable'
        , elem: '#services_datatable'
        , url: serviceListByPageUrl
        , method: 'post' //如果无需自定义HTTP类型，可不加该参数
        , page: true
        , height: 'full-200'
        , cols: [[
            {type: 'numbers'}
//            , {field: 'servcieCode',   title: '编号'}
            , {field: 'mainService',   title: '微服务'}
            , {field: 'mainServiceName',   title: '微服务名称'}
            , {field: 'app',    title: '所属系统'}
            , {field: 'status',  templet: '#statusTpl', title: '状态'}
            , {field: 'createOperator',   title: '创建人'}
            , {field: 'createdDatetime',  templet: '#createTimeTpl', title: '创建时间'}
            , {fixed: 'right',   title: '操作',   toolbar: '#services_datatable_bar'}
        ]]
    });
    //监听操作栏
    table.on('tool(filter_services_datatable)', function (obj) {
        var data = obj.data;
        if (obj.event === 'stopOrStart') {
        	if(data.status==0){//启用状态，是否需要禁用
        		layer.confirm('是否禁用微服务？', function (index) {
                  	 $.post(statusServiceUrl+"?id=" + data.id+"&status=1", null, function (result) {
                           if (result["returnCode"] == "0000") {
                               refreshServieDataTable();
                               layer.close(index);
                               layer.msg("禁用微服务成功");
                           } else {
                               layer.msg(result.codeDesc);
                           }
                       });
                  });
        	}else{
        		layer.confirm('是否启用微服务？', function (index) {
                  	 $.post(statusServiceUrl+"?id=" + data.id+"&status=0", null, function (result) {
                           if (result["returnCode"] == "0000") {
                               refreshServieDataTable();
                               layer.close(index);
                               layer.msg("启用微服务成功");
                           } else {
                               layer.msg(result.codeDesc);
                           }
                       });
                  });
        	}
        } else if (obj.event === 'del') {
       	 layer.confirm('是否确认删除微服务？', function (index) {
         	 $.post(delRoleUrl+"?id=" + data.id, null, function (result) {
                  if (result["returnCode"] == "0000") {
                	  obj.del();
                      refreshTable();
                      layer.close(index);
                      layer.msg("删除微服务成功");
                  } else {
                      layer.msg(result.codeDesc);
                  }
              });
         });
       }else if (obj.event === 'edit') {
        	 editDialog = layer.open({
            	 type: 1,
                 area: ['400px', '400px'],
                 maxmin: true,
                 shadeClose: true,
                 title: "修改微服务",
                 content: $("#services_modify_services_data_div").html(),
                 btn: ['保存', '取消'],
                 yes: function (index, layero) {
                     var $submitBtn = $("button[lay-filter=filter_modify_services_form]", layero);
                     if ($submitBtn) {
                         $submitBtn.click();
                     } else {
                         throw new Error("没有找到submit按钮。");
                     }
                 },
                 btn2: function () {
                     layer.closeAll('tips');
                 },
                success: function (layero) {
                	//加载表单数据
                    $.each(data, function (name, value) {
                        var $input = $("input[name=" + name + "]", layero);
                        if ($input && $input.length == 1) {
                            $input.val(value);
                        }
                    });
                    form.render(null, "filter_modify_services_data_form");
                    form.on('submit(filter_modify_services_data_form)', function (data) {
                        $.ajax({
                            type: "POST",
                            url: addServiceUrl,
                            data: JSON.stringify(data.field),
                            contentType: "application/json; charset=utf-8",
                            success: function (result2) {
                                layer.close(editDialog);
                                if (result2["returnCode"] == '0000') {
                                    refreshServieDataTable();
                                    layer.alert("微服务修改成功");
                                }
                            },
                            error: function (message) {
                                layer.msg("微服务修改发生异常，请联系管理员。");
                                console.error(message);
                            }
                        });
                        return false;
                    });
                }
            });
        }
    });
    table.on('renderComplete(filter_services_datatable)', function (obj) {
        ht_auth.render("services_auth");
    });
    //监听工具栏
    $('#services_table_tools .layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    var $keywordInput = $("#services_search_keyword");
    $keywordInput.keydown(function (e) {
        if (e.keyCode == 13) {
            var keyWord = $keywordInput.val();
            refreshServieDataTable(keyWord);
        }
    });
    //刷新树的数据
    $('#services_app_btn_refresh_tree').on('click', function (e) {
        if (appTree) {
            appTree.reAsyncChildNodes(null, "refresh");
        }
    });
    var nodeList = [];
    //搜索树的数据
    $('#services_app_search_tree').bind('input', function (e) {
        if (appTree && $(this).val() != "") {
            nodeList = appTree.getNodesByParamFuzzy("name", $(this).val());
            updateNodes(true);
        } else {
            updateNodes(false);
        }
    });

    //刷新树节点
    function updateNodes(highlight) {
        for (var i = 0, l = nodeList.length; i < l; i++) {
            nodeList[i].highlight = highlight;
            appTree.updateNode(nodeList[i]);
            if (highlight) {
                appTree.expandNode(appTree.getNodeByParam("app", nodeList[i]["parentOrgCode"]), true, false, null, null);
            }
        }
    }
    
    ht_auth.render("services_auth");

})