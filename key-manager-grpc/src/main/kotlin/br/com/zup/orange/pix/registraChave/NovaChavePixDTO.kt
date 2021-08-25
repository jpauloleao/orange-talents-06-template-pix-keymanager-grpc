package br.com.zup.orange.pix.registraChave


import br.com.zup.orange.integracao.bcb.*
import br.com.zup.orange.pix.ChavePix
import br.com.zup.orange.pix.TipoChave
import br.com.zup.orange.pix.TipoConta
import br.com.zup.orange.pix.compartilhado.Conta
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
class NovaChavePixDTO(
    @field:NotBlank val clienteId: String,
    @field:NotNull val tipoChave: TipoChave,
    val chave: String,
    @field:NotNull val tipoConta: TipoConta
) {

    fun toModel(@Valid conta: Conta): ChavePix {
        return ChavePix(
            clienteId,
            tipoChave = TipoChave.valueOf(this.tipoChave.name),
            chave = if (this.tipoChave == TipoChave.ALEATORIA) UUID.randomUUID().toString() else this.chave,
            tipoConta = TipoConta.valueOf(this.tipoConta.name),
            conta = conta
        )
    }

    fun toBcb(@Valid conta: Conta): CreatePixKeyRequest {
        return CreatePixKeyRequest(
            keyType = when (tipoChave) {
                TipoChave.CPF -> PixKeyType.CPF
                TipoChave.EMAIL -> PixKeyType.EMAIL
                TipoChave.ALEATORIA -> PixKeyType.RANDOM
                TipoChave.CELULAR -> PixKeyType.PHONE
            }, key = chave, bankAccount = BankAccount(
                participant = conta.ispb, branch = conta.agencia, accountNumber = conta.numero,
                accountType = when (tipoConta) {
                    TipoConta.CONTA_CORRENTE -> AccountType.CACC
                    TipoConta.CONTA_POUPANCA -> AccountType.SVGS
                },
            ),
            owner = Owner(Owner.OwnerType.NATURAL_PERSON, conta.nome, conta.cpf)
        )
    }

}