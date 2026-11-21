package com.duoc.ayanad

import com.duoc.ayanad.model.LoginFormError
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Prueba unitaria del login: formulario sin mensajes de error.
 */
class LoginSinErroresTest {

    // Sin ningún mensaje de error, el formulario de login se considera válido
    @Test
    fun sinErrores_formularioEsValido() {
        val errores = LoginFormError()

        assertTrue(errores.isValid())
    }
}