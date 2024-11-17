package com.example.di

import com.example.data.db.UserDao
import com.example.data.mongodb.controller.Controller
import com.example.data.mongodb.controller.ControllerImpl
import com.example.data.repositories.AuthRepositoryImpl
import com.example.data.repositories.RepositoryImpl
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.Repository
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mainModule = module {
    single {
        KMongo.createClient()
            .coroutine
            .getDatabase("teamManger_db")
    }
    single<Controller> { ControllerImpl(get()) }
    single<Repository> { RepositoryImpl(get(), get()) }
    single { UserDao() }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
}