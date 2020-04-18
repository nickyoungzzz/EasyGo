# EasyHttp
#### 简介：
基于kotlin、retrofit、okhttp简单整合的Android链式网络请求框架。
#### 特性：
1、支持restful风格api接口请求，支持get、post、put、delete请求方式；

2、支持表单格式和多请求体格式请求；

3、支持文件断点下载、文件保存进度获取、中断文件下载；

4、支持多baseUrl的请求；

5、分层设计。请求数据装载层、网络请求层、数据解析层分离；

6、网络层设计统一的接口，目前已内置OkHttp，HttpsUrlConnection，可加入其他网络框架作为网络请求层；

7、文件下载使用okio来进行文件读写，可以自定义其他框架来读写文件；
### 一、添加依赖 
```
implementation 'com.nick.common:easyhttp:1.1.2'
```
### 二、开始使用
#### 1、普通请求
```
val url = "https://www.baidu.com"
url.post()
   .addQuery("key1", "value1") // 添加查询1
   .addQuery("key2", "value2") // 添加查询2
   .addHeader("Content-Type", "application/json") // 添加头
   .addJsonString("{\"account\":\"LiSi\", \"password\":123456}") // 添加json请求体
   .setHttpInterceptor(object : HttpInterceptor {
	// 请求之前
	override fun beforeExecute(httpReq: HttpReq): HttpReq {
		return httpReq
	}

	// 请求之后
	override fun afterExecute(httpReq: HttpReq, httpResp: HttpResp): HttpResp {
		return httpResp
	}
}).execute { data: String -> data } // 数据转换，原始类型为String，可转为其他类型				
```
#### 2、文件下载
```
val url = "http://192.168.0.103:8080/docs/1.zip"
url.post()
val url = "https://www.baidu.com"
url.post()
   .addQuery("key1", "value1") // 添加查询1
   .addQuery("key2", "value2") // 添加查询2
   .asDownload()
   .addHeader("Content-Type", "application/json") // 添加头
   .addJsonString("{\"account\":\"LiSi\", \"password\":123456}") // 添加json请求体
   .setHttpInterceptor(object : HttpInterceptor {
	// 请求之前
	override fun beforeExecute(httpReq: HttpReq): HttpReq {
		return httpReq
	}

	// 请求之后
	override fun afterExecute(httpReq: HttpReq, httpResp: HttpResp): HttpResp {
		return httpResp
	}
}).execute { current, total, finished, canceled -> {}）// current为当前进度、total为文件大小、finished为是否完成、canceled为是否取消
```
### 三、注意事项
1、上述请求及文件下载需要在子线程中进行、可使用RxJava、kotlin协程（推荐）、thread中进行；

2、普通请求得到的是一个HttpResult对象，里面是包含请求结果的封装，结果泛型类型取决于当时解析时转换的数据类型；

3、文件下载最终需在回调中进行，回调方法中包含下载进度的信息；

4、该请求在java和kotlin环境中均可使用，调用方式有所不同；

5、更多详情可实际使用时查看源码可得知；
