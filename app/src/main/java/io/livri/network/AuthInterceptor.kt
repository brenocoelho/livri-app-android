package io.livri.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

class AuthInterceptor : Interceptor {
    private var token: String ="Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJsaXZyaS5pbyIsImV4cCI6MTU5Mjk5NTU2MywiaWF0IjoxNTkwNTc2MzYzLCJpc3MiOiJsaXZyaS5pbyIsImp0aSI6IjRiNTljODZiLThkZGQtNDZhMS04NjU1LWE2MTNjYTQzMTk3YyIsIm5iZiI6MTU5MDU3NjM2Miwic3ViIjoiNTEzNzIzOWItMGYwZi00MzFjLTk4NmQtN2MyNDBhZjlkNmRmIiwidHlwIjoiYWNjZXNzIn0.fx3GdwK4Au9Uvw7IoB6dzCw3DLdvo9YDazfuBGuP2wRLeS2um3X2cbvnudmv4tBKwqAQQaSFG5L9VKWu8X8gJg"

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder().header("Authorization", token).build()
        return chain.proceed(request)
    }

    private fun Interceptor.Chain.proceedDeletingTokenOnError(request: Request): Response {
        val response = proceed(request)
//        if (response.code() == 401) {
//            repository.deleteToken()
//        }
        return response
    }

    private fun Request.Builder.addHeaders(token: String) =
        this.apply { header("Authorization", "Bearer $token") }

}