layui.use(['element', 'form', 'ztree', 'laytpl', 'table', 'ht_config', 'ht_auth', 'treegrid'], function () {
    var $ = layui.jquery
        , laytpl = layui.laytpl
        , element = layui.element
        , table = layui.table
        , treegrid = layui.treegrid
        , config = layui.ht_config
        , ht_auth = layui.ht_auth
        , appAndResourceTree //组织机构树控件
        , savePath = config.basePath + "role/res/save"
        , active = {
        save: function () {
            var selectNodes = appAndResourceTree.getSelectedNodes();
            if (selectNodes && selectNodes.length == 1 && selectNodes[0]["app"] != selectNodes[0]["code"]) {
                var $checkeds = $("#roleRes_grid input:checkbox:checked")
                    , app = selectNodes[0]["app"]
                    , roleCode = selectNodes[0]["code"]
                    , resCodes = [];
                layui.each($checkeds, function (index, item) {
                    resCodes.push($(item).val());
                });
                console.info(resCodes, app, roleCode);
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
        reset: function () {
            var selectNodes = appAndResourceTree.getSelectedNodes();
            if (selectNodes && selectNodes.length == 1 && selectNodes[0]["app"] != selectNodes[0]["code"]) {
                refresh(selectNodes[0]["app"], selectNodes[0]["code"]);
            }
        },
        search: function () {
            var selectNodes = appAndResourceTree.getSelectedNodes();
            if (selectNodes && selectNodes.length == 1) {
                //var keyword = $("#resource_" + type + "_search_keyword").val();

            }
        }
    };
    //渲染组织机构树
    appAndResourceTree = $.fn.zTree.init($('#roleRes_app_auth_ztree_left'), {
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
                    refresh(treeNode["app"], treeNode["code"]);
                },
                onAsyncSuccess: function (event, treeId, treeNode) {
                    var node = appAndResourceTree.getNodeByParam("level ", "0");
                    if (node) {
                        appAndResourceTree.selectNode(node);
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

    var refresh = function (app, role) {
        if (app == null || role == null || app == role) {
            treegrid.createNew({
                elem: 'roleRes_grid',
                view: 'roleRes_table_laytpl',
                data: {rows: []},
                searchData: {
                    app: app,
                    role: role
                },
                id: 'code',
                height: "full-270",
                parentid: 'parentCode',
                root: null
            }).build();
        } else {
            treegrid.createNew({
                elem: 'roleRes_grid',
                view: 'roleRes_table_laytpl',
                url: config.basePath + "role/res/auth/load",
                searchData: {
                    app: app,
                    role: role
                },
                id: 'code',
                height: "full-270",
                parentid: 'parentCode',
                root: null
            }).build();
        }
    };

    refresh(null, null);

    //菜单和模块tab页切换事件
    element.on('tab(resource_top_tab)', function (data) {
        switch (data.index) {
            case 0:
                element.tabChange("resource_bottom_tab", "resBtnType");
                break;
            case 1:
                renderTable("module");
                break;
        }
    });
    //监听工具栏
    $('#resource_content .layui-btn').on('click', function () {
        var type = $(this).data('type');
        var resType = $(this).data('res-type');
        active[type] ? active[type].call(this, resType) : '';
    });
    //刷新树的数据
    $('#roleRes_btn_refresh_tree').on('click', function (e) {
        if (appAndResourceTree) {
            appAndResourceTree.reAsyncChildNodes(null, "refresh");
        }
    });
    var nodeList = [];
    //搜索树的数据
    $('#user_search_tree_org').bind('input', function (e) {
        if (appAndResourceTree && $(this).val() != "") {
            nodeList = appAndResourceTree.getNodesByParamFuzzy("name", $(this).val());
            updateNodes(true);
        } else {
            updateNodes(false);
        }
    });

    //刷新树节点
    function updateNodes(highlight) {
        for (var i = 0, l = nodeList.length; i < l; i++) {
            nodeList[i].highlight = highlight;
            appAndResourceTree.updateNode(nodeList[i]);
            if (highlight) {
                appAndResourceTree.expandNode(appAndResourceTree.getNodeByParam("code", nodeList[i]["parentCode"]), true, false, null, null);
            }
        }
    }

    //渲染权限按钮
    ht_auth.render("resouce_auth");
})