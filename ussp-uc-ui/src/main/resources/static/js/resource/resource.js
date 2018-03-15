layui.use(['element', 'form', 'ztree', 'table', 'ht_config', 'ht_auth'], function () {
    var $ = layui.jquery
        , element = layui.element
        , form = layui.form
        , table = layui.table
        , config = layui.ht_config
        , ht_auth = layui.ht_auth
        , addDialog = 0 //新增弹出框的ID
        , viewDialog = 0 //查询弹出框的ID
        , editDialog = 0 //修改弹出框的ID
        , relevanceDialog = 0
        , selectBottomTabIndex = 0//当前选中的tab标签页
        , isCutModule = false, isCutBtn = false, isCutTab = false, isCutApi = false//是否切换了模块tab
        , appAndResourceTree //组织机构树控件
        , selectTableData = {}//table选中的行数据
        , getNewResCodeUrl = config.basePath + 'resource/rescode/load'
        , active = {
        add: function (type, relevanceType) { //弹出用户新增弹出框
            var nodes = appAndResourceTree.getSelectedNodes(),
                selectData;
            layer.close(addDialog);
            var title;
            switch (type) {
                case "menu":
                    title = "新增菜单";
                    break;
                case "btn":
                    title = "新增按钮权限";
                    break;
                case "tab":
                    title = "新增TAB权限";
                    break;
                case "api":
                    title = "新增API权限";
                    break;
                case "module":
                    title = "新增模块";
                    break;
                case "custom":
                    title = "新增自定义权限";
                    break;
            }
            switch (type) {
                case "custom":
                    if (nodes.length == 0) {
                        layer.alert("请先在左边选择一个系统。");
                        return false;
                    }
                    break;
                case "module":
                    if (nodes.length == 0) {
                        layer.alert("请先在左边选择一个父模块或系统。");
                        return false;
                    } else if (nodes[0]["type"] != "module" && nodes[0]["type"] != "moduleType") {
                        layer.alert("请选择正确的模块作为父模块。");
                        return false;
                    }
                    break;
                case "menu":
                    if (nodes.length == 0) {
                        layer.alert("请先在左边选择一个父菜单或系统。");
                        return false;
                    } else if (nodes[0]["type"] == "view") {
                        layer.alert("您选择的菜单【" + nodes[0]["name"] + "】不能增加下级菜单，请重新选择。");
                        return false;
                    } else if (nodes[0]["type"] != "group" && nodes[0]["type"] != "menuType") {
                        layer.alert("请选择正确的菜单作为父菜单。");
                        return false;
                    }
                    break;
                case "api":
                    if (relevanceType) {
                        selectData = selectTableData[relevanceType];
                        if (!selectData) {
                            layer.alert("请先在左边选择父资源！");
                            return false;
                        }
                        break;
                    }
                case "tab":
                case "btn":
                    selectData = selectTableData["menu"];
                    if (!selectData) {
                        layer.alert("请先在上面选择父菜单！");
                        return false;
                    }
                    break;
            }
            addDialog = layer.open({
                type: 1,
                area: ['400px', '450px'],
                shadeClose: true,
                title: title,
                content: $("#resource_" + type + "_add_data_div").html(),
                btn: ['保存', '取消'],
                yes: function (index, layero) {
                    var $submitBtn = $("button[lay-filter=resource_" + type + "_add_data_form]", layero);
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
                    var app = nodes[0]["app"]
                        , appName = appAndResourceTree.getNodesByParam("code", app)[0]["name"]
                        , resParent = ""
                        , resParentName = "";
                    //初始弹出框表单数据
                    switch (type) {
                        case "menu":
                        case "module":
                            if (nodes[0]["type"] == "module" || nodes[0]["type"] == "group") {
                                resParent = nodes[0]["code"];
                                resParentName = nodes[0]["name"];
                            }
                            break;
                        case "api":
                        case "tab":
                        case "btn":
                            resParent = selectData["resCode"];
                            resParentName = selectData["resNameCn"];
                            break;
                    }
                    $("input[name=app]", layero).val(app);
                    $("input[name=appName]", layero).val(appName);
                    $("input[name=resParent]", layero).val(resParent);
                    $("input[name=resParentName]", layero).val(resParentName);
                    $.ajax({
                        type: "GET",
                        url: getNewResCodeUrl,
                        data: {
                            app: app,
                            parent: resParent,
                            type: type
                        },
                        dataType: "text",
                        success: function (result) {
                            $("input[name=resCode]", layero).val(result);
                        }
                    })
                    form.render(null, "resource_" + type + "_add_data_form");
                    form.on("submit(resource_" + type + "_add_data_form)", function (data) {
                        data.field.resType = type;
                        $.ajax({
                            type: "POST",
                            url: config.basePath + "resource/add",
                            data: JSON.stringify(data.field),
                            contentType: "application/json; charset=utf-8",
                            success: function (result) {
                                layer.close(index);
                                if (result["returnCode"] == '0000') {
                                    renderTable(relevanceType ? relevanceType : type, resParent);
                                    layer.alert(title + "成功。");
                                }
                            },
                            error: function (result) {
                                layer.msg(title + "发生异常，请联系管理员。");
                                console.error(result);
                            }
                        });
                        return false;
                    });
                }
            })
        },
        relevance: function (resType, relevanceType) {
            layer.close(relevanceDialog);
            var selectData = selectTableData[relevanceType];
            switch (relevanceType) {
                case "module":
                case "menu":
                    if (!selectData) {
                        layer.alert("请先选择一个父菜单。");
                        return false;
                    } else if (selectData["resType"] == "group") {
                        layer.alert("您选择的菜单【" + selectData["resNameCn"] + "】不能关联API资源，请重新选择。");
                        return false;
                    }
                    break;
                case "api":
                case "tab":
                case "btn":
                    if (!selectData) {
                        layer.alert("请先在左边选择父资源！");
                        return false;
                    }
                    break;
            }
            var app = selectData["app"];
            var parentCode = selectData["resCode"];
            var parentName = selectData["resNameCn"];
            relevanceDialog = layer.open({
                type: 1,
                area: ['1200px', '645px'],
                shadeClose: true,
                title: "选择关联的API",
                content: $("#resource_api_data_div").html(),
                btn: ['确认', '取消'],
                yes: function (index, layero) {
                    var apiCheckStatus = table.checkStatus('resource_api_dalog_datatable');
                    $.ajax({
                        type: "POST",
                        url: config.basePath + 'resource/relevance',
                        data: JSON.stringify({
                            parentCode: parentCode,
                            app: app,
                            resourceList: apiCheckStatus.data
                        }),
                        contentType: "application/json; charset=utf-8",
                        success: function (result) {
                            layer.close(index);
                            if (result["returnCode"] == '0000') {
                                renderTable(relevanceType == 'menu' || relevanceType == 'module' ? "api" : relevanceType + "_api", parentCode);
                                layer.alert("关联API成功。");
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
                    $("input[name=menuCode]", layero).val(parentCode);
                    $("input[name=menuName]", layero).val(parentName);
                    table.render({
                        id: 'resource_api_dalog_datatable'
                        , elem: $('#resource_api_dalog_datatable', layero)
                        , url: config.basePath + 'resource/api/page/load'
                        , where: {
                            app: app,
                            parentCode: parentCode,
                            resType: "api"
                        }
                        , initSort: {field: 'resContent', type: 'asc'}
                        , page: true
                        , height: "471"
                        , cols: [[
                            {type: 'numbers'}
                            , {type: 'checkbox'}
                            , {field: 'resContent', width: 300, title: 'API链接'}
                            , {field: 'resNameCn', width: 200, title: 'API名称'}
                            , {field: 'remark', title: '方法名'}
                        ]]
                    });
                    var $keywordInput = $("#resource_api_dialog_search_keyword", layero);
                    $('#resource_dalog_api_search', layero).on('click', function () {
                        var keyWord = $keywordInput.val();
                        refreshDalogAPIDataTable(app, keyWord);
                    });
                    $keywordInput.keydown(function (e) {
                        if (e.keyCode == 13) {
                            var keyWord = $keywordInput.val();
                            refreshDalogAPIDataTable(app, keyWord);
                        }
                    });
                }
            });
        },
        search: function (type, relevanceType) {
            if ("api" == type && relevanceType && relevanceType != "") {
                type = relevanceType + "_" + type;
            }
            var keyword = $("#resource_" + type + "_search_keyword").val();
            renderTable(type, undefined, keyword);
        }
    };
    var refreshDalogAPIDataTable = function (app, keyword, relevanceType) {
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
    }
    //渲染组织机构树
    appAndResourceTree = $.fn.zTree.init($('#resource_app_auth_ztree_left'), {
            async: {
                enable: true,
                url: config.basePath + "resource/app/load",
                dataFilter: function (treeId, parentNode, childNodes) {
                    if (!childNodes) return null;
                    for (var i = 0, l = childNodes.length; i < l; i++) {
                        //展开一级节点
                        // if (childNodes[i].parentCode == '0') {
                        //     childNodes[i].open = true;
                        // }
                        childNodes[i].name = childNodes[i]["nameCn"];
                    }
                    return childNodes;
                }
            }
            , view: {
                height: "full-183"
                , showIcon: false
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
                            renderTable('menu');
                            renderTable('btn', treeNode["code"]);
                            renderTable('btn_api');
                            element.tabChange("resource_top_tab", "resMenuType");
                            element.tabChange("resource_bottom_tab", "resBtnType");
                            break;
                        case "moduleType":
                        case "module":
                            renderTable('module');
                            element.tabChange("resource_top_tab", "resModuleType");
                            break;
                        case "customType":
                        case "custom":
                            renderTable('custom');
                            element.tabChange("resource_top_tab", "resCustomType");
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
    //渲染用户数据表格
    renderTable("menu");
    renderTable("btn");
    renderTable("btn_api");
    renderTable("custom");

    /**
     * 刷新数据表
     * @param type 表格标识类型
     * @param parentCode 过滤的父资源编号（为空，则获取左边树选中的资源编号）
     * @param keyword 过滤的关键字
     */
    function renderTable(type, parentCode, keyword) {
        var app = null, resType = null;
        if (!keyword) {
            keyword = null;
        }
        var selectData;
        switch (type) {
            case "module":
            case "custom":
            case "menu":
                var selectNodes = appAndResourceTree.getSelectedNodes();
                if (selectNodes && selectNodes.length == 1) {
                    selectData = selectNodes[0];
                    if (!parentCode) {
                        parentCode = selectNodes[0]["code"];
                        if (parentCode.indexOf("custom") > 0 || parentCode.indexOf("menu") > 0 || parentCode.indexOf("module") > 0) {
                            parentCode = null;
                        }
                    }
                }
                break;
            case "btn_api":
                selectData = selectTableData["btn"];
                break;
            case "tab_api":
                selectData = selectTableData["tab"];
                break;
            case "module_api":
                selectData = selectTableData["module"];
                break;
            case "api":
            case "tab":
            case "btn":
                selectData = selectTableData["menu"];
                break;
        }
        if (selectData) {
            app = selectData["app"];
            if (!parentCode) {
                parentCode = selectData["resCode"];
            }
        }
        var clos = [[]], height = 'full', page = false, limit = 999, limits = [],
            initSort;
        switch (type) {
            case 'menu':
                resType = "group,view";
                height = '276';
                page = false;
                // limit = 5;
                // limits = [5, 10, 20, 30, 40, 50];
                // initSort = {field: 'resParent', type: 'asc'};
                clos = [[
                    {type: 'numbers'}
                    , {type: 'checkbox', event: 'rowClick'}
                    , {field: 'resCode', width: 120, title: '菜单编号', event: 'rowClick'}
                    , {field: 'resNameCn', width: 150, title: '菜单名称', event: 'rowClick'}
                    , {field: 'resContent', cellMinWidth: 110, minWidth: 110, title: '菜单链接', event: 'rowClick'}
                    , {field: 'fontIcon', align: 'center', width: 60, title: '图标', templet: "#resource_menu_font_icon_laytpl", event: 'rowClick'}
                    , {field: 'sequence', align: 'center', width: 60, title: '顺序', event: 'rowClick'}
                    , {field: 'resParent', width: 100, title: '父菜单编号', event: 'rowClick'}
                    , {field: 'status', width: 60, title: '状态', templet: "#resource_table_status_laytpl", event: 'rowClick'}
                    , {field: 'updateOperator', width: 100, title: '更新人', event: 'rowClick'}
                    , {field: 'lastModifiedDatetime', width: 150, title: '更新时间', event: 'rowClick'}
                    , {fixed: 'right', width: 250, title: '操作', align: 'center', toolbar: '#resource_menu_table_btn', event: 'rowClick'}
                ]];
                break;
            case 'btn':
                resType = 'btn';
                height = 'full-601';
                //initSort = {field: 'resCode', type: 'asc'};
                clos = [[
                    {type: 'numbers', event: 'rowClick'}
                    , {type: 'checkbox', event: 'rowClick'}
                    , {field: 'resCode', width: 150, title: '按钮编号', event: 'rowClick'}
                    , {field: 'resContent', width: 110, title: '权限标识', event: 'rowClick'}
                    , {field: 'resNameCn', cellMinWidth: 110, minWidth: 110, title: '按钮名称', event: 'rowClick'}
                    // , {field: 'fontIcon', width: 60, title: '图标'}
                    // , {field: 'sequence', width: 60, title: '顺序'}
                    , {field: 'resParent', width: 120, title: '父菜单编号', event: 'rowClick'}
                    , {field: 'status', width: 60, title: '状态', templet: "#resource_table_status_laytpl", event: 'rowClick'}
                    , {field: 'updateOperator', width: 100, title: '更新人', event: 'rowClick'}
                    , {field: 'lastModifiedDatetime', width: 150, title: '更新时间', event: 'rowClick'}
                    , {fixed: 'right', width: 200, title: '操作', align: 'center', toolbar: '#resource_table_btn', event: 'rowClick'}
                ]];
                break;
            case 'tab':
                resType = 'tab';
                height = 'full-601';
                //initSort = {field: 'resCode', type: 'asc'};
                clos = [[
                    {type: 'numbers'}
                    , {type: 'checkbox', event: 'rowClick'}
                    , {field: 'resCode', width: 150, title: 'TAB编号', event: 'rowClick'}
                    , {field: 'resNameCn', width: 100, title: 'TAB名称', event: 'rowClick'}
                    , {field: 'resContent', title: 'TAB链接', event: 'rowClick'}
                    // , {field: 'sequence', width: 60, title: '顺序'}
                    , {field: 'resParent', width: 120, title: '父菜单编号', event: 'rowClick'}
                    , {field: 'status', width: 60, title: '状态', templet: "#resource_table_status_laytpl", event: 'rowClick'}
                    , {field: 'updateOperator', width: 100, title: '更新人', event: 'rowClick'}
                    , {field: 'lastModifiedDatetime', width: 150, title: '更新时间', event: 'rowClick'}
                    , {fixed: 'right', width: 178, title: '操作', align: 'center', toolbar: '#resource_table_btn', event: 'rowClick'}
                ]];
                break;
            case 'api':
            case "btn_api":
            case "tab_api":
            case "module_api":
                resType = 'api';
                height = 'full-601';
                //initSort = {field: 'resCode', type: 'asc'};
                clos = [[
                    {type: 'numbers'}
                    , {field: 'resCode', width: 150, title: 'API编号'}
                    , {field: 'resContent', cellMinWidth: 125, minWidth: 125, title: 'API链接'}
                    , {field: 'resNameCn', width: 120, title: 'API名称'}
                    , {field: 'remark', width: 200, title: '方法名'}
                    , {field: 'resParent', width: 120, title: '父资源编号'}
                    , {field: 'status', width: 60, title: '状态', templet: "#resource_table_status_laytpl"}
                    , {field: 'updateOperator', width: 100, title: '更新人'}
                    , {field: 'lastModifiedDatetime', width: 150, title: '更新时间'}
                    , {fixed: 'right', width: 200, title: '操作', align: 'center', toolbar: '#resource_table_btn'}
                ]];
                break;
            case 'module':
                resType = 'module';
                height = 'full-234';
                page = true;
                limit = 20;
                limits = [20, 30, 40, 50, 60, 70];
                // initSort = {field: 'sequence', type: 'asc'};
                clos = [[
                    {type: 'numbers'}
                    , {field: 'resCode', width: 120, title: '模块编号', event: 'rowClick'}
                    , {field: 'resNameCn', width: 150, title: '模块名称', event: 'rowClick'}
                    , {field: 'resContent', title: '模块链接', event: 'rowClick'}
                    , {field: 'fontIcon', width: 60, title: '图标', event: 'rowClick'}
                    , {field: 'sequence', width: 60, title: '顺序', event: 'rowClick'}
                    , {field: 'resParent', width: 120, title: '父模块编号', event: 'rowClick'}
                    , {field: 'status', width: 60, title: '状态', templet: "#resource_table_status_laytpl", event: 'rowClick'}
                    , {field: 'updateOperator', width: 100, title: '更新人', event: 'rowClick'}
                    , {field: 'lastModifiedDatetime', width: 150, title: '更新时间', event: 'rowClick'}
                    , {fixed: 'right', width: 200, title: '操作', align: 'center', toolbar: '#resource_table_btn', event: 'rowClick'}
                ]];
                break;
            case 'custom':
                resType = 'custom';
                height = 'full-234';
                page = true;
                limit = 20;
                limits = [20, 30, 40, 50, 60, 70];
                clos = [[
                    {type: 'numbers'}
                    , {field: 'resCode', width: 120, title: '权限编号'}
                    , {field: 'resNameCn', width: 150, title: '权限名称'}
                    , {field: 'remark', title: '权限备注'}
                    , {field: 'status', width: 60, title: '状态', templet: "#resource_table_status_laytpl"}
                    , {field: 'updateOperator', width: 100, title: '更新人', event: 'rowClick'}
                    , {field: 'lastModifiedDatetime', width: 150, title: '更新时间'}
                    , {fixed: 'right', width: 200, title: '操作', align: 'center', toolbar: '#resource_table_btn'}
                ]];
                break;
        }
        table.render({
            id: 'resource_' + type + '_datatable'
            , elem: '#resource_' + type + '_datatable'
            , url: config.basePath + 'resource/page/load'
            , where: {
                app: app,
                resType: resType,
                parentCode: parentCode,
                keyWord: keyword
            }
            , page: page
            , limit: limit
            , limits: limits
            , height: height
            , cols: clos
        });
    }

    var tableToolEvent = function (obj, type) {
        var data = obj.data;
        if (obj.event === 'rowClick') {
            var $checkbox = obj.tr.find('input[name="layTableCheckbox"]'),
                allCheckbox = obj.tr.parent().find('input[name="layTableCheckbox"]');
            if (selectTableData && selectTableData[type] && selectTableData[type]["resCode"] == data["resCode"]) {
                $checkbox.prop("checked", false);
                selectTableData[type] = undefined;
            } else {
                //重置已选的数据
                allCheckbox.prop('checked', false);
                //重新设置当前checkbox为选中
                $checkbox.prop("checked", true);
                selectTableData[type] = data;
                //刷新子项的数据
                if (type == 'btn') {
                    renderTable("btn_api", data.resCode);
                }
                if (type == "menu") {
                    isCutBtn = false;
                    isCutTab = false;
                    isCutApi = false;
                    switch (selectBottomTabIndex) {
                        case 0:
                            selectTableData["btn"] = undefined;
                            renderTable("btn", data.resCode);
                            renderTable("btn_api");
                            break;
                        case 1:
                            selectTableData["tab"] = undefined;
                            renderTable("tab", data.resCode);
                            renderTable("tab_api");
                            break;
                        case 2:
                            renderTable("api", data.resCode);
                            break;
                    }
                }
            }
            var layFilter = obj.tr.parents("[lay-filter]").attr("lay-filter");
            form.render("checkbox", layFilter);
        } else if (obj.event === 'detail') {
            if (type == "btn_api" || type == 'tab_api' || type == 'module_api') {
                type = "api";
            }
            $.post(config.basePath + 'resource/view?id=' + data.id, null, function (result) {
                if (result["returnCode"] == "0000") {
                    viewDialog = layer.open({
                        type: 1,
                        area: ['400px', '450px'],
                        shadeClose: true,
                        title: "查看资源",
                        content: $("#resource_" + type + "_add_data_div").html(),
                        btn: ['取消'],
                        btn2: function () {
                            layer.closeAll('tips');
                        },
                        success: function (layero) {
                            var status = result.data.status;
                            var appName = appAndResourceTree.getNodesByParam("code", result.data.app)[0]["name"];
                            var resParentNames = result.data.resParent == null ? "" : appAndResourceTree.getNodesByParam("code", result.data.resParent);
                            var resParentName = resParentNames != null && resParentNames.length > 0 ? resParentNames[0]["name"] : "";
                            result.data.status = (status === "0" ? "正常" : (status === "1" ? "禁用" : result.data.status));
                            result.data.appName = appName;
                            result.data.resParentName = resParentName;
                            $.each(result.data, function (name, value) {
                                var $input = $("input[name=" + name + "]", layero);
                                if ($input && $input.length == 1) {
                                    $input.attr("readonly", "readonly");
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
            layer.confirm('是否删除资源' + data.resNameCn + "？", function (index) {
                $.post(config.basePath + 'resource/delete?id=' + data.id, null, function (result) {
                    if (result["returnCode"] == "0000") {
                        renderTable(type);
                        layer.close(index);
                        layer.alert("删除成功。");
                    } else {
                        layer.alert(result.codeDesc);
                    }
                });
            });
        } else if (obj.event === 'edit') {
            if (type == "btn_api" || type == 'tab_api' || type == 'module_api') {
                type = "api";
            }
            layer.close(editDialog);
            $.post(config.basePath + 'resource/view?id=' + data.id, null, function (result) {
                if (result["returnCode"] == "0000") {
                    editDialog = layer.open({
                        type: 1,
                        area: ['400px', '450px'],
                        shadeClose: true,
                        title: "修改资源信息",
                        content: $("#resource_" + type + "_add_data_div").html(),
                        btn: ['保存', '取消'],
                        yes: function (index, layero) {
                            var $submitBtn = $("button[lay-filter=resource_" + type + "_add_data_form]", layero);
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
                            var appName = appAndResourceTree.getNodesByParam("code", result.data.app)[0]["name"];
                            var resParentNames = result.data.resParent == null ? "" : appAndResourceTree.getNodesByParam("code", result.data.resParent);
                            var resParentName = resParentNames != null && resParentNames.length > 0 ? resParentNames[0]["name"] : "";
                            result.data.appName = appName;
                            result.data.resParentName = resParentName;
                            //表单数据填充
                            $.each(result.data, function (name, value) {
                                var $input = $("input[name=" + name + "]", layero);
                                if (name == "resCode") {
                                    $input.attr("readonly", "readonly");
                                }
                                if ($input && $input.length == 1) {
                                    $input.val(value);
                                }
                            });
                            form.render(null, "resource_" + type + "_add_data_form");
                            form.on("submit(resource_" + type + "_add_data_form)", function (data) {
                                data.field.resType = type;
                                $.ajax({
                                    type: "POST",
                                    url: config.basePath + 'resource/update',
                                    data: JSON.stringify($.extend({}, result.data, data.field)),
                                    contentType: "application/json; charset=utf-8",
                                    success: function (result) {
                                        layer.close(index);
                                        if (result["returnCode"] == '0000') {
                                            if (type == 'module') {
                                                moduleTableLoad = false;
                                            } else {
                                                menuTableLoad = false;
                                                btnTableLoad = false;
                                                tabTableLoad = false;
                                                apiTableLoad = false;
                                            }
                                            renderTable(type);
                                            layer.alert("资源修改成功。");
                                        }
                                    },
                                    error: function (result) {
                                        layer.msg(title + "发生异常，请联系管理员。");
                                        console.error(result);
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
        } else if (obj.event === 'disable') { //禁用 
            layer.confirm('是否禁用?', function (index) {
                $.post(config.basePath + 'resource/changeApiState?id=' + data.id + "&status=1", null, function (result) {
                    if (result["returnCode"] == "0000") {
                        renderTable(type);
                        layer.close(index);
                        layer.msg("禁用成功");
                    } else {
                        layer.alert(result.codeDesc);
                    }
                });
            });
        } else if (obj.event === 'enable' || obj.event === 'show') { //启用
            layer.confirm('是否启用?', function (index) {
                $.post(config.basePath + 'resource/changeApiState?id=' + data.id + "&status=0", null, function (result) {
                    if (result["returnCode"] == "0000") {
                        renderTable(type);
                        layer.close(index);
                        layer.msg("启用成功");
                    } else {
                        layer.alert(result.codeDesc);
                    }
                });
            });
        } else if (obj.event === 'hide') { //隐藏
            layer.confirm('是否设置隐藏?', function (index) {
                $.post(config.basePath + 'resource/changeApiState?id=' + data.id + "&status=2", null, function (result) {
                    if (result["returnCode"] == "0000") {
                        renderTable(type);
                        layer.close(index);
                        layer.msg("设置隐藏成功");
                    } else {
                        layer.alert(result.codeDesc);
                    }
                });
            });
        }
        /*else if (obj.event === 'enable' ||obj.event === 'show') { //显示
                    layer.confirm('是否设置显示?', function (index) {
                     $.post(config.basePath + 'resource/changeApiState?id=' + data.id+"&status=3", null, function (result) {
                         if (result["returnCode"] == "0000") {
                             renderTable(type);
                             layer.close(index);
                             layer.msg("设置显示成功");
                         } else {
                             layer.alert(result.codeDesc);
                         }
                     });
                 });
            } */
    };
    //监听操作栏
    table.on('tool(resource_menu_datatable)', function (obj) {
        tableToolEvent(obj, "menu");
    });
    table.on('tool(resource_btn_datatable)', function (obj) {
        tableToolEvent(obj, "btn")
    });
    table.on('tool(resource_tab_datatable)', function (obj) {
        tableToolEvent(obj, "tab")
    });
    table.on('tool(resource_api_datatable)', function (obj) {
        tableToolEvent(obj, "api")
    });
    table.on('tool(resource_btn_api_datatable)', function (obj) {
        tableToolEvent(obj, "btn_api")
    });
    table.on('tool(resource_tab_api_datatable)', function (obj) {
        tableToolEvent(obj, "tab_api")
    });
    table.on('tool(resource_module_api_datatable)', function (obj) {
        tableToolEvent(obj, "module_api")
    });
    table.on('tool(resource_module_datatable)', function (obj) {
        tableToolEvent(obj, "module")
    });
    table.on('tool(resource_custom_datatable)', function (obj) {
        tableToolEvent(obj, "custom")
    });
    //菜单和模块tab页切换事件
    element.on('tab(resource_top_tab)', function (data) {
        switch (data.index) {
            case 0:
                element.tabChange("resource_bottom_tab", "resBtnType");
                break;
            case 1:
                if (!isCutModule) {
                    isCutModule = true;
                    renderTable("module");
                }
                break;
        }
    });
    //按钮权限/TAB权限/API权限tab页切换事件
    element.on('tab(resource_bottom_tab)', function (data) {
        selectBottomTabIndex = data.index;
        switch (data.index) {
            case 0:
                if (!isCutBtn) {
                    isCutBtn = true;
                    renderTable("btn");
                    renderTable("btn_api");
                }
                break;
            case 1:
                if (!isCutTab) {
                    isCutTab = true;
                    renderTable("tab");
                    renderTable("tab_api");
                }
                break;
            case 2:
                if (!isCutApi) {
                    isCutApi = true;
                    renderTable("api");
                }
                break;
        }
    });
    //监听工具栏
    $('#resource_content .layui-btn').on('click', function () {
        var type = $(this).data('type');
        var resType = $(this).data('res-type'),
            relevanceType = $(this).data('relevance-type');
        active[type] ? active[type].call(this, resType, relevanceType) : '';
    });
    //监听树的工具栏
    $('#resource_tree .btn').on('click', function () {
        var type = $(this).data('type');
        switch (type) {
            case "refresh":
                if (appAndResourceTree) {
                    appAndResourceTree.reAsyncChildNodes(null, "refresh");
                }
                break;
            case "expandAll":
                if (appAndResourceTree) {
                    appAndResourceTree.expandAll(true);
                }
                break;
            case "collapseAll":
                if (appAndResourceTree) {
                    appAndResourceTree.expandAll(false);
                }
                break;
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