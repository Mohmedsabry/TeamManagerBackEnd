package com.example.data.db

import com.example.domain.model.User
import org.ktorm.dsl.*
import org.mindrot.jbcrypt.BCrypt

class UserDao {
    private val db by lazy {
        DataBaseConnection.db
    }

    fun addUser(
        user: User
    ) {
        db.insert(UserEntity) {
            set(it.email, user.email)
            set(it.age, user.age)
            set(it.gender, user.gender)
            set(it.phone, user.phoneNumber)
            set(it.password, BCrypt.hashpw(user.password, BCrypt.gensalt()))
            set(it.image, user.image)
            set(it.name, user.username)
        }
    }

    fun getUser(email: String): User? {
        return db.from(UserEntity).select()
            .where(email eq UserEntity.email)
            .map {
                val age = it[UserEntity.age]?.toDouble() ?: 0.0
                val gender = it[UserEntity.gender] ?: ""
                val phone = it[UserEntity.phone] ?: ""
                val password = it[UserEntity.password] ?: ""
                val image = it[UserEntity.image] ?: ""
                val name = it[UserEntity.name] ?: ""
                User(
                    email = email,
                    phoneNumber = phone,
                    age = age,
                    gender = gender,
                    password = password,
                    image = image,
                    username = name
                )
            }
            .firstOrNull()
    }

    fun updatePassword(email: String, newPassword: String) {
        db.update(UserEntity) {
            set(it.password, newPassword)
            where {
                it.email eq email
            }
        }
    }
}

