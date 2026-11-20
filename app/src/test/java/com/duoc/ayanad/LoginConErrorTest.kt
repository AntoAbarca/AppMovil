package com.duoc.ayanad

import com.duoc.ayanad.model.LoginFormError
import org.junit.Assert.assertFalse
import org.junit.Test

/**
 * Prueba unitaria del login: formulario con un mensaje de error.
 */
class LoginConErrorTest {

    // Con al menos un mensaje de error, el formulario de login no es válido
    @Test
    fun conErrorEnUsuario_formularioNoEsValido() {
        val errores = LoginFormError(usuario = "El usuario es obligatorio")

        assertFalse(errores.isValid())
    }
}