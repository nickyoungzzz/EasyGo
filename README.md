# EasyHttp
#### 简介：
基于Kotlin、OkHttp、HttpsUrlConnection简单整合的Http链式请求框架。
#### 特性：
1、支持restful风格api接口请求，支持get、post、put、delete、head、patch请求方式；

2、请求数据格式支持Form表单、MultiPart多请求体、ResquestBody；

3、支持文件断点下载、下载进度获取、中断文件下载；

4、支持多项全局配置。如代理、超时时间、dns解析、HTTPS验证SSL等；

6、支持配置全局的拦截器，也可以为每个请求单独设置拦截器；

5、分层设计。请求数据装载层、网络请求层、数据解析层分离；

6、网络层设计统一的接口，目前已内置OkHttp，HttpsUrlConnection，可实现IHttpHandler使用其他网络框架作为请求层；

7、可在全局配置网络请求层的Handler，也可为每个请求单独配置请求层的Handler；

8、文件下载时文件保存使用okio来读写，可以实现IDownloadhandler自定义其他框架来读写文件；

### 一、添加依赖 
```
implementation 'com.nick.common:easyhttp:1.1.2'
```
### 二、使用方法
#### 1、全局配置
```
// 添加自定义配置
	val httpConfig = HttpConfig.Builder()
		.httpHandler(UrlConnectionHandler()) // 设置HttpHandler，已内置OkHttpHandler和UrlConnectionHandler
		.connectTimeOut(20000L) // 设置连接超时时间，默认15s，单位为毫秒
		.proxy(Proxy.NO_PROXY) // 设置代理，默认不需要代理
		.readTimeOut(20000L) // 设置读取时间，默认15s，单位为毫秒
		.dns { host: String -> InetAddress.getAllByName(host) } // 自定义dns， 默认为系统dns解析
		.hostNameVerifier(HostnameVerifier { hostname, session -> true }) // 自定义主机名验证，默认为不验证
		.sslSocketFactory(SslHelper.getSSLSocketFactory()) // 自定义证书，使用默认证书
		.interceptor { httpReq, httpResp -> run {
			println(httpReq.url) // 比如打印日志，url、header等请求信息
			httpResp // 最终返回一个HttpResp
		}} // 添加全局拦截器，在请求之后，并返回HttpResp
		.build()
	
	// 配置全局的HttpConfig，该行代码只可执行一次，可在项目初始化时执行。如果不配置，则使用默认该库中的默认配置。
	EasyHttp.init(httpConfig)
```
#### 2、发送请求
```
// 发起普通请求
	val httpResult: HttpResult = request.request()

	// 获取请求成功时的数据
	val success: Int? = httpResult.getSuccess { string: String -> string.length } // 转换器, success的类型跟实际转换器泛型类型相关
	val successString: String? = httpResult.getSuccessString() // 直接获取请求成功的信息，默认是String类型

	// 获取请求失败时的数据
	val error: Int? = httpResult.getError { string: String -> string.length } // 转换器, error的类型跟实际转换器泛型类型相关
	val errorString: String? = httpResult.getErrorString() // 直接获取请求失败的信息，默认是String类型

	// 获取请求异常时的数据
	val throwable = httpResult.getException { exception: Throwable? -> RuntimeException(exception) } // 转换为其他异常
	val exception = httpResult.getException() // 直接获取失败的异常，如SocketTimeoutException, ConnectionTimeOutException

	
// 进行文件下载
	val downloadUrl = "https://www.baidu.com/pic/girl.png"

	// 请求参数配置跟发起普通请求一样的
	downloadUrl.post()
		// 配置下载参数，文件保存位置和是否断点
		.asDownload(downloadParam = DownloadParam(File(""), false))
		.asDownload() // 不配置，文件位置为File("")，会报错
		.setDownloadHandler(object : IDownloadHandler {
			override fun saveFile(inputStream: InputStream, downloadParam: DownloadParam,
			                      contentLength: Long, listener: (state: DownloadState) -> Unit
			) {
				// 文件保存过程
			}

			override fun cancel() {
				// 取消保存
			}
		}) // 不配置，就默认使用OkIoDownloadHandler
		.execute(exc = fun(e: Throwable) { // 出现异常
			println(e) // 如打印异常
		}, download = fun(downloadState: DownloadState) { // 文件保存进度回调
			val current = downloadState.current // 当前大小
			val total = downloadState.total // 文件总大小
			val finished = downloadState.finished // 是否下载完成
			val canceled = downloadState.canceled // 是否被取消下载
		})			
```
### 三、注意事项
1、该库未加入线程调度机制，可使用RxJava、Kotlin协程（推荐）、ThreadPool开启的线程中进行；

2、普通请求得到的是一个HttpResult对象，里面是包含请求结果的封装，结果泛型类型取决于当时解析时转换的数据类型；

3、文件下载最终需在回调中进行，回调方法中包含下载进度的信息；

4、该库为纯Java Library，不受Android开发环境限制，可在任意Java/Kotlin开发环境使用，调用方式有所不同；

5、cookie和缓存的位置配置尚未接入，后续会陆续完善；

6、更多详情可实际使用时查看源码可得知；
