package com.hyogeun.githubrepos.model

import com.google.gson.annotations.SerializedName
import com.hyogeun.githubrepos.common.BaseModel

data class Repo(
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("stargazers_count")
    val starCount: Int = 0
) : BaseModel()