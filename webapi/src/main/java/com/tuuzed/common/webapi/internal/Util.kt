package com.tuuzed.common.webapi.internal

import okhttp3.RequestBody


val EMPTY_BYTE_ARRAY = ByteArray(0)
val EMPTY_REQUEST = RequestBody.create(null, EMPTY_BYTE_ARRAY)
