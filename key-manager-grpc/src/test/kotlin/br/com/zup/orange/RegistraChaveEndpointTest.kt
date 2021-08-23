package br.com.zup.orange

import br.com.zup.orange.integracao.ItauClient
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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class RegistraChaveEndpointTest(
    val chavePixRepository: ChavePixRepository,
    val grpcClient: KeyManagerRegistaChaveGrpcServiceGrpc.KeyManagerRegistaChaveGrpcServiceBlockingStub
) {

    @Inject
    lateinit var itauClient: ItauClient

    @BeforeEach
    fun setup(){
        chavePixRepository.deleteAll()
    }

    @Test
    fun `cadastrar uma chave pix`(){
        //cenario
        val clienteId = UUID.randomUUID()
        `when`(itauClient.buscaConta(clienteId = clienteId.toString(), "CONTA_CORRENTE")).thenReturn(HttpResponse.ok(dadosDaContaResponse()))

        //acao
        val response = grpcClient.registraChave(
            RegistraChavePixRequest.newBuilder()
            .setClientID(clienteId.toString())
            .setTipoChave(TipoChave.EMAIL)
            .setChave("rponte@gmail.com")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build())


        //validacao
        with(response){
            assertNotNull(pixId)
        }
    }


    @Test
    fun `nao cadastra uma chave pix ja existente`(){
        //cenario
        val dadosContaResponse = dadosDaContaResponse()
        chavePixRepository.save(ChavePix("e703fe17-cd54-4721-9a85-099eed7037ba",br.com.zup.orange.pix.TipoChave.EMAIL, "rafael@gmail.com", br.com.zup.orange.pix.TipoConta.CONTA_CORRENTE, dadosContaResponse.toModel()))

        //acao
        val retorno = assertThrows<StatusRuntimeException> {
            grpcClient.registraChave(
                RegistraChavePixRequest.newBuilder()
                    .setClientID("e703fe17-cd54-4721-9a85-099eed7037ba")
                    .setTipoChave(TipoChave.EMAIL)
                    .setChave("rafael@gmail.com")
                    .setTipoConta(TipoConta.CONTA_CORRENTE)
                    .build())
        }

        //validacao
        with(retorno){
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Chave Pix 'rafael@gmail.com' existente", status.description)
        }
    }

    @Test
    fun `nao cadastra uma chave pix quando os parametros forem invalidos`(){
        //cenario

        //acao
        val retorno = assertThrows<StatusRuntimeException> {
            grpcClient.registraChave(RegistraChavePixRequest.newBuilder().build())
        }

        //validacao
        with(retorno){
            println(Status.INVALID_ARGUMENT)
            println(Status.INVALID_ARGUMENT.code)
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    //Cria CLiente GRPC
    @Factory
    class Clients{
        @Bean
        fun blokingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel) : KeyManagerRegistaChaveGrpcServiceGrpc.KeyManagerRegistaChaveGrpcServiceBlockingStub {
            return KeyManagerRegistaChaveGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

    //Mock para simular API externa
    @MockBean(ItauClient::class)
    fun itauClient() : ItauClient?{
        return Mockito.mock(ItauClient::class.java)
    }

    private fun dadosDaContaResponse(): DadosContaResponse {
        return DadosContaResponse(
            tipo = "CONTA_CORRENTE",
            instituicao = InstituicaoResponse("UNIBANCO ITAU SA", "60701190"),
            agencia = "1218",
            numero = "291900",
            titular = TitularResponse("Rafael Ponte", "63657520325")
        )
    }
}