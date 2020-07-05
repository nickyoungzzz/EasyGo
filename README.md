# EasyHttp
#### 简介：
基于Kotlin、OkHttp、HttpsUrlConnection简单整合的Http链式调用框架。
#### 特性：
1、充分应用了Kotlin DSL语法模式，代码调用简洁易读，像写HTML界面文件一样写网络调用代码；

2、支持restful风格api接口请求，支持get、post、put、delete、head、patch、head请求方式；

3、请求数据格式支持Form表单格式、MultiPart多请求体格式、RJson请求体格式；

4、支持文件断点下载、下载进度监听、文件下载中断；

5、支持多项全局性配置，如代理、超时时间、dns解析、HTTPS验证SSL等等；

6、支持配置全局的拦截器，也可以为每个请求单独设置拦截器，对请求进行拦截配置；

7、框架进行了分层设计，请求数据装载层、网络请求层、数据解析层完全解耦分离；

8、网络请求层设计了统一的接口，目前已内置OkHttp，HttpsUrlConnection，可自主实现IHttpHandler使用其他网络框架作为HTTP请求层；

9、可在全局配置网络请求层的Handler（默认为OkHttpHandler，基于OkHttp），可以在全局配置，也可以在每次网络请求时自由切换；

10、文件下载时文件保存使用okio来读写，可以实现Downloadhandler自定义其他框架来进行文件的读写；

11、支持动态配置各个请求的各自的超时时间，全局一次配置，各个请求全都生效；

12、依赖库相当少，仅依赖OkHtt和Okio框架，接入成本较低；

......

### 一、添加依赖 
```
implementation 'com.nick.common:easyhttp:1.2.0'
```
### 二、使用方法
#### 1、全局配置
```
	// 配置全局的HttpConfig，该配置代码只可执行一次，可在项目初始化时执行
	EasyHttp.init {
		httpHandler(HttpHandler.OK_HTTP_HANDLER)
		connectTimeOut(20000L) // 设置连接超时时间，默认15s，单位为毫秒
		readTimeOut(20000L) // 设置读取时间，默认15s，单位为毫秒
		proxy(Proxy.NO_PROXY) // 设置代理，默认不需要代理
		dns { host: String -> InetAddress.getAllByName(host) } // 自定义dns， 默认为系统dns解析
		hostNameVerifier { hostname, session -> true } // 自定义主机名验证，默认为不验证
		sslSocketFactory(SslHelper.getSSLSocketFactory()) // 自定义证书，使用默认证书
		beforeSend { httpReq -> httpReq } // 请求之前（全局配置，可对请求的信息进行调整，对所有请求生效）
		afterReply { httpReq, httpResp -> httpResp }// 请求之后（全局配置，对请求返回的信息进行调整，对所有请求生效）
		timeoutHandler { url, tag, method, headers -> TimeoutConfig() } // 有条件的进行超时配置
		build()
	}
```
#### 2、发送请求		
```
// 比如当前以post请求方式
	val result = httpPost {
		url("https://www.baidu.com/app/search") // 配置请求的url
		// 配置头
		head {
			// 配置header
			header {
				"header1" with "value1"
				"header2" with "value2"
			}
			// 配置url上的查询，url?query1=value1&query2=value2
			query {
				"query1" with "value1"
				"query2" with "value2"
			}
		}
		// 配置请求体
		body {
			// 配置表单数据
			form {
				"field1" with "value1"
				"field2" with "value2"
			}
			// 配置json请求体数据
			json("{\"name\":\"zhangsan\", \"password\":\"123456\"}")
			// 配置part数据
			part {
				"part1" with "value1"
				"part2" with "value2"
			}
			// 配置是否是多请求体方式，默认为false
			multi(true)
		}
	}.deploy {
		httpHandler(HttpHandler.OK_HTTP_HANDLER) // 配置网络层的HttpHandler
		tag("tag") // 配置请求的tag
		whenLaunch { httpReq -> httpReq } // 请求之前（单个配置，可对当前请求的信息进行调整，对单个请求生效）
		afterLaunch { httpReq, httpResp -> httpResp } // 请求之后（单个配置，对当前请求返回的信息进行调整，对单个请求生效）
		// 当前是否是下载文件，默认不是
		asDownload {
			source("C://file") // 文件下载位置
			breakpoint(true) // 是否断点，默认不断点
		}
	}.launch { // 若为下载，需调用download
		success { } // 请求成功之后的回调
		error { } // 请求失败之后的回调
		exception { } // 请求异常之后的回调
	}.analysis<Int, String, RuntimeException> {
		success { r -> r.length } // 请求成功时数据的转换（仅为示意）
		error { r -> r.substringAfterLast("/") } // 请求失败时数据的转换（仅为示意）
		exception { r -> RuntimeException(r) } // 请求异常时数据的转换（仅为示意）
	}.also {
		println(it.code) // http code
		println(it.success) // 请求成功转换后的数据
		println(it.error) // 请求失败转换后的数据
		println(it.headers) // 清求返回的header
		// ......
	}
	
```
### 三、注意事项
1、该库未加入线程调度机制，可使用RxJava、Kotlin协程（推荐）、ThreadPool开启的线程中进行网络请求的调用；

2、该库为纯Kotlin Library，不依赖Retrofit、不受Android开发环境限制，可在任意的Kotlin开发环境中使用；
