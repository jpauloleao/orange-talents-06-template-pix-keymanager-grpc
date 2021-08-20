package br.com.zup.orange.tratamentoErros

import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import io.micronaut.http.client.exceptions.HttpClientResponseException
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
@InterceptorBean(ErrorHandler::class)
class ErrorAroundHandlerInterceptor : MethodInterceptor<Any, Any> {

    override fun intercept(context: MethodInvocationContext<Any, Any>): Any? {

        try {
            return context.proceed()
        } catch (ex: Exception) {

            val responseObserver = context.parameterValues[1] as StreamObserver<*>

            val status = when(ex) {
                is ConstraintViolationException -> Status.INVALID_ARGUMENT.withCause(ex).withDescription(ex.message)
                is IllegalArgumentException -> Status.INVALID_ARGUMENT.withCause(ex).withDescription(ex.message)
                is IllegalStateException -> Status.ALREADY_EXISTS.withCause(ex).withDescription(ex.message)
                is HttpClientResponseException -> Status.NOT_FOUND.withCause(ex.cause).withDescription("Conta não registada no Itau")
                else -> Status.UNKNOWN.withCause(ex).withDescription("Erro inesperado")
            }

            responseObserver.onError(status.asRuntimeException())
        }

        return null
    }

}