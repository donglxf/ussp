var roleListByPageUrl=basepath +"role/in/list"; //列出所有角色记录列表信息  
var addRoleUrl=basepath +"role/in/add"; //添加角色信息
var delRoleUrl=basepath +"role/in/delete"; //删除角色信息
var statusRoleUrl=basepath +"role/in/stop"; //禁用
var checkRoleCodeExist = basepath +"role/isExistRoleCode"; //校验角色编码是否已经存在
layui.use(['form',   'table', 'ht_auth' ], function () {
    var $ = layui.jquery
        , form = layui.form
        , table = layui.table
        , ht_auth = layui.ht_auth
        , addDialog = 0 //新增弹出框的ID
        , viewDialog = 0 //查询弹出框的ID
        , editDialog = 0 //修改弹出框的ID
        , active = {
        add: function () { //弹出用户新增弹出框
            layer.close(addDialog);
            addDialog = layer.open({
                type: 1,
                area: ['400px', '400px'],
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
        	refreshTable($("#role_search_keyword").val());
        }
    };
    
    //自定义验证规则
	form.verify({
		  //校验编码是否已经存在
		  checkExistRoleCode : function(value) {
			  var isExist="";
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
				  return "新增角色编码不可用，请重新输入角色编码";
			  } 
		  },
		  
	});
    var refreshTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
        table.reload('role_datatable', {
            page: {
                curr: 1 //重新从第 1 页开始
            }
            , where: {
                keyWord: keyword
            }
        });
        ht_auth.render();
    };
    //渲染用户数据表格
    table.render({
        id: 'role_datatable'
        , elem: '#role_datatable'
        , url: roleListByPageUrl
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
            , {field: 'roleCode', width: 150, title: '角色编号'}
            , {field: 'roleNameCn',   title: '角色名称'}
            , {field: 'status', width: 100,templet: '#statusTpl', title: '状态'}
            , {field: 'createOperator',   title: '创建人'}
            , {field: 'createdDatetime', width: 200,templet: '#createTimeTpl', title: '创建时间'}
            , {fixed: 'right', width: 300,  title: '操作',   toolbar: '#role_datatable_bar'}
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
             	obj.del();
             	 $.post(delRoleUrl+"?id=" + data.id, null, function (result) {
                      if (result["returnCode"] == "0000") {
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
                 title: "新增岗位",
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
        ht_auth.render();
    });
    //监听工具栏
    $('#role_table_tools .layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    
    ht_auth.render("role_auth");

})