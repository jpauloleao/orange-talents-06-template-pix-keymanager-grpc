package br.com.zup.orange.pix.consultaChave

import br.com.zup.orange.*
import br.com.zup.orange.integracao.bcb.BcbClient
import br.com.zup.orange.pix.ChavePixRepository
import br.com.zup.orange.tratamentoErros.ErrorHandler
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import io.micronaut.validation.validator.Validator
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandler
class ConsultaChaveEndpoint(
    @Inject val repository: ChavePixRepository,
    @Inject val bcbClient: BcbClient,
    @Inject val validator: Validator
) : KeyManagerConsultaChaveGrpcServiceGrpc.KeyManagerConsultaChaveGrpcServiceImplBase() {

    override fun consultaChave(
        request: ConsultaChavePixRequest,
        responseObserver: StreamObserver<ConsultaChavePixResponse>
    ) {

        val seletor = request.toModel(validator)
        val chaveInfo = seletor.filtraTipoConsulta(repository = repository, bcbClient = bcbClient)

        responseObserver.onNext(
            ConsultaChavePixResponse.newBuilder()
                .setClienteId(chaveInfo.clienteId ?: "")
                .setPixId(chaveInfo.pixId?.toString() ?: "")
                .setChave(
                    ConsultaChavePixResponse.ChavePix
                        .newBuilder()
                        .setTipo(TipoChave.valueOf(chaveInfo.tipoChave.name))
                        .setChave(chaveInfo.chave)
                        .setConta(
                            ConsultaChavePixResponse.ChavePix.DadosConta.newBuilder()
                                .setTipo(TipoConta.valueOf(chaveInfo.tipoConta.name))
                                .setInstituicao(chaveInfo.conta.instituicao)
                                .setNomeTitular(chaveInfo.conta.nome)
                                .setCpfTitular(chaveInfo.conta.cpf)
                                .setAgencia(chaveInfo.conta.agencia)
                                .setNumeroConta(chaveInfo.conta.numero)
                                .build()
                        )
                        .setCriadaEm(chaveInfo.registradaEm.let {dataRegistro ->
                            val createdAt = dataRegistro.atZone(ZoneId.of("UTC")).toInstant()
                            Timestamp.newBuilder()
                                .setSeconds(createdAt.epochSecond)
                                .setNanos(createdAt.nano)
                                .build()
                        })
                )
                .build()
        )

        responseObserver.onCompleted()
    }
}