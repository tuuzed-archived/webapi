package com.tuuzed.webapi

import okhttp3.Response


class DefaultResponseConverter : ResponseConverter {
    override fun invoke(response: Response) = response
}