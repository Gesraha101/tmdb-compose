package com.example.core.ui

import com.example.core.domain.ApiError


object Loading : ViewState

class Error(val error: ApiError?) : ViewState