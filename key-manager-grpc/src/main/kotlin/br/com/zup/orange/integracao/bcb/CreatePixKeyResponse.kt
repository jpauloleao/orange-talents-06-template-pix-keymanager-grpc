package br.com.zup.orange.integracao.bcb

import java.time.LocalDateTime

class CreatePixKeyResponse(
    val keyType: PixKeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner
) {
    val criadaEm: LocalDateTime = LocalDateTime.now()
}
