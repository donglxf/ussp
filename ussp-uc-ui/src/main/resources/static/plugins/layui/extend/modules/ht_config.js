/**
 * add by tanrq 2018/1/21
 */
layui.define(function (exports) {
    var base = "http://localhost:30111/", tempInstanceId, tempUnInstanceId;
    try {
        base = gatewayUrl ? gatewayUrl : base;
    } catch (e) {
    }
    try {
        tempInstanceId = instanceId;
    } catch (e) {
    }
    try {
        tempUnInstanceId = unInstanceId;
    } catch (e) {
    }
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
        , loadSelfinfoUrl: base + "uc/user/in/selfinfo"
        , refreshTokenUrl: base + "uaa/auth/token"
        , instanceId: tempInstanceId  //指定路由的服务名（instanceId：注册中心注册的服务实例名）
        , unInstanceId: tempUnInstanceId //指定不路由的服务名（uninstanceId：注册中心注册的服务实例名）
    });
});