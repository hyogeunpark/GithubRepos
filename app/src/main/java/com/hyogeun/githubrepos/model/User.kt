package com.hyogeun.githubrepos.model

import com.google.gson.annotations.SerializedName
import com.hyogeun.githubrepos.common.BaseModel

data class User(
    @SerializedName("login")
    val name: String = "",
    @SerializedName("avatar_url")
    val image: String = ""
) : BaseModel()