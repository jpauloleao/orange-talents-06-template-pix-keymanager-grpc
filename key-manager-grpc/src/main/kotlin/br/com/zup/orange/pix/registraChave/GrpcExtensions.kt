package br.com.zup.orange.pix.registraChave

import br.com.zup.orange.RegistraChavePixRequest
import br.com.zup.orange.pix.TipoChave
import br.com.zup.orange.pix.TipoConta


import java.lang.IllegalArgumentException


fun RegistraChavePixRequest.toModel() : NovaChavePixDTO {
    return NovaChavePixDTO(
        clienteId = clientID,
        tipoChave = when(tipoChave) {br.com.zup.orange.TipoChave.CHAVE_NAO_ESPEFICICADA -> throw IllegalArgumentException("Deve preencher uma chave válida") else -> TipoChave.valueOf(tipoChave.name)},
        chave = chave,
        tipoConta = when(tipoConta) {br.com.zup.orange.TipoConta.CONTA_NAO_ESPEFICICADA -> throw IllegalArgumentException("Deve preencher uma conta válida") else -> TipoConta.valueOf(tipoConta.name)}
    )
}
