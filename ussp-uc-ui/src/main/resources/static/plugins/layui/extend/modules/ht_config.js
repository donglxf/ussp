/**
 * add by tanrq 2018/1/21
 */
layui.define(function (exports) {
    var basePath = "http://localhost:9000/",
        rule = "uc";
    exports('ht_config', {
        app: "UC"
        , basePath: basePath + rule + "/"
        , loginPath: "/login.html"
        , indexPath: "/"
        , loadMenuUrl: basePath + rule + "/auth/loadMenu"
        , loadBtnAndTabUrl: basePath + rule + "/auth/loadBtnAndTab"
        , loginUrl: basePath + "uaa/auth/login"
        , refreshTokenUrl: basePath + "uaa/auth/token"
    });
});