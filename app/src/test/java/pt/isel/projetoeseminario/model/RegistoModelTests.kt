package pt.isel.projetoeseminario.model

import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class RegistoModelTests {

    @Test
    fun testRegistoOutputModel() {
        val entrada = LocalDateTime.of(2024, 1, 1, 10, 0)
        val saida = LocalDateTime.of(2024, 1, 1, 17, 0)
        val nomeObra = "Obra"
        val model = RegistoOutputModel(
            entrada = entrada,
            saida = saida,
            nome_obra = nomeObra
        )

        assertEquals(entrada, model.entrada)
        assertEquals(saida, model.saida)
        assertEquals(nomeObra, model.nome_obra)
    }

    @Test
    fun testRegistoOutputModelWithNullSaida() {
        val entrada = LocalDateTime.of(2024, 1, 1, 10, 0)
        val nomeObra = "Obra"
        val model = RegistoOutputModel(
            entrada = entrada,
            saida = null,
            nome_obra = nomeObra
        )

        assertEquals(entrada, model.entrada)
        assertEquals(null, model.saida)
        assertEquals(nomeObra, model.nome_obra)
    }

    @Test
    fun testUserRegisterOutputModel() {
        val entrada = LocalDateTime.of(2024, 1, 1, 10, 0)
        val saida = LocalDateTime.of(2024, 1, 1, 17, 0)
        val register1 = RegistoOutputModel(entrada, saida, "Obra")
        val register2 = RegistoOutputModel(entrada, null, "Obra2")
        val userRegister = UserRegisterOutputModel(
            registers = listOf(register1, register2)
        )

        assertEquals(2, userRegister.registers.size)
        assertEquals(register1, userRegister.registers[0])
        assertEquals(register2, userRegister.registers[1])
    }

    @Test
    fun testRegistoPostOutputModel() {
        val message = "Registration successful"
        val model = RegistoPostOutputModel(message)

        assertEquals(message, model.message)
    }

    @Test
    fun testRegistoInputModel() {
        val time = LocalDateTime.of(2024, 1, 1, 10, 0)
        val obraId = 123
        val model = RegistoInputModel(time, obraId)

        assertEquals(time, model.time)
        assertEquals(obraId, model.obraId)
    }

    @Test
    fun testRegistoByNFCInputModel() {
        val time = LocalDateTime.of(2024, 1, 1, 10, 0)
        val nfcId = "nfc123"
        val model = RegistoByNFCInputModel(time, nfcId)

        assertEquals(time, model.time)
        assertEquals(nfcId, model.nfcId)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testRegistoOutputModelInvalidData() {
        val entrada = LocalDateTime.of(2024, 1, 1, 10, 0)
        val invalidName = ""

        RegistoOutputModel(
            entrada = entrada,
            saida = null,
            nome_obra = invalidName
        )
    }
}