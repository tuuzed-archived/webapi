package com.tuuzed.webapi

import okhttp3.Response

typealias ResponseConverter = (response: Response) -> Any