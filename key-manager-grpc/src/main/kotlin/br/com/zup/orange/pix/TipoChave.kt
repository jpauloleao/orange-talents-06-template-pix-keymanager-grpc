package br.com.zup.orange.pix

import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator

enum class TipoChave {
    CPF {
        override fun validaChave(chave: String?) : Boolean{
            if (chave.isNullOrBlank()) {
                throw IllegalArgumentException("Chave não deve ser nula")
            }

            if (!chave.matches("^[0-9]{11}\$".toRegex())) {
                throw IllegalArgumentException("Chave não segue o padrão. Ex: 12345678901")
            }

            val retornoValidacao = CPFValidator().run {
                initialize(null)
                isValid(chave, null) }

            if(!retornoValidacao){
                throw IllegalArgumentException("CPF Invalido")
            }

            return true
        }
    },
    CELULAR {
        override fun validaChave(chave: String?) : Boolean{
            if (chave.isNullOrBlank()) {
                throw IllegalArgumentException("Chave não deve ser nula")
            }
            if (!chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())) {
                throw IllegalArgumentException("Chave não segue o padrão. Ex: +5585988714077")
            }

            return true
        }
    },
    EMAIL {
        override fun validaChave(chave: String?) : Boolean{
            if (chave.isNullOrBlank()) {
                throw IllegalArgumentException("Chave não deve ser nula")
            }

            if(!chave.matches("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})\$".toRegex())){
                throw IllegalArgumentException("Email em formato Invalido")
            }
            return true

        }
    },

    ALEATORIA {
        override fun validaChave(chave: String?) : Boolean{
            //Requisito: Quando a chave for aleatoria, o sistema que gera
            if(!chave.isNullOrBlank()){
                throw IllegalArgumentException("Chave Aleatoria é gerada pelo sistema, não deve ser preenchida")
            }
            return true
        }
    };

    abstract fun validaChave(chave: String?) : Boolean
}
