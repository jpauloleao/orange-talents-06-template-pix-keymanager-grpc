package br.com.zup.orange.integracao.bcb

import br.com.zup.orange.pix.TipoChave
import br.com.zup.orange.pix.TipoConta
import br.com.zup.orange.pix.compartilhado.Conta
import br.com.zup.orange.pix.compartilhado.Instituicoes
import br.com.zup.orange.pix.consultaChave.ChavePixInfoDto
import br.com.zup.orange.tratamentoErros.TipoChaveException
import java.time.LocalDateTime

class PixKeyDetailsResponse(
    val keyType: PixKeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
) {

    fun toModel(): ChavePixInfoDto {
        return ChavePixInfoDto(
            tipoChave = when (this.keyType) {
                PixKeyType.EMAIL -> TipoChave.EMAIL
                PixKeyType.CPF -> TipoChave.CPF
                PixKeyType.PHONE -> TipoChave.CELULAR
                PixKeyType.RANDOM -> TipoChave.ALEATORIA
                else -> throw TipoChaveException("Tipo de Chave Invalida")
            },
            chave = this.key,
            tipoConta = when (this.bankAccount.accountType) {
                AccountType.CACC -> TipoConta.CONTA_CORRENTE
                AccountType.SVGS -> TipoConta.CONTA_POUPANCA
            },
            conta = Conta(
                ispb = Instituicoes.nome(bankAccount.participant),
                nome = owner.name,
                cpf = owner.taxIdNumber,
                agencia = bankAccount.branch,
                numero = bankAccount.accountNumber,
            )
        )
    }
}