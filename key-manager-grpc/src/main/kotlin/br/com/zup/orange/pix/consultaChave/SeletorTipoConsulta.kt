package br.com.zup.orange.pix.consultaChave

import br.com.zup.orange.integracao.bcb.BcbClient
import br.com.zup.orange.pix.ChavePixRepository
import br.com.zup.orange.tratamentoErros.ChavePixBcbException
import br.com.zup.orange.tratamentoErros.ChavePixNaoExistenteException
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

sealed class SeletorTipoConsulta {

    abstract fun filtraTipoConsulta(repository: ChavePixRepository, bcbClient: BcbClient): ChavePixInfoDto

    @Introspected
    data class PorPixId(
        @field:NotBlank val clienteId: String,
        @field:NotBlank val pixId: String,
    ) : SeletorTipoConsulta() {

        override fun filtraTipoConsulta(repository: ChavePixRepository, bcbClient: BcbClient): ChavePixInfoDto {
            return repository.findById(UUID.fromString(pixId))
                .filter { chavePix -> chavePix.pertenceAo(clienteId) }
                .map(ChavePixInfoDto::newObject)
                .orElseThrow { ChavePixNaoExistenteException("Chave Pix não encontrada") }
        }
    }

    @Introspected
    data class PorChave(@field:NotBlank @Size(max = 77) val chave: String) : SeletorTipoConsulta() {

        override fun filtraTipoConsulta(repository: ChavePixRepository, bcbClient: BcbClient): ChavePixInfoDto {
            return repository.findByChave(chave)
                .map(ChavePixInfoDto::newObject)
                .orElseGet {
                    val response = bcbClient.consultaChaveBcb(chave)
                    when (response.status) {
                        HttpStatus.OK -> response.body()?.toModel()
                        else -> throw ChavePixBcbException("Chave Pix não encontrada")
                    }
                }
        }
    }

    @Introspected
    class Invalido() : SeletorTipoConsulta() {
        override fun filtraTipoConsulta(repository: ChavePixRepository, bcbClient: BcbClient): ChavePixInfoDto {
            throw IllegalArgumentException("Chave Pix inválida ou não informada")
        }
    }
}