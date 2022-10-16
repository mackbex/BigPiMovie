package com.bigpi.movie.data.module

import com.bigpi.movie.data.api.remote.MovieService
import com.bigpi.movie.data.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(
        @HeaderInterceptorOkHttpClient headerInterceptor: Interceptor
    ) : OkHttpClient {
        val client = OkHttpClient.Builder()
            .readTimeout(REMOTE_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(REMOTE_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(REMOTE_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(headerInterceptor)

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(Level.BODY)
            client.addInterceptor(logging)
        }

        return client.build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL_V1)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideRankingService(retrofit: Retrofit): MovieService {
        return retrofit.create(MovieService::class.java)
    }

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class HeaderInterceptorOkHttpClient

    @HeaderInterceptorOkHttpClient
    @Singleton
    @Provides
    fun providesHeaderInterceptor(): Interceptor = HeaderInterceptor()
}

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val ongoing = request.newBuilder()
            .header(CLIENT_ID_KEY, CLIENT_ID_VALUE)
            .header(CLIENT_SECRET_KEY, CLIENT_SECRET_VALUE)
        return chain.proceed(ongoing.build())
    }
}