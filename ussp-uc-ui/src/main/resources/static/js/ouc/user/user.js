layui.use([ 'form',   'table', 'ht_config', 'ht_auth'], function () {
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
        , active = {
    	out_user_search: function () {
            //执行重载
            refreshOutUserTable($("#out_user_search_keyword").val());
        },
        
    };
	
	var loadListUserByPage=config.baseOucPath + 'user/loadListByPage'; //列出所有外部用户
    
  //自定义验证规则
    var refreshOutUserTable = function (keyword) {
    	table.reload('out_user_datatable', {
            height: 'full-200',
            page: {
                curr: 1 //重新从第 1 页开始
            }
            , where: {
                keyWord: keyword,
            }
        });
    };
    //渲染用户数据表格
    table.render({
        id: 'out_user_datatable'
        , elem: '#out_user_datatable'
        , url: loadListUserByPage
        , page: true
        , height: 'full-200'
        , cols: [[
            {type: 'numbers',event: 'rowClick'}
            , {field: 'userName', width: 300,   title: '用户姓名',event: 'rowClick'}
            , {field: 'mobile', width: 200,   title: '手机',event: 'rowClick'}
            , {field: 'email', width: 300,   title: '邮箱'}
            , {field: 'dataSource', width: 200,  title: '注册系统'}
            , {field: 'status', width: 150, title: '状态', templet: "#user_status_laytpl",event: 'rowClick'}
            , {fixed: 'right', width: 250, title: '操作', align: 'center', toolbar: '#out_user_datatable_bar'}
        ]]
    });
    //监听操作栏
    table.on('tool(filter_out_user_datatable)', function (obj) {
            var data = obj.data;
            if (obj.event === 'detail') {
                $.post(config.baseOucPath + "user/view?userId=" + data.userId,  null, function (result) {
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
                    $.post(config.baseOucPath + "user/delete?userId=" + data.userId, null, function (result) {
                        if (result["returnCode"] == "0000") {
                            refreshOutUserTable();
                            layer.close(index);
                            layer.msg("删除用户成功。");
                        } else {
                            layer.msg(result.codeDesc);
                        }
                    });
                });
            } else if (obj.event === 'edit') {
                layer.close(editDialog);
                $.post(config.baseOucPath + "user/view?userId=" + data.userId, null, function (result) {
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
                                        url: config.baseOucPath + "user/update",
                                        data: JSON.stringify(data.field),
                                        contentType: "application/json; charset=utf-8",
                                        success: function (result2) {
                                            if (result2["returnCode"] == '0000') {
                                            	layer.close(index);
                                                refreshOutUserTable();
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
            		$.post(config.baseOucPath + "user/sendEmailRestPwd?userId=" + data.userId, null, function (result) {
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
            		$.post(config.baseOucPath + "user/changUserState?userId=" + data.userId+"&status=0", null, function (result) {
                        if (result["returnCode"] == "0000") {
                          layer.close(index);
                       	  layer.msg("恢复用户状态成功");
                       	  refreshOutUserTable();
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
            }
        }
    );
    
    var $keywordInput = $("#out_user_search_keyword");
    $keywordInput.keydown(function (e) {
        if (e.keyCode == 13) {
            var keyWord = $keywordInput.val();
            refreshOutUserTable(keyWord);
        }
    });
    
    table.on('renderComplete(filter_out_user_datatable)', function (obj) {
        ht_auth.render("user_auth");
    });
    //监听工具栏
    $('#user_table_tools .layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });

    ht_auth.render("user_auth");
})