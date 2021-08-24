package br.com.zup.orange.integracao.bcb

import java.time.LocalDateTime

class DeletePixKeyResponse( val key: String,
                            val participant: String,
                            val deletedAt: LocalDateTime
)
