package com.sge.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.sge.entity.Usuario;
import com.sge.exceptions.InfoException;

public class UtilUsuarioTest {
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    public void testValidarUsuarioInvalido(String nome, TestReporter testReporter) throws Exception {
        testReporter.publishEntry("Nome: " + nome);

        Usuario usuario = new Usuario();
        usuario.setNome(nome);

        Exception exception = assertThrows(InfoException.class, () -> {
            UtilUsuario.validarUsuario(usuario);
        });

        assertEquals("MESSAGE.NOME_REQUIRED", exception.getMessage());
    }

    @Test
    public void testValidarUsuarioComNomeVazio() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("");

        Exception exception = assertThrows(InfoException.class, () -> {
            UtilUsuario.validarUsuario(usuario);
        });

        assertEquals("MESSAGE.NOME_REQUIRED", exception.getMessage());
    }

    @Test
    public void testValidarUsuarioSemDocumento() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("Nome");
        usuario.setDocumento(null);

        Exception exception = assertThrows(InfoException.class, () -> {
            UtilUsuario.validarUsuario(usuario);
        });

        assertEquals("MESSAGE.CPF_REQUIRED", exception.getMessage());
    }

    @Test
    public void testValidarUsuarioComDocumentoVazio() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("Nome");
        usuario.setDocumento("");

        Exception exception = assertThrows(InfoException.class, () -> {
            UtilUsuario.validarUsuario(usuario);
        });

        assertEquals("MESSAGE.CPF_REQUIRED", exception.getMessage());
    }

    @Test
    public void testValidarUsuarioSemEndereco() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("Nome");
        usuario.setDocumento("12345678901");
        usuario.setEndereco(null);

        Exception exception = assertThrows(InfoException.class, () -> {
            UtilUsuario.validarUsuario(usuario);
        });

        assertEquals("MESSAGE.ENDERECO_REQUIRED", exception.getMessage());
    }

    @Test
    public void testValidarUsuarioComEnderecoVazio() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("Nome");
        usuario.setDocumento("12345678901");
        usuario.setEndereco("");

        Exception exception = assertThrows(InfoException.class, () -> {
            UtilUsuario.validarUsuario(usuario);
        });

        assertEquals("MESSAGE.ENDERECO_REQUIRED", exception.getMessage());
    }

    @Test
    public void testValidarUsuarioSemCep() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("Nome");
        usuario.setDocumento("12345678901");
        usuario.setEndereco("Endereco");
        usuario.setCep(null);

        Exception exception = assertThrows(InfoException.class, () -> {
            UtilUsuario.validarUsuario(usuario);
        });

        assertEquals("MESSAGE.CEP_REQUIRED", exception.getMessage());
    }

    @Test
    public void testValidarUsuarioComCepVazio() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("Nome");
        usuario.setDocumento("12345678901");
        usuario.setEndereco("Endereco");
        usuario.setCep("");

        Exception exception = assertThrows(InfoException.class, () -> {
            UtilUsuario.validarUsuario(usuario);
        });

        assertEquals("MESSAGE.CEP_REQUIRED", exception.getMessage());
    }

    @Test
    public void testValidarUsuarioSemEmail() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("Nome");
        usuario.setDocumento("12345678901");
        usuario.setEndereco("Endereco");
        usuario.setCep("12345678");
        usuario.setEmail(null);

        Exception exception = assertThrows(InfoException.class, () -> {
            UtilUsuario.validarUsuario(usuario);
        });

        assertEquals("MESSAGE.EMAIL_REQUIRED", exception.getMessage());
    }

    @Test
    public void testValidarUsuarioComEmailVazio() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("Nome");
        usuario.setDocumento("12345678901");
        usuario.setEndereco("Endereco");
        usuario.setCep("12345678");
        usuario.setEmail("");

        Exception exception = assertThrows(InfoException.class, () -> {
            UtilUsuario.validarUsuario(usuario);
        });

        assertEquals("MESSAGE.EMAIL_REQUIRED", exception.getMessage());
    }

    @Test
    public void testValidarUsuarioComTodosOsCamposPreenchidos() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("Nome");
        usuario.setDocumento("12345678901");
        usuario.setEndereco("Endereco");
        usuario.setCep("12345678");
        usuario.setEmail("teste@teste.com");

        assertEquals(true, UtilUsuario.validarUsuario(usuario));
    }
}