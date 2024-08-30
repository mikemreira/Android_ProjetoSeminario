package pt.isel.projetoeseminario.model

import junit.framework.TestCase.assertEquals
import org.junit.Test

class UserModelTests {

    @Test
    fun testUserLoginInputModel() {
        val email = "test@example.com"
        val password = "password"
        val model = UserLoginInputModel(email, password)

        assertEquals(email, model.email)
        assertEquals(password, model.password)
    }

    @Test
    fun testUserLoginOutputModel() {
        val token = "token123"
        val model = UserLoginOutputModel(token)

        assertEquals(token, model.token)
    }

    @Test
    fun testImageOutputModel() {
        val foto = "image_url"
        val model = ImageOutputModel(foto)

        assertEquals(foto, model.foto)
    }

    @Test
    fun testUserSignupInputModel() {
        val name = "Grupo 5"
        val email = "grupo5@test.com"
        val password = "password"
        val model = UserSignupInputModel(name, email, password)

        assertEquals(name, model.name)
        assertEquals(email, model.email)
        assertEquals(password, model.password)
    }

    @Test
    fun testUserSignupOutputModel() {
        val message = "Signup successful"
        val model = UserSignupOutputModel(message)

        assertEquals(message, model.message)
    }

    @Test
    fun testUserOutputModel() {
        val nome = "Grupo 5"
        val email = "grupo5@example.com"
        val model = UserOutputModel(nome, email)

        assertEquals(nome, model.nome)
        assertEquals(email, model.email)
    }
}