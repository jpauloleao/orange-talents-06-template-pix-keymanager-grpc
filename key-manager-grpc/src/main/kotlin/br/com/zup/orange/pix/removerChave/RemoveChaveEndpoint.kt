package br.com.zup.orange.pix.removerChave

import br.com.zup.orange.*
import br.com.zup.orange.integracao.bcb.BcbClient
import br.com.zup.orange.integracao.bcb.DeletePixKeyRequest
import br.com.zup.orange.pix.ChavePixRepository
import br.com.zup.orange.tratamentoErros.ChavePixBcbException
import br.com.zup.orange.tratamentoErros.ChavePixNaoExistenteException
import br.com.zup.orange.tratamentoErros.ErrorHandler
import io.grpc.stub.StreamObserver
import io.micronaut.http.HttpStatus
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandler
class RemoveChaveEndpoint(@Inject val repository: ChavePixRepository, @Inject val bcbClient: BcbClient) :
    KeyManagerRemoveChaveGrpcServiceGrpc.KeyManagerRemoveChaveGrpcServiceImplBase() {

    override fun removeChave(
        request: RemoveChavePixRequest,
        responseObserver: StreamObserver<RemoveChavePixResponse>
    ) {

        //Verifica se chave existe
        val chavePix = repository.findByIdAndClienteId(UUID.fromString(request.chavePixId), request.clientId)
        if (chavePix.isEmpty) {
            throw ChavePixNaoExistenteException("A chave pix não está cadastrada no sistema ou não pertence ao Cliente")
        }

        //Remove chave no BCB
        val bcbResponse = bcbClient.removeChaveBcb(chavePix.get().chave, DeletePixKeyRequest(chavePix.get().chave))
        if (bcbResponse.status != HttpStatus.OK)
            throw ChavePixBcbException("Erro ao remover chave Pix no Banco Central do Brasil (BCB)")

        repository.delete(chavePix.get())

        responseObserver.onNext(
            RemoveChavePixResponse.newBuilder()
                .setClientId(request.clientId)
                .build()
        )
        responseObserver.onCompleted()
    }
}
