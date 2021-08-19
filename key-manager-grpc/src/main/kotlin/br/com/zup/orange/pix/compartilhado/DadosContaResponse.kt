package br.com.zup.orange.pix.compartilhado

class DadosContaResponse(
    val tipo: String,
    val agencia: String,
    val numero: String,
    val instituicao: InstituicaoResponse,
    val titular: TitularResponse
) {
    fun toModel() : Conta {
        return Conta(tipo, titular.nome, titular.cpf, agencia, numero, instituicao.nome, instituicao.ispb)
    }
}

class InstituicaoResponse(val nome : String, val ispb: String) {}
class TitularResponse(val nome: String, val cpf: String) {}

