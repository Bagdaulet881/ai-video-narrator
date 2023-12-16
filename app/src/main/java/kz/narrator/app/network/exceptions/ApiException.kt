package kz.narrator.app.network.exceptions

import kz.narrator.app.data.models.network.error.ApiError

class ApiException(val apiError: ApiError) : Exception()