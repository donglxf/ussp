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
                                   /* table.reload('app_datatable', {
                                        page: {
                                            curr: 1 //重新从第 1 页开始
                                        }
                                    });*/
                                	refreshTable();
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
        	refreshTable($("#app_search_keyword").val());
        }
    };
    
    var appListByPageUrl=config.basePath +"apps/list"; //列出所有系统记录列表信息  
    var addappUrl=config.basePath +"apps/add"; //添加系统信息
    var delappUrl=config.basePath +"apps/delete"; //删除系统信息
    var statusappUrl=config.basePath +"apps/stop"; //禁用
    var checkAppCodeExistUrl = config.basePath +"apps/isExistAppCode"; //校验岗位编码是否已经存在
    var exportAppExcelUrl = config.basePath +"apps/exportAppExcel"; //导出
    var importAppExcelUrl = config.basePath +"apps/importAppExcel"; //导入
    
    upload.render({
		elem: '#importApp'
		,url:importAppExcelUrl
		,accept: 'file' //普通文件
		,exts: 'xls|xlsx' //只允许上传压缩文件
		,done: function(res){
			if (res.returnCode == '0000') {
            	layer.msg("系统导入成功");
            	refreshTable();
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
	
    var refreshTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
        table.reload('app_datatable', {
            page: {
                curr: 1 //重新从第 1 页开始
            }
            , where: {
                keyWord: keyword
            }
        });
    };
    //渲染用户数据表格
    table.render({
        id: 'app_datatable'
        , elem: '#app_datatable'
        , url: appListByPageUrl
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
            , {field: 'app', width: 150, title: '系统编号'}
            , {field: 'nameCn', width: 400,   title: '系统名称'}
            , {field: 'status', width: 100,templet: '#statusTpl', title: '状态'}
            , {field: 'createOperator', width: 350,   title: '创建人'}
            , {field: 'createdDatetime', width: 300,templet: '#createTimeTpl', title: '创建时间'}
            , {fixed: 'right', width: 330,  title: '操作',   toolbar: '#app_datatable_bar'}
        ]]
    });
    //监听操作栏
    table.on('tool(filter_app_datatable)', function (obj) {
        var data = obj.data;
        if (obj.event === 'stopOrStart') {
        	if(data.status==0){//启用状态，是否需要禁用
        		layer.confirm('是否禁用系统？', function (index) {
                  	 $.post(statusappUrl+"?id=" + data.id+"&status=1", null, function (result) {
                           if (result["returnCode"] == "0000") {
                               refreshTable();
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
                               refreshTable();
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
                          refreshTable();
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
                                    refreshTable();
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
    var $keywordInput = $("#app_search_keyword");
    $keywordInput.keydown(function (e) {
        if (e.keyCode == 13) {
            var keyWord = $keywordInput.val();
            refreshTable(keyWord);
        }
    });
    
    ht_auth.render("app_auth");

})