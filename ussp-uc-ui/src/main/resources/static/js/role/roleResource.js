layui.use(['element', 'form', 'ztree', 'laytpl', 'table', 'ht_config', 'ht_auth', 'treegrid'], function () {
    var $ = layui.jquery
        , laytpl = layui.laytpl
        , element = layui.element
        , form = layui.form
        , table = layui.table
        , treegrid = layui.treegrid
        , config = layui.ht_config
        , ht_auth = layui.ht_auth
        , menuTableLoad = false//菜单数据表格是否已加载
        , btnTableLoad = false//按钮数据表格是否已加载
        , tabTableLoad = false//tab数据表格是否已加载
        , apiTableLoad = false//api数据表格是否已加载
        , moduleTableLoad = false//module数据表格是否已加载
        , appAndResourceTree //组织机构树控件
        , active = {
        search: function (type) {
            var selectNodes = appAndResourceTree.getSelectedNodes();
            if (selectNodes && selectNodes.length == 1) {
                var keyword = $("#resource_" + type + "_search_keyword").val();
                var resType = selectNodes[0]["type"];
                if (type == 'module') {
                    moduleTableLoad = false;
                } else {
                    menuTableLoad = false;
                    btnTableLoad = false;
                    tabTableLoad = false;
                    apiTableLoad = false;
                }
                if (resType != "view" && resType != "group" && resType != "module") {
                    renderTable(type, null, keyword);
                } else {
                    renderTable(type, undefined, keyword);
                }
            }
        }
    };
    var refreshDalogAPIDataTable = function (app, keyword) {
        if (!keyword) {
            keyword = null;
        }
        table.reload('resource_api_dalog_datatable', {
            page: {
                curr: 1 //重新从第 1 页开始
            }
            , where: {
                keyWord: keyword,
                app: app,
                resType: "api"
            }
        });
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
                    var resType = treeNode["type"];
                    switch (resType) {
                        case "view":
                        case "group":
                        case "app":
                        case "menuType":
                            menuTableLoad = false;
                            btnTableLoad = false;
                            tabTableLoad = false;
                            apiTableLoad = false;
                            if (resType != "view" && resType != "group") {
                                renderTable('menu', null);
                            } else {
                                renderTable('menu');
                            }
                            renderTable('btn', treeNode["code"]);
                            element.tabChange("resource_top_tab", "resMenuType");
                            element.tabChange("resource_bottom_tab", "resBtnType");
                            break;
                        case "moduleType":
                        case "module":
                            moduleTableLoad = false;
                            if (resType != "module") {
                                renderTable('module', null);
                            } else {
                                renderTable('module');
                            }
                            element.tabChange("resource_top_tab", "resModuleType");
                            break;
                    }
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
    treegrid.config.render = function (viewid, data) {
        var view = document.getElementById(viewid).innerHTML;
        return laytpl(view).render(data) || '';
    };
    var id = 1;
    treegrid.createNew({
        elem: 'roleRes_grid',
        view: 'roleRes_table_laytpl',
        url: config.basePath + "role/res/auth/load",
        id: 'code',
        height: "full-270",
        parentid: 'parentCode',
        root: null,
        loadRow: function (data) {
            var rows = [];
            for (var i = 1; i <= 3; i++) {
                rows.push({
                    id: id,
                    pid: data.parentCode,
                    title: 'load' + id,
                    name: '-1',
                    code: 'loading' + id,
                    tel: '',
                    _children: i == 3 ? false : true
                });
                id++;
            }
            setTimeout(function () {
                data.children(rows);
            }, 2000);
        },
        success: function () {
            //form.render("checkbox", "roleRes_grid");
        }
    }).build();

    // treeGrid.render($("#myTruee"));

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