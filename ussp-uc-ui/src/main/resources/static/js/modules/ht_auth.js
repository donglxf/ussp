/**
 * add by  2018/1/22
 */
/**
 *
 */
layui.define(['jquery', 'tab', 'ht_config'], function (exports) {
    "use strict";

    var $ = layui.jquery
        , tab = layui.tab
        , config = layui.ht_config
        , ELEM = '.layui-tab-item '
        , AllAuth = []
        , HtAuth = function () {
        this.config = {}
    };
    HtAuth.prototype.set = function (options) {
        var that = this;
        $.extend(true, that.config, options);
        return that;
    };
    HtAuth.prototype.render = function (filter) {
        var elemTab = $(ELEM + function () {
            return filter ? ('[lay-filter="' + filter + '"]') : '';
        }())
            , elemButton = elemTab.find("[ht-atuh]")
            , menuCode = elemTab.parent("[lay-item-id]").attr("lay-item-id");
        menuCode = menuCode ? menuCode : tab.getId();
        layui.each(elemButton, function (index, btn) {
            var layAuth = $(btn).attr("ht-atuh");
            var isAuth = false;
            layui.each(AllAuth, function (index, item) {
                //验证菜单编码和权限编码
                if (menuCode == item.resParent && layAuth == item.resCode) {
                    isAuth = true;
                }
            });
            if (!isAuth) {
                $(btn).remove();
            }
        });
    }
    HtAuth.prototype.load = function () {
        $.ajax({
            url: config.loadBtnAndTabUrl
            , type: "GET"
            , error: function (xhr, status, thrown) {
                layui.hint().error('Navbar error:AJAX请求出错.' + thrown);
            }
            , success: function (data) {
                AllAuth = data;
            }
        })
    };

    //自动完成渲染
    var ht_auth = new HtAuth();

    // ht_auth.render();

    exports('ht_auth', ht_auth);
})
;