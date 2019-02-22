package com.tuuzed.common.webapi.client

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("msg") var msg: String? = null,
    @SerializedName("error") var error: Boolean? = null
) {
    constructor() : this(null)
}