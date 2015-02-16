## HTTP API文档	总览

### [文件上传](file.md)

### [用户相关](user.md)


***

## 意见反馈

#### URL
	/api/feedback

#### METHOD
	POST(必须是POST)

#### 参数

* **token**：令牌（可选项）
* **suggestion**: 内容（必需项）

#### 响应结果说明

**节点说明**：

* code：表示响应结果状态，1表示成功，0表示失败
* message：响应结果的文字说明

##### 简要示例

	{
	  "code": 1,
	  "message": "意见反馈成功"
	}


***

## 版本更新检查

#### URL
	/api/version/check

#### METHOD
	GET

#### 参数

* **version**：当前客户端版本号（必需项）
* **client**: 客户端类型, 可选值只能是android和iphone（必需项）

#### 响应结果说明

**节点说明**：

* code：表示响应结果状态，1表示有更新，0表示无更新
* message：响应结果状态的文字说明

**datum节点说明**:

* message: 更新说明
* url: 新版本下载地址
* version: 新版本号

##### 简要示例

	{
       "code": 1,
       "datum": {
           "message": "修复bug",
           "url": "http://mlongbo.com/android_0_1_1.apk",
           "version": "0.1.2"
       }
    }

***

## 附录(很重要)

### 文件地址说明

接口中所有的文件地址都是相对路径, 需要拼接地址前缀才能正常使用.

在登录成功后,登录接口返回的`resourceServer`字段为地址前缀.

如: resourceServer为`http://mlongbo.com`, 用户头像地址为`/img/avatar/rose.jpg`,
那么完整的地址就是`http://mlongbo.com/img/avatar/rose.jpg`

### 时间戳说明

文档中提到的时间戳全部精确到毫秒

### code对照表

* 1 ok - 成功状态
* 0 faild
* 2 argument error - 表示请求参数值有误, 或未携带需要的参数值
* 3 帐号已存在
* 4 验证码错误
* 500 error - 服务器错误
* 404 not found - 请求的资源或接口不存在
* 422 token error - 未传递token参数,或token值非法