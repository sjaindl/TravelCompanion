package com.sjaindl.travelcompanion.api

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class HttpClientBuilder {
    private var logging: LoggingConfig? = null

    private var userAgent: String? = null

    private var installJsonSerializer = false
    private var enableJsonPrettyPrinting = false
    private var disableJsonHtmlEscaping = false

    private var userName: String? = null
    private var password: String? = null
    private var addBasicAuth = false

    fun withLogging(
        logger: Logger = Logger.DEFAULT,
        logLevel: LogLevel = LogLevel.INFO,
    ): HttpClientBuilder {
        this.logging = LoggingConfig(logger, logLevel)

        return this
    }

    fun withJsonSerialization(
        prettyPrinting: Boolean = true,
        disableHtmlEscaping: Boolean = true,
    ): HttpClientBuilder {
        installJsonSerializer = true
        enableJsonPrettyPrinting = prettyPrinting
        disableJsonHtmlEscaping = disableHtmlEscaping

        return this
    }

    fun withBasicAuth(userName: String?, password: String?): HttpClientBuilder {
        addBasicAuth = true
        this.userName = userName
        this.password = password

        return this
    }

    fun build(): HttpClient {
        return HttpClient {
            logging?.let {
                install(Logging) {
                    logger = it.logger
                    level = it.logLevel
                }
            }

            if (addBasicAuth) {
                val credentials = getBasicAuthCredentials()

                if (credentials != null) {
                    install(Auth) {
                        basic {
                            sendWithoutRequest { true } // Do not wait for preflight 401 response
                            credentials { credentials }
                        }
                    }
                }
            }

            userAgent?.let {
                install(UserAgent) {
                    agent = it
                }
            }

            if (installJsonSerializer) {
                install(ContentNegotiation) {
                    json(Json {
                        prettyPrint = enableJsonPrettyPrinting
                        ignoreUnknownKeys = true
                        isLenient = true
                    })
                }
            }
        }
    }

    private fun getBasicAuthCredentials(): BasicAuthCredentials? {
        val userName = userName ?: return null
        val password = password ?: return null

        return BasicAuthCredentials(userName, password)
    }
}

private data class LoggingConfig(val logger: Logger, val logLevel: LogLevel)
