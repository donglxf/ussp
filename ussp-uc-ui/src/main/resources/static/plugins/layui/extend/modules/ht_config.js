/**
 * add by tanrq 2018/1/21
 */
layui.define(function (exports) {
    var base = gatewayUrl ? gatewayUrl : "http://localhost:30111/";
    exports('ht_config', {
        isLocal: false
        , app: "UC"
        , basePath: base + "uc/"
        , basePath1: base + "xxx/" //如果访问多个微服务，请自行扩展
        , loginPath: "/login.html"
        , indexPath: "/"
        , loadMenuUrl: base + "uc/auth/loadMenu"
        , loadBtnAndTabUrl: base + "uc/auth/loadBtnAndTab"
        , loginUrl: base + "uaa/auth/login"
        , refreshTokenUrl: base + "uaa/auth/token"
    });
});