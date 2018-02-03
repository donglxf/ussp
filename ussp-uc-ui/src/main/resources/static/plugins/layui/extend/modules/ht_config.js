/**
 * add by tanrq 2018/1/21
 */
layui.define(function (exports) {
    var base = "http://10.110.1.240:30111/";
    exports('ht_config', {
        app: "UC"
        , basePath: base + "uc/"
        , basePath1: base + "xxx/"
        , loginPath: "/login.html"
        , indexPath: "/"
        , loadMenuUrl: base + "uc/auth/loadMenu"
        , loadBtnAndTabUrl: base + "uc/auth/loadBtnAndTab"
        , loginUrl: base + "uaa/auth/login"
        , refreshTokenUrl: base + "uaa/auth/token"
    });
});