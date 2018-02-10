## 升级说明
###0.0.1
###0.0.2
###0.0.3
###0.0.4
新增LoginUserInfoHelper类，用于获取登录用户信息，使用方法如下：
1、首先注入：LoginUserInfoHelper 对象，如下：
2、获取登录用户信息的方法：
``` java
LoginInfoDto loginInfo = loginUserInfoHelper.getLoginInfo();
```
###0.0.5
修复一些BUG
###0.0.6
1、修改ResourceApiSynchHelper类，修复API资源同步到数据时，有些API的URL尾部多拼接了一个"/"的问题。
2、新增GatewayURLHelper类，该类提供了一个API（/common/config.js），通过该API可以通过application.yml配置文件动态配置网关地址。
a)首先前端项目增加ussp-common的依赖；
b)在启动类里面增加com.ht.ussp.bean包的扫描；
``` java
@SpringBootApplication
@ComponentScan(basePackages = {"com.ht.ussp.bean"})
public class UcUiApplication {
    public static void main(String[] args) {
        SpringApplication.run(UcUiApplication.class, args);
    }
}
```

c)在application.yml增加如下配置：
``` yml
ht.config.ui:
  gatewayUrl: http://localhost:30111/
```

d)在前端页面引用/common/config.js

``` javascript 
<script src="/common/config.js"></script>
```

e)在js里面通过gatewayUrl这个变量就可以获取动态的网关配置了，具体可以查看ussp-uc-ui里面ht_config.js的使用
###0.0.7
UCClient新增两个接口：
1、获取用户自定义权限列表
``` java
 @GetMapping(value = "/auth/getCustomResouce")
 Result getCustomResouce(@RequestParam("userId") String userId,@RequestParam("app") String app);
```
2、验证用户自定义权限
``` java
 @GetMapping(value = "/auth/IsHasCustomResouce")
 Boolean IsHasCustomResouce(@RequestParam("userId") String userId,@RequestParam("rescode") String rescode,@RequestParam("app") String app);
```