/**
 * add by  2018/1/22
 */
var AllAuth = [];
layui.define(['jquery', 'tab', 'ht_config'], function (exports) {
    "use strict";

    var $ = layui.jquery
        , tab = layui.tab
        , config = layui.ht_config
        , HtAuth = function () {
        this.config = {}
    };
    HtAuth.prototype.set = function (options) {
        var that = this;
        $.extend(true, that.config, options);
        return that;
    };
    HtAuth.prototype.render = function (filter) {
        var ELEM = $('.layui-tab-item ').length == 0 ? "body" : '.layui-tab-item ',
            elemTab = $(ELEM + function () {
                return filter ? (' [lay-filter="' + filter + '"]') : '';
            }())
            , elemButton = elemTab.find("[ht-auth]")
            , menuCode = elemTab.parent("[lay-item-id]").attr("lay-item-id")
            , frameMenuCode = $(self.frameElement).parent().attr("lay-item-id");
        menuCode = menuCode ? menuCode : (frameMenuCode ? frameMenuCode : tab.getId());
        AllAuth = AllAuth.length == 0 ? parent.AllAuth : AllAuth;
        if (AllAuth == null || AllAuth.length == 0) {
            this.load();
        }
        layui.each(elemButton, function (index, btn) {
            var layAuth = $(btn).attr("ht-auth");
            var isAuth = false;
            layui.each(AllAuth, function (index, item) {
                //验证菜单编码和权限编码
                if (menuCode == item.resParent && layAuth == item.resContent) {
                    isAuth = true;
                }
            });
            if (!isAuth) {
                $(btn).remove();
            }
        });
    }
    HtAuth.prototype.load = function (success) {
        $.ajax({
            url: config.loadBtnAndTabUrl
            , async: false
            , type: "GET"
            , error: function (xhr, status, thrown) {
                layui.hint().error('Navbar error:AJAX请求出错.' + thrown);
            }
            , success: function (data) {
                AllAuth = data;
                if(success){
                    success(data);
                }
            }
        })
    };

    //自动完成渲染
    var ht_auth = new HtAuth();

    exports('ht_auth', ht_auth);
})
;