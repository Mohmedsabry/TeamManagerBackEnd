package com.example.domain.repository

import com.example.domain.model.User
import com.example.domain.util.GlobalError
import com.example.util.Result

interface AuthRepository {
    suspend fun signup(
        user: User
    ): Result<GlobalError, Unit>

    suspend fun login(
        email: String,
        password: String
    ): Result<GlobalError, User>

    suspend fun forgotPassword(
        email: String,
        restCode: String
    ): Result<GlobalError, Unit>

    suspend fun checkCode(
        sender: String,
        restCode: String
    ): Result<GlobalError, Unit>

    suspend fun updatePassword(
        newPassword: String,
        email: String,
    ): Result<GlobalError, User>
}