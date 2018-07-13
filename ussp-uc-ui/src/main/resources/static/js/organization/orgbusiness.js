
layui.use(['form', 'ztree', 'table','ht_config', 'ht_auth', ], function () {
    var $ = layui.jquery
        , form = layui.form
        , table = layui.table
        , config = layui.ht_config
        , ht_auth = layui.ht_auth
        , upload = layui.upload
        , addDialog = 0 //新增弹出框的ID
        , viewDialog = 0 //查询弹出框的ID
        , editDialog = 0 //修改弹出框的ID
        , orgBusiTree //组织机构树控件
        , active = {
        add: function () { //弹出机构新增弹出框
            var nodes = orgBusiTree.getSelectedNodes();
            if (nodes.length == 0) {
                layer.alert("请先选择一个组织机构。");
                return false;
            }
            layer.close(addDialog);
            addDialog = layer.open({
                type: 1,
                area: ['700px', '820px'],
                maxmin: true,
                shadeClose: false,
                title: "新增机构",
                content: $("#organization_busi_add_data_div").html(),
                btn: ['保存', '取消'],
                yes: function (index, layero) {
                    var $submitBtn = $("button[lay-filter=filter_add_organization_busi_form]", layero);
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
                    $("input[name=parentOrgNamcn]", layero).val(nodes[0]["businessOrgName"]);
                    $("input[name=parentOrgCode]", layero).val(nodes[0]["businessOrgCode"]);
                    $("input[name=districtCode]", layero).val(nodes[0]["districtCode"]);
                    $("input[name=branchCode]", layero).val(nodes[0]["branchCode"]);
                    $.ajax({
                        type: "GET",
                        url: getNewOrgCodeUrl,
                        data: {
                        	parentOrgCode: nodes[0]["businessOrgCode"],
                        },
                        dataType: "text",
                        success: function (result) {
                            $("input[name=businessOrgCode]", layero).val(result);
                        }
                    });
                    getNextChilds("province",null,layero);
                    formOnSelect(layero);
                    form.render(null, "filter_add_organization_busi_form");
                    form.on('submit(filter_add_organization_busi_form)', function (data) {
                        $.ajax({
                            type: "POST",
                            url: addOrganizationUrl,
                            data: JSON.stringify(data.field),
                            contentType: "application/json; charset=utf-8",
                            success: function (message) {
                                layer.close(addDialog);
                                if (message.returnCode == '0000') {
                                	layer.msg("机构新增成功");
                                	refreshOrgBusiTable();
                                }
                            },
                            error: function (message) {
                                layer.msg("机构新增发生异常，请联系管理员。");
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
            refreshOrgBusiTable($("#organization_busi_search_keyword").val());
        },
    };
    
    var orgListByPageUrl=config.basePath +"orgbusiness/list"; //列出所有机构记录列表信息  
    var addOrganizationUrl=config.basePath +"orgbusiness/add"; //添加机构信息
    var delOrganizationUrl=config.basePath +"orgbusiness/delete"; //添加机构信息
    var orgTreeUrl = config.basePath +"orgbusiness/tree"; //机构列表
    var checkOrgCodeExistUrl = config.basePath +"orgbusiness/isExistOrgCode"; //校验岗位编码是否已经存在
    var getNewOrgCodeUrl = config.basePath +"orgbusiness/getNewOrgCode"; //获取OrgCode
    var getNextChildsUrl =  config.basePath +"orgbusiness/getNextChilds"; //获取省市区
    //自定义验证规则
	form.verify({
		  //校验编码是否已经存在
		  checkExistOrgCode : function(value) {
			  var isExist="1";
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
				  return "机构编码已存在或不可用";
			  } 
		  },
		  
	});
    var refreshOrgBusiTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
        var selectNodes = orgBusiTree.getSelectedNodes();
        var businessOrgCode = "";
        if (selectNodes && selectNodes.length == 1) {
        	businessOrgCode = selectNodes[0]["businessOrgCode"];
        }
        
        table.reload('organization_busi_datatable', {
        	  height: 'full-200',
            page: {
                curr: 1 //重新从第 1 页开始
            }
            , where: {
            	 query: {
            		  keyWord: keyword,
                      businessOrgCode: businessOrgCode
                 }
            }
        });
    };
    //渲染组织机构树
    orgBusiTree = $.fn.zTree.init($('#organization_busi_org_ztree_left'), {
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
                        childNodes[i].name = childNodes[i]["businessOrgName"].replace(/\.n/g, '.');
                    }
                    return childNodes;
                }
            }
            , callback: {
                onClick: function (event, treeId, treeNode, clickFlag) {
                    //执行重载
                	refreshOrgBusiTable();
                },
                onAsyncSuccess: function (event, treeId, treeNode, msgString) {
                    var node = orgBusiTree.getNodeByParam("level ", "0");
                    if (node) {
                        orgBusiTree.selectNode(node);
                    }
                }
            },
            data: {
                simpleData: {
                    enable: true
                    , idKey: "businessOrgCode"
                    , pIdKey: "parentOrgCode"
                }
            }
        }
    );
    //渲染机构数据表格
    table.render({
        id: 'organization_busi_datatable'
        , elem: '#organization_busi_datatable'
        , url: orgListByPageUrl
        , method: 'post' //如果无需自定义HTTP类型，可不加该参数
        , page: true,
        limit: 20
        , height: 'full-200'
        , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
        , cols: [[
            {type: 'numbers'}
            , {field: 'businessOrgCode', width: 120, title: '机构编号'}
            , {field: 'businessOrgName',   title: '机构名称'}
            , {field: 'orgType', width: 100,   title: '机构类型',templet:'#orgTypeTpl',}
            , {field: 'parentOrgCode', width: 220, title: '所属机构'}
            , {field: 'delFlag', templet: '#orgBusiStatusTpl', width: 100, title: '状态'}
            , {field: 'createOperator', width: 100, title: '创建人'}
            , {field: 'createdDatetime',   templet:'#createTimeTpl', title: '创建时间'}
            , {fixed: 'right', width: 178, title: '操作', align: 'center', toolbar: '#organization_busi_datatable_bar'}
        ]]
    });
    //监听操作栏
    table.on('tool(filter_organization_busi_datatable)', function (obj) {
        var data = obj.data;
        if (obj.event === 'detail') {
        	 viewDialog = layer.open({
                 type: 1,
                  area: ['700px', '500px'],
                 shadeClose: true,
                 title: "机构详情",
                 content: $("#organization_busi_view_data_div").html(),
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
                         if("orgLevel"==name){
                        	 if(value){
                        		 $("input:radio[name='orgLevel'][value="+value+"]", layero).attr("checked",true);
                        	 }
                        }
                        if("isHeadDept"==name){
                        	 $("input:radio[name='isHeadDept'][value="+value+"]", layero).attr("checked",true);
                       }
                        if("isAppRovalDept"==name){
                        	$("input:radio[name='isAppRovalDept'][value="+value+"]", layero).attr("checked",true);
                       }
                     });
                     form.render(null, "filter_view_organization_busi_form");
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
                     refreshOrgBusiTable();
                 });
            });
        } else if (obj.event === 'edit') {
            editDialog = layer.open({
            	 type: 1,
            	  area: ['700px', '720px'],
                 maxmin: true,
                 shadeClose: true,
                 title: "修改机构",
                 content: $("#organization_busi_modify_data_div").html(),
                 btn: ['保存', '取消'],
                 yes: function (index, layero) {
                     var $submitBtn = $("button[lay-filter=filter_modify_organization_busi_form]", layero);
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
                	console.log(data);
                	//加载表单数据
                    $.each(data, function (name, value) {
                        var $input = $("input[name=" + name + "]", layero);
                        if ($input && $input.length == 1) {
                            $input.val(value);
                        }
                        if("orgLevel"==name){
                        	 if(value){
                        		 $("input:radio[name='orgLevel'][value="+value+"]", layero).attr("checked",true);
                        	 }
                        }
                        if("isHeadDept"==name){
                        	 $("input:radio[name='isHeadDept'][value="+value+"]", layero).attr("checked",true);
                       }
                       if("isAppRovalDept"==name){
                        	$("input:radio[name='isAppRovalDept'][value="+value+"]", layero).attr("checked",true);
                       }
                    });
                    getNextChilds("province",null,layero,data.province);
                    getNextChilds("city",data.province,layero,data.city);
                    getNextChilds("county",data.city,layero,data.county);
                    
                    formOnSelect(layero);
                    form.render(null, "filter_modify_organization_busi_form");
                    form.on('submit(filter_modify_organization_busi_form)', function (data) {
                        $.ajax({
                            type: "POST",
                            url: addOrganizationUrl,
                            data: JSON.stringify(data.field),
                            contentType: "application/json; charset=utf-8",
                            success: function (result2) {
                                layer.close(editDialog);
                                if (result2["returnCode"] == '0000') {
                                	layer.msg("机构修改成功");
                                    refreshOrgBusiTable();
                                }
                               /* if (orgBusiTree) {
                                    orgBusiTree.reAsyncChildNodes(null, "refresh");
                                }*/
                            },
                            error: function (message) {
                                layer.msg("机构修改发生异常，请联系管理员。");
                                if (orgBusiTree) {
                                    orgBusiTree.reAsyncChildNodes(null, "refresh");
                                }
                                console.error(message);
                            }
                        });
                        return false;
                    });
                }
            });
        }
    });
    var formOnSelect = function (layeros) {
        //监听省份下拉改变
        form.on('select(province)', function(data){
            getNextChilds("city",data.value,layeros);
            getNextChilds("county",data.value,layeros);
        });
        //监听市下拉改变
        form.on('select(city)', function(data){
            getNextChilds("county",data.value,layeros);
        });
    };
    var getNextChilds = function (typeCode,parentId, layeros,defaultValue) {
        var dto = {
            typeCode:typeCode,
            parentId:parentId
        };
        $.ajax({
            type: "POST",
            url: getNextChildsUrl,
            contentType: "application/json; charset=utf-8",
            data:JSON.stringify(dto),
            success: function (result) {
            	$('#'+typeCode,layeros).html('<option value>请选择</option>');
                if (result.returnCode == '0000') {
                	 $.each(result.data,function(index,item){    // 下拉菜单里添加元素
                     	var opt = null;  
                     	if(item.id == defaultValue){
         	        		opt = new Option(item.name, item.id, false, true);
         	        	}else{
         	        		opt = new Option(item.name, item.id);
         	        	}
                     	$('#'+typeCode,layeros).append(opt);
                     });
                }
                form.render("select");//下拉菜单渲染 把内容加载进去
            },
            error: function (message) {
                console.error(message);
            }
        });
    };
    
    var getEditInitParams = function(type, parentValue, defaultValue){
    	if(!parentValue){
    		$.ajax({
                type: "GET",
                url: getProvinceUrl+"?typeCode="+type,
                success: function (result) {
                	$('#'+type).html('<option value>请选择</option>');
                    if (result.returnCode == '0000') {
                        $.each(result.data,function(index,item){    // 下拉菜单里添加元素
                        	var opt = null;  
                        	if(item.code == defaultValue){
            	        		opt = new Option(item.name, item.code, false, true);
            	        	}else{
            	        		opt = new Option(item.name, item.code);
            	        	}
                        	$('#'+type).append(opt);
                        });
                    }
                    form.render("select");//下拉菜单渲染 把内容加载进去
                },
                error: function (message) {
                    console.error(message);
                }
            });
    	}else{
    		// 级联下拉数据
    		getNextChilds(type, parentValue, defaultValue);
    	}
        
    };

    table.on('renderComplete(filter_organization_busi_datatable)', function (obj) {
    	ht_auth.render("organization_busi_auth");
    });
    //监听工具栏
    $('#organization_busi_table_tools .layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    var $keywordOrgBusiInput = $("#organization_busi_search_keyword");
    $keywordOrgBusiInput.keydown(function (e) {
        if (e.keyCode == 13) {
            var keyWord = $keywordOrgBusiInput.val();
            refreshOrgBusiTable(keyWord);
        }
    });
    $('#organization_busi_btn_tree .btn').on('click', function () {
        var type = $(this).data('type');
        switch (type) {
            case "refresh":
                if (orgBusiTree) {
                	orgBusiTree.reAsyncChildNodes(null, "refresh");
                }
                break;
            case "expandAll":
                if (orgBusiTree) {
                	orgBusiTree.expandAll(true);
                }
                break;
            case "collapseAll":
                if (orgBusiTree) {
                	orgBusiTree.expandAll(false);
                }
                break;
        }
    });
    var nodeList = [];
    //搜索树的数据
    $('#organization_busi_search_tree_org').bind('input', function (e) {
        if (orgBusiTree && $(this).val() != "") {
            nodeList = orgBusiTree.getNodesByParamFuzzy("name", $(this).val());
            updateNodes(true);
        } else {
            updateNodes(false);
        }
    });

    //刷新树节点
    function updateNodes(highlight) {
        for (var i = 0, l = nodeList.length; i < l; i++) {
            nodeList[i].highlight = highlight;
            orgBusiTree.updateNode(nodeList[i]);
            if (highlight) {
                orgBusiTree.expandNode(orgBusiTree.getNodeByParam("businessOrgCode", nodeList[i]["parentOrgCode"]), true, false, null, null);
            }
        }
    }
    ht_auth.render("organization_busi_auth");
})