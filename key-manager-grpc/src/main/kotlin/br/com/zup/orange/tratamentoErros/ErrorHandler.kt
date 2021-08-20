package br.com.zup.orange.tratamentoErros

import io.micronaut.aop.Around
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*

@MustBeDocumented
@Retention(RUNTIME)
@Target(CLASS, FIELD, TYPE, FUNCTION)
@Around
annotation class ErrorHandler {

}
