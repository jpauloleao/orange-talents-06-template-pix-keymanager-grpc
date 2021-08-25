package br.com.zup.orange.integracao.bcb

data class CreatePixKeyRequest(val keyType: PixKeyType,
                          val key: String,
                          val bankAccount: BankAccount,
                          val owner: Owner) {
}

enum class PixKeyType() {
    CPF,
    CNPJ,
    PHONE,
    EMAIL,
    RANDOM;
}

data class Owner(
    val type: OwnerType,
    val name: String,
    val taxIdNumber: String
) {

    enum class OwnerType {
        NATURAL_PERSON,
        LEGAL_PERSON
    }
}

data class BankAccount(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccountType
)

enum class AccountType() {
    CACC,
    SVGS;
}