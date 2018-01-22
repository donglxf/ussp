/**
 * add by tanrq 2018/1/21
 */
layui.define([], function (exports) {
    var config = {
        app: "UC"
        , basePath: "http://localhost:9999/"
        , refreshTokenUrl: 'http://localhost:9000/api/auth/token'
    };
    exports('config', config);
});