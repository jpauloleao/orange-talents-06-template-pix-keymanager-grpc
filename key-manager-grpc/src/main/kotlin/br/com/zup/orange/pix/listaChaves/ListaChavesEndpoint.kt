package br.com.zup.orange.pix.listaChaves

import br.com.zup.orange.*
import br.com.zup.orange.pix.ChavePixRepository
import br.com.zup.orange.tratamentoErros.ErrorHandler
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandler
class ListaChavesEndpoint(@Inject private val repository: ChavePixRepository) : KeyManagerListaChavesGrpcServiceGrpc.KeyManagerListaChavesGrpcServiceImplBase(){

    override fun listaChaves(request: ListaChavePixRequest, responseObserver: StreamObserver<ListaChavePixResponse>) {
        if (request.clienteId.isNullOrBlank()) throw IllegalArgumentException("O id do Cliente não deve ser nulo")
        if (!repository.existsByClienteId(request.clienteId)) throw IllegalArgumentException("Cliente não registrado")

        val chaves = repository.findAllByClienteId(request.clienteId).map {
            chavePix ->  ListaChavePixResponse.ChavePix.newBuilder()
            .setPixId(chavePix.id.toString())
            .setClienteId(chavePix.clienteId)
            .setTipo(TipoChave.valueOf(chavePix.tipoChave.name))
            .setChave(chavePix.chave)
            .setTipoConta(TipoConta.valueOf(chavePix.tipoConta.name))
            .setCriadaEm(chavePix.criadaEm.let { dataCriacao ->
                val createdAt = dataCriacao.atZone(ZoneId.of("UTC")).toInstant()
                Timestamp.newBuilder()
                    .setSeconds(createdAt.epochSecond)
                    .setNanos(createdAt.nano)
                    .build()
            })
            .build()
        }

        responseObserver.onNext(ListaChavePixResponse.newBuilder()
            .addAllChaves(chaves)
            .build())
        responseObserver.onCompleted()
    }
}