package br.com.zup.orange.pix.removerChave

import br.com.zup.orange.*
import br.com.zup.orange.pix.ChavePixRepository
import br.com.zup.orange.tratamentoErros.ChavePixNaoExistenteException
import br.com.zup.orange.tratamentoErros.ErrorHandler
import io.grpc.stub.StreamObserver
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandler
class RemoveChaveEndpoint(@Inject val repository: ChavePixRepository) :
    KeyManagerRemoveChaveGrpcServiceGrpc.KeyManagerRemoveChaveGrpcServiceImplBase() {

    override fun removeChave(
        request: RemoveChavePixRequest,
        responseObserver: StreamObserver<RemoveChavePixResponse>
    ) {

        //Verifica se chave existe
        val chavePix = repository.findByIdAndClienteId(UUID.fromString(request.chavePixId),request.clientId)
        if (chavePix.isEmpty) {
            throw ChavePixNaoExistenteException("A chave pix não está cadastrada no sistema ou não pertence ao Cliente")
        }

        repository.delete(chavePix.get())

        responseObserver.onNext(
            RemoveChavePixResponse.newBuilder()
                .setClientId(request.clientId)
                .build()
        )
        responseObserver.onCompleted()
    }
}
