layui.use(['form',   'table', 'ht_config','ht_auth','upload' ], function () {
    var $ = layui.jquery
        , form = layui.form
        , table = layui.table
        , config = layui.ht_config
        , ht_auth = layui.ht_auth
        , upload = layui.upload
        , addDialog = 0 //新增弹出框的ID
        , viewDialog = 0 //查询弹出框的ID
        , editDialog = 0 //修改弹出框的ID
        , appCode=""
        , active = {
        add: function () { //弹出用户新增弹出框
            layer.close(addDialog);
            addDialog = layer.open({
                type: 1,
                area: ['600px', '500px'],
                maxmin: true,
                shadeClose: true,
                title: "新增系统",
                content: $("#app_add_app_div").html(),
                btn: ['保存', '取消'],
                yes: function (index, layero) {
                    var $submitBtn = $("button[lay-filter=filter_add_app_form]", layero);
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
                    form.render(null, "filter_add_app_form");
                    form.on('submit(filter_add_app_form)', function (data) {
                        $.ajax({
                            type: "POST",
                            url: addappUrl,
                            data: JSON.stringify(data.field),
                            contentType: "application/json; charset=utf-8",
                            success: function (message) {
                                layer.close(addDialog);
                                if (message.returnCode == '0000') {
                                	refreshAppsTable();
                                    layer.alert("系统新增成功");
                                }
                            },
                            error: function (message) {
                                layer.msg("系统新增发生异常，请联系管理员。");
                                console.error(message);
                            }
                        });
                        return false;
                    });
                }
            })
        },
        exportApp:function(){
        	   $.post(exportAppExcelUrl, null, function (result) {
              console.log(result);
            });
         },
        search: function () {
            //执行重载
        	refreshAppsTable($("#app_search_keyword").val());
        },
        searchAppUsers: function () {
            //执行重载
        	refreshAppUsersTable($("#app_appUsers_search_keyword").val());
        }
    };
    
    var appListByPageUrl=config.basePath +"apps/list"; //列出所有系统记录列表信息  
    var addappUrl=config.basePath +"apps/add"; //添加系统信息
    var delappUrl=config.basePath +"apps/delete"; //删除系统信息
    var statusappUrl=config.basePath +"apps/stop"; //禁用
    var checkAppCodeExistUrl = config.basePath +"apps/isExistAppCode"; //校验岗位编码是否已经存在
    var exportAppExcelUrl = config.basePath +"apps/exportAppExcel"; //导出
    var importAppExcelUrl = config.basePath +"apps/importAppExcel"; //导入
    
    var getUserInfoForAppUrl=config.basePath +"userapp/getUserInfoForApp"; //列出所有角色记录列表信息  
    var delAppUserUrl=config.basePath +"userapp/deleteByUserIdAndAppCode"; //删除角色用户关系信息
    
    upload.render({
		elem: '#importApp'
		,url:importAppExcelUrl
		,accept: 'file' //普通文件
		,exts: 'xls|xlsx' //只允许上传压缩文件
		,done: function(res){
			if (res.returnCode == '0000') {
            	layer.msg("系统导入成功");
            	refreshAppsTable();
            }
           
		}
	});
    
    //自定义验证规则
	form.verify({
		  //校验编码是否已经存在
		  checkExistAppCode : function(value) {
			  var isExist="1";
			  if(value){
					  $.ajax({
						url : checkAppCodeExistUrl + "?appCode=" + value,
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
				  return "新增系统编码不可用，请重新输入系统编码";
			  } 
		  },
		  
	});
	
	var refreshAppsTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
        table.reload('app_datatable', {
        	height: 'full-635',
            page: {
                curr: 1 //重新从第 1 页开始
            }
            , where: {
                keyWord: keyword
            }
        });
    };
    
    var refreshAppUsersTable = function (keyword) {
   	 if (!keyword) {
            keyword = null;
        }
   	if(appCode){
   		table.reload('app_appusers_datatable', {
           	height: 'full-605'
               , where: {
            	   query:{
            		   appCode:appCode,
            		   keyWord:keyword
            	   }
               }
           });
   	}
   };
    //渲染用户数据表格
    table.render({
        id: 'app_datatable'
        , elem: '#app_datatable'
        , url: appListByPageUrl
        , method: 'post' //如果无需自定义HTTP类型，可不加该参数
        , page: true
        , limit : 5
        , limits :[5, 10, 20, 30, 40, 50]
        , height: 'full-635'
        , cols: [[
            {type: 'numbers'}
            , {field: 'app', width: '10%', title: '系统编号', event:'getAppUsers'}
            , {field: 'nameCn',  width: '20%', title: '系统名称', event:'getAppUsers'}
            , {field: 'isOS', width: '15%', templet: '#isOSTpl', title: '系统类型', event:'getAppUsers'}
            , {field: 'status', width: '15%',templet: '#statusTpl', title: '状态', event:'getAppUsers'}
            , {field: 'createOperator', width: '10%',  title: '创建人', event:'getAppUsers'}
            , {field: 'createdDatetime', width: '10%',templet: '#createTimeTpl', title: '创建时间', event:'getAppUsers'}
            , {fixed: 'right', width: '14%', title: '操作',   toolbar: '#app_datatable_bar'}
        ]]
    });
    table.render({
        id: 'app_appusers_datatable'
        , elem: '#app_appusers_datatable'
        , url: getUserInfoForAppUrl
        , page: true
        , method: 'post'  
        , height: 'full-600'
        , cellMinWidth: 80  
        , cols: [[
            {type: 'numbers'}
            , {field: 'userId',width: '10%',   title: '用户编号'}
            , {field: 'userName',width: '15%',   title: '用户名'}
            , {field: 'orgCode', width: '10%',title: '所属机构'}
            , {field: 'mobile',width: '15%',  title: '手机'}
            , {field: 'email',  width: '20%', title: '邮箱'}
            , {field: 'jobNumber', width: '10%', title: '工号' }
            , {field: 'status', width: '8%', title: '状态', templet: "#user_status_laytpl"}
            , {fixed: 'right', width: '10%',  title: '操作',   toolbar: '#app_appuser_datatable_bar'}
        ]]
    });
    table.on('tool(filter_app_appusers_datatable)', function (obj) {
    	var data = obj.data;
    	if (obj.event === 'delAppUser') {
       	 layer.confirm('是否确认删除系统用户？', function (index) {
            	 $.post(delAppUserUrl+"?userId=" + data.userId+"&appCode="+appCode, null, function (result) {
                     if (result["returnCode"] == "0000") {
                   	    obj.del();
                   	    refreshAppUsersTable();
                         layer.close(index);
                         layer.msg("删除系统用户成功");
                     } else {
                         layer.msg(result.codeDesc);
                     }
                 });
            });
       } 
    });
    //监听操作栏
    table.on('tool(filter_app_datatable)', function (obj) {
        var data = obj.data;
        if (obj.event == 'getAppUsers') {
        	appCode= data.app;
        	refreshAppUsersTable();
        } else if (obj.event === 'stopOrStart') {
        	if(data.status==0){//启用状态，是否需要禁用
        		layer.confirm('是否禁用系统？', function (index) {
                  	 $.post(statusappUrl+"?id=" + data.id+"&status=1", null, function (result) {
                           if (result["returnCode"] == "0000") {
                               refreshAppsTable();
                               layer.close(index);
                               layer.msg("禁用系统成功");
                           } else {
                               layer.msg(result.codeDesc);
                           }
                       });
                  });
        	}else{
        		layer.confirm('是否启用系统？', function (index) {
                  	 $.post(statusappUrl+"?id=" + data.id+"&status=0", null, function (result) {
                           if (result["returnCode"] == "0000") {
                               refreshAppsTable();
                               layer.close(index);
                               layer.msg("启用系统成功");
                           } else {
                               layer.msg(result.codeDesc);
                           }
                       });
                  });
        	}
        } else if (obj.event === 'del') {
        	 layer.confirm('是否确认删除系统？', function (index) {
             	 $.post(delappUrl+"?id=" + data.id, null, function (result) {
                      if (result["returnCode"] == "0000") {
                    	  obj.del();
                          refreshAppsTable();
                          layer.close(index);
                          layer.msg("删除系统成功");
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
                 content: $("#app_modify_data_div").html(),
                 btn: ['保存', '取消'],
                 yes: function (index, layero) {
                     var $submitBtn = $("button[lay-filter=filter_modify_app_form]", layero);
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
                    
                    form.render(null, "filter_modify_app_form");
                    form.on('submit(filter_modify_app_form)', function (data) {
                        $.ajax({
                            type: "POST",
                            url: addappUrl,
                            data: JSON.stringify(data.field),
                            contentType: "application/json; charset=utf-8",
                            success: function (result2) {
                                layer.close(editDialog);
                                if (result2["returnCode"] == '0000') {
                                    refreshAppsTable();
                                    layer.alert("系统修改成功");
                                }
                            },
                            error: function (message) {
                                layer.msg("系统修改发生异常，请联系管理员。");
                                console.error(message);
                            }
                        });
                        return false;
                    });
                }
            });
        }
    });
    table.on('renderComplete(filter_app_datatable)', function (obj) {
    	ht_auth.render("app_auth");
    });
    //监听工具栏
    $('#app_table_tools .layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    $('#app_appusers_table_tools .layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    
    var $keywordAppInput = $("#app_search_keyword");
    $keywordAppInput.keydown(function (e) {
        if (e.keyCode == 13) {
            var keyWord = $keywordAppInput.val();
            refreshAppsTable(keyWord);
        }
    });
    
    var $keywordAppUserInput = $("#app_appUsers_search_keyword");
    $keywordAppUserInput.keydown(function (e) {
        if (e.keyCode == 13) {
            var keyWord = $keywordAppUserInput.val();
            refreshAppUsersTable(keyWord);
        }
    });
    
    ht_auth.render("app_auth");

})