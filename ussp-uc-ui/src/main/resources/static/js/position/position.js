
layui.use(['form', 'ztree', 'table','ht_config', 'ht_auth','upload'], function () {
    var $ = layui.jquery
        , form = layui.form
        , config = layui.ht_config
        , table = layui.table
        , ht_auth = layui.ht_auth
        , upload = layui.upload
        , addDialog = 0 //新增弹出框的ID
        , viewDialog = 0 //查询弹出框的ID
        , editDialog = 0 //修改弹出框的ID
        , orgTree //组织机构树控件
        , active = {
        add: function () { //弹出岗位新增弹出框
            var nodes = orgTree.getSelectedNodes();
            if (nodes.length == 0) {
                layer.alert("请先选择一个组织机构。");
                return false;
            }
            layer.close(addDialog);
            addDialog = layer.open({
                type: 1,
                area: ['400px', '400px'],
                maxmin: true,
                shadeClose: true,
                title: "新增岗位",
                content: $("#position_add_data_div").html(),
                btn: ['保存', '取消'],
                yes: function (index, layero) {
                    var $submitBtn = $("button[lay-filter=filter_add_position_form]", layero);
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
                    //填充选中的组织机构
                    $("input[name=orgName]", layero).val(nodes[0]["orgNameCn"]);
                    $("input[name=orgCode]", layero).val(nodes[0]["orgCode"]);
                    $("input[name=orgPath]", layero).val(nodes[0]["orgPath"]);
                    $("input[name=rOrgCode]", layero).val(nodes[0]["rootOrgCode"]);
                    $("input[name=pOrgCode]", layero).val(nodes[0]["orgCode"]);
                    form.render(null, "filter_add_position_form");
                    form.on('submit(filter_add_position_form)', function (data) {
                        $.ajax({
                            type: "POST",
                            url: addPositionUrl,
                            data: JSON.stringify(data.field),
                            contentType: "application/json; charset=utf-8",
                            success: function (message) {
                                layer.close(addDialog);
                                if (message.returnCode == '0000') {
                                	 layer.msg("岗位新增成功");
                                   /* table.reload('position_datatable', {
                                        page: {
                                            curr: 1 //重新从第 1 页开始
                                        }
                                        , where: {
                                            query: {
                                                orgCode: nodes[0]["orgCode"]
                                            }
                                        }
                                    });*/
                                	 refreshTable();
                                }
                            },
                            error: function (message) {
                                layer.msg("岗位新增发生异常，请联系管理员。");
                                console.error(message);
                            }
                        });
                        return false;
                    });
                }
            })
        },
        exportPosition:function(){
     	   $.post(exportPositionExcelUrl, null, function (result) {
           console.log(result);
         });
       },
        search: function () { 
        	//执行重载
            refreshTable($("#position_search_keyword").val());
        }
    };
    var positionListByPageUrl=config.basePath +"position/in/list"; //列出所有岗位记录列表信息  
    var addPositionUrl=config.basePath +"position/in/add"; //添加岗位信息
    var delPositionUrl=config.basePath +"position/in/delete"; //添加岗位信息
    var orgTreeUrl = config.basePath +"org/tree"; //机构列表
    var statusPositionUrl=config.basePath +"position/in/stop"; //禁用
    var checkPositionCodeExistUrl=config.basePath +"position/isExistPositionCode"; //校验岗位编码是否已经存在
    var exportPositionExcelUrl = config.basePath +"position/exportPositionExcel"; //导出
    var importPositionExcelUrl = config.basePath +"position/importPositionExcel"; //导入
    
    upload.render({
		elem: '#importPosition'
		,url:importPositionExcelUrl
		,accept: 'file' //普通文件
		,exts: 'xls|xlsx' //只允许上传压缩文件
		,done: function(res){
			if (res.returnCode == '0000') {
            	layer.msg("岗位导入成功");
            	refreshTable();
            }
           
		}
	});
    
    //自定义验证规则
	form.verify({
		  //校验编码是否已经存在
		  checkExistPosCode : function(value) {
			  var isExist="";
			  if(value){
					  $.ajax({
						url : checkPositionCodeExistUrl + "?positionCode=" + value,
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
				  return "新增岗位编码已存在或不可用，请重新输入岗位编码";
			  } 
		  },
		  
	});
    var refreshTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
        var selectNodes = orgTree.getSelectedNodes();
        if (selectNodes && selectNodes.length == 1) {
            table.reload('position_datatable', {
            	height: 'full-200',
                page: {
                    curr: 1 //重新从第 1 页开始
                }
                , where: {
                	 query: {
                		  keyWord: keyword,
                          orgCode: selectNodes[0]["orgCode"]
                     }
                }
            });
        }
    };
    //渲染组织机构树
    orgTree = $.fn.zTree.init($('#position_org_ztree_left'), {
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
                    /*table.reload('position_datatable', {
                        page: {
                            curr: 1 //重新从第 1 页开始
                        }
                        , where: {
                            query: {
                                orgCode: treeNode["orgCode"]
                            }
                        }
                    });*/
                    refreshTable();
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
        id: 'position_datatable'
        , elem: '#position_datatable'
        , url: positionListByPageUrl
        , method: 'post' //如果无需自定义HTTP类型，可不加该参数
        , response: {
            statusName: 'returnCode' //数据状态的字段名称，默认：code
            , statusCode: "0000" //成功的状态码，默认：0
            , msgName: 'msg' //状态信息的字段名称，默认：msg
            , countName: 'count' //数据总数的字段名称，默认：count
            , dataName: 'data' //数据列表的字段名称，默认：data
        } //如果无需自定义数据响应名称，可不加该参数
        , page: true
        , height: 'full-200'
        , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
        , cols: [[
            {type: 'numbers'}
            , {field: 'positionCode', width: 120, title: '岗位编号'}
            , {field: 'positionNameCn',   title: '岗位名称'}
            , {field: 'porgNameCn', width: 220, title: '所属机构'}
            , {field: 'status', templet: '#statusTpl', width: 100, title: '状态'}
            , {field: 'createOperator', width: 100, title: '创建人'}
            , {field: 'createdDatetime',   templet:'#createTimeTpl', title: '创建时间'}
            , {fixed: 'right', width: 178, title: '操作', align: 'center', toolbar: '#position_datatable_bar'}
        ]]
    });
    //监听操作栏
    table.on('tool(filter_position_datatable)', function (obj) {
        var data = obj.data;
        if (obj.event === 'stopOrStart') {
        	if(data.status==0){//启用状态，是否需要禁用
        		layer.confirm('是否禁用岗位？', function (index) {
                  	 $.post(statusPositionUrl+"?id=" + data.id+"&status=1", null, function (result) {
                           if (result["returnCode"] == "0000") {
                               refreshTable();
                               layer.close(index);
                               layer.msg("禁用岗位成功");
                           } else {
                               layer.msg(result.codeDesc);
                           }
                       });
                  });
        	}else{
        		layer.confirm('是否启用岗位？', function (index) {
                  	 $.post(statusPositionUrl+"?id=" + data.id+"&status=0", null, function (result) {
                           if (result["returnCode"] == "0000") {
                               refreshTable();
                               layer.close(index);
                               layer.msg("启用岗位成功");
                           } else {
                               layer.msg(result.codeDesc);
                           }
                       });
                  });
        	}
        }  else if (obj.event === 'detail') {
        	 viewDialog = layer.open({
                 type: 1,
                 area: ['400px', '400px'],
                 shadeClose: true,
                 title: "岗位详情",
                 content: $("#position_view_data_div").html(),
                 btn: ['取消'],
                 btn2: function () {
                     layer.closeAll('tips');
                 },
                 success: function (layero) {
                     $.each(data, function (name, value) {
                         var $input = $("input[name=" + name + "]", layero);
                         if ($input && $input.length == 1) {
                             $input.val(value);
                         }
                     });
                 }
             });
        } else if (obj.event === 'del') {
            layer.confirm('是否确认删除岗位？', function (index) {
            	 $.post(delPositionUrl+"?id=" + data.id, null, function (result) {
                     if (result["returnCode"] == "0000") {
                    	 obj.del();
                         layer.close(index);
                         layer.msg("删除岗位成功");
                     } else {
                         layer.msg(result.codeDesc);
                     }
                     refreshTable();
                 });
            });
        } else if (obj.event === 'edit') {
            editDialog = layer.open({
            	 type: 1,
                 area: ['400px', '400px'],
                 maxmin: true,
                 shadeClose: true,
                 title: "修改岗位",
                 content: $("#position_modify_data_div").html(),
                 btn: ['保存', '取消'],
                 yes: function (index, layero) {
                     var $submitBtn = $("button[lay-filter=filter_modify_position_form]", layero);
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
                    
                    form.render(null, "filter_modify_position_form");
                    form.on('submit(filter_modify_position_form)', function (data) {
                        $.ajax({
                            type: "POST",
                            url: addPositionUrl,
                            data: JSON.stringify(data.field),
                            contentType: "application/json; charset=utf-8",
                            success: function (result2) {
                                layer.close(editDialog);
                                if (result2["returnCode"] == '0000') {
                                	layer.msg("岗位修改成功");
                                    refreshTable();
                                }
                            },
                            error: function (message) {
                                layer.msg("岗位修改发生异常，请联系管理员。");
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
    table.on('renderComplete(filter_position_datatable)', function (obj) {
    	ht_auth.render("position_auth");
    });
    //监听工具栏
    $('#position_table_tools .layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    //刷新树的数据
    $('#position_btn_refresh_tree').on('click', function (e) {
        if (orgTree) {
            orgTree.reAsyncChildNodes(null, "refresh");
        }
    });
    var nodeList = [];
    //搜索树的数据
    $('#position_search_tree_org').bind('input', function (e) {
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
    ht_auth.render("position_auth");
})