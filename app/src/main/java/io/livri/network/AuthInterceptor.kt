package io.livri.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

class AuthInterceptor : Interceptor {
    private var token: String ="Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJsaXZyaS5pbyIsImV4cCI6MTU5MDQxNDExOSwiaWF0IjoxNTg3OTk0OTE5LCJpc3MiOiJsaXZyaS5pbyIsImp0aSI6IjNlOTgzMzhlLTViNzYtNGFkZS04YTliLWEzZDIxMmRmMGQ3OSIsIm5iZiI6MTU4Nzk5NDkxOCwic3ViIjoiNTEzNzIzOWItMGYwZi00MzFjLTk4NmQtN2MyNDBhZjlkNmRmIiwidHlwIjoiYWNjZXNzIn0.UQCvwqRXbXWPYX0kejI2TF02-UZpImBeBendw-k40nyz-jvtpTffPgnqvPpWOQySxatdDiTxAGxpqbIHDs637w"

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