package com.ku.kuvn.api

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.internal.bind.DateTypeAdapter
import com.ku.kuvn.BuildConfig
import com.ku.kuvn.api.league.League
import com.ku.kuvn.api.league.LeagueResponse
import io.reactivex.rxjava3.core.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.*
import java.util.concurrent.TimeUnit

object KuService {

    private const val API_URL = "https://quanlyku.us"
    val kuApi: KuApi

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        val builder = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)

        kuApi = Retrofit.Builder().client(builder.build())
            .addConverterFactory(GsonConverterFactory.create(createGsonObject()))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .baseUrl(API_URL)
            .build()
            .create(KuApi::class.java)
    }

    private fun createGsonObject(): Gson {
        return GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(Date::class.java, DateTypeAdapter())
            .create()
    }

    interface KuApi {

        @GET("api/menus")
        fun getMenus(@Query("page") page: Int = 1): Single<BaseResponse<MenuResponse>>

        @GET("api/banners")
        fun getBanners(): Single<BaseResponse<List<Banner>>>

        @GET("api/blogs")
        fun getGifts(@Query("page") page: Int = 1): Single<BaseResponse<BlogResponse>>

        @GET("api/blog_categories/{id}/blogs")
        fun getBlogsByCategory(@Path("id") id: String, @Query("page") page: Int = 1): Single<BaseResponse<BlogResponse>>

        @GET("api/blogs/{id}")
        fun getGift(@Path("id") id: String): Single<BaseResponse<Blog>>

        @GET("api/games")
        fun getGames(): Single<GameResponse>

        @GET("api/leagues")
        fun getLeagues(): Single<LeagueResponse>

        @GET("api/leagues/{id}")
        fun getLeague(@Path("id") id: String): Single<BaseResponse<League>>

        @GET("api/menus/register")
        fun getMenuSetting(): Single<BaseResponse<Menu>>

        @GET("api/settings/mobile")
        fun getSettings(): Single<BaseResponse<Setting>>
    }
}