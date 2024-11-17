package com.example.util

sealed interface Result<E : Error, D> {
    data class Success<E : Error, D>(val data: D) : Result<E, D>
    data class Failure<E : Error, D>(val error: E) : Result<E, D>
}