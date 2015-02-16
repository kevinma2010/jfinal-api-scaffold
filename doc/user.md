## account

### 检查账号是否被注册

#### URL
	/api/account/checkUser

#### METHOD
	GET

#### 参数
* **loginName**:唯一登录名, 即手机号(必填项)

#### 响应

##### 结果说明

code值含义:

* 1表示已被注册
* 0表示未被注册

##### 简要示例

	{
		"code":1,
		"message": "registered"
	}
	
	
### 发送手机验证码

#### URL
	/api/account/sendCode

#### METHOD
	POST(推荐)或GET

#### 参数
* **loginName**:唯一登录名, 即手机号(必填项)

#### 响应

##### 结果说明

code值含义:

* 1表示已发送
* 3表示该手机已被注册
* 0表示发送失败

##### 简要示例

	{
		"code":1,
		"message": "验证码已发送"
	}
	

### 注册

#### URL
	/api/account/register

#### METHOD
	POST

#### 参数
* **loginName**:唯一登录名, 即手机号码(必填项)
* **password**:密码(必填项)
* **code**: 手机验证码(必需项)
* **nickName**：用户昵称(必填项)
* **avatar**:用户头像url(选填项)

#### 响应
	{
		"code":1,
		"message": "注册成功"
	}
	
##### 结果说明
	code为1表示注册成功，如果用户头像URL为空，则会使用默认的用户头像。code不为1表示注册失败


### 登录

#### URL
	/api/account/login

#### METHOD
	POST

#### 参数

* **loginName**：用户登录名, 即手机号码（必需项）
* **password**：登录密码（必需项）

#### 登录成功后响应如下：

	{
		message: "login success",
		constant: {
			resourceServer: "http://mlongbo.com/upload"
		},
		token: "9afe4bda832c4b7aa0d255f6bc86486cJ96XKU",
		code: 1,
		info: {
			sex: 1,
			status: 1,
			avatar: "/imgs/avatar.jpg",
			creationDate: 1422782128161,
			nickName: "Longbo Ma",
			email: "mlongbo@gmail.com",
			userId: "f0f504f5710946e5b0a1d6a1acde4210"
		}
	}
	
##### 结果说明

如果code值是0，则可能没有其他节点。token值是全局的，请求请他接口，必须带这个值，否则接口权限将会拒绝访问，通过token值可以获取当前用户ID，即不用再提交用户ID。

**主节点说明**：

* code：表示响应结果状态，1表示成功，0表示用户名或密码错误
* message：响应结果的文字说明
* token: 接口口令，与该用户对应
* info：用户信息
* constant: 一些固定变量 

**info节点说明**：

* userId：用户ID
* nickName：昵称
* email：邮箱
* avatar: 头像地址
* sex: 性别, 0表示女, 1表示男
* status: 账号状态, 1表示启用(正常使用), 0表示禁用
* creationDate: 注册时间

**constant节点说明**:

* resourceServer: 文件下载地址前缀，后接文件地址即可使用

***

### 查询用户资料

#### URL
	/api/account/profile

#### METHOD
	GET(必须是get方法，否则请求出错)

#### 参数

* token：令牌符（必需项）
* userId：要查询的用户ID（可选项，如果没有则查询当前用户的信息）


#### 响应
##### 结果说明

**节点说明**：

* code：表示响应结果状态，1表示查询成功，0表示未查询到信息
* message：响应结果的文字说明

**datum节点说明**：

* userId：用户ID
* nickName：昵称
* email：邮箱
* avatar: 头像地址
* sex: 性别, 0表示女, 1表示男
* status: 账号状态, 1表示启用(正常使用), 0表示禁用
* creationDate: 注册时间

##### 简要示例

	{
	  "code": 1,
      "datum": {
			sex: 1,
			status: 1,
			avatar: "/imgs/avatar.jpg",
			creationDate: 1422782128161,
			nickName: "Longbo Ma",
			email: "mlongbo@gmail.com",
			userId: "f0f504f5710946e5b0a1d6a1acde4210"
	    }
	}

### 修改资料

#### 说明

除token必须以外，修改哪个参数，就传递哪个参数

#### URL
	/api/account/profile

#### METHOD
	PUT

#### 参数

* token：令牌符（必需项）
* nickName：姓名（可选项）
* sex：姓名（可选项）
* avatar：姓名（可选项）
* email：邮箱（可选项）


#### 响应结果说明

* code：表示响应结果状态，1表示成功，0表示失败
* message：响应结果的文字说明

##### 简要示例

	{
	  "code": 1,
      "message": "修改成功"
	}

*** 

### 修改密码

#### URL
	/api/account/password

#### METHOD
	PUT

#### 参数

* token：令牌符（必需项）
* oldPwd：旧密码（必需项）
* newPwd：新密码（必需项）

#### 响应结果说明

* code：表示响应结果状态，1表示成功，0表示失败
* message：响应结果的文字说明

##### 简要示例

	{
	  "code": 1,
      "message": "修改成功"
	}
	
***

### 修改头像

#### URL
	/api/account/avatar

#### METHOD
	PUT

#### 参数

* **token**：令牌（必需项）
* **avatar**：头像文件地址，请先使用文件上传接口获取上传后的访问路径（必需项）

#### 响应结果说明

* code：表示响应结果状态，1表示成功，0表示失败
* message：响应结果的文字说明

##### 简要示例

	{
	    "code": 1,
	    "message": "update avatar success"
	}