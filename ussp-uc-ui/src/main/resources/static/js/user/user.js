layui.use(['ztree', 'table'], function () {
    var $ = layui.jquery;
    // layui.tree({
    //     elem: '#jstree' //传入元素选择器
    //     , skin: ''
    //     , nodes: [{ //节点
    //         name: '广东鸿特信息咨询有限公司'
    //         , children: [{
    //             name: '信息技术中'
    //             , children: [{
    //                 name: '研发一部'
    //             }, {
    //                 name: '研发二部'
    //             }]
    //         }, {
    //             name: '贷后管理中心'
    //             , children: [{
    //                 name: '电催部'
    //             }]
    //         }]
    //     }]
    // });
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
    var table = layui.table;
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
            , {fixed: 'right', width: 178, title: '操作', align: 'center', toolbar: '#user_datatabl_barDemo'}
        ]]
    });
})