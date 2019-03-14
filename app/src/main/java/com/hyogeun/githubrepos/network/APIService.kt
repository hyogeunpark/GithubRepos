package com.hyogeun.githubrepos.network

import com.hyogeun.githubrepos.model.Repo
import com.hyogeun.githubrepos.model.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by hyogeun.park on 2017. 12. 20..
 */
interface APIService {
    @GET("users/{username}")
    fun user(@Path("username") userName: String): Call<User>

    @GET("users/{username}/repos")
    fun repos(@Path("username") userName: String): Call<ArrayList<Repo>>

}