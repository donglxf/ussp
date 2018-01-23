/**
 * add by  2018/1/22
 */
/**
 *
 */
layui.define(['jquery', 'ht_config'], function (exports) {
    "use strict";

    var $ = layui.jquery
        , ht_config = layui.ht_config
        , ELEM = '.layui-tab-content'
        , HtAuthButtion = function () {
        this.config = {}
    };
    HtAuthButtion.prototype.set = function (options) {
        var that = this;
        $.extend(true, that.config, options);
        return that;
    };
    HtAuthButtion.prototype.render = function (filter) {
        var that = this
            , elemTab = $(ELEM + function () {
            return filter ? ('[lay-filter="' + filter + '"]') : '';
        }()), elemButton = elemTab.find("[lay-auth]");
        //console.info(elemTab, elemButton);
        //elemButton.hide();
        // layui.each(elemButton, function (index, btn) {
        //     //console.info(btn);
        //     $(btn).hide();
        // });
    };
    HtAuthButtion.prototype.load = function (menuCode) {
    };

    //自动完成渲染
    var ht_auth_button = new HtAuthButtion();

    // ht_auth_button.render();

    exports('ht_auth_button', ht_auth_button);
});