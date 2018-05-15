layui.use(['form', 'laytpl', 'table', 'ht_config', 'ht_auth' ], function () {
    var $ = layui.jquery
        , laytpl = layui.laytpl
        , table = layui.table
        , config = layui.ht_config
        , ht_auth = layui.ht_auth
        , selectTab = 0
        , selectBmUserData
        , active = {
    		syndatadd: function () {
	    		 $.post(getDDUrl, null, function (result) {
	    	            console.log(result);
	    	      });
	        },
	        searchContrast: function () {
	        	renderSynDataTable("user_all",$("#syndata_user_search_keyword").val());
	        },
	        searchBM: function () {
	        	renderSynDataTable("user_bm",$("#syndata_bm_user_search_keyword").val());
	        },
	        
    }; 

    var getDDUrl = config.basePath +"syndata/user/getDownDD"; //导入
    var queryAllUserContrastUrl = config.basePath +"syndata/queryAllUserContrast"; //导入
    var queryBmUserUrl = config.basePath +"syndata/queryBmUser"; //导入
    var removeBmUserUrl = config.basePath +"syndata/removeBmUser"; //解除关联
    var addBmUserUrl = config.basePath +"syndata/addBmUser"; //添加关联
    
     /**
      * 
      */
    function renderSynDataTable(type,keyword) {
    	 if (!keyword) {
             keyword = null;
         }
        var clos = [[]], height = 'full', page = false, limit = 999, limits = [],id,elem,url,userDataUrl,
            initSort;
        switch (type) {
            case 'user_all':
            	id = 'syndata_user_datatable';
            	elem='#syndata_user_datatable';
            	userDataUrl= queryAllUserContrastUrl;
                height = '276';
                clos = [[
                    {type: 'numbers',event: 'rowClick'}
                    , {field: 'userId',   title: '用户编号',event: 'rowClick'}
                    , {field: 'userName',     title: '用户姓名',event: 'rowClick'}
                    //, {field: 'jobNumber',  title: '工号',event: 'rowClick'}
                    , {field: 'mobile',   title: '手机',event: 'rowClick'}
                    , {field: 'email',   title: '邮箱'}
                    , {field: 'orgName',   title: '所属机构',event: 'rowClick'}
                    , {field: 'bmUserId',   title: '信贷用户',event: 'rowClick'}
                    , {field: 'bmUserName',   title: '信贷用户姓名',event: 'rowClick'}
                    //, {field: 'bmJobNumber',   title: '工号',event: 'rowClick'}
                    , {field: 'bmMobile',   title: '手机',event: 'rowClick'}
                    , {field: 'bmEmail',   title: '邮箱',event: 'rowClick'}
                    , {field: 'bmOrgName',   title: '所属机构',event: 'rowClick'}
                    , {fixed: 'right',  title: '操作', align: 'center', toolbar: '#syndata_user_datatable_bar'}
                ]];
                break;
            case 'user_bm':
            	id = 'syndata_bm_user_datatable';
            	elem='#syndata_bm_user_datatable';
            	userDataUrl= queryBmUserUrl;
            	 height = 'full-623';
                clos = [[
                    {type: 'radio',width: 50}
                    , {field: 'bmUserId',  title: '用户编号'}
                    , {field: 'bmUserName',  title: '信贷用户姓名'}
                    , {field: 'bmMobile',   title: '手机'  }
                    , {field: 'bmEmail',   title: '邮箱' }
                    , {field: 'bmJobNumber',   title: '工号' }
                    , {field: 'bmOrgName', 　title: '所属机构' }
                    , {field: 'userId',   title: '已关联用户编号',event: 'rowClick' }
                ]];
                break;
        }
       
        table.render({
            id: id
            , elem: elem
            , where: {
                keyWord: keyword
            }
            , limit : 5
            , limits :[5, 10, 20, 30, 40, 50]
            , url: userDataUrl
            , page: true
            , height: height
            , cols: clos
            ,done : function(res, curr, count) {
            	if("user_all"==type){
            		$("[data-field='orgName']").css('border-right','solid 2px blue');
            	}else if("user_bm"==type){
            		selectBmUserData = "";
            	}
    		}
        });
    }
    
    //渲染用户数据表格
    renderSynDataTable("user_all");
    renderSynDataTable("user_bm");
  //监听工具栏
    $('#syndata_content .layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
  //监听操作栏
    table.on('tool(syndata_user_datatable)', function (obj) {
        var data = obj.data;
        if (obj.event === 'rowClick') {
        	renderSynDataTable("user_bm",data.bmUserId);
        } else if (obj.event === 'delete') {
	       	 layer.confirm('是否确认解除信贷用户？', function (index) {
	         	 $.post(removeBmUserUrl+"?id=" + data.id, null, function (result) {
	                  if (result["returnCode"] == "0000") {
	                	  layer.close(index);
                          layer.msg("解除成功");
	                	  renderSynDataTable("user_all",$("#syndata_user_search_keyword").val());
           	        	  renderSynDataTable("user_bm",$("#syndata_bm_user_search_keyword").val());
	                  } else {
	                      layer.msg(result.codeDesc);
	                  }
	              });
	         });
	    } else if (obj.event === 'add') {
	    	 // var radioStatus = table.radioStatus('syndata_bm_user_datatable');
	    	 // layer.alert(JSON.stringify(radioStatus));
	    	if(selectBmUserData){
	    		if(!selectBmUserData.bmUserId){
	    			layer.alert("请选择需要关联的信贷用户");
	    			return;
	    		}
	    		if(selectBmUserData.userId){
	    			layer.alert("信贷用户已经被关联，请重新选择信贷用户");
	    			return;
	    		}else{
	    			 $.ajax({
                         type: "POST",
                         url: addBmUserUrl,
                         data: {
                        	 uc_userId: data.userId,
                        	 bm_userId:selectBmUserData.bmUserId
                         },
                         dataType: "json",
                         success: function (result) {
                             if (result.returnCode == '0000') {
                            	 layer.msg("关联成功");
                            	 renderSynDataTable("user_all",$("#syndata_user_search_keyword").val());
                  	        	 renderSynDataTable("user_bm",$("#syndata_bm_user_search_keyword").val());
                             }
                         },
                         error: function (message) {
                             layer.msg("关联发生异常，请联系管理员");
                             console.error(message);
                         }
                     });
	    		}
	    	}else{
	    		layer.alert("请选择需要关联的信贷用户");
	    	}
	    	
	    } 
    });
    table.on('tool(syndata_bm_user_datatable)', function (obj) {
        var data = obj.data;
        if (obj.event === 'rowClick') {
	        if (data.userId!=''&&data.userId!=null) {
	        	renderSynDataTable("user_all",data.userId);
	        }/*else{
	        	renderSynDataTable("user_all",$("#syndata_user_search_keyword").val());
	        } */
        }
    });
    var $keywordInput = $("#syndata_user_search_keyword");
    $keywordInput.keydown(function (e) {
        if (e.keyCode == 13) {
            var keyWord = $keywordInput.val();
            renderSynDataTable("user_all",keyWord);
        }
    });
    
    var $keywordInput = $("#syndata_bm_user_search_keyword");
    $keywordInput.keydown(function (e) {
        if (e.keyCode == 13) {
            var keyWord = $keywordInput.val();
            renderSynDataTable("user_bm",keyWord);
        }
    });
    
    table.on('radio(syndata_bm_user_datatable)', function(obj){
        //console.log(obj.checked); //当前是否选中状态
    	//console.log(obj.data); //选中行的相关数据
    	selectBmUserData = obj.data;
    });
      
    //渲染权限按钮
     ht_auth.render("syndata_auth");
})