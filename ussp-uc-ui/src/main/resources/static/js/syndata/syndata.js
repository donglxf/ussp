layui.use([  'form','element',   'laytpl', 'table', 'ht_config', 'ht_auth' ], function () {
    var $ = layui.jquery
        , laytpl = layui.laytpl
        , element = layui.element
        , table = layui.table
        , config = layui.ht_config
        , ht_auth = layui.ht_auth
        , selectTab = 0
        , active = {
    		syndatadd: function () {
	    		alert(0);
	    		 $.post(getDDUrl, null, function (result) {
	    	            console.log(result);
	    	      });
	        },
	        search: function () {
	            alert(2);
	        },
	        
    };
    var getDDUrl = config.basePath +"syndata/user/getDownDD"; //导入
 
  //菜单和模块tab页切换事件
    element.on('tab(resource_top_tab)', function (data) {
        switch (data.index) {
            case 0:
                alert(data.index);
                break;
            case 1:
            	alert(data.index);
                
                break;
        }
    });
  //监听工具栏
    $('#syndata_content .layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    //渲染权限按钮
  //  ht_auth.render("syndata_auth");
})