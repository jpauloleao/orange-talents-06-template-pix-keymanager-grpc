package br.com.zup.orange.pix


import br.com.zup.orange.pix.compartilhado.Conta
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull


@Entity
class ChavePix(
    @field:NotNull
    val clienteId: String,

    @field:NotNull
    @Enumerated(EnumType.STRING)
    val tipoChave: TipoChave,

    @field:NotBlank
    var chave: String,

    @field:NotNull
    @Enumerated(EnumType.STRING)
    val tipoConta: TipoConta,

    @field:Valid
    @Embedded
    val conta: Conta
) {
    @Id
    @GeneratedValue
    val id: UUID? = null

    val criadaEm: LocalDateTime = LocalDateTime.now()
}