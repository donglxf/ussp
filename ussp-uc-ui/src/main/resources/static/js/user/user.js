var userapp_userId = "";
var userrole_userId = "";
var userpositionUserId = "";
var refreshUserAppTable = "";
var refreshUserRoleTable = "";
var refreshuserpositionTable="";
layui.use(['element','form', 'ztree', 'table', 'ht_config', 'ht_auth'], function () {
    var $ = layui.jquery
        , config = layui.ht_config
        , element = layui.element
        , form = layui.form
        , table = layui.table
        , ht_auth = layui.ht_auth
        , addDialog = 0 //新增弹出框的ID
        , viewDialog = 0 //查询弹出框的ID
        , editDialog = 0 //修改弹出框的ID
        , resetPwdDialog = 0
        , selectBottomTabIndex = 0//当前选中的tab标签页
        , userOrgTree //组织机构树控件
        , active = {
        add: function () { //弹出用户新增弹出框
            var nodes = userOrgTree.getSelectedNodes();
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
                content: $("#user_add_data_div").html(),
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
                    $("input[name=orgCode]", layero).val(nodes[0]["orgCode"]);
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
                                    refreshTable();
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
            refreshTable($("#user_search_keyword").val());
        },
        getDtoUserInfo: function () {
      	  $.ajax({
					url : config.basePath + "user/getDtoUserInfo",
					type : 'POST',
					async : false,
					success : function(result) {
						 console.info(result);
					}
				}); 
        },
	     getOrgSub: function () {
	    	  $.ajax({
					url : config.basePath + "org/testGetOrgInfoByOrgType",
					type : 'POST',
					async : false,
					success : function(result) {
						 console.info(result);
					}
				}); 
	      },
        
        batchResetPwd: function () {
        	 var nodes = userOrgTree.getSelectedNodes();
             if (nodes.length == 0) {
                 layer.alert("请先选择一个组织机构。");
                 return false;
             }
            layer.close(resetPwdDialog);
            resetPwdDialog = layer.open({
                type: 1,
                area: ['1000px', '645px'],
                shadeClose: true,
                title: "设置用户密码",
                content: $("#batch_resetpwd_data_div").html(),
                btn: ['设置密码', '取消'],
                yes: function (index, layero) {
                    var checkStatus = table.checkStatus('batch_resetpwd_dalog_datatable');
                    var resetPwdUserdata = checkStatus.data;
                    if(resetPwdUserdata.length>0){
                    	 $.ajax({
                             type: "POST",
                             url: config.basePath + 'user/sendEmailRestPwdBatch',
                             data: JSON.stringify({
                                 resetPwdUserdata: resetPwdUserdata
                             }),
                             contentType: "application/json; charset=utf-8",
                             success: function (result) {
                                 layer.close(index);
                                 if (result["returnCode"] == '0000') {
                                     layer.msg("密码设置成功");
                                 }
                             },
                             error: function (result) {
                                 console.error(result);
                             }
                         })
                    }else{
                    	layer.msg("请选择用户");
                    }
                    
                },
                btn2: function () {
                    layer.closeAll('tips');
                },
                success: function (layero, index) {
                    table.render({
                        id: 'batch_resetpwd_dalog_datatable'
                        , elem: $('#batch_resetpwd_dalog_datatable', layero)
                        , url: config.basePath + 'user/queryUserIsNullPwd'
                        ,where: {
                        	orgCode: nodes[0]["orgCode"],
                        }
                        , page: true
                        , height: "471"
                        , cols: [[
                            {type: 'numbers'}
                            , {type: 'checkbox'}
                            , {field: 'userId', width: 100, title: '用户编号',}
                            , {field: 'userName', width: 100, title: '用户姓名'}
                            , {field: 'jobNumber', width: 100, title: '工号'}
                            , {field: 'mobile', width: 120, title: '手机'}
                            , {field: 'email',   title: '邮箱'}
                            , {field: 'idNo', minWidth: 100, title: '身份证'}
                            , {field: 'orgName', minWidth: 100, title: '所属机构'}
                        ]]
                    });
                    var $keywordInput = $("#batch_resetpwd_search_keyword", layero);
                    $('#batch_resetpwd_dialog_search', layero).on('click', function () {
                        var keyWord = $keywordInput.val();
                        refreshDalogBatchResetDataTable(keyWord);
                    });
                    $keywordInput.keydown(function (e) {
                        if (e.keyCode == 13) {
                            var keyWord = $keywordInput.val();
                            refreshDalogBatchResetDataTable( keyWord);
                        }
                    });
                }
            });
        },
    };
    
  //自定义验证规则
	form.verify({
		//校验工号是否已经存在
		  checkExistJobNum : function(value) {
			  var isExist="1";
			  if(value){
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
			  }
			  if(isExist=="1"){
				  return "工号已存在或不可用";
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
	//刷新用户系统 refreshUserAppTable
	   refreshUserAppTable = function (keyword) {
	        if (!keyword) {
	            keyword = null;
	        }
	        if(!userapp_userId){
	        	return;
	        }
	        table.reload('userapp_app_datatable', {
	   		 height: 'full-600' ,
	   	        page: {
	   	            curr: 1 //重新从第 1 页开始
	   	        }
	   	        , where: {
	   	        	query: {
	             		   keyWord: keyword,
	                       userId:userapp_userId
	                  }
	   	        }
	   	   });
	    };
	  //刷新用户岗位
	      refreshuserpositionTable = function (keyword) {
	        if (!keyword) {
	            keyword = null;
	        }
	        table.reload('userposition_position_datatable', {
	   		 height: 'full-600',
	   	        page: {
	   	            curr: 1 //重新从第 1 页开始
	   	        }
	   	        , where: {
	   	        	query: {
	             		   keyWord: keyword,
	                       userId:userpositionUserId
	                  }
	   	        }
	   	   });
	    };
	  //刷新用户角色 refreshUserRoleTable 
	    refreshUserRoleTable = function (keyword) {
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
	  }
    var refreshTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
        var selectNodes = userOrgTree.getSelectedNodes();
        if (selectNodes && selectNodes.length == 1) {
            table.reload('user_datatable', {
                height: 'full-673',
                page: {
                    curr: 1 //重新从第 1 页开始
                }
                , where: {
                    keyWord: keyword,
                    orgCode: selectNodes[0]["orgCode"]
                }
            });
        }else{
        	table.reload('user_datatable', {
                height: 'full-673',
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
        var selectNodes = userOrgTree.getSelectedNodes();
        if (selectNodes && selectNodes.length == 1) {
        	 table.reload('batch_resetpwd_dalog_datatable', {
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
    //渲染组织机构树
    userOrgTree = $.fn.zTree.init($('#user_org_ztree_left'), {
            async: {
                enable: true,
                url: config.basePath + "org/tree",
                dataFilter: function (treeId, parentNode, childNodes) {
                    if (!childNodes) return null;
                    for (var i = 0, l = childNodes.length; i < l; i++) {
                    	//  childNodes[i].open = true;
                        childNodes[i].name = childNodes[i]["orgNameCn"].replace(/\.n/g, '.');
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
                    refreshTable();
                },
                onAsyncSuccess: function (event, treeId, treeNode) {
                    var node = userOrgTree.getNodeByParam("level ", "0");
                    if (node) {
                        userOrgTree.selectNode(node);
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
    //渲染用户数据表格
    table.render({
        id: 'user_datatable'
        , elem: '#user_datatable'
        , url: config.basePath + 'user/loadListByPage'
        // , where: {
        //     query: {
        //         orgCode: "DEV1"
        //     }
        // }5
        , limit : 5
        , limits :[5, 10, 20, 30, 40, 50]
        , page: true
        , height: 'full-673'
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
            , {field: 'status', width: 60, title: '状态', templet: "#user_status_laytpl",event: 'rowClick'}
           // , {field: 'updateOperator', width: 100, title: '更新人'}
           // , {field: 'lastModifiedDatetime', width: 150, title: '更新时间',event: 'rowClick'}
            , {fixed: 'right', width: 230, title: '操作', align: 'center', toolbar: '#user_datatable_bar'}
        ]]
    });
    //监听操作栏
    table.on('tool(filter_user_datatable)', function (obj) {
            var data = obj.data;
            if (obj.event === 'detail') {
                $.post(config.basePath + "user/view?userId=" + data.userId,  null, function (result) {
                    if (result["returnCode"] == "0000") {
                        viewDialog = layer.open({
                            type: 1,
                            area: ['720px', '430px'],
                            shadeClose: true,
                            title: "用户详情",
                            content: $("#user_view_data_div").html(),
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
                            refreshTable();
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
                            shadeClose: true,
                            title: "修改用户",
                            content: $("#user_modify_data_div").html(),
                            btn: ['保存', '取消'],
                            yes: function (index, layero) {
                                var $submitBtn = $("button[lay-filter=user_filter_modify_data_form]", layero);
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
                                form.render(null, "user_filter_modify_data_form");
                                form.on('submit(user_filter_modify_data_form)', function (data) {
                                    $.ajax({
                                        type: "POST",
                                        url: config.basePath + "user/update",
                                        data: JSON.stringify(data.field),
                                        contentType: "application/json; charset=utf-8",
                                        success: function (result2) {
                                            if (result2["returnCode"] == '0000') {
                                            	layer.close(index);
                                                refreshTable();
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
                       	  refreshTable();
                        } else {
                            layer.msg(result.codeDesc);
                        }
                    });
            	 });
            } else if (obj.event === 'rowClick') {
            	var data = obj.data;
                userapp_userId = data.userId;
                userrole_userId = data.userId;
                userpositionUserId = data.userId;
            	 switch (selectBottomTabIndex) {
                 case 0:
                     refreshUserAppTable();
                     break;
                 case 1:
                	 refreshuserpositionTable();
                     break;
                 case 2:
                	 refreshUserRoleTable();
                     break;
             }
                //刷新用户角色 refreshUserRoleTable 刷新用户系统 refreshUserAppTable

            }
        }
    );
    
    //菜单和模块tab页切换事件
    element.on('tab(user_bottom_tab)', function (data) {
    	selectBottomTabIndex = data.index;
        switch (data.index) {
            case 0:
            	refreshUserAppTable();
                break;
            case 1:
            	refreshuserpositionTable();
                break;
            case 2:
            	refreshUserRoleTable();
                break;
        }
    });
    
    var $keywordInput = $("#user_search_keyword");
    $keywordInput.keydown(function (e) {
        if (e.keyCode == 13) {
            var keyWord = $keywordInput.val();
            refreshTable(keyWord);
        }
    });
    
    table.on('renderComplete(filter_user_datatable)', function (obj) {
        ht_auth.render("user_auth");
    });
    //监听工具栏
    $('#user_table_tools .layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    //刷新树的数据
   /* $('#user_btn_refresh_tree').on('click', function (e) {
        if (userOrgTree) {
            userOrgTree.reAsyncChildNodes(null, "refresh");
        }
    });*/
    
    $('#user_btn_tree .btn').on('click', function () {
        var type = $(this).data('type');
        switch (type) {
            case "refresh":
                if (userOrgTree) {
                	userOrgTree.reAsyncChildNodes(null, "refresh");
                }
                break;
            case "expandAll":
                if (userOrgTree) {
                	userOrgTree.expandAll(true);
                }
                break;
            case "collapseAll":
                if (userOrgTree) {
                	userOrgTree.expandAll(false);
                }
                break;
        }
    });
    var nodeList = [];
    //搜索树的数据
    $('#user_search_tree_org').bind('input', function (e) {
        if (userOrgTree && $(this).val() != "") {
            nodeList = userOrgTree.getNodesByParamFuzzy("name", $(this).val());
            updateNodes(true);
        } else {
            updateNodes(false);
        }
    });

    //刷新树节点
    function updateNodes(highlight) {
        for (var i = 0, l = nodeList.length; i < l; i++) {
            nodeList[i].highlight = highlight;
            userOrgTree.updateNode(nodeList[i]);
            if (highlight) {
                userOrgTree.expandNode(userOrgTree.getNodeByParam("orgCode", nodeList[i]["parentOrgCode"]), true, false, null, null);
            }
        }
    }

    ht_auth.render("user_auth");
})