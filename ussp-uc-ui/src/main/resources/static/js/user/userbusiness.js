layui.use([ 'form', 'ztree', 'table', 'ht_config', 'ht_auth'], function () {
    var $ = layui.jquery
        , config = layui.ht_config
        , form = layui.form
        , table = layui.table
        , ht_auth = layui.ht_auth
        , addDialog = 0 //新增弹出框的ID
        , viewDialog = 0 //查询弹出框的ID
        , editDialog = 0 //修改弹出框的ID
        , resetPwdDialog = 0
        , selectBottomTabIndex = 0//当前选中的tab标签页
        , userBusiOrgTree //组织机构树控件
        , active = {
        add: function () { //弹出用户新增弹出框
            var nodes = userBusiOrgTree.getSelectedNodes();
            if (nodes.length == 0) {
                layer.alert("请先选择一个组织机构。");
                return false;
            }
            layer.close(addDialog);
            addDialog = layer.open({
                type: 1,
                area: ['400px', '400px'],
                shadeClose: true,
                title: "新增用户",
                content: $("#user_busi_add_data_div").html(),
                btn: ['保存', '取消'],
                yes: function (index, layero) {
                    var $submitBtn = $("button[lay-filter=filter_add_data_form]", layero);
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
                    $("input[name=businessOrgCode]", layero).val(nodes[0]["businessOrgCode"]);
                    $("input[name=orgPath]", layero).val(nodes[0]["orgPath"]);
                    $("input[name=rootOrgCode]", layero).val(nodes[0]["rootOrgCode"]);
                    form.render(null, "filter_add_data_form");
                    form.on('submit(filter_add_data_form)', function (data) {
                        $.ajax({
                            type: "POST",
                            url: config.basePath + "user/add",
                            data: JSON.stringify(data.field),
                            contentType: "application/json; charset=utf-8",
                            success: function (result) {
                                layer.close(index);
                                if (result["returnCode"] == '0000') {
                                    refreshUserBusiTable();
                                    layer.alert("用户新增成功。");
                                }
                            },
                            error: function (result) {
                                layer.msg("用户新增发生异常，请联系管理员。");
                                console.error(result);
                            }
                        });
                        return false;
                    });
                }
            })
        },
        search: function () {
            //执行重载
            refreshUserBusiTable($("#user_busi_search_keyword").val());
        },
    };
    var orgTreeUrl = config.basePath +"orgbusiness/tree"; //机构列表
    var loadListByPageUrl = config.basePath + 'userbusiness/loadListByPage'; //机构列表
  //自定义验证规则
	form.verify({
		//校验工号是否已经存在
		  checkExistJobNum : function(value) {
			  if(value){
				  var isExist="1";
					  $.ajax({
						  url : config.basePath + "user/checkUserExist?jobnum=" + value+"&userId="+$("#userId").val(),
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
					if(isExist=="1"){
						 return "工号已存在或不可用";
				    } 
			  }
		  },
		//校验用户名是否已经存在
		  checkExistLoginId : function(value) {
			  var isExist="1";
			  if(value){
					  $.ajax({
						  url : config.basePath + "user/checkUserExist?loginid=" + value+"&userId="+$("#userId").val(),
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
				  return "用户登录ID已存在或不可用";
			  } 
		  },
		//校验手机号是否已经存在
		  checkExistMobile : function(value) {
			  var isExist="1";
			  if(value){
					  $.ajax({
						  url : config.basePath + "user/checkUserExist?mobile=" + value+"&userId="+$("#userId").val(),
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
				  return "手机号已存在或不可用";
			  } 
		  },
		//校验邮箱是否已经存在
		  checkExistEmail : function(value) {
			  var isExist="";
			  if(value){
					  $.ajax({
						url : config.basePath + "user/checkUserExist?email=" + value+"&userId="+$("#userId").val(),
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
				  return "邮箱已存在或不可用";
			  } 
		  },
		  
	});
 
    var refreshUserBusiTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
        var selectNodes = userBusiOrgTree.getSelectedNodes();
        if (selectNodes && selectNodes.length == 1) {
            table.reload('user_busi_datatable', {
                height: 'full-200',
                page: {
                    curr: 1 //重新从第 1 页开始
                }
                , where: {
                    keyWord: keyword,
                    orgCode: selectNodes[0]["businessOrgCode"]
                }
            });
        }else{
        	table.reload('user_busi_datatable', {
                height: 'full-200',
                page: {
                    curr: 1 //重新从第 1 页开始
                }
                , where: {
                    keyWord: keyword,
                }
            });
        }
    };
    var refreshDalogBatchResetDataTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
        var selectNodes = userBusiOrgTree.getSelectedNodes();
        if (selectNodes && selectNodes.length == 1) {
        	 table.reload('batch_resetpwd_dalog_datatable', {
                 page: {
                     curr: 1 //重新从第 1 页开始
                 }
                 , where: {
                     keyWord: keyword,
                     businessOrgCode: selectNodes[0]["businessOrgCode"]
                 }
             });
        }
       
    };
    //渲染组织机构树
    userBusiOrgTree = $.fn.zTree.init($('#user_busi_org_ztree_left'), {
            async: {
                enable: true,
                url: orgTreeUrl,
                dataFilter: function (treeId, parentNode, childNodes) {
                    if (!childNodes) return null;
                    for (var i = 0, l = childNodes.length; i < l; i++) {
                    	//  childNodes[i].open = true;
                    	childNodes[i].name = childNodes[i]["businessOrgName"].replace(/\.n/g, '.');
                    }
                    return childNodes;
                }
            }
            , view: {
            	 height: "full-183"
                ,showIcon: false
                , selectedMulti: false
                , fontCss: function (treeId, treeNode) {
                    return (!!treeNode.highlight) ? {color: "#A60000", "font-weight": "bold"} : {
                        color: "#333",
                        "font-weight": "normal"
                    };
                }
            }
            , callback: {
                onClick: function (event, treeId, treeNode) {
                    //执行重载
                    refreshUserBusiTable();
                },
                onAsyncSuccess: function (event, treeId, treeNode) {
                    var node = userBusiOrgTree.getNodeByParam("level ", "0");
                    if (node) {
                        userBusiOrgTree.selectNode(node);
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
    //渲染用户数据表格
    table.render({
        id: 'user_busi_datatable'
        , elem: '#user_busi_datatable'
        , url: loadListByPageUrl
        // , where: {
        //     query: {
        //         businessOrgCode: "DEV1"
        //     }
        // }5
        , limit : 15
        , limits :[5, 10,15, 20, 30, 40, 50]
        , page: true
        , height: 'full-200'
        , cols: [[
            {type: 'numbers',event: 'rowClick'}
            , {field: 'userId', width: 110, title: '用户编号',event: 'rowClick'}
            , {field: 'userName',   title: '用户姓名',event: 'rowClick'}
            , {field: 'jobNumber', width: 100, title: '工号',event: 'rowClick'}
            , {field: 'loginId',   title: '登录ID',event: 'rowClick'}
            , {field: 'mobile', width: 120, title: '手机',event: 'rowClick'}
            , {field: 'email',   title: '邮箱'}
            //, {field: 'idNo', minWidth: 100, title: '身份证',event: 'rowClick'}
            , {field: 'orgName',   title: '所属机构',event: 'rowClick'}
            , {field: 'status', width: 60, title: '状态', templet: "#user_busi_status_laytpl",event: 'rowClick'}
           // , {field: 'updateOperator', width: 100, title: '更新人'}
           // , {field: 'lastModifiedDatetime', width: 150, title: '更新时间',event: 'rowClick'}
            , {fixed: 'right', width: 230, title: '操作', align: 'center', toolbar: '#user_busi_datatable_bar'}
        ]]
    });
    //监听操作栏
    table.on('tool(filter_user_busi_datatable)', function (obj) {
            var data = obj.data;
            if (obj.event === 'detail') {
                $.post(config.basePath + "user/view?userId=" + data.userId,  null, function (result) {
                    if (result["returnCode"] == "0000") {
                        viewDialog = layer.open({
                            type: 1,
                            area: ['720px', '430px'],
                            shadeClose: true,
                            title: "用户详情",
                            content: $("#user_busi_view_data_div").html(),
                            btn: ['取消'],
                            btn2: function () {
                                layer.closeAll('tips');
                            },
                            success: function (layero) {
                                var status = result.data.status;
                                result.data.status = status === "0" ? "正常" : (status === "2" ? "离职" : (status === "4" ? "冻结" : (status === "5" ? "锁定" : result.data.status)));
                                $.each(result.data, function (name, value) {
                                    var $input = $("input[name=" + name + "]", layero);
                                    if ($input && $input.length == 1) {
                                        $input.val(value);
                                    }
                                });
                            }
                        })
                    } else {
                        layer.msg(result.codeDesc);
                    }
                });
            } else if (obj.event === 'del') {
                layer.confirm('是否删除用户' + data.userName + "？", function (index) {
                    $.post(config.basePath + "user/delete?userId=" + data.userId, null, function (result) {
                        if (result["returnCode"] == "0000") {
                            refreshUserBusiTable();
                            layer.close(index);
                            layer.msg("删除用户成功。");
                        } else {
                            layer.msg(result.codeDesc);
                        }
                    });
                });
            } else if (obj.event === 'edit') {
                layer.close(editDialog);
                $.post(config.basePath + "user/view?userId=" + data.userId, null, function (result) {
                    if (result["returnCode"] == "0000") {
                        editDialog = layer.open({
                            type: 1,
                            area: ['400px', '450px'],
                            shadeClose: false,
                            title: "修改用户",
                            content: $("#user_busi_modify_data_div").html(),
                            btn: ['保存', '取消'],
                            yes: function (index, layero) {
                                var $submitBtn = $("button[lay-filter=user_busi_filter_modify_data_form]", layero);
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
                                //表单数据填充
                                $.each(result.data, function (name, value) {
                                    var $input = $("input[name=" + name + "]", layero);
                                    if ($input && $input.length == 1) {
                                        $input.val(value);
                                    }
                                });
                                $('#orgNameEdit',layero).on('click', function () {
                                	layer.open({
                                        type: 2,
                                        area: ['400px', '600px'],
                                        maxmin: true,
                                        shadeClose: true,
                                        title: "选择机构",
                                        content: "/html/user/orgTreeDialog.html",
                                        success: function (layero, index) {
                                       	 /* 渲染表单 */
                                            form.render();
                                        }
                                    })
                                });
                                
                                form.render(null, "user_busi_filter_modify_data_form");
                                form.on('submit(user_busi_filter_modify_data_form)', function (data) {
                                    $.ajax({
                                        type: "POST",
                                        url: config.basePath + "user/update",
                                        data: JSON.stringify(data.field),
                                        contentType: "application/json; charset=utf-8",
                                        success: function (result2) {
                                            if (result2["returnCode"] == '0000') {
                                            	layer.close(index);
                                                refreshUserBusiTable();
                                                layer.alert("用户修改成功。");
                                            }else{
                                                 layer.msg(result2["msg"]);
                                            }
                                        },
                                        error: function (message) {
                                            layer.msg("用户新增发生异常，请联系管理员。");
                                            layer.close(index);
                                            console.error(message);
                                        }
                                    });
                                    return false;
                                });
                            }
                        })
                    } else {
                        layer.msg(result.codeDesc);
                    }
                });
            }else if (obj.event === 'resetpwd') {
            	layer.confirm("确认重置用户 密码？", function (index) {
            		$.post(config.basePath + "user/sendEmailRestPwd?userId=" + data.userId, null, function (result) {
                        if (result["returnCode"] == "0000") {
                          layer.close(index);
                       	  layer.msg("密码重置成功");
                        } else {
                            layer.msg(result.codeDesc);
                        }
                    });
            	 });
            }else if (obj.event === 'restate') {
            	layer.confirm("确认恢复用户状态？", function (index) {
            		$.post(config.basePath + "user/changUserState?userId=" + data.userId+"&status=0", null, function (result) {
                        if (result["returnCode"] == "0000") {
                          layer.close(index);
                       	  layer.msg("恢复用户状态成功");
                       	  refreshUserBusiTable();
                        } else {
                            layer.msg(result.codeDesc);
                        }
                    });
            	 });
            }  
        }
    );
    
    
    var $keywordInput = $("#user_busi_search_keyword");
    $keywordInput.keydown(function (e) {
        if (e.keyCode == 13) {
            var keyWord = $keywordInput.val();
            refreshUserBusiTable(keyWord);
        }
    });
    
    table.on('renderComplete(filter_user_busi_datatable)', function (obj) {
        ht_auth.render("user_busi_auth");
    });
    //监听工具栏
    $('#user_busi_table_tools .layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    //刷新树的数据
   /* $('#user_busi_btn_refresh_tree').on('click', function (e) {
        if (userBusiOrgTree) {
            userBusiOrgTree.reAsyncChildNodes(null, "refresh");
        }
    });*/
    
    $('#user_busi_btn_tree .btn').on('click', function () {
        var type = $(this).data('type');
        switch (type) {
            case "refresh":
                if (userBusiOrgTree) {
                	userBusiOrgTree.reAsyncChildNodes(null, "refresh");
                }
                break;
            case "expandAll":
                if (userBusiOrgTree) {
                	userBusiOrgTree.expandAll(true);
                }
                break;
            case "collapseAll":
                if (userBusiOrgTree) {
                	userBusiOrgTree.expandAll(false);
                }
                break;
        }
    });
    var nodeList = [];
    //搜索树的数据
    $('#user_busi_search_tree_org').bind('input', function (e) {
        if (userBusiOrgTree && $(this).val() != "") {
            nodeList = userBusiOrgTree.getNodesByParamFuzzy("name", $(this).val());
            updateNodes(true);
        } else {
            updateNodes(false);
        }
    });

    //刷新树节点
    function updateNodes(highlight) {
        for (var i = 0, l = nodeList.length; i < l; i++) {
            nodeList[i].highlight = highlight;
            userBusiOrgTree.updateNode(nodeList[i]);
            if (highlight) {
                userBusiOrgTree.expandNode(userBusiOrgTree.getNodeByParam("businessOrgCode", nodeList[i]["parentOrgCode"]), true, false, null, null);
            }
        }
    }
    ht_auth.render("user_busi_auth");
})