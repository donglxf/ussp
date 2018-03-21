layui.use(['element', 'form',   'laytpl', 'table', 'ht_config', 'ht_auth' ], function () {
    var $ = layui.jquery
        , laytpl = layui.laytpl
        , element = layui.element
        , table = layui.table
        , config = layui.ht_config
        , ht_auth = layui.ht_auth
        , selectTab = 0
        , active = {
        save: function (resType) {
        },
        reset: function (resType) {
        }
    };

 

    //渲染权限按钮
    ht_auth.render("syndata_auth");
})