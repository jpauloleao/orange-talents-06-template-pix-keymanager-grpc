package br.com.zup.orange.pix.consultaChave

import br.com.zup.orange.pix.ChavePix
import br.com.zup.orange.pix.TipoChave
import br.com.zup.orange.pix.TipoConta
import br.com.zup.orange.pix.compartilhado.Conta
import java.time.LocalDateTime
import java.util.*

class ChavePixInfoDto(
    val pixId: UUID? = null,
    val clienteId: String? = null,
    val tipoChave: TipoChave,
    val chave: String,
    val tipoConta: TipoConta,
    val conta: Conta,
    val registradaEm: LocalDateTime = LocalDateTime.now()
) {
    companion object{
        fun newObject(chavePix: ChavePix): ChavePixInfoDto {
            return ChavePixInfoDto(
                pixId = chavePix.id,
                clienteId = chavePix.clienteId,
                tipoChave = chavePix.tipoChave,
                chave = chavePix.chave,
                tipoConta = chavePix.tipoConta,
                conta = chavePix.conta,
                registradaEm = chavePix.criadaEm
            )
        }
    }
}