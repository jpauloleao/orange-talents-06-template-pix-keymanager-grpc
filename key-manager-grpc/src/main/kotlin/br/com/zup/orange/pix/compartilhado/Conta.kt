package br.com.zup.orange.pix.compartilhado

import io.micronaut.core.annotation.Introspected
import io.micronaut.data.annotation.Embeddable
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Embeddable
@Introspected
class Conta(
    @field:NotBlank
    var tipo : String = "",

    @field:NotBlank
    var nome: String = "",

    @field:NotBlank
    @field:Size(max = 11)
    var cpf: String = "",

    @field:NotBlank
    var agencia: String = "",

    @field:NotBlank
    var numero: String ="",

    @field:NotBlank
    var instituicao: String = "",

    @field:NotBlank
    var ispb: String = ""
){

}

