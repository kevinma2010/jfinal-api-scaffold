## 文件上传

### 接口说明

文件上传是一个通用的总接口，凡是涉及文件上传操作的功能，请先调用文件上传接口，返回正确文件访问路径后，再提交纯文本表单。即提交文本表单时，用户头像、图片相关的表单字段，为上传接口返回的URL值。

### url
	/api/fs/upload

### method
	post

### 参数

* token：必要参数，为空或错误将不能上传。token可以从登录后的用户信息里获得(不登录是无法获取上传权限的)

该接口支持单文件及多文件上传, 每个文件对应一个请求参数, 如: file1对应a.jpg, file2对应b.jpg

### 响应结果说明

**节点说明**：

* code：表示响应结果状态，1表示成功，0表示有一或多个文件上传失败
* message：响应结果的文字说明
* failed: 此字段标记上传失败的请求参数名称(名称对应上传时所传递的文件),如: ['file1','file2']
* datum: 此字段返回了上传成功的文件所对应的文件地址, key为上传时传递的请求参数, value为文件地址

#### 上传成功

	{
		code: 1,
		datum: {
			fileUpload1: "/imgs/2015/02/01/20131117223307_JMMX5.thumb.700_0.jpeg",
			fileUpload2: "/imgs/2015/02/01/shortcut.png"
		}
	}

#### 包含上传失败的文件

	{
		code: 0,
		failed: ['fileUpload1']
	}

#### 请求中未包含文件

	{
		message: "uploadFileName can not be null",
		code: 2
	}