/**
 * add by tanrq 2018/1/20
 */
layui.define(["cookie", "config"], function (exports) {
        var $ = layui.jquery, cookie = layui.cookie, config = layui.config;

        var ht_ajax = {
            config: {},
            set: function (options) {
                var that = this;
                $.extend(true, that.config, options);
                return that;
            },
            init: function () {
                var that = this;
                ht_ajax.extendsAjax();
                return that;
            },
            /**
             * 验证token是否有效，如果无效则重新刷新token，如果无法拉取则重新登录
             * @returns {boolean}
             */
            validationAndRefreshToken: function (noToken) {
                var token = cookie.getToken();
                var refreshToken = cookie.getRefreshToken();
                // 如果refreshToken失效，则直接跳转到登录页面
                if (refreshToken == null || refreshToken == "") {
                    top.location.href = "/login.html";
                } else {
                    //延长refreshToken时间
                    cookie.setRefreshToken(refreshToken);
                }
                // 验证token是否失效，失效则重新请求后台更新token，如果无法更新，则重新登录
                if ((token == null || token == "") && (noToken == false || !noToken)) {
                    console.debug("token无效，重新加载token", refreshToken, config.refreshTokenUrl);
                    $.ajax({
                        type: 'GET',
                        url: config.refreshTokenUrl,
                        headers: {
                            Authorization: "Bearer " + refreshToken
                        },
                        noToken: true,
                        success: function (data) {
                            data.code
                            if (data == null || data["token"] == null || data["token"] == "") {
                                top.location.href = "/login.html";
                            } else {
                                cookie.setToken(data["token"]);
                            }
                        },
                        error: function () {
                            top.location.href = "/login.html";
                        }
                    });
                }
            },
            /**
             * 扩展jquery ajax方法
             */
            extendsAjax: function () {
                // 备份jquery的ajax方法
                var _ajax = $.ajax;

                // 重写jquery的ajax方法
                $.ajax = function (opt) {
                    //所有ajax请求前先验证token是否存在
                    ht_ajax.validationAndRefreshToken(opt.noToken);

                    //因为可能存在同域名下的不同异步请求，所有不能通过地址过滤的方式来判断是否需要拼接basePath
                    // var ignoreUrl = ['.js', '.css', '.html', '.htm', '.png', '.gif', '.jpg', '.icon'];
                    // //自动拼接请求地址
                    // layui.each(ignoreUrl, function (index, item) {
                    //     if (opt.url && opt.url.toUpperCase().indexOf(item.toUpperCase()) > 0) {
                    //         console.info(opt.url, item);
                    //     }
                    // });

                    var headers = {
                        app: config.app /*系统编码统一通过http headers进行传输*/
                    }
                    //noToken 默认为false，当为true时，则不传输token
                    if (opt.noToken == false || !opt.noToken) {
                        headers.Authorization = "Bearer " + cookie.getToken();
                    }
                    headers = $.extend({}, opt.headers, headers);
                    var _opt = $.extend({}, opt, {
                        headers: $.extend({}, opt.headers, headers)
                    });

                    // 备份opt中error和success方法
                    var fn = {
                        error: function (XMLHttpRequest, textStatus, errorThrown) {
                        },
                        success: function (data, textStatus) {
                        }
                    }
                    if (opt.error) {
                        fn.error = opt.error;
                    }
                    if (opt.success) {
                        fn.success = opt.success;
                    }
                    // 扩展增强处理
                    _opt = $.extend({}, _opt, {
                        error: function (XMLHttpRequest, textStatus, errorThrown) {
                            //TODO 需判断是否token失效，和API权限是否验证通过
                            fn.error(XMLHttpRequest, textStatus, errorThrown);
                        },
                        success: function (data, textStatus) {
                            fn.success(data, textStatus);
                        },
                        complete: function (XHR, TS) {
                            // 请求完成后回调函数 (请求成功或失败之后均调用)。
                        }
                    });
                    return _ajax(_opt);
                };
            }
        };

        ht_ajax.init();
        exports('ht_ajax', ht_ajax);
    }
);