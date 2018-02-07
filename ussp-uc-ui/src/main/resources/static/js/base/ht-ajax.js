/**
 * 重写ajax请求
 * by 谭荣巧 2018-02-06
 */
(function ($) {
    var loginUrl = "/login.html",
        app = "",
        refreshTokenUrl = "http://localhost:30111/uaa/auth/token",
        isLocal = false;
    /**
     * 验证token是否有效，如果无效则重新刷新token，如果无法拉取则重新登录
     * @returns {boolean}
     */
    var validationAndRefreshToken = function (noToken) {
        var token = getToken();
        var refreshToken = getRefreshToken();
        // 如果refreshToken失效，则直接跳转到登录页面
        if (refreshToken == null || refreshToken == "") {
            layer.confirm('登录超时，请重新登录。', function (index) {
                top.location.href = loginUrl;
                layer.close(index);
            });
            return false;
        } else {
            //延长refreshToken时间
            setRefreshToken(refreshToken);
        }
        // 验证token是否失效，失效则重新请求后台更新token，如果无法更新，则重新登录
        if ((token == null || token == "") && (noToken == false || !noToken)) {
            //console.debug("token无效，重新加载token");
            $.ajax({
                type: 'GET',
                async: false,
                url: refreshTokenUrl,
                headers: {
                    Authorization: "Bearer " + refreshToken
                },
                noToken: true,
                success: function (data) {
                    if (data == null || data["token"] == null || data["token"] == "") {
                        layer.confirm('登录超时，请重新登录。', function (index) {
                            top.location.href = loginUrl;
                            layer.close(index);
                        });
                        return false;
                    } else {
                        setToken(data["token"]);
                        return true;
                    }
                },
                error: function () {
                    layer.confirm('登录超时，请重新登录。', function (index) {
                        top.location.href = loginUrl;
                        layer.close(index);
                    });
                    return false;
                }
            });
        }
        return true;
    }

    // 备份jquery的ajax方法
    var _ajax = $.ajax;

    // 重写jquery的ajax方法
    $.ajax = function (opt) {
        if (!isLocal) {
            //所有ajax请求前先验证token是否存在
            var isValidation = validationAndRefreshToken(opt.noToken);
            if (!isValidation) {
                return false;
            }
        }

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
        $.extend(opt, {
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    //TODO 需判断是否token失效，和API权限是否验证通过
                    fn.error(XMLHttpRequest, textStatus, errorThrown);
                },
                success: function (data, textStatus) {
                    if (!isLocal && data != null) {
                        switch (data["status_code"]) {
                            case "9921"://TOKEN过期
                            case "9922"://验签失败
                            case "9924"://授权失败
                            case "9925"://TOKEN不能为空
                                layer.confirm(data['result_msg'] + '，请重新登录。', function (index) {
                                    top.location.href = loginUrl;
                                    layer.close(index);
                                });
                                return false;
                            case "9926":
                                layer.alert("对不起，您没有该功能的操作权限，如有疑问，请联系管理员。");
                                return false;
                        }
                    }
                    fn.success(data, textStatus);
                },
                complete: function (XHR, TS) {
                    // 请求完成后回调函数 (请求成功或失败之后均调用)。
                }
            }
        );
        if (!isLocal) {
            var headers = {
                app: app /*系统编码统一通过http headers进行传输*/
            }
            //noToken 默认为false，当为true时，则不传输token
            if (opt.noToken == false || !opt.noToken) {
                headers.Authorization = "Bearer " + getToken();
            }
            headers = $.extend({}, opt.headers, headers);
            $.extend(opt, {
                headers: $.extend({}, opt.headers, headers)
            });
        }
        return _ajax(opt);

    };
})(jQuery);