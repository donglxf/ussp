layui.use(['element', 'form', 'ztree', 'table'], function () {
    var $ = layui.jquery
        , element = layui.element
        , form = layui.form
        , table = layui.table
        , addDialog = 0 //新增弹出框的ID
        , viewDialog = 0 //查询弹出框的ID
        , editDialog = 0 //修改弹出框的ID
        , orgTree //组织机构树控件
        , active = {
        add: function () { //弹出用户新增弹出框
            var nodes = orgTree.getSelectedNodes();
            if (nodes.length == 0) {
                layer.alert("请先选择一个组织机构。");
                return false;
            }
            layer.close(addDialog);
            addDialog = layer.open({
                type: 1,
                area: ['400px', '400px'],
                shadeClose: true,
                title: "新增用户",
                content: $("#user_add_data_div").html(),
                btn: ['保存', '取消'],
                yes: function (index, layero) {
                    var $submitBtn = $("button[lay-filter=filter_add_data_form]", layero);
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
                    $("input[name=orgName]", layero).val(nodes[0]["orgNameCn"]);
                    $("input[name=orgCode]", layero).val(nodes[0]["orgCode"]);
                    $("input[name=orgPath]", layero).val(nodes[0]["orgPath"]);
                    $("input[name=rootOrgCode]", layero).val(nodes[0]["rootOrgCode"]);
                    form.render(null, "filter_add_data_form");
                    form.on('submit(filter_add_data_form)', function (data) {
                        $.ajax({
                            type: "POST",
                            url: "http://localhost:9999/user/add",
                            data: JSON.stringify(data.field),
                            contentType: "application/json; charset=utf-8",
                            success: function (result) {
                                layer.close(index);
                                if (result["returnCode"] == '0000') {
                                    refreshTable();
                                    layer.alert("用户新增成功。");
                                }
                            },
                            error: function (result) {
                                layer.msg("用户新增发生异常，请联系管理员。");
                                console.error(result);
                            }
                        });
                        return false;
                    });
                }
            })
        },
        search: function () {
            //执行重载
            refreshTable($("#user_search_keyword").val());
        }
    };
    var refreshTable = function (keyword) {
        if (!keyword) {
            keyword = null;
        }
        var selectNodes = orgTree.getSelectedNodes();
        if (selectNodes && selectNodes.length == 1) {
            table.reload('user_datatable', {
                page: {
                    curr: 1 //重新从第 1 页开始
                }
                , where: {
                    keyWord: keyword,
                    orgCode: selectNodes[0]["orgCode"]
                }
            });
        }
    }
    //渲染组织机构树
    orgTree = $.fn.zTree.init($('#resource_app_auth_ztree_left'), {
            async: {
                enable: true,
                url: basepath + "system/appandauth/load",
                dataFilter: function (treeId, parentNode, childNodes) {
                    if (!childNodes) return null;
                    for (var i = 0, l = childNodes.length; i < l; i++) {
                        //childNodes[i].open = true;
                        childNodes[i].name = childNodes[i]["nameCn"].replace(/\.n/g, '.');
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
                    //执行重载
                    refreshTable();
                },
                onAsyncSuccess: function (event, treeId, treeNode) {
                    var node = orgTree.getNodeByParam("level ", "0");
                    if (node) {
                        orgTree.selectNode(node);
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
    //渲染用户数据表格
    renderTable("menu");
    renderTable("btn");
    element.on('tab(resource_bottom_tab)', function (data) {
        switch (data.index) {
            case 1:
                renderTable("tab");
                break;
            case 2:
                renderTable("api");
                break;
        }
    });
    element.on('tab(resource_top_tab)', function (data) {
        switch (data.index) {
            case 1:
                renderTable("module");
                break;
        }
    });

    function renderTable(type) {
        var clos = [[]], height = 'full', page = false, limit = 999, limits = [];
        switch (type) {
            case 'menu':
                height = '276';
                page = true;
                limit = 5;
                limits = [5, 10, 20, 30, 40, 50];
                clos = [[
                    {type: 'numbers'}
                    , {field: 'jobNumber', width: 100, title: '菜单编号'}
                    , {field: 'userName', width: 100, title: '菜单名称'}
                    , {field: 'mobile', width: 120, title: '父菜单'}
                    , {field: 'email', width: 100, title: '菜单链接'}
                    , {field: 'email', width: 100, title: '菜单图标'}
                    , {field: 'idNo', title: '顺序号'}
                    , {field: 'status', width: 60, title: '状态', templet: "#resource_table_status_laytpl"}
                    , {field: 'updateOperator', width: 100, title: '更新人'}
                    , {field: 'lastModifiedDatetime', width: 150, title: '更新时间'}
                    , {width: 178, title: '操作', align: 'center', toolbar: '#resource_table_btn'}
                ]];
                break;
            case 'btn':
                height = 'full-601';
                clos = [[
                    {type: 'numbers'}
                    , {field: 'jobNumber', width: 100, title: '按钮编号'}
                    , {field: 'userName', width: 100, title: '按钮名称'}
                    , {field: 'userName', width: 100, title: '按钮图标'}
                    , {field: 'mobile', width: 120, title: '所属菜单'}
                    , {field: 'idNo', title: '顺序号'}
                    , {field: 'status', width: 60, title: '状态', templet: "#resource_table_status_laytpl"}
                    , {field: 'updateOperator', width: 100, title: '更新人'}
                    , {field: 'lastModifiedDatetime', width: 150, title: '更新时间'}
                    , {width: 178, title: '操作', align: 'center', toolbar: '#resource_table_btn'}
                ]];
                break;
            case 'tab':
                height = 'full-601';
                clos = [[
                    {type: 'numbers'}
                    , {field: 'jobNumber', width: 100, title: 'TAB编号'}
                    , {field: 'userName', width: 100, title: 'TAB名称'}
                    , {field: 'userName', width: 100, title: 'TAB链接'}
                    , {field: 'mobile', width: 120, title: '所属菜单'}
                    , {field: 'idNo', title: '顺序号'}
                    , {field: 'status', width: 60, title: '状态', templet: "#resource_table_status_laytpl"}
                    , {field: 'updateOperator', width: 100, title: '更新人'}
                    , {field: 'lastModifiedDatetime', width: 150, title: '更新时间'}
                    , {width: 178, title: '操作', align: 'center', toolbar: '#resource_table_btn'}
                ]];
                break;
            case 'api':
                height = 'full-601';
                clos = [[
                    {type: 'numbers'}
                    , {field: 'jobNumber', width: 100, title: 'API编号'}
                    , {field: 'userName', width: 100, title: 'API名称'}
                    , {field: 'userName', width: 100, title: 'API链接'}
                    , {field: 'mobile',  title: '所属菜单'}
                    , {field: 'status', width: 60, title: '状态', templet: "#resource_table_status_laytpl"}
                    , {field: 'updateOperator', width: 100, title: '更新人'}
                    , {field: 'lastModifiedDatetime', width: 150, title: '更新时间'}
                    , {width: 178, title: '操作', align: 'center', toolbar: '#resource_table_btn'}
                ]];
                break;
            case 'module':
                height = 'full-234';
                page = true;
                limit = 20;
                limits = [20, 30, 40, 50, 60, 70];
                clos = [[
                    {type: 'numbers'}
                    , {field: 'jobNumber', width: 100, title: '模块编号'}
                    , {field: 'userName', width: 100, title: '模块名称'}
                    , {field: 'mobile', width: 120, title: '父模块'}
                    , {field: 'email', width: 100, title: '模块链接'}
                    , {field: 'email', width: 100, title: '模块图标'}
                    , {field: 'idNo', title: '顺序号'}
                    , {field: 'status', width: 60, title: '状态', templet: "#resource_table_status_laytpl"}
                    , {field: 'updateOperator', width: 100, title: '更新人'}
                    , {field: 'lastModifiedDatetime', width: 150, title: '更新时间'}
                    , {width: 178, title: '操作', align: 'center', toolbar: '#resource_table_btn'}
                ]];
                break;
        }
        table.render({
            id: 'resource_' + type + '_datatable'
            , elem: '#resource_' + type + '_datatable'
            , url: basepath + 'user/loadListByPage.json'
            , where: {
                orgCode: "DEV1"
            }
            , page: page
            , limit: 9999
            , limits: limits
            , height: height
            , cols: clos
        });
    }

    //监听操作栏
    table.on('tool(resource_menu_datatable)', function (obj) {
            var data = obj.data;
            if (obj.event === 'detail') {
                $.post("http://localhost:9999/user/view/" + data.userId, null, function (result) {
                    if (result["returnCode"] == "0000") {
                        viewDialog = layer.open({
                            type: 1,
                            area: ['680px', '360px'],
                            shadeClose: true,
                            title: "修改用户",
                            content: $("#user_view_data_div").html(),
                            btn: ['取消'],
                            btn2: function () {
                                layer.closeAll('tips');
                            },
                            success: function (layero) {
                                var status = result.data.status;
                                result.data.status = status === "0" ? "正常" : (status === "1" ? "禁用" : (status === "4" ? "冻结" : (status === "1" ? "锁定" : result.data.status)));
                                $.each(result.data, function (name, value) {
                                    var $input = $("input[name=" + name + "]", layero);
                                    if ($input && $input.length == 1) {
                                        $input.val(value);
                                    }
                                });
                            }
                        })
                    } else {
                        layer.msg(result.codeDesc);
                    }
                });
            } else if (obj.event === 'del') {
                layer.confirm('是否删除用户' + data.userName + "？", function (index) {
                    $.post("http://localhost:9999/user/delete/" + data.userId, null, function (result) {
                        if (result["returnCode"] == "0000") {
                            refreshTable();
                            layer.close(index);
                            layer.msg("删除用户成功。");
                        } else {
                            layer.msg(result.codeDesc);
                        }
                    });
                });
            } else if (obj.event === 'edit') {
                layer.close(editDialog);
                $.post("http://localhost:9999/user/view/" + data.userId, null, function (result) {
                    if (result["returnCode"] == "0000") {
                        editDialog = layer.open({
                            type: 1,
                            area: ['400px', '380px'],
                            shadeClose: true,
                            title: "修改用户",
                            content: $("#user_modify_data_div").html(),
                            btn: ['保存', '取消'],
                            yes: function (index, layero) {
                                var $submitBtn = $("button[lay-filter=user_filter_modify_data_form]", layero);
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
                                //表单数据填充
                                $.each(result.data, function (name, value) {
                                    var $input = $("input[name=" + name + "]", layero);
                                    if ($input && $input.length == 1) {
                                        $input.val(value);
                                    }
                                });
                                form.render(null, "user_filter_modify_data_form");
                                form.on('submit(user_filter_modify_data_form)', function (data) {
                                    $.ajax({
                                        type: "POST",
                                        url: "http://localhost:9999/user/update",
                                        data: JSON.stringify(data.field),
                                        contentType: "application/json; charset=utf-8",
                                        success: function (result2) {
                                            layer.close(index);
                                            if (result2["returnCode"] == '0000') {
                                                refreshTable();
                                                layer.alert("用户修改成功。");
                                            }
                                        },
                                        error: function (message) {
                                            layer.msg("用户新增发生异常，请联系管理员。");
                                            layer.close(index);
                                            console.error(message);
                                        }
                                    });
                                    return false;
                                });
                            }
                        })
                    } else {
                        layer.msg(result.codeDesc);
                    }
                });
            }
        }
    );
    //监听工具栏
    $('#user_table_tools .layui-btn').on('click', function () {
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    //刷新树的数据
    $('#user_btn_refresh_tree').on('click', function (e) {
        if (orgTree) {
            orgTree.reAsyncChildNodes(null, "refresh");
        }
    });
    var nodeList = [];
    //搜索树的数据
    $('#user_search_tree_org').bind('input', function (e) {
        if (orgTree && $(this).val() != "") {
            nodeList = orgTree.getNodesByParamFuzzy("name", $(this).val());
            updateNodes(true);
        } else {
            updateNodes(false);
        }
    });

    //刷新树节点
    function updateNodes(highlight) {
        for (var i = 0, l = nodeList.length; i < l; i++) {
            nodeList[i].highlight = highlight;
            orgTree.updateNode(nodeList[i]);
            if (highlight) {
                orgTree.expandNode(orgTree.getNodeByParam("orgCode", nodeList[i]["parentOrgCode"]), true, false, null, null);
            }
        }
    }
})