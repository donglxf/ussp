/**
 * add by  2018/1/22
 */
/**
 *
 */
layui.define(['jquery', 'ht_config'], function (exports) {
    "use strict";

    var $ = layui.jquery
        , config = layui.ht_config
        , ELEM = '.layui-tab-content'
        , AuthBtns = []
        , HtAuthButtion = function () {
        this.config = {}
    };
    HtAuthButtion.prototype.set = function (options) {
        var that = this;
        $.extend(true, that.config, options);
        return that;
    };
    HtAuthButtion.prototype.render = function (filter) {
        var elemTab = $(ELEM + function () {
            return filter ? ('[lay-filter="' + filter + '"]') : '';
        }()), elemButton = elemTab.find("[lay-auth]");
        layui.each(AuthBtns, function (index, item) {
            console.info(item);
            layui.each(elemButton, function (index, btn) {
                var layAuth = $(btn).attr("lay-auth");
                if(layAuth==item.resContent){
                    $(btn).hide();
                }
            });
        });
        console.info(elemTab, elemButton);
        //elemButton.hide();

    };
    HtAuthButtion.prototype.load = function () {
        $.ajax({
            url: config.loadBtnAndTabUrl
            , type: "GET"
            , error: function (xhr, status, thrown) {
                layui.hint().error('Navbar error:AJAX请求出错.' + thrown);
            }
            , success: function (data) {
                AuthBtns = data;
            }
        })
    };

    //自动完成渲染
    var ht_auth_button = new HtAuthButtion();

    // ht_auth_button.render();

    exports('ht_auth_button', ht_auth_button);
});