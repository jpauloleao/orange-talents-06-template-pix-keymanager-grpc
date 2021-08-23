package br.com.zup.orange

import br.com.zup.orange.pix.TipoChave
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TipoChaveTest {

    @Nested
    inner class CPF {
        @Test
        fun `invalido quando cpf for vazio`() {
            with(TipoChave.CPF) {
                val retorno = assertThrows<IllegalArgumentException> {
                    validaChave("")
                }
                assertEquals(retorno.message, "Chave não deve ser nula")
            }
        }

        @Test
        fun `invalido quando cpf for nao seguir o padrão definido no regex`() {
            with(TipoChave.CPF) {
                val retorno = assertThrows<IllegalArgumentException> {
                    validaChave("123456789")
                }
                assertEquals(retorno.message, "Chave não segue o padrão. Ex: 12345678901")
            }
        }

        @Test
        fun `invalido quando cpf for incorreto`() {
            with(TipoChave.CPF) {
                val retorno = assertThrows<IllegalArgumentException> {
                    validaChave("12345678910")
                }
                assertEquals(retorno.message, "CPF Invalido")
            }
        }

        @Test
        fun `deve ser valido quando cpf for um numero valido`() {
            with(TipoChave.CPF) {
                assertTrue(validaChave("29136294071"))
            }
        }

    }

    @Nested
    inner class CELULAR {
        @Test
        fun `invalido quando celular for vazio`() {
            with(TipoChave.CELULAR) {
                val retorno = assertThrows<IllegalArgumentException> {
                    validaChave("")
                }
                assertEquals(retorno.message, "Chave não deve ser nula")
            }
        }

        @Test
        fun `invalido quando celular nao seguir o padrão definido no regex`() {
            with(TipoChave.CELULAR) {
                val retorno = assertThrows<IllegalArgumentException> {
                    validaChave("8899112233")
                }
                assertEquals(retorno.message, "Chave não segue o padrão. Ex: +5585988714077")
            }
        }

        @Test
        fun `deve ser valido quando celular for um numero valido`() {
            with(TipoChave.CELULAR) {
                assertTrue(validaChave("+5585988714077"))
            }
        }

        @Nested
        inner class EMAIL {
            @Test
            fun `invalido quando email for vazio`() {
                with(TipoChave.EMAIL) {
                    val retorno = assertThrows<IllegalArgumentException> {
                        validaChave("")
                    }
                    assertEquals(retorno.message, "Chave não deve ser nula")
                }
            }
            @Test
            fun `invalido quando email nao seguir o padrão definido no regex`() {
                with(TipoChave.EMAIL) {
                    val retorno = assertThrows<IllegalArgumentException> {
                        validaChave("joao.com")
                    }
                    assertEquals(retorno.message, "Email em formato Invalido")
                }
            }
            @Test
            fun `deve ser valido quando email for um numero valido`() {
                with(TipoChave.EMAIL) {
                    assertTrue(validaChave("joao@gmail.com"))
                }
            }
        }
    }
}