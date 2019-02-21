package com.tuuzed.common.webapi

import com.google.gson.annotations.SerializedName
import com.tuuzed.common.webapi.internal.WebApiIgnore

data class Resp<T>(

    @SerializedName("error") var error: Boolean = true,

    @SerializedName("msg") var msg: String? = null,

    @SerializedName("payload") var payload: T? = null,

    @WebApiIgnore var tr: Throwable? = null

) {
    constructor() : this(true)
}

data class ListResp<T>(

    @SerializedName("error") var error: Boolean = true,

    @SerializedName("msg") var msg: String? = null,

    @SerializedName("payload") var payload: List<T> = emptyList(),

    @WebApiIgnore var tr: Throwable? = null

) {
    constructor() : this(true)
}
