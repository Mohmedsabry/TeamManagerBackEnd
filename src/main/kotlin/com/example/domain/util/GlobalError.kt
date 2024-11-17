package com.example.domain.util

import com.example.util.Error

enum class GlobalError : Error {
    UnknownError,
    ThereIsNoUserFound,
    PasswordError,
    VERIFY_CREDENTIALS,
}