package com.example.data.db

import org.ktorm.database.Database

object DataBaseConnection {
    val db = Database.connect(
        url = "jdbc:mysql://localhost:3306/team_manager",
        driver = "com.mysql.cj.jdbc.Driver",
        user = "root"
    )
}