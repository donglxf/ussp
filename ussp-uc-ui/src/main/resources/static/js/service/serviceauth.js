layui.use(['form', 'ztree', 'table','ht_config', 'ht_auth'], function () {
    var $ = layui.jquery
        , form = layui.form
        , table = layui.table
        , config = layui.ht_config
        , ht_auth = layui.ht_auth
        , addDialog = 0 //新增弹出框的ID
        , viewDialog = 0 //查询弹出框的ID
        , editDialog = 0 //修改弹出框的ID
        , serviceAuthAppTree //组织机构树控件
        ,authServiceCode=""
        ,callApplicationService=""
        , active = {
    		addService: function () { //弹出用户新增弹出框
            	 var nodes = serviceAuthAppTree.getSelectedNodes();
                 if (nodes.length == 0) {
                     layer.alert("请先选择主微服务");
                     return false;
                 }else{
                	 if(nodes[0]["parentCode"]==null){
                		 layer.alert("请先选择主微服务");
                         return false;
                	 }
                 }
                layer.close(addDialog);
                addDialog =  layer.open({
                    type: 1,
                    area: ['1200px', '645px'],
                    shadeClose: true,
                    title: "选择授权微服务",
                    content: $("#serviceauth_service_data_div").html(),
                    btn: ['确认', '取消'],
                    yes: function (index, layero) {
                        var serviceCheckStatus = table.checkStatus('serviceauth_service_dalog_datatable');
                        $.ajax({
                            type: "POST",
                            url: addCalledServiceBatchUrl,
                            data: JSON.stringify({
                            	mainServiceCode: nodes[0]["serviceCode"],
                            	serviceList: serviceCheckStatus.data
                            }), 
                            contentType: "application/json; charset=utf-8",
                            success: function (result) {
                                layer.close(index);
                                if (result["returnCode"] == '0000') {
                                	refreshServiceServiceTable();
                                	refreshServiceApiTable();
                                    layer.alert("授权微服务成功");
                                }
                            },
                            error: function (result) {
                                layer.msg("关联API发生异常，请联系管理员。");
                                console.error(result);
                            }
                        })
                    },
                    btn2: function () {
                        layer.closeAll('tips');
                    },
                    success: function (layero, index) {
                        $("input[name=applicationService]", layero).val(nodes[0]["applicationService"]);
                        $("input[name=applicationServiceName]", layero).val(nodes[0]["applicationServiceName"]);
                        table.render({
                            id: 'serviceauth_service_dalog_datatable'
                            , elem: $('#serviceauth_service_dalog_datatable', layero)
                            , url: listServiceByPageUrl
                            , page: true
                            , height: "471"
                            , cols: [[
                                {type: 'numbers'}
                                , {type: 'checkbox'}
                                , {field: 'app', width: 200, title: '所属系统'}
                                , {field: 'applicationService', width: 300, title: '微服务'}
                                , {field: 'applicationServiceName', width: 200, title: '微服务名称'}
                                , {field: 'status', title: '状态'}
                            ]]
                        });
                        var $keywordInput = $("#serviceauth_service_dialog_search_keyword", layero);
                        $('#searchMainService', layero).on('click', function () {
                            var keyWord = $keywordInput.val();
                            refreshServiceAuthDalogTable($keywordInput.val());
                        });
                        $keywordInput.keydown(function (e) {
                            if (e.keyCode == 13) {
                                var keyWord = $keywordInput.val();
                                refreshServiceAuthDalogTable($keywordInput.val());
                            }
                        });
                    }
                });
            },
    		search: function () { 
            	//执行重载
    			refreshServiceServiceTable($("#servcieauth_service_search_keyword").val());
            },
            searchservcieauth: function () { 
            	//执行重载
            	refreshServiceApiTable($("#servcieauth_api_search_keyword").val());
            },
            chooiceAddServiceApi: function () { //弹出用户新增弹出框
           	 var nodes = serviceAuthAppTree.getSelectedNodes();
	           	if(authServiceCode==""){
	        		layer.msg("请先选择已授权的微服务！");
	        		return;
	        	}
               layer.close(addDialog);
               addDialog =  layer.open({
                   type: 1,
                   area: ['1200px', '645px'],
                   shadeClose: true,
                   title: "选择授权api",
                   content: $("#serviceauth_service_api_data_div").html(),
                   btn: ['确认', '取消'],
                   yes: function (index, layero) {
                       var serviceApiCheckStatus = table.checkStatus('serviceauth_service_api_dalog_datatable');
                       console.log(serviceApiCheckStatus);
                       $.ajax({
                           type: "POST",
                           url: addCalledServiceApiBatchUrl,
                           data: JSON.stringify({
                        	 authServiceCode: authServiceCode,
                        	 recourceApiList: serviceApiCheckStatus.data
                           }), 
                           contentType: "application/json; charset=utf-8",
                           success: function (result) {
                               layer.close(index);
                               if (result["returnCode"] == '0000') {
                               	refreshServiceServiceTable();
                               	refreshServiceApiTable();
                                layer.alert("授权微服务成功");
                               }
                           },
                           error: function (result) {
                               layer.msg("关联API发生异常，请联系管理员。");
                               console.error(result);
                           }
                       })
                   },
                   btn2: function () {
                       layer.closeAll('tips');
                   },
                   success: function (layero, index) {
                       $("input[name=applicationService]", layero).val(nodes[0]["applicationService"]);
                       $("input[name=applicationServiceName]", layero).val(nodes[0]["applicationServiceName"]);
                       table.render({
                           id: 'serviceauth_service_api_dalog_datatable'
                           , elem: $('#serviceauth_service_api_dalog_datatable', layero)
                           , url: getAppApiUrl+"?serviceCode="+nodes[0]["serviceCode"]
                           , page: true
                           , where: {
                               resType: "api"
                           }
                           , height: "471"
                           , cols: [[
                               {type: 'numbers'}
                               , {type: 'checkbox'}
                               , {field: 'resContent', width: 300, title: 'API'}
                               , {field: 'resNameCn', width: 200, title: 'API名称'}
                               , {field: 'remark', title: '方法名'}
                           ]]
                       });
                       var $keywordInputApi = $("#serviceauth_service_api_dialog_search_keyword", layero);
                       $('#searchMainServiceApi', layero).on('click', function () {
                           var keyWord = $keywordInputApi.val();
                           refreshServiceApiAuthDalogTable($keywordInputApi.val());
                       });
                       $keywordInputApi.keydown(function (e) {
                           if (e.keyCode == 13) {
                               var keyWord = $keywordInputApi.val();
                               refreshServiceApiAuthDalogTable($keywordInputApi.val());
                           }
                       });
                   }
               });
           },
            addServiceApi:function(){
            	if(authServiceCode==""){
            		layer.msg("请先选择已授权的微服务！");
            		return;
            	}
            	 layer.close(addDialog);
            	 addDialog = layer.open({
                     type: 1,
                     area: ['600px', '500px'],
                     maxmin: true,
                     shadeClose: true,
                     title: "指定微服务API",
                     content: $("#serviceauth_api_add_data_div").html(),
                     btn: ['保存', '取消'],
                     yes: function (index, layero) {
                         var $submitBtn = $("button[lay-filter=filter_add_serviceauth_api_form]", layero);
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
                    	 $("#callApplicationService", layero).val(callApplicationService);
                    	 $("#authServiceCode", layero).val(authServiceCode);
                    	 
                         //填充选中的组织机构
                         form.render(null, "filter_add_serviceauth_api_form");
                         form.on('submit(filter_add_serviceauth_api_form)', function (data) {
                             $.ajax({
                                 type: "POST",
                                 url: addServiceAPIUrl,
                                 data: JSON.stringify(data.field),
                                 contentType: "application/json; charset=utf-8",
                                 success: function (message) {
                                     layer.close(addDialog);
                                     if (message.returnCode == '0000') {
                                         table.reload('servcieauth_api_datatable', {
                                             page: {
                                                 curr: 1 //重新从第 1 页开始
                                             }
                                         });
                                         layer.alert("指定api成功");
                                     }
                                 },
                                 error: function (message) {
                                     layer.msg("指定api发生异常，请联系管理员。");
                                     console.error(message);
                                 }
                             });
                             return false;
                         });
                     }
                 })
            },
            /*searchMainService: function () { 
            	//执行重载
            	refreshServiceApiTable($("#servcieauth_api_search_keyword").val());
            },*/
            
    };
    var listCalledServiceUrl=config.basePath +"authservice/listCalledService"; //列出对应授权的微服务
    var addCalledServiceBatchUrl=config.basePath + 'authservice/addCalledServiceBatch'; //添加微服务间的授权管理
    var listServiceByPageUrl=config.basePath + 'minservice/getServiceListByPage'; //所有微服务列表
    var listServiceAPIUrl=config.basePath +"authservice/listServiceAPI"; //列出对应授权的微服务
    var changeServiceSatusUrl=config.basePath + 'authservice/stop'; //0开启所有 1开启指定api 2禁用
    var appListByPageUrl=config.basePath +"authservice/app/getAppServiceTree"; //系统列表
    var addServiceAPIUrl=config.basePath + 'authservice/addServiceAPI'; //指定微服务api
    var addCalledServiceApiBatchUrl=config.basePath + 'authservice/addCalledServiceApiBatch'; //添加微服务间的授权管理
    var stopServiceApiUrl=config.basePath + 'authservice/stopServiceApi'; //指定微服务api
    var delServiceApiUrl=config.basePath + 'authservice/delServiceApi'; //指定微服务api
    
    var getAppApiUrl=config.basePath + 'authservice/getAppApi'; //获取系统的api
    
    var refreshServiceServiceTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
        var selectNodes = serviceAuthAppTree.getSelectedNodes();
        if (selectNodes && selectNodes.length == 1) {
        	 table.reload('servcieauth_service_datatable', {
                 page: {
                     curr: 1 //重新从第 1 页开始
                 }
                 , where: {
                     query: {
               		     keyWord: keyword,
               		     serviceCode: selectNodes[0]["serviceCode"]
                    }
                 }
             });
        }
    };
    var refreshServiceApiTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
        table.reload('servcieauth_api_datatable', {
  		  height: 'full-600',
  	        page: {
  	            curr: 1 //重新从第 1 页开始
  	        }
  	        , where: {
  	        	query: {
            		  keyWord: keyword,
            		  authServiceCode: authServiceCode,
                 }
  	        }
  	   });
    };
    var refreshServiceAuthDalogTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
        table.reload('serviceauth_service_dalog_datatable', {
  		  height: 'full-600',
  	        page: {
  	            curr: 1 //重新从第 1 页开始
  	        }
  	        , where: {
  	        	query: {
            		  keyWord: keyword,
                 }
  	        }
  	   });
    };
    
    var refreshServiceApiAuthDalogTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
        table.reload('serviceauth_service_api_dalog_datatable', {
  	        page: {
  	            curr: 1 //重新从第 1 页开始
  	        }
  	        , where: {
  	        	query: {
            		  keyWord: keyword,
            		  resType: "api"
                 }
  	        }
  	   });
    };
    
    
    //渲染组织机构树
    serviceAuthAppTree = $.fn.zTree.init($('#servcieauth_org_ztree_left'), { 
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
            otherParam: ["page", "1", "limit", "1000"],
            url: appListByPageUrl,
            dataFilter: function (treeId, parentNode, childNodes) {
                if (!childNodes) return null;
                var appData = childNodes;
            	for (var i=0, l=appData.length; i<l; i++) {
            		/*appData[i].name = appData[i].mainService;
            		appData[i].id = appData[i].app;
            		appData[i].name = appData[i].name.replace(/\.n/g, '.');*/
            		if(childNodes[i]["applicationService"]){
            			childNodes[i].name = childNodes[i]["applicationService"];
            		}
            	}
            	return appData;
            }
        }
        , callback: {
        	onClick: function (event, treeId, treeNode, clickFlag) {
        		authServiceCode = "";
        		refreshServiceServiceTable();
            },
            onAsyncSuccess: function (event, treeId, treeNode, msgString) {
                var node = serviceAuthAppTree.getNodeByParam("level ", "0");
                if (node) {
                	serviceAuthAppTree.selectNode(node);
                }
            }
        },
        data: {
            simpleData: {
                enable: true
                , idKey: "serviceCode"
                , pIdKey: "parentCode"
            }
        }
    }
    );
    //渲染岗位数据表格
    table.render({
        id: 'servcieauth_service_datatable'
        , elem: '#servcieauth_service_datatable'
        , url: listCalledServiceUrl
        , method: 'post' //如果无需自定义HTTP类型，可不加该参数
        , page: true
        , height: '310'
        , cols: [[
            {type: 'numbers'}
            //, {field: 'applicationService',  title: '主微服务'}
           // , {field: 'callServiceCode',   title: '授权微服务'}
            , {field: 'callApp',  title: '所属系统',event:'getServiceApiData'}
            , {field: 'callApplicationService',  title: '授权微服务',event:'getServiceApiData'}
            , {field: 'callApplicationServiceName',  title: '授权微服务名称',event:'getServiceApiData'}
            , {field: 'status',  templet: '#servcieauth_service_statusTpl', title: '状态',event:'getServiceApiData'}
            , {field: 'createOperator',   title: '创建人',event:'getServiceApiData'}
            , {field: 'createdDatetime',   title: '创建时间',templet: '#createTimeTpl',event:'getServiceApiData'}
            , {fixed: 'right',   title: '操作',   toolbar: '#servcieauth_service_datatable_bar'}
        ]]
    });
    table.render({
        id: 'servcieauth_api_datatable'
        , elem: '#servcieauth_api_datatable'
        , url: listServiceAPIUrl
        , method: 'post' //如果无需自定义HTTP类型，可不加该参数
        , page: true
        , height: 'full-600'
        , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
        , cols: [[
            {type: 'numbers'}
            //, {field: 'authServiceCode', width: 120, title: '授权code'}
            , {field: 'apiContent',   title: '微服务接口'}
            , {field: 'apiDesc',   title: '接口描述'}
            , {field: 'app', width: 200, title: '授权微服务'}
            , {field: 'status', width: 100,templet: '#servcieauth_api_statusTpl' ,title: '状态'}
            , {field: 'createOperator', width: 100, title: '创建人'}
            , {field: 'createdDatetime', width: 200, title: '创建时间',templet: '#createTimeTpl'}
            , {fixed: 'right',   title: '操作',   toolbar: '#servcieauth_api_datatable_bar'}
        ]]
    });
    //监听操作栏
    table.on('tool(filter_servcieauth_service_datatable)', function (obj) {
        var data = obj.data;
        if (obj.event === 'getServiceApiData') {
        	authServiceCode= data.authServiceCode;
        	callApplicationService = data.callApplicationService;
        	refreshServiceApiTable();
        }else if(obj.event=="startService"){ //启用所有接口
        	layer.confirm('是否对该微服务开放所有接口功能？', function (index) {
             	 $.post(changeServiceSatusUrl+"?id=" + data.id+"&status=0", null, function (result) {
                      if (result["returnCode"] == "0000") {
                    	  refreshServiceServiceTable();
                          layer.close(index);
                          layer.msg("启用成功");
                      } else {
                          layer.msg(result.codeDesc);
                      }
                  });
             });
        }else if(obj.event=="stopService"){ //禁用所有接口
        	layer.confirm('是否对该微服务关闭所有接口功能？', function (index) {
             	 $.post(changeServiceSatusUrl+"?id=" + data.id+"&status=2", null, function (result) {
                      if (result["returnCode"] == "0000") {
                    	  refreshServiceServiceTable();
                          layer.close(index);
                          layer.msg("禁用成功");
                      } else {
                          layer.msg(result.codeDesc);
                      }
                  });
             });
        }else if(obj.event=="startServiceApi"){//启用指定api
        	layer.confirm('是否对该微服务开放指定接口功能？', function (index) {
             	 $.post(changeServiceSatusUrl+"?id=" + data.id+"&status=1", null, function (result) {
                      if (result["returnCode"] == "0000") {
                    	  refreshServiceServiceTable();
                          layer.close(index);
                          layer.msg("启用成功");
                      } else {
                          layer.msg(result.codeDesc);
                      }
                  });
             });
        }
    });
    table.on('tool(filter_servcieauth_api_datatable)', function (obj) {
        var data = obj.data;
        if (obj.event === 'stopServiceApi') {
        	layer.confirm('是否禁用api功能？', function (index) {
             	 $.post(stopServiceApiUrl+"?id=" + data.id+"&status=1", null, function (result) {
                      if (result["returnCode"] == "0000") {
                   	   refreshServiceApiTable();
                          layer.close(index);
                          layer.msg("禁用api功能成功");
                      } else {
                          layer.msg(result.codeDesc);
                      }
                  });
             });
        } else if (obj.event === 'startServiceApi') {
        	 layer.confirm('是否启用api功能？', function (index) {
        		 $.post(stopServiceApiUrl+"?id=" + data.id+"&status=0", null, function (result) {
                      if (result["returnCode"] == "0000") {
                    	  refreshServiceApiTable();
                          layer.close(index);
                          layer.msg("启用api功能成功");
                      } else {
                          layer.msg(result.codeDesc);
                      }
                  });
             });
        } else if (obj.event === 'delServiceApi') {
        	if(data.status=="2"){
        		layer.confirm('是否确认彻底删除api？', function (index) {
                	 $.post(delServiceApiUrl+"?id=" + data.id, null, function (result) {
                         if (result["returnCode"] == "0000") {
                       	  obj.del();
                       	  refreshServiceApiTable();
                             layer.close(index);
                             layer.msg("删除api成功");
                         } else {
                             layer.msg(result.codeDesc);
                         }
                     });
                 });
        	}else{
        		layer.confirm('是否确认删除api？', function (index) {
        			 $.post(stopServiceApiUrl+"?id=" + data.id+"&status=2", null, function (result) {
                         if (result["returnCode"] == "0000") {
                       	  refreshServiceApiTable();
                             layer.close(index);
                             layer.msg("删除api功能成功");
                         } else {
                             layer.msg(result.codeDesc);
                         }
                     });
                });
        	}
       	  
        } 
    });
    
    table.on('renderComplete(filter_servcieauth_service_datatable)', function (obj) {
    	ht_auth.render("servcieauth_auth");
    });
    table.on('renderComplete(filter_servcieauth_api_datatable)', function (obj) {
    	ht_auth.render("servcieauth_auth");
    });
    
    var $keywordServiceInput = $("#servcieauth_service_search_keyword");
    $keywordServiceInput.keydown(function (e) {
        if (e.keyCode == 13) {
            var keyWord = $keywordServiceInput.val();
            refreshServiceServiceTable(keyWord);
        }
    });
    
    var $keywordServiceApiInput = $("#servcieauth_api_search_keyword");
    $keywordServiceApiInput.keydown(function (e) {
        if (e.keyCode == 13) {
            var keyWord = $keywordServiceApiInput.val();
            refreshServiceApiTable(keyWord);
        }
    });
    
    //监听工具栏
    $('#servcieauth_service_table_tools .layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    
    $('#servcieauth_api_table_tools .layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    
    $('#servcieauth_btn_tree .btn').on('click', function () {
        var type = $(this).data('type');
        switch (type) {
            case "refresh":
                if (serviceAuthAppTree) {
                	serviceAuthAppTree.reAsyncChildNodes(null, "refresh");
                }
                break;
            case "expandAll":
                if (serviceAuthAppTree) {
                	serviceAuthAppTree.expandAll(true);
                }
                break;
            case "collapseAll":
                if (serviceAuthAppTree) {
                	serviceAuthAppTree.expandAll(false);
                }
                break;
        }
    });
    
    var nodeList = [];
   
    //搜索树的数据
    $('#servcieauth_search_tree_org').bind('input', function (e) {
        if (serviceAuthAppTree && $(this).val() != "") {
            nodeList = serviceAuthAppTree.getNodesByParamFuzzy("name", $(this).val());
            updateNodes(true);
        } else {
            updateNodes(false);
        }
    });

    //刷新树节点
    function updateNodes(highlight) {
        for (var i = 0, l = nodeList.length; i < l; i++) {
            nodeList[i].highlight = highlight;
            serviceAuthAppTree.updateNode(nodeList[i]);
            if (highlight) {
                serviceAuthAppTree.expandNode(serviceAuthAppTree.getNodeByParam("orgCode", nodeList[i]["parentOrgCode"]), true, false, null, null);
            }
        }
    }
    ht_auth.render("servcieauth_auth");
})