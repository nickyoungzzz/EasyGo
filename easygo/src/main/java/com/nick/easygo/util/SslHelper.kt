package com.nick.easygo.util

import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

class SslHelper {
	companion object {
		//获取这个SSLSocketFactory
		fun getSSLSocketFactory(): SSLSocketFactory {
			try {
				val sslContext = SSLContext.getInstance("SSL")
				sslContext.init(null, arrayOf(getTrustManager()), SecureRandom())
				return sslContext.socketFactory
			} catch (e: Exception) {
				throw RuntimeException(e)
			}
		}

		//获取TrustManager
		fun getTrustManager(): X509TrustManager {
			return object : X509TrustManager {
				override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
				}

				override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
				}

				override fun getAcceptedIssuers(): Array<X509Certificate> {
					return arrayOf()
				}
			}
		}

		//获取HostnameVerifier
		fun getHostnameVerifier(): HostnameVerifier {
			return HostnameVerifier { _, _ -> true }
		}
	}
}