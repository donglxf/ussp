## 升级说明
### 0.0.1
### 0.0.2
### 0.0.3
### 0.0.4
新增LoginUserInfoHelper类，用于获取登录用户信息，使用方法如下：
1. 首先注入：LoginUserInfoHelper 对象，如下：
2. 获取登录用户信息的方法：
``` java
LoginInfoDto loginInfo = loginUserInfoHelper.getLoginInfo();
```
###0.0.5
修复一些BUG
###0.0.6
1. 修改ResourceApiSynchHelper类，修复API资源同步到数据时，有些API的URL尾部多拼接了一个"/"的问题。
2. 新增GatewayURLHelper类，该类提供了一个API（/common/config.js），通过该API可以通过application.yml配置文件动态配置网关地址。
- a)首先前端项目增加ussp-common的依赖；
- b)在启动类里面增加com.ht.ussp.bean包的扫描；
    ``` java
    @SpringBootApplication
    @ComponentScan(basePackages = {"com.ht.ussp.bean"})
    public class UcUiApplication {
        public static void main(String[] args) {
            SpringApplication.run(UcUiApplication.class, args);
        }
    }
    ```

- c)在application.yml增加如下配置：
    ``` yml
    ht.config.ui:
      gatewayUrl: http://localhost:30111/
    ```

- d)在前端页面引用/common/config.js

    ``` javascript 
    <script src="/common/config.js"></script>
    ```

- e)在js里面通过gatewayUrl这个变量就可以获取动态的网关配置了，具体可以查看ussp-uc-ui里面ht_config.js的使用


### 0.0.7
UCClient新增两个接口：
1. 获取用户自定义权限列表
``` java
 @GetMapping(value = "/auth/getCustomResouce")
 Result getCustomResouce(@RequestParam("userId") String userId,@RequestParam("app") String app);
```
2. 验证用户自定义权限
``` java
 @GetMapping(value = "/auth/IsHasCustomResouce")
 Boolean IsHasCustomResouce(@RequestParam("userId") String userId,@RequestParam("rescode") String rescode,@RequestParam("app") String app);
```
### 0.0.8
UCClient新增两个接口：
1. LoginUserInfoHelper#getLoginInfo()方法的返回值，增加机构名称。

### 0.0.9
1. LoginUserInfoHelper#getLoginInfo()方法的返回值，增加信贷钉钉对照数据（userId和orgCode）。
2. API资源同步功能新增旧资源删除功能。

### 0.0.10
1. LoginUserInfoHelper#getLoginInfo()方法的返回值，增加信贷钉钉对照数据（controller Y:是管理员  N:不是管理员）。
2.增加MenuInfoHelper菜单管理，增加addMenu，修改updateMenu，禁用changeApiState(1，禁用，0，启用)功能。

### 0.0.11
1.MenuInfoHelper菜单管理，增加关联系统

### 0.0.12
1.MenuInfoHelper菜单管理，增加菜单修改返回值
### 0.0.13
1.OrgInfoHelper 增加获取用户所属片区 (10:公司 20:中心 30:片区 40:分公司 50部门 60小组)
### 0.0.14
1、config.js 新增instanceId和unInstanceId参数，通过bootstrap.yml的ht.config.ui.instanceId和ht.config.ui.unInstanceId进行配置

### 0.0.15
1、LoginUserInfoHelper 新增getUserInfoByUserId 根据userId获取用户信息 ,若userId不为空则按userId查询,userId为空则根据bmUserId查询,

### 0.0.16
1.新增外部系统修改密码，以及修改登录状态码
2.修复新增菜单出错
3. 修复登录加密算法
4.增加信贷获取token接口，加密解密工具类
5.增加获取指定时间更新的机构信息，人员信息
6.获取所有系统接口

###0.0.26
1.添加短信发送和验证工具类
2.添加调用外部用户认证接口bean
3.添加短信验证码标识

###0.0.27
增加业务组织机构类型接口 BusinessHelper  
1.根据用户id,手机号，用户姓名，email获取用户机构信息   getUserOrg
2.根据用户id,手机号，用户姓名，email获取用户角色信息  getBusiUserRole
3.根据用户id,手机号，用户姓名，email获取用户岗位信息 getBusiUserPosition
4.获取所有下级机构信息  getSubOrgInfoByCode
5.获取业务机构  getBusiOrgList

###0.0.28
增加业务组织机构类型接口 BusinessHelper  
1. 获取指定业务机构下用户列表 getUserListByBusiOrg
2.查询角色下所有用户 getUserInfoForRole

###0.0.29
1.获取登录接口信息增加分公司 业务组织机构代码  片区 是否是总部的标记 LoginUserInfoHelper#getLoginInfo()
2.新增BusinessHelper getBusiOrgInfoByOrgType 获取指定业务机构所属的 片区，分公司 (20-公司层级  40-片区层级  60-分公司层级  80-部门层级  100-小组层级)
