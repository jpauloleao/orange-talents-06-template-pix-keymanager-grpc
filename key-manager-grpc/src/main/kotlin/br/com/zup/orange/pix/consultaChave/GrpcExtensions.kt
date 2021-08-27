package br.com.zup.orange.pix.consultaChave

import br.com.zup.orange.ConsultaChavePixRequest
import br.com.zup.orange.ConsultaChavePixRequest.SeletorTipoConsultaCase.*
import io.micronaut.validation.validator.Validator
import javax.validation.ConstraintViolationException

fun ConsultaChavePixRequest.toModel(validator: Validator): SeletorTipoConsulta {

    val filtroTipoConsulta = when(seletorTipoConsultaCase) {
        PIXID -> pixId.let { consultaPorPixId ->
            SeletorTipoConsulta.PorPixId(clienteId = consultaPorPixId.clienteId, pixId = consultaPorPixId.pixId)
        }
        CHAVE -> SeletorTipoConsulta.PorChave(chave)
        SELETORTIPOCONSULTA_NOT_SET -> SeletorTipoConsulta.Invalido()
    }

    val violations = validator.validate(filtroTipoConsulta)
    if (violations.isNotEmpty()) {
        throw ConstraintViolationException(violations);
    }

    return filtroTipoConsulta
}