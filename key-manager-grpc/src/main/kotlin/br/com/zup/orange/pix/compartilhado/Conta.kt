package br.com.zup.orange.pix.compartilhado

import io.micronaut.core.annotation.Introspected
import io.micronaut.data.annotation.Embeddable
import javax.persistence.Column
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Embeddable
@Introspected
class Conta(
    @field:NotBlank
    @Column(nullable = false)
    var tipo : String = "",

    @field:NotBlank
    @Column(nullable = false)
    var nome: String = "",

    @field:NotBlank
    @field:Size(max = 11)
    @Column(nullable = false)
    var cpf: String = "",

    @field:NotBlank
    @Column(nullable = false)
    var agencia: String = "",

    @field:NotBlank
    @Column(nullable = false)
    var numero: String ="",

    @field:NotBlank
    @Column(nullable = false)
    var instituicao: String = "",

    @field:NotBlank
    @Column(nullable = false)
    var ispb: String = ""
){

}

