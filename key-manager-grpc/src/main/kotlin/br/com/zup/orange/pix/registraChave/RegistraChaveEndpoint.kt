package br.com.zup.orange.pix.registraChave

import br.com.zup.orange.KeyManagerRegistaChaveGrpcServiceGrpc
import br.com.zup.orange.RegistraChavePixRequest
import br.com.zup.orange.RegistraChavePixResponse
import br.com.zup.orange.integracao.bcb.BcbClient
import br.com.zup.orange.integracao.itau.ItauClient
import br.com.zup.orange.pix.ChavePixRepository
import br.com.zup.orange.tratamentoErros.ChavePixBcbException
import br.com.zup.orange.tratamentoErros.ErrorHandler
import io.grpc.stub.StreamObserver
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandler
class RegistraChaveEndpoint(
    @Inject private val repository: ChavePixRepository,
    @Inject private val itauClient: ItauClient,
    @Inject private val bcbClient: BcbClient
) :
    KeyManagerRegistaChaveGrpcServiceGrpc.KeyManagerRegistaChaveGrpcServiceImplBase() {

    override fun registraChave(
        request: RegistraChavePixRequest,
        responseObserver: StreamObserver<RegistraChavePixResponse>
    ) {
        val novaChave = request.toModel()

        //Verifica se chave já existe
        if (repository.existsByChave(novaChave.chave)) throw IllegalStateException("Chave Pix '${novaChave.chave}' existente")

        //Faz a validação das Chaves
        novaChave.tipoChave.validaChave(novaChave.chave)

        //Busca dados da conta na API do ITAU
        val response = itauClient.buscaConta(novaChave.clienteId, novaChave.tipoConta.name)
        val conta = response.body()?.toModel() ?: throw HttpClientResponseException(
            "Cliente não encontrado no Itau",
            HttpResponse.notFound("")
        )

        //Registra chave no BCB
        val bcbRequest = novaChave.toBcb(conta)
        val bcbResponse = bcbClient.criaChaveBcb(bcbRequest)
        if (bcbResponse.status != HttpStatus.CREATED)
            throw ChavePixBcbException("Erro ao registrar chave Pix no Banco Central do Brasil (BCB)")

        //Grava os dados no banco
        val chave = novaChave.toModel(conta)
        chave.atualizaChave(bcbResponse.body().key)
        repository.save(chave)

        responseObserver.onNext(
            RegistraChavePixResponse.newBuilder()
                .setPixId(chave.id.toString())
                .build()
        )
        responseObserver.onCompleted()
    }
}