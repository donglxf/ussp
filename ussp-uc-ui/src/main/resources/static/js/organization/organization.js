
layui.use(['form', 'ztree', 'table','ht_config', 'ht_auth','upload'], function () {
    var $ = layui.jquery
        , form = layui.form
        , table = layui.table
        , config = layui.ht_config
        , ht_auth = layui.ht_auth
        , upload = layui.upload
        , addDialog = 0 //新增弹出框的ID
        , viewDialog = 0 //查询弹出框的ID
        , editDialog = 0 //修改弹出框的ID
        , organizationTree //组织机构树控件
        , active = {
        add: function () { //弹出机构新增弹出框
            var nodes = organizationTree.getSelectedNodes();
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
                title: "新增机构",
                content: $("#organization_add_data_div").html(),
                btn: ['保存', '取消'],
                yes: function (index, layero) {
                    var $submitBtn = $("button[lay-filter=filter_add_organization_form]", layero);
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
                    $("input[name=parentOrgNamcn]", layero).val(nodes[0]["orgNameCn"]);
                    $("input[name=parentOrgCode]", layero).val(nodes[0]["orgCode"]);
                    $("input[name=orgPath]", layero).val(nodes[0]["orgPath"]);
                    $("input[name=rootOrgCode]", layero).val(nodes[0]["rootOrgCode"]);
                    form.render(null, "filter_add_organization_form");
                    form.on('submit(filter_add_organization_form)', function (data) {
                        $.ajax({
                            type: "POST",
                            url: addOrganizationUrl,
                            data: JSON.stringify(data.field),
                            contentType: "application/json; charset=utf-8",
                            success: function (message) {
                                layer.close(addDialog);
                                if (message.returnCode == '0000') {
                                	layer.msg("机构新增成功");
                                	refreshOrgTable();
                                }
                                if (organizationTree) {
                                    organizationTree.reAsyncChildNodes(null, "refresh");
                                }
                            },
                            error: function (message) {
                                layer.msg("机构新增发生异常，请联系管理员。");
                                if (organizationTree) {
                                    organizationTree.reAsyncChildNodes(null, "refresh");
                                }
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
            refreshOrgTable($("#organization_search_keyword").val());
        },
        exportOrg:function(){
        	   $.post(exportOrgExcelUrl, null, function (result) {
              console.log(result);
            });
         },
         getDDOrg:function(){
       	   $.post(getDDUrl, null, function (result) {
             console.log(result);
           });
        },
        dataConver:function(){
            $.post(dataConverExcelUrl);
        },
        getDDOrgDZ:function(){
      	   $.post(getDDOrgDZUrl, null, function (result) {
            console.log(result);
          });
       },

    };
    
    var orgListByPageUrl=config.basePath +"org/list"; //列出所有机构记录列表信息  
    var addOrganizationUrl=config.basePath +"org/add"; //添加机构信息
    var delOrganizationUrl=config.basePath +"org/delete"; //添加机构信息
    var orgTreeUrl = config.basePath +"org/tree"; //机构列表
    var checkOrgCodeExistUrl = config.basePath +"org/isExistOrgCode"; //校验岗位编码是否已经存在
    var exportOrgExcelUrl = config.basePath +"org/exportOrgExcel"; //导出
    var importOrgExcelUrl = config.basePath +"org/importOrgExcel"; //导入
    var getDDUrl = config.basePath +"org/getDDOrg"; //导入
    var getDDOrgDZUrl = config.basePath +"org/getDDOrgDZ"; //导入

    upload.render({
		elem: '#importOrg'
		,url:importOrgExcelUrl
		,accept: 'file' //普通文件
		,exts: 'xls|xlsx' //只允许上传压缩文件
		,done: function(res){
			if (res.returnCode == '0000') {
				if (organizationTree) {
		           organizationTree.reAsyncChildNodes(null, "refresh");
		        }
            	layer.msg("机构导入成功");
            	refreshOrgTable();
            }
           
		}
	});
    
    //自定义验证规则
	form.verify({
		  //校验编码是否已经存在
		  checkExistOrgCode : function(value) {
			  var isExist="";
			  if(value){
					  $.ajax({
						url : checkOrgCodeExistUrl + "?orgCode=" + value,
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
				  return "新增机构编码已存在或不可用，请重新输入机构编码";
			  } 
		  },
		  
	});
    var refreshOrgTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
        var selectNodes = organizationTree.getSelectedNodes();
        var orgCode = "";
        if (selectNodes && selectNodes.length == 1) {
        	orgCode = selectNodes[0]["orgCode"];
        }
        
        table.reload('organization_datatable', {
        	  height: 'full-200',
            page: {
                curr: 1 //重新从第 1 页开始
            }
            , where: {
            	 query: {
            		  keyWord: keyword,
                      orgCode: orgCode
                 }
            }
        });
    };
    //渲染组织机构树
    organizationTree = $.fn.zTree.init($('#organization_org_ztree_left'), {
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
                    	//childNodes[i].open = true;
                        childNodes[i].name = childNodes[i]["orgNameCn"].replace(/\.n/g, '.');
                    }
                    return childNodes;
                }
            }
            , callback: {
                onClick: function (event, treeId, treeNode, clickFlag) {
                    //执行重载
                	refreshOrgTable();
                },
                onAsyncSuccess: function (event, treeId, treeNode, msgString) {
                    var node = organizationTree.getNodeByParam("level ", "0");
                    if (node) {
                        organizationTree.selectNode(node);
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
    //渲染机构数据表格
    table.render({
        id: 'organization_datatable'
        , elem: '#organization_datatable'
        , url: orgListByPageUrl
        , method: 'post' //如果无需自定义HTTP类型，可不加该参数
        , response: {
            statusName: 'returnCode' //数据状态的字段名称，默认：code
            , statusCode: "0000" //成功的状态码，默认：0
            , msgName: 'msg' //状态信息的字段名称，默认：msg
            , countName: 'count' //数据总数的字段名称，默认：count
            , dataName: 'data' //数据列表的字段名称，默认：data
        } //如果无需自定义数据响应名称，可不加该参数
        , page: true,
        limit: 20
        , height: 'full-200'
        , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
        , cols: [[
            {type: 'numbers'}
            , {field: 'orgCode', width: 120, title: '机构编号'}
            , {field: 'orgNameCn',   title: '机构名称'}
            , {field: 'parentOrgCode', width: 220, title: '所属机构'}
            , {field: 'delFlag', templet: '#orgStatusTpl', width: 100, title: '状态'}
            , {field: 'createOperator', width: 100, title: '创建人'}
            , {field: 'createdDatetime',   templet:'#createTimeTpl', title: '创建时间'}
            , {fixed: 'right', width: 178, title: '操作', align: 'center', toolbar: '#organization_datatable_bar'}
        ]]
    });
    //监听操作栏
    table.on('tool(filter_organization_datatable)', function (obj) {
        var data = obj.data;
        if (obj.event === 'detail') {
        	 viewDialog = layer.open({
                 type: 1,
                 area: ['400px', '400px'],
                 shadeClose: true,
                 title: "机构详情",
                 content: $("#organization_view_data_div").html(),
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
            layer.confirm('是否确认删除机构？', function (index) {
            	 $.post(delOrganizationUrl+"?id=" + data.id, null, function (result) {
                     if (result["returnCode"] == "0000") {
                    	 obj.del();
                         layer.close(index);
                         layer.msg("删除机构成功");
                     } else {
                         layer.msg(result.codeDesc);
                     }
                     refreshOrgTable();
                     if (organizationTree) {
                         organizationTree.reAsyncChildNodes(null, "refresh");
                     }
                 });
            });
        } else if (obj.event === 'edit') {
            editDialog = layer.open({
            	 type: 1,
                 area: ['400px', '400px'],
                 maxmin: true,
                 shadeClose: true,
                 title: "修改机构",
                 content: $("#organization_modify_data_div").html(),
                 btn: ['保存', '取消'],
                 yes: function (index, layero) {
                     var $submitBtn = $("button[lay-filter=filter_modify_organization_form]", layero);
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
                    
                    form.render(null, "filter_modify_organization_form");
                    form.on('submit(filter_modify_organization_form)', function (data) {
                        $.ajax({
                            type: "POST",
                            url: addOrganizationUrl,
                            data: JSON.stringify(data.field),
                            contentType: "application/json; charset=utf-8",
                            success: function (result2) {
                                layer.close(editDialog);
                                if (result2["returnCode"] == '0000') {
                                	layer.msg("机构修改成功");
                                    refreshOrgTable();
                                }
                                if (organizationTree) {
                                    organizationTree.reAsyncChildNodes(null, "refresh");
                                }
                            },
                            error: function (message) {
                                layer.msg("机构修改发生异常，请联系管理员。");
                                if (organizationTree) {
                                    organizationTree.reAsyncChildNodes(null, "refresh");
                                }
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
    table.on('renderComplete(filter_organization_datatable)', function (obj) {
    	ht_auth.render("organization_auth");
    });
    //监听工具栏
    $('#organization_table_tools .layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    //刷新树的数据
   /* $('#organization_btn_refresh_tree').on('click', function (e) {
        if (organizationTree) {
            organizationTree.reAsyncChildNodes(null, "refresh");
        }
    });*/
    $('#organization_btn_tree .btn').on('click', function () {
        var type = $(this).data('type');
        switch (type) {
            case "refresh":
                if (organizationTree) {
                	organizationTree.reAsyncChildNodes(null, "refresh");
                }
                break;
            case "expandAll":
                if (organizationTree) {
                	organizationTree.expandAll(true);
                }
                break;
            case "collapseAll":
                if (organizationTree) {
                	organizationTree.expandAll(false);
                }
                break;
        }
    });
    var nodeList = [];
    //搜索树的数据
    $('#organization_search_tree_org').bind('input', function (e) {
        if (organizationTree && $(this).val() != "") {
            nodeList = organizationTree.getNodesByParamFuzzy("name", $(this).val());
            updateNodes(true);
        } else {
            updateNodes(false);
        }
    });

    //刷新树节点
    function updateNodes(highlight) {
        for (var i = 0, l = nodeList.length; i < l; i++) {
            nodeList[i].highlight = highlight;
            organizationTree.updateNode(nodeList[i]);
            if (highlight) {
                organizationTree.expandNode(organizationTree.getNodeByParam("orgCode", nodeList[i]["parentOrgCode"]), true, false, null, null);
            }
        }
    }
    ht_auth.render("organization_auth");
})