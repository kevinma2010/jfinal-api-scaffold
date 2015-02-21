# jfinal-api-scaffold

### 项目介绍

实际上这个项目更像一个脚手架，是我多次开发HTTP API应用的经验总结。其中包含了常用的模块（如账户相关，版本更新等），以及本人认为比较好的开发方式和规范。

### 项目配置

* **version.xml:** 存放版本更新信息。entry节点代表一个版本，可设置一到多个，可自由切换。android和iphone节点的default属性表示当前的版本号, 对应entry的version节点值；

* **jdbc.properties:** 这个都懂的，存放数据库连接信息；
* **configure.xml:** root下的子节点可以随便写，但不能包含属性。在服务器运行过程中，一旦此文件内容发生了变化，会实时生效，无需重启。

举例，从下面的文件中获取prefix节点的值可使用`AppProperty.me().getProperty("resource.prefix");`读取.
```xml
<?xml version="1.0" encoding="UTF-8"?>
<root>
    <resource>
        <!--文件下载的地址前缀-->
        <prefix>http://mlongbo.com/upload</prefix>
    </resource>
</root>
```

AppConstant类存放诸如`resource.prefix`的配置常量, AppProperty类用于读取配置，因此建议使用如: 

```java
//AppContant类的伪代码
public static final String RES_PREFIX = "resource.prefix";

//AppProperty类的伪代码
public String resourcePrefix() {
    return getProperty(AppConstant.RES_PREFIX);
}

//业务模块中调用。获取配置
String str = AppProperty.me().resourcePrefix();
```

### 已实现的常用接口列表

* 检查账号是否被注册: `GET` `/api/account/checkUser`
* 发送注册验证码: `POST` `/api/account/sendCode`
* 注册: `POST` `/api/account/register`
* 登录： `POST` `/api/account/login`
* 查询用户资料: `GET` `/api/account/profile`
* 修改用户资料: `PUT` `/api/account/profile`
* 修改密码: `PUT` `/api/account/password`
* 修改头像: `PUT` `/api/account/avatar`
* 意见反馈: `POST` `/api/feedback`
* 版本更新检查: `GET` `/api/version/check`
* 文件上传: `POST` `/api/fs/upload`

### 数据响应规范

避免手拼json导致的错误，而使用将Java Bean序列化为JSON的方式。

json数据的根节点使用code字段标识本次响应的状态，如成功、失败、参数缺少或参数值有误，以及服务器错误等；message节点包含服务器响应回来的一些提示信息，主要是方便客户端开发人员在对接接口时定位错误出现的原因。

一般的，响应数据会分为两种，第一种为列表数据，如一组用户信息，另外一种是实体信息，如一条用户信息。data字段值为数组，携带列表数据，datum字段值为json对象，携带实体数据。

如:
```javascript
//实体数据, 此结构对应DatumResponse类
{
  "code": 1,
  "message": "成功查询到该用户信息",
  "datum": {
    "name": "jack",
  "lover": "rose",
  "sex": 1,
  "email": "jack@gmail.com"
  }
}
//列表数据, 此结构对应DataResponse类
{
  "code": 1,
  "message": "成功查询到两条用户信息",
  "data": [{
    "name": "jack",
  "lover": "rose",
  "sex": 1,
  "email": "jack@gmail.com"
  },{
    "name": "rose",
  "lover": "jack",
  "sex": 0,
  "email": "rose@gmail.com"
  }]
}
//登录成功, 此结构对应LoginResponse类
{
  "code": 1,
  "message": "登录成功",
  "constant": {
    "resourceServer": "http://fs.mlongbo.com" //文件地址前缀
  }
  "info": {
    "name": "jack",
  "lover": "rose",
  "sex": 1,
  "email": "jack@gmail.com"
  }
}
//多文件上传, 部分成功，部分失败. 此结构对应FileResponse类
{
  "code": 0, //只要有一个文件上传失败，code就会是0
  "failed": ["file3"]
  "datum": {
    "file1": "/upload/images/file1.jpg",
  "file2": "/upload/images/file2.jpg"
  }
}
//缺少参数
{
  "code": 2,
  "message": "缺少name参数"
}
//参数值有误
{
  "code": 2,
  "message": "sex参数值只能为0或1"
}

//token无效
{
  "code": 422,
  "message": "token值是无效的，请重新登录获取"
}
```

附上本人常用的几种code值:


* 1 ok - 成功状态。查询成功，操作成功等；
* 0 faild - 失败状态
* 2 argument error - 表示请求参数值有误, 或未携带必需的参数值
* 3 帐号已存在
* 4 注册验证码错误
* 500 error - 服务器错误
* 404 not found - 请求的资源或接口不存在
* 422 token error - 未传递token参数,或token值非法

### 请求参数校验

最多的还是非空检查, 这里重点说一下。因此，我写了一个工具。使用方法如下：

```java
String name = getPara("name");
String lover = getPara("lover");
//使用此方式的前提是当前controller类要继承自BaseAPIController类
if (!notNull(Require.me().put(name, "name参数不能为空").put(lover,"lover参数不能为空"))) {
  return;
}

//效果等同于如下代码:
if (StringUtils.isEmpty(name)) {
  renderJson(new BaseResponse(2, "name参数不能为空"));
  return;
}
if (StringUtils.isEmpty(lover)) {
  renderJson(new BaseResponse(2, "lover参数不能为空"));
  return;
}

```
```javascript
//如果没有传递name参数，将会得到如下响应:
{
  "code": 2,
  "message": "name参数不能为空"
}
```

### 已实现的公共模块

公共模块实现了基本功能，你可以根据自己的业务需求自由调整数据字段。

#### Token模块

token, 顾名思义, 表示令牌，用于标识当前用户，同时增加接口的安全性。目前不支持过期策略，也仅支持一个用户一个终端的方式，即用户在一处登录后，再在另一处登录会使之前登录的token失效。

要启用token功能只需要配置TokenInterceptor拦截器类即可。

在使用时，客户端必须在配置了拦截器的接口请求中携带名为"token"的请求参数。

服务端在继承了BaseAPIController类后可以直接调用`getUser();`函数获取当前用户对象. **注意: ** 为了正常地使用getUser函数，必须在登录接口中查出用户对象后，使用类似如下代码建立token与用户对象的映射：
```java
User nowUser = User.user.findFirst(sql, loginName, StringUtils.encodePassword(password, "md5"));

//之后要将token值响应给客户端
String token = TokenManager.getMe().generateToken(nowUser));
```

#### 文件上传模块

在以往的接口开发过程中，我们都是使用一个统一的文件上传接口上传文件后，服务器响应上传成功后的文件地址，客户端再使用业务接口将文件地址作为参数值发送到服务器。这样做的好处之一是便于服务端将文件统一管理，比如做缓存或CDN；另一方面是为了减小耦合度，比如此时要换成七牛CDN存放静态文件，客户端只需要改写文件上传部分的代码即可。

目前的文件上传接口已实现一或多个文件同时上传，客户端在上传时，必须要为每个文件指定一个请求参数名, 参数名用于在上传结束后，根据服务器的响应数据判断哪些文件是上传失败的，哪些是上传成功的，以及成功后的文件地址是什么。

服务器响应实例如下：
```javascript
//全部上传成功
{
  "code": 1,
  "message": "success",
  "datum": {
    "file1": "/upload/images/file1.jpg",
  "file2": "/upload/images/file2.jpg"
  }
}
//全部上传失败
{
  "code": 0,
  "message": "failed",
  "failed": ["file1", "file2"]
}
//部分成功，部分失败
{
  "code": 0, //只要有一个文件上传失败，code就会是0
  "failed": ["file3"]
  "datum": {
    "file1": "/upload/images/file1.jpg",
  "file2": "/upload/images/file2.jpg"
  }
}
```

#### 版本更新和意见反馈

关于版本更新的说明，请查看第一章节中的项目配置，以及API文档；

意见反馈模块比较简单，你可以根据你的业务需求改动数据库字段，以及接口参数。

#### 用户账号模块

这个模块目前实现的接口有：

* 检查账号是否已注册
* 发送手机验证码
* 注册
* 登录
* 查询用户资料
* 修改用户资料
* 修改密码

如果使用手机验证码功能，你需要改写SMSUtils类的sendCode函数以实现短信发送功能；如果不使用手机验证码功能，可以在注册的接口代码中将验证码检查的功能去掉。在开发调试的过程中，可以访问code.html查询手机验证码。

如果用户表中的字段不能满足你的业务需求，你可以自由增删修改，但同时也需要修改注册和修改用户资料接口。

#### 工具

* Jetty插件: 无需使用tomcat，直接使用maven的jetty插件启动项目；
* ant工具： 一般情况下，我们的项目是在服务端使用maven自动构建的，但在开发过程中，代码经常改变需要重新部署，如果重新打包更新又比较麻烦，因此在服务端使用maven命令重新构建后，可直接执行ant命令将已改动的文件copy到tomcat应用目录。所以，若想正常使用该工具，你需要修改build.xml，将tomapp值修改为你的tomcat应用路径。

### 资源

[现有的API接口文档](doc/index.md)


## Copyright & License

Copyright (c) 2015  Released under the [MIT license](LICENSE).
