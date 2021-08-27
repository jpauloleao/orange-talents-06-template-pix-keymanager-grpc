package br.com.zup.orange.tratamentoErros

import io.grpc.StatusRuntimeException
import java.lang.RuntimeException

class TipoChaveException(message : String) : RuntimeException(message){
}