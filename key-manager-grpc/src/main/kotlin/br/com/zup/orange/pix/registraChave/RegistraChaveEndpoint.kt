package br.com.zup.orange.pix.registraChave

import br.com.zup.orange.KeyManagerRegistaChaveGrpcServiceGrpc
import br.com.zup.orange.RegistraChavePixRequest
import br.com.zup.orange.RegistraChavePixResponse
import br.com.zup.orange.integracao.ItauClient
import br.com.zup.orange.pix.ChavePixRepository
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.exceptions.HttpClientResponseException
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional

@Singleton
class RegistraChaveEndpoint(@Inject val repository: ChavePixRepository, @Inject val itauClient: ItauClient) :
    KeyManagerRegistaChaveGrpcServiceGrpc.KeyManagerRegistaChaveGrpcServiceImplBase() {

    override fun registraChave(
        request: RegistraChavePixRequest,
        responseObserver: StreamObserver<RegistraChavePixResponse>
    ) {
        val novaChave = request.toModel()

        try {

            //Verifica se chave já existe
            if (repository.existsByChave(novaChave.chave)) // 1
                throw IllegalStateException("Chave Pix '${novaChave.chave}' existente")

            //Faz a validação das Chaves
            novaChave.tipoChave.validaChave(novaChave.chave)

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

        } catch (ex: IllegalArgumentException) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription(ex.message)
                    .asRuntimeException()
            )
        } catch (ex: IllegalStateException) {

            responseObserver.onError(
                Status.ALREADY_EXISTS
                    .withDescription(ex.message)
                    .asRuntimeException()
            )
        } catch (ex: HttpClientResponseException) {
            responseObserver.onError(
                Status.NOT_FOUND
                    .withDescription("Conta não registada no Itau")
                    .withCause(ex.cause)
                    .asRuntimeException()
            )
        } catch (ex: Exception) {
            responseObserver.onError(
                Status.INTERNAL
                    .withDescription(ex.message)
                    .withCause(ex.cause)
                    .asRuntimeException()
            )
        }

    }
}