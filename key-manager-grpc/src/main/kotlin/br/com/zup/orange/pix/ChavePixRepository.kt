package br.com.zup.orange.pix

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository : JpaRepository<ChavePix,UUID> {
    fun existsByChave(chave: String): Boolean
    fun findByIdAndClienteId(id: UUID, clienteId: String) : Optional<ChavePix>
    fun findByChave(chave: String) : Optional<ChavePix>
    fun findAllByClienteId(clienteId: String): List<ChavePix>
    fun existsByClienteId(clienteId: String): Boolean
}