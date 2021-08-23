package br.com.zup.orange.pix.registraChave

import br.com.zup.orange.KeyManagerRegistaChaveGrpcServiceGrpc
import br.com.zup.orange.RegistraChavePixRequest
import br.com.zup.orange.RegistraChavePixResponse
import br.com.zup.orange.integracao.ItauClient
import br.com.zup.orange.pix.ChavePixRepository
import br.com.zup.orange.tratamentoErros.ErrorHandler
import io.grpc.stub.StreamObserver
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.exceptions.HttpClientResponseException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandler
class RegistraChaveEndpoint(@Inject val repository: ChavePixRepository, @Inject val itauClient: ItauClient) :
    KeyManagerRegistaChaveGrpcServiceGrpc.KeyManagerRegistaChaveGrpcServiceImplBase() {

    override fun registraChave(
        request: RegistraChavePixRequest,
        responseObserver: StreamObserver<RegistraChavePixResponse>
    ) {
        val novaChave = request.toModel()

            //Verifica se chave já existe
            if (repository.existsByChave(novaChave.chave)) throw IllegalStateException("Chave Pix '${novaChave.chave}' existente")

            //Faz a validação das Chaves
            val validacao = novaChave.tipoChave.validaChave(novaChave.chave)

            //Busca dados da conta na API do ITAU
            val response = itauClient.buscaConta(novaChave.clienteId, novaChave.tipoConta.name)
            val conta = response.body()?.toModel() ?: throw HttpClientResponseException("Cliente não encontrado no Itau", HttpResponse.notFound(""))

            //Grava os dados no banco
            val chave = novaChave.toModel(conta)
            repository.save(chave)

            responseObserver.onNext(
                RegistraChavePixResponse.newBuilder()
                    .setPixId(chave.id.toString())
                    .build()
            )
            responseObserver.onCompleted()
    }
}