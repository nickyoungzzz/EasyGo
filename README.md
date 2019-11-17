# EasyHttp
基于rxjava、retrofit、okhttp简单整合的Android网络请求链框架  
### 一、初始化（在application中）  
Java：  
``` 
EasyHttp.configEasyHttp(new HttpConfig() {

            @NotNull
            @Override
            public OkHttpClient okHttpConfig(@NotNull OkHttpClient.Builder okHttpBuilder) {
                return super.okHttpConfig(okHttpBuilder);
            }

            @NotNull
            @Override
            public String baseUrl() {
                return super.baseUrl();
            }
        });
```

Kotlin：
```
configEasyHttp(object : HttpConfig() {
			
			override fun okHttpConfig(okHttpBuilder: OkHttpClient.Builder): OkHttpClient {
				return super.okHttpConfig(okHttpBuilder)
			}

			override fun baseUrl(): String {
				return super.baseUrl()
			}
		})
   ```
   初始化时可配置自己的OKhttpClient和baseURL  
   ### 二、开始使用
   
