

layui.use(['form', 'laytpl' , 'ztree','table','ht_config', 'ht_auth' ,'upload'], function () {

    var $ = layui.jquery
        , form = layui.form
        , table = layui.table
        , config = layui.ht_config
        ,laytpl = layui.laytpl
        , ht_auth = layui.ht_auth
        , upload = layui.upload
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
        exportRole:function(){
        	   $.post(exportRoleExcelUrl, null, function (result) {
               console.log(result);
            });
         },
        search: function () {
            //执行重载
        	refreshTable($("#role_search_keyword").val());
        },
        getDDRole:function(){
     	   $.post(getDDUrl, null, function (result) {
            console.log(result);
         });
      },
        
    };
    
    var roleListByPageUrl=config.basePath +"role/in/list"; //列出所有角色记录列表信息  
    var addRoleUrl=config.basePath +"role/in/add"; //添加角色信息
    var delRoleUrl=config.basePath +"role/in/delete"; //删除角色信息
    var statusRoleUrl=config.basePath +"role/in/stop"; //禁用
    var checkRoleCodeExist = config.basePath +"role/isExistRoleCode"; //校验角色编码是否已经存在
    var appListByPageUrl=config.basePath +"userapp/listAppByPage"; //列出所有角色记录列表信息
    var exportRoleExcelUrl = config.basePath +"role/exportRoleExcel"; //导出
    var importRoleExcelUrl = config.basePath +"role/importRoleExcel"; //导入
    var getDDUrl = config.basePath +"role/getDDRole"; //导入
    upload.render({
		elem: '#importRole'
		,url:importRoleExcelUrl
		,accept: 'file' //普通文件
		,exts: 'xls|xlsx' //只允许上传压缩文件
		,done: function(res){
			if (res.returnCode == '0000') {
             	layer.msg("角色导入成功");
             	refreshTable();
            }
           
		}
	});
    //自定义验证规则
	form.verify({
		  //校验编码是否已经存在
		  checkExistRoleCode : function(value) {
			  var isExist="1";
			  if(value){
					  $.ajax({
						url : checkRoleCodeExist + "?roleCode=" + value,
						type : 'POST',
						async : false,
						success : function(result) {
							if (result["returnCode"] == "0000") {
								isExist="";
						    } else{
						    	isExist = "1";
						    }
						}
					});
			  }
			  if(isExist=="1"){
				  return "角色编码存在或不可用";
			  } 
		  },
		  
	});
    var refreshTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
        
        var selectNodes = appTree.getSelectedNodes();
        if (selectNodes && selectNodes.length == 1) {
        	table.reload('role_datatable', {
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
        	table.reload('role_datatable', {
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
    appTree = $.fn.zTree.init($('#role_app_ztree_left'), {
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
            		refreshTable();
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
        id: 'role_datatable'
        , elem: '#role_datatable'
        , url: roleListByPageUrl
        , method: 'post' //如果无需自定义HTTP类型，可不加该参数
        , page: true
        , height: 'full-200'
        , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
        , cols: [[
            {type: 'numbers'}
            , {field: 'roleCode',   title: '角色编号'}
            , {field: 'roleNameCn',   title: '角色名称'}
            , {field: 'app',    title: '所属系统'}
            , {field: 'status',  templet: '#statusTpl', title: '状态'}
            , {field: 'createOperator',   title: '创建人'}
            , {field: 'createdDatetime',  templet: '#createTimeTpl', title: '创建时间'}
            , {fixed: 'right',   title: '操作',   toolbar: '#role_datatable_bar'}
        ]]
    });
    //监听操作栏
    table.on('tool(filter_role_datatable)', function (obj) {
        var data = obj.data;
        if (obj.event === 'stopOrStart') {
        	if(data.status==0){//启用状态，是否需要禁用
        		layer.confirm('是否禁用角色？', function (index) {
                  	 $.post(statusRoleUrl+"?id=" + data.id+"&status=1", null, function (result) {
                           if (result["returnCode"] == "0000") {
                               refreshTable();
                               layer.close(index);
                               layer.msg("禁用角色成功");
                           } else {
                               layer.msg(result.codeDesc);
                           }
                       });
                  });
        	}else{
        		layer.confirm('是否启用角色？', function (index) {
                  	 $.post(statusRoleUrl+"?id=" + data.id+"&status=0", null, function (result) {
                           if (result["returnCode"] == "0000") {
                               refreshTable();
                               layer.close(index);
                               layer.msg("启用角色成功");
                           } else {
                               layer.msg(result.codeDesc);
                           }
                       });
                  });
        	}
        } else if (obj.event === 'del') {
        	 layer.confirm('是否确认删除角色？', function (index) {
             	 $.post(delRoleUrl+"?id=" + data.id, null, function (result) {
                      if (result["returnCode"] == "0000") {
                    	  obj.del();
                          refreshTable();
                          layer.close(index);
                          layer.msg("删除角色成功");
                      } else {
                          layer.msg(result.codeDesc);
                      }
                  });
             });
        } else if (obj.event === 'edit') {
        	 editDialog = layer.open({
            	 type: 1,
                 area: ['400px', '400px'],
                 maxmin: true,
                 shadeClose: true,
                 title: "修改角色",
                 content: $("#role_modify_data_div").html(),
                 btn: ['保存', '取消'],
                 yes: function (index, layero) {
                     var $submitBtn = $("button[lay-filter=filter_modify_role_form]", layero);
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
                   //初始化
               	  $.post(appListByPageUrl,{"page":1,"limit":100},function(appdata){
                    	var optionHtml="<option value=''>请选择系统</option>";
                    	 $.each(appdata.data, function (name, value) {
                    		 if(data.app==value.app){
                    			 optionHtml += "<option value='"+value.app+"' selected>"+value.nameCn+"</option>";
                    		 }else{
                    			 optionHtml += "<option value='"+value.app+"'>"+value.nameCn+"</option>";
                    		 }
                         });
                        var getTpl = $("#app",layero).html(optionHtml);
                        form.render('select','filter_modify_data_form');
                    },'json');
               	  
               	  
                    form.render(null, "filter_modify_role_form");
                    form.on('submit(filter_modify_role_form)', function (data) {
                        $.ajax({
                            type: "POST",
                            url: addRoleUrl,
                            data: JSON.stringify(data.field),
                            contentType: "application/json; charset=utf-8",
                            success: function (result2) {
                                layer.close(editDialog);
                                if (result2["returnCode"] == '0000') {
                                    refreshTable();
                                    layer.alert("角色修改成功");
                                }
                            },
                            error: function (message) {
                                layer.msg("角色修改发生异常，请联系管理员。");
                                layer.close(index);
                                console.error(message);
                            }
                        });
                        return false;
                    });
                }
            });
        }
    });
    table.on('renderComplete(filter_role_datatable)', function (obj) {
        ht_auth.render("role_auth");
    });
    //监听工具栏
    $('#role_table_tools .layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    var $keywordInput = $("#role_search_keyword");
    $keywordInput.keydown(function (e) {
        if (e.keyCode == 13) {
            var keyWord = $keywordInput.val();
            refreshTable(keyWord);
        }
    });
    //刷新树的数据
    $('#role_app_btn_refresh_tree').on('click', function (e) {
        if (appTree) {
            appTree.reAsyncChildNodes(null, "refresh");
        }
    });
    var nodeList = [];
    //搜索树的数据
    $('#role_app_search_tree').bind('input', function (e) {
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
    
    ht_auth.render("role_auth");

})