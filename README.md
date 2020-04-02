# EasyHttp
#### 简介：
基于kotlin、retrofit、okhttp简单整合的Android链式网络请求框架。
#### 特性：
1、支持restful风格api接口请求，支持get、post、put、delete请求方式；
2、支持表单格式和多请求体格式请求；
3、支持文件断点下载、文件保存进度获取、中断文件下载；
4、支持多baseUrl的请求；
5、分层设计。请求数据装载层、网络请求层、数据解析层分离；
6、网络层设计统一的接口，目前已内置retrofit，可加入其他网络框架作为网络请求层；
7、文件下载使用okio来进行文件读写，可以自定义其他框架来读写文件；
### 一、添加依赖 
```
implementation 'com.nick.common:easyhttp:1.0.9'
```
### 二、开始使用
   
