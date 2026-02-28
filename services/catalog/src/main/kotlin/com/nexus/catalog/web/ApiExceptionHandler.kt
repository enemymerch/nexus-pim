package com.nexus.catalog.web

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

data class ApiError(val message: String)

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(NoSuchElementException::class)
    fun notFound(ex: NoSuchElementException) =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiError(ex.message ?: "Not found"))

    @ExceptionHandler(IllegalArgumentException::class)
    fun badRequest(ex: IllegalArgumentException) =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiError(ex.message ?: "Bad request"))

    @ExceptionHandler(IllegalStateException::class)
    fun conflict(ex: IllegalStateException) =
        ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError(ex.message ?: "Conflict"))
}
