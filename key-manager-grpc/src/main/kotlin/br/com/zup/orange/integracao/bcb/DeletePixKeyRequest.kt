package br.com.zup.orange.integracao.bcb


data class DeletePixKeyRequest(val key: String) {
    val participant: String = "60701190"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DeletePixKeyRequest

        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }


}