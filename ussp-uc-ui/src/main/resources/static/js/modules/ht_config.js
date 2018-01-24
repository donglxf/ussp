/**
 * add by tanrq 2018/1/21
 */
layui.define(function (exports) {
    var basePath = "http://localhost:9000/";
    exports('ht_config', {
        app: "UC"
        , basePath: basePath + "uc/"
        , loadMenuUrl: "http://localhost:9999/auth/loadMenu?app=UC&userId=10001"//basePath + "uc/auth/loadMenu"
        , loadBtnAndTabUrl: "http://localhost:9999/auth/loadBtnAndTab?app=UC&userId=10001"//basePath + "uc/auth/loadMenu"
        , loginUrl: basePath + "uaa/auth/login"
        , refreshTokenUrl: basePath + "uaa/auth/token"
    });
});