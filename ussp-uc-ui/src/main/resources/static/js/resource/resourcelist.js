

layui.use(['form', 'laytpl' , 'ztree','table','ht_config', 'ht_auth' ], function () {

    var $ = layui.jquery
        , form = layui.form
        , table = layui.table
        , config = layui.ht_config
        ,laytpl = layui.laytpl
        , ht_auth = layui.ht_auth
        , upload = layui.upload
        , addDialog = 0 //新增弹出框的ID
        , viewDialog = 0 //查询弹出框的ID
        , editDialog = 0 //修改弹出框的ID
        , appResListsTree //组织机构树控件
        ,resListsCode
        , active = {
         search: function () {
             //执行重载
         	refreshResListsTable($("#resLists_search_keyword").val());
         },
        
    };
    
    var resListsListByPageUrl=config.basePath +"resource/listByPage"; //列出所有记录列表信息  
    var delresListsUrl=config.basePath +"resource/deleteTrunc"; //删除信息
    var getResByResCodeUrl=config.basePath +"resource/getResByResCode"; //
    
    var appListByPageUrl=config.basePath +"userapp/listAppByPage"; //列出所有角色记录列表信息
    
    //自定义验证规则
	form.verify({
		  //校验编码是否已经存在
		checkResCodeIsExist : function(value) {
			  var isExist="1";
			  if(value){
					  $.ajax({
						url : getResByResCodeUrl + "?resCode=" + value,
						type : 'POST',
						async : false,
						success : function(result) {
							var data = result.data;
						    if(!data[0]["resCode"]){
						    	isExist = "0";
						    }  
						}
					});
			  }
			  if(isExist=="1"){
				  return "资源编码存在或不可用";
			  } 
		  },
		  
	});
	
    var refreshResListsTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
        
        var selectNodes = appResListsTree.getSelectedNodes();
        if (selectNodes && selectNodes.length == 1) {
        	table.reload('resLists_datatable', {
        		height: 'full-200'
                , page: {
                    curr: 1 //重新从第 1 页开始
                }
                , where: {
                    query: {
                    	app: selectNodes[0]["app"],
                    	keyWord: keyword,
                    }
                }
            });
        }else{
        	table.reload('resLists_datatable', {
        		height: 'full-200'
                , page: {
                    curr: 1 //重新从第 1 页开始
                }
                , where: {
                	 query: {
                     	keyWord: keyword,
                     }
                }
            });
        }
    };
    
  //渲染组织机构树
    appResListsTree = $.fn.zTree.init($('#resLists_app_ztree_left'), {
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
                otherParam: ["page", "1", "limit", "100"],
                url: appListByPageUrl,
                dataFilter: function (treeId, parentNode, childNodes) {
                    if (!childNodes) return null;
                    var appData = childNodes.data;
                	for (var i=0, l=appData.length; i<l; i++) {
                		appData[i].name = appData[i].nameCn;
                		appData[i].id = appData[i].app;
                		appData[i].name = appData[i].name.replace(/\.n/g, '.');
                	}
                	
                	return appData;
                }
            }
            , callback: {
            	onClick: function (event, treeId, treeNode, clickFlag) {
            		refreshResListsTable();
                },
                onAsyncSuccess: function (event, treeId, treeNode, msgString) {
                    var node = appResListsTree.getNodeByParam("level ", "0");
                    if (node) {
                        appResListsTree.selectNode(node);
                    }
                }
            },
            data: {
                simpleData: {
                    enable: true
                }
            }
        }
    );
    //渲染用户数据表格
    table.render({
        id: 'resLists_datatable'
        , elem: '#resLists_datatable'
        , url: resListsListByPageUrl
        , method: 'post' //如果无需自定义HTTP类型，可不加该参数
        , page: true
        , height: 'full-200'
        , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
        , cols: [[
            {type: 'numbers'}
            , {field: 'resCode',   title: '资源编码'}
            , {field: 'resNameCn',   title: '资源名称'}
            , {field: 'resType',    title: '资源类型'}
            , {field: 'app',    title: '所属系统'}
            , {field: 'status',    title: '状态'}
            , {field: 'createOperator',   title: '创建人'}
            , {field: 'createdDatetime',  templet: '#createTimeTpl', title: '创建时间'}
            , {fixed: 'right',   title: '操作',   toolbar: '#resLists_datatable_bar'}
        ]]
    });
    //监听操作栏
    table.on('tool(filter_resLists_datatable)', function (obj) {
        var data = obj.data;
        if (obj.event === 'del') {
        	 layer.confirm('是否确认删除资源？', function (index) {
             	 $.post(delresListsUrl+"?id=" + data.id, null, function (result) {
                      if (result["returnCode"] == "0000") {
                    	  obj.del();
                          refreshResListsTable();
                          layer.close(index);
                          layer.msg("删除资源成功");
                      } else {
                          layer.msg(result.codeDesc);
                      }
                  });
             });
        }  
    });
    
    $('#resLists_table_tools .layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    
    
    table.on('renderComplete(filter_resLists_datatable)', function (obj) {
        ht_auth.render("resLists_auth");
    });
    
 
    var $keywordInputresLists = $("#resLists_search_keyword");
    $keywordInputresLists.keydown(function (e) {
        if (e.keyCode == 13) {
            var keyWord = $keywordInputresLists.val();
            refreshResListsTable(keyWord);
        }
    });
    //刷新树的数据
    $('#resLists_app_btn_refresh_tree').on('click', function (e) {
        if (appResListsTree) {
            appResListsTree.reAsyncChildNodes(null, "refresh");
        }
    });
    var nodeList = [];
    //搜索树的数据
    $('#resLists_app_search_tree').bind('input', function (e) {
        if (appResListsTree && $(this).val() != "") {
            nodeList = appResListsTree.getNodesByParamFuzzy("name", $(this).val());
            updateNodes(true);
        } else {
            updateNodes(false);
        }
    });

    //刷新树节点
    function updateNodes(highlight) {
        for (var i = 0, l = nodeList.length; i < l; i++) {
            nodeList[i].highlight = highlight;
            appResListsTree.updateNode(nodeList[i]);
            if (highlight) {
                appResListsTree.expandNode(appResListsTree.getNodeByParam("app", nodeList[i]["parentOrgCode"]), true, false, null, null);
            }
        }
    }
    
    ht_auth.render("resLists_auth");

})