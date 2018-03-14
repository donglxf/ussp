layui.use(['element', 'form', 'ztree', 'laytpl', 'table', 'ht_config', 'ht_auth', 'treegrid'], function () {
    var $ = layui.jquery
        , laytpl = layui.laytpl
        , element = layui.element
        , table = layui.table
        , treegrid = layui.treegrid
        , config = layui.ht_config
        , ht_auth = layui.ht_auth
        , isMenuLoad = false
        , isModuleLoad = false
        , isCustomLoad = false
        , selectTab = 0
        , roleAndResourceTree //组织机构树控件
        , savePath = config.basePath + "role/res/save"
        , active = {
        save: function (resType) {
            var selectNodes = roleAndResourceTree.getSelectedNodes();
            if (selectNodes && selectNodes.length == 1 && selectNodes[0]["app"] != selectNodes[0]["code"]) {
                var $checkeds = $("#roleRes_" + resType + "_grid input:checkbox:checked")
                    , app = selectNodes[0]["app"]
                    , roleCode = selectNodes[0]["code"]
                    , resCodes = [];
                layui.each($checkeds, function (index, item) {
                    resCodes.push($(item).val());
                });
                $.ajax({
                    type: "POST",
                    url: savePath,
                    data: JSON.stringify({
                        app: app
                        , roleCode: roleCode
                        , resCodes: resCodes
                    }),
                    contentType: "application/json;charset=utf-8",
                    success: function (message) {
                        if (message["returnCode"] == '0000') {
                            layer.alert("保存成功");
                        }
                    },
                    error: function (message) {
                        layer.msg("保存发生异常，请联系管理员。");
                        console.error(message);
                    }
                });
            } else {
                layer.alert("您未选择您要保存的角色与资源，无法保存。");
            }
        },
        reset: function (resType) {
            var selectNodes = roleAndResourceTree.getSelectedNodes();
            if (selectNodes && selectNodes.length == 1 && selectNodes[0]["app"] != selectNodes[0]["code"]) {
                refresh(resType, selectNodes[0]["app"], selectNodes[0]["code"]);
            }
        }
    };
    //渲染组织机构树
    roleAndResourceTree = $.fn.zTree.init($('#roleRes_app_auth_ztree_left'), {
            async: {
                enable: true,
                url: config.basePath + "role/res/app/load",
                dataFilter: function (treeId, parentNode, childNodes) {
                    if (!childNodes) return null;
                    for (var i = 0, l = childNodes.length; i < l; i++) {
                        childNodes[i].name = childNodes[i]["nameCn"];
                    }
                    return childNodes;
                }
            }
            , view: {
            	height: "full-183",
                showIcon: false
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
                    var app = treeNode["app"], roleCode = treeNode["code"]
                        , isMenuLoad = false
                        , isModuleLoad = false
                        , isCustomLoad = false
                    switch (selectTab) {
                        case 0:
                            refresh("menu", app, roleCode);
                            break;
                        case 1:
                            refresh("module", app, roleCode);
                            break;
                        case 2:
                            refresh("custom", app, roleCode);
                            break;
                    }
                },
                onAsyncSuccess: function () {
                    var node = roleAndResourceTree.getNodeByParam("level ", "0");
                    if (node) {
                        roleAndResourceTree.selectNode(node);
                    }
                }
            },
            data: {
                simpleData: {
                    enable: true
                    , idKey: "code"
                    , pIdKey: "parentCode"
                }
            }
        }
    );

    var refresh = function (type, app, role) {
        var treegridOptions = {
            elem: 'roleRes_' + type + '_grid',
            view: 'roleRes_' + type + '_laytpl',
            searchData: {
                type: type,
                app: app,
                role: role
            },
            id: 'code',
            height: "full-270",
            parentid: 'parentCode',
            root: null
        };
        if (app == null || role == null || app == role) {
            treegridOptions = $.extend({}, treegridOptions, {
                data: {rows: []}
            });
        } else {
            treegridOptions = $.extend({}, treegridOptions, {
                url: config.basePath + "role/res/auth/load"
            });
        }
        treegrid.createNew(treegridOptions).build();
    };

    refresh("menu", null, null);
    refresh("module", null, null);
    refresh("custom", null, null);

    //菜单和模块tab页切换事件
    element.on('tab(resource_top_tab)', function (data) {
        selectTab = data.index;
        var selectNodes = roleAndResourceTree.getSelectedNodes();
        if (selectNodes && selectNodes.length == 1 && selectNodes[0]["app"] != selectNodes[0]["code"]) {
            var app = selectNodes[0]["app"]
                , roleCode = selectNodes[0]["code"];
            switch (data.index) {
                case 0:
                    if (!isMenuLoad) {
                        isMenuLoad = true;
                        refresh("menu", app, roleCode);
                    }
                    break;
                case 1:
                    if (!isModuleLoad) {
                        isModuleLoad = true;
                        refresh("module", app, roleCode);
                    }
                    break;
                case 2:
                    if (!isCustomLoad) {
                        isCustomLoad = true;
                        refresh("custom", app, roleCode);
                    }
                    break;
            }
        }
    });
    //监听工具栏
    $('#resource_content .layui-btn').on('click', function () {
        var type = $(this).data('type');
        var resType = $(this).data('res-type');
        active[type] ? active[type].call(this, resType) : '';
    });
    //刷新树的数据
   /* $('#roleRes_btn_refresh_tree').on('click', function (e) {
        if (roleAndResourceTree) {
            roleAndResourceTree.reAsyncChildNodes(null, "refresh");
        }
    });*/
    $('#roleRes_btn_tree .btn').on('click', function () {
        var type = $(this).data('type');
        switch (type) {
            case "refresh":
                if (roleAndResourceTree) {
                	roleAndResourceTree.reAsyncChildNodes(null, "refresh");
                }
                break;
            case "expandAll":
                if (roleAndResourceTree) {
                	roleAndResourceTree.expandAll(true);
                }
                break;
            case "collapseAll":
                if (roleAndResourceTree) {
                	roleAndResourceTree.expandAll(false);
                }
                break;
        }
    });
    
    var nodeList = [];
    //搜索树的数据
    $('#user_search_tree_org').bind('input', function (e) {
        if (roleAndResourceTree && $(this).val() != "") {
            nodeList = roleAndResourceTree.getNodesByParamFuzzy("name", $(this).val());
            updateNodes(true);
        } else {
            updateNodes(false);
        }
    });

    //刷新树节点
    function updateNodes(highlight) {
        for (var i = 0, l = nodeList.length; i < l; i++) {
            nodeList[i].highlight = highlight;
            roleAndResourceTree.updateNode(nodeList[i]);
            if (highlight) {
                roleAndResourceTree.expandNode(roleAndResourceTree.getNodeByParam("code", nodeList[i]["parentCode"]), true, false, null, null);
            }
        }
    }

    //渲染权限按钮
    ht_auth.render("resouce_auth");
})