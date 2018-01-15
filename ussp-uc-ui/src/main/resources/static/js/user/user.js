layui.use(['form', 'ztree', 'table', 'treeselect'], function () {
    var $ = layui.jquery
        , form = layui.form
        , table = layui.table
        , treeselect = layui.treeselect
        , addDialog = 0 //新增弹出框的ID
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
                maxmin: true,
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
                        console.info(data);
                        $.ajax({
                            type: "POST",
                            url: "http://localhost:9999/member/add",
                            data: JSON.stringify(data.field),
                            contentType: "application/json; charset=utf-8",
                            success: function (message) {
                                layer.close(addDialog);
                                if (message.returnCode == '0000') {
                                    table.reload('user_datatable', {
                                        page: {
                                            curr: 1 //重新从第 1 页开始
                                        }
                                        , where: {
                                            //keyWord: $("#user_search_keyword").val(),
                                            query: {
                                                orgCode: nodes[0]["orgCode"]
                                            }
                                        }
                                    });
                                    layer.alert("用户新增成功。");
                                }
                            },
                            error: function (message) {
                                layer.msg("用户新增发生异常，请联系管理员。");
                                console.error(message);
                            }
                        });
                        return false;
                    });
                }
            })
        },
        search: function () {
            //执行重载
            table.reload('user_datatable', {
                page: {
                    curr: 1 //重新从第 1 页开始
                }
                , where: {
                    keyWord: $("#user_search_keyword").val()
                    , query: {
                        orgCode: "DEV1"
                    }
                }
            });
        }
    };
    //渲染组织机构树
    orgTree = $.fn.zTree.init($('#user_org_ztree_left'), {
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
                url: "http://localhost:9999/org/tree.json",
                dataFilter: function (treeId, parentNode, childNodes) {
                    if (!childNodes) return null;
                    for (var i = 0, l = childNodes.length; i < l; i++) {
                        childNodes[i].open = true;
                        childNodes[i].name = childNodes[i]["orgNameCn"].replace(/\.n/g, '.');
                    }
                    return childNodes;
                }
            }
            , callback: {
                onClick: function (event, treeId, treeNode, clickFlag) {
                    //执行重载
                    table.reload('user_datatable', {
                        page: {
                            curr: 1 //重新从第 1 页开始
                        }
                        , where: {
                            //keyWord: $("#user_search_keyword").val(),
                            query: {
                                orgCode: treeNode["orgCode"]
                            }
                        }
                    });
                },
                onAsyncSuccess: function (event, treeId, treeNode, msgString) {
                    var node = orgTree.getNodeByParam("level ", "0");
                    console.info(treeNode, node)
                    if (node) {
                        orgTree.selectNode(node);
                    }
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
    //渲染用户数据表格
    table.render({
        id: 'user_datatable'
        , elem: '#user_datatable'
        , url: 'http://localhost:9999/member/loadListByPage.json'
        , method: 'post' //如果无需自定义HTTP类型，可不加该参数
        // , where: {
        //     query: {
        //         orgCode: "DEV1"
        //     }
        // }5
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
            , {field: 'jobNumber', width: 100, title: '工号'}
            , {field: 'userName', width: 100, title: '用户名'}
            , {field: 'mobile', width: 120, title: '手机'}
            , {field: 'email', width: 100, title: '邮箱'}
            , {field: 'idNo', title: '身份证'}
            , {field: 'orgCode', title: '所属机构'}
            , {field: 'updateOperator', width: 100, title: '更新人'}
            , {field: 'lastModifiedDatetime', width: 150, title: '更新时间'}
            , {fixed: 'right', width: 178, title: '操作', align: 'center', toolbar: '#user_datatable_bar'}
        ]]
    });
    //监听操作栏
    table.on('tool(filter_user_datatable)', function (obj) {
        var data = obj.data;
        if (obj.event === 'detail') {
            layer.msg('ID：' + data.id + ' 的查看操作');
        } else if (obj.event === 'del') {
            layer.confirm('真的删除行么', function (index) {
                obj.del();
                layer.close(index);
            });
        } else if (obj.event === 'edit') {
            layer.alert('编辑行：<br>' + JSON.stringify(data))
        }
    });
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