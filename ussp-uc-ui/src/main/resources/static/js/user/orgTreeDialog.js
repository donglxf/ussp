
layui.config({
    base: '/plugins/layui/extend/modules/',
    version: false
}).use(['app', 'message', 'ht_ajax', 'ht_cookie', 'ht_auth','ztree'],function () {
    var $ = layui.jquery
        , form = layui.form
        , userOrgTree //组织机构树控件
        , config = layui.ht_config
        ,cookie = layui.ht_cookie;
  
  //渲染组织机构树
    userOrgTree = $.fn.zTree.init($('#user_org_ztree_main'), {
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
            	 height: "full"
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
                	parent.$1("#orgNameEdit",parent.layeroEdit).val(treeNode["orgNameCn"]);
                	parent.$1("#orgCodeEdit",parent.layeroEdit).val(treeNode["orgCode"]);
                	parent.$1("#orgPathEdit",parent.layeroEdit).val(treeNode["orgPath"]);
                    var index = parent.layer.getFrameIndex(window.name);
                    parent.layer.close(index);
                },
                onAsyncSuccess: function (event, treeId, treeNode) {
                    //获取根节点个数,getNodes获取的是根节点的集合            
                    var nodeList = userOrgTree.getNodes();　　　　　　
                    //展开第一个根节点           
                    userOrgTree.expandNode(nodeList[0], true);　　　　　　
                    //当再次点击节点时条件不符合,直接跳出方法     
                    
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
})