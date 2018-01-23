/**
 * add by tanrq 2018/1/21
 */
layui.define(function (exports) {
    exports('ht_config', {
        app: "UC"
        , basePath: "http://localhost:9999/"
        , loginUrl: "http://localhost:9000/uaa/auth/login"
        , refreshTokenUrl: "http://localhost:9000/uaa/auth/token"
    });
});