package br.com.zup.orange

import br.com.zup.orange.integracao.bcb.BcbClient
import br.com.zup.orange.integracao.bcb.DeletePixKeyRequest
import br.com.zup.orange.integracao.bcb.DeletePixKeyResponse
import br.com.zup.orange.pix.ChavePix
import br.com.zup.orange.pix.ChavePixRepository
import br.com.zup.orange.pix.compartilhado.DadosContaResponse
import br.com.zup.orange.pix.compartilhado.InstituicaoResponse
import br.com.zup.orange.pix.compartilhado.TitularResponse
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject


@MicronautTest(transactional = false)
internal class RemoveChavePixEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: KeyManagerRemoveChaveGrpcServiceGrpc.KeyManagerRemoveChaveGrpcServiceBlockingStub
) {

    lateinit var chaveExistente: ChavePix

    @Inject
    lateinit var bcbClient: BcbClient

    @BeforeEach
    fun setup() {
        val clienteId = UUID.randomUUID()
        val dadosContaResponse = dadosContaResponse()
        chaveExistente = repository.save(ChavePix(clienteId.toString(),br.com.zup.orange.pix.TipoChave.EMAIL, "jpaulo@gmail.com", br.com.zup.orange.pix.TipoConta.CONTA_CORRENTE, dadosContaResponse.toModel()))
    }

    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }

    @Test
    fun `remove uma chave pix existente`() {
        // cenário
        `when`(bcbClient.removeChaveBcb("jpaulo@gmail.com", DeletePixKeyRequest("jpaulo@gmail.com")))
            .thenReturn(
                HttpResponse.ok(DeletePixKeyResponse(key = "jpaulo@gmail.com",
                participant = "60701190",
                deletedAt = LocalDateTime.now())
                )
            )

        // ação
        val retorno = grpcClient.removeChave(RemoveChavePixRequest.newBuilder()
            .setClientId(chaveExistente.clienteId)
            .setChavePixId(chaveExistente.id.toString())
            .build())

        // validação
        with(retorno) {
            assertEquals(chaveExistente.clienteId, clientId)
        }
    }

    @Test
    fun `não remove uma chave pix quando chave inexistente`() {
        // cenário
        val chaveInexistente = UUID.randomUUID()

        // ação
        val retorno = assertThrows<StatusRuntimeException> {
            grpcClient.removeChave(RemoveChavePixRequest.newBuilder()
                .setChavePixId(chaveInexistente.toString())
                .setClientId(chaveExistente.clienteId)
                .build())
        }

        // validação
        with(retorno) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("A chave pix não está cadastrada no sistema ou não pertence ao Cliente", status.description)
        }
    }

    /*@Test
    fun `não remove uma chave pix quando ocorre erro no serviço do BCB`(){
        //cenario
        `when`(bcbClient.removeChaveBcb("joao@gmail.com",DeletePixKeyRequest("joao@gmail.com"))).thenReturn(HttpResponse.unprocessableEntity())

        //ação
        val retorno = assertThrows<StatusRuntimeException> {
            grpcClient.removeChave(RemoveChavePixRequest.newBuilder()
                .setChavePixId(chaveExistente.id.toString())
                .setClientId(chaveExistente.clienteId)
                .build())
        }

        //validação
        with(retorno){
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("Erro ao remover chave Pix no Banco Central do Brasil (BCB)", status.description)
        }

    }*/

    @Factory
    class Clients {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerRemoveChaveGrpcServiceGrpc.KeyManagerRemoveChaveGrpcServiceBlockingStub {
            return KeyManagerRemoveChaveGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

    @MockBean(BcbClient::class)
    fun  bcbClient() : BcbClient?{
        return Mockito.mock(BcbClient::class.java)
    }

    private fun dadosContaResponse(): DadosContaResponse {
        return DadosContaResponse(
            tipo = "CONTA_CORRENTE",
            instituicao = InstituicaoResponse("UNIBANCO ITAU SA", "60701190"),
            agencia = "1111",
            numero = "234565",
            titular = TitularResponse("João", "88333415008")
        )
    }
}