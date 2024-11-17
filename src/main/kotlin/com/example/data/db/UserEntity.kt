package com.example.data.db

import org.ktorm.schema.Table
import org.ktorm.schema.double
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object UserEntity : Table<Nothing>("user") {
    val id = int("id").primaryKey()
    val email = varchar("email")
    val name = varchar("name")
    val password = varchar("password")
    val age = double("age")
    val gender = varchar("gender")
    val phone = varchar("phoneNumber")
    val image = varchar("img")
}