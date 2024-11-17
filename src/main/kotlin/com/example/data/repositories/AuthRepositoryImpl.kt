package com.example.data.repositories

import com.example.data.db.UserDao
import com.example.data.mongodb.controller.Controller
import com.example.data.mongodb.entity.Members
import com.example.domain.model.User
import com.example.domain.repository.AuthRepository
import com.example.domain.util.GlobalError
import com.example.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt

class AuthRepositoryImpl(
    private val controller: Controller,
    private val dao: UserDao
) : AuthRepository {
    override suspend fun signup(
        user: User
    ): Result<GlobalError, Unit> {
        return withContext(Dispatchers.IO) {
            try {
                dao.addUser(user)
                controller.addMember(
                    Members(
                        email = user.email,
                        name = user.username,
                        image = user.image,
                        gender = user.gender
                    )
                )
                Result.Success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(GlobalError.UnknownError)
            }
        }
    }

    override suspend fun login(
        email: String,
        password: String
    ): Result<GlobalError, User> {
        return withContext(Dispatchers.IO) {
            try {
                val user =
                    dao.getUser(email = email) ?: return@withContext Result.Failure(GlobalError.ThereIsNoUserFound)
                if (BCrypt.checkpw(password, user.password))
                    Result.Success(user)
                else Result.Failure(GlobalError.PasswordError)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(GlobalError.UnknownError)
            }
        }
    }

    override suspend fun forgotPassword(
        email: String,
        restCode: String
    ): Result<GlobalError, Unit> {
        return withContext(Dispatchers.IO) {
            try {
                controller.requestNewPassword(
                    email, restCode
                )
                sendVerificationEmail(
                    email, restCode
                )
                Result.Success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(GlobalError.ThereIsNoUserFound)
            }
        }
    }

    override suspend fun checkCode(
        sender: String,
        restCode: String
    ): Result<GlobalError, Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val entity =
                    controller.getRequestTime(sender) ?: return@withContext Result.Failure(GlobalError.UnknownError)
                println("$entity ${System.currentTimeMillis()}")
                if (entity.restCode == restCode && entity.expirationTime!!.div(1000) >= System.currentTimeMillis()
                        .div(1000)
                ) return@withContext Result.Success(
                    Unit
                )
                Result.Failure(GlobalError.VERIFY_CREDENTIALS)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(GlobalError.UnknownError)
            }
        }
    }

    override suspend fun updatePassword(
        newPassword: String,
        email: String
    ): Result<GlobalError, User> {
        return withContext(Dispatchers.IO) {
            try {
                val user = dao.getUser(email) ?: return@withContext Result.Failure(GlobalError.ThereIsNoUserFound)
                val hashPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt())
                dao.updatePassword(
                    email, newPassword
                )
                controller.removeRequestForPassword(sender = email)
                Result.Success(user.copy(password = hashPassword))
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(GlobalError.UnknownError)
            }
        }
    }
}