layui.use(['form', 'ztree', 'table'], function () {
    var $ = layui.jquery
        , form = layui.form
        , table = layui.table
        , addDialog = 0,
        active = {
            add: function () { //弹出用户新增弹出框
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
                        var $submitBtn = $("button[lay-filter=filter_add_data_form_btn]", layero);
                        if ($submitBtn) {
                            $submitBtn.click();
                            // $submitBtn.click();
                        } else {
                            throw new Error("没有找到submit按钮。");
                        }
                    },
                    btn2: function () {
                        layer.closeAll('tips');
                    },
                    success: function (layero, index) {
                        form.render(null, "filter_add_data_form");
                        form.on('submit(filter_add_data_form)', function (data) {
                            layer.alert(JSON.stringify(data.field), {
                                title: '最终的提交信息'
                            });
                            return false;
                        });
                    }
                })
            }
        }
    //渲染组织机构树
    $.fn.zTree.init($('#user_org_ztree_left'), {
            view: {showIcon: true}
            , callback: {
                onClick: function (event, treeId, treeNode, clickFlag) {
                    console.info(event + "\t" + treeId + "\t" + treeNode + "\t" + clickFlag);
                }
            }
        },
        [{ //节点
            name: '广东鸿特信息咨询有限公司'
            , open: true
            , children: [{
                name: '信息技术中'
                , children: [{
                    name: '研发一部'
                }, {
                    name: '研发二部'
                }]
            }, {
                name: '贷后管理中心'
                , children: [{
                    name: '电催部'
                }]
            }]
        }]
    );
    //渲染用户数据表格
    table.render({
        elem: '#user_datatable'
        , url: '/datas/table.json'
        , page: true
        , height: 'full-200'
        , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
        , cols: [[
            {field: 'id', width: 80, title: 'ID', sort: true}
            , {field: 'username', width: 80, title: '用户名'}
            , {field: 'sex', width: 80, title: '性别', sort: true}
            , {field: 'city', width: 80, title: '城市'}
            , {field: 'sign', title: '签名', width: '30%', minWidth: 100} //minWidth：局部定义当前单元格的最小宽度，layui 2.2.1 新增
            , {field: 'experience', title: '积分', sort: true}
            , {field: 'score', title: '评分', sort: true}
            , {field: 'classify', title: '职业'}
            , {field: 'wealth', width: 100, title: '财富', sort: true}
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
})