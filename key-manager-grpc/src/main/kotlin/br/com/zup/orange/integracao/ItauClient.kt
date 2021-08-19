package br.com.zup.orange.integracao

import br.com.zup.orange.pix.compartilhado.DadosContaResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("\${itau.url}")
interface ItauClient {
    @Get("/api/v1/clientes/{clienteId}/contas{?tipo}")
    fun buscaConta(@PathVariable clienteId: String, @QueryValue tipo: String): HttpResponse<DadosContaResponse>
}