package com.sge.service.usuario;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import com.sge.dto.UsuarioDTO;
import com.sge.entity.Usuario;
import com.sge.exceptions.InfoException;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@WebAppConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioServiceImplTest {
    @Autowired
    private UsuarioServiceImpl usuarioService;

    @BeforeAll
    private void setUp(TestInfo testInfo) throws Exception {
        System.out.println("@BeforeAll - " + testInfo.getDisplayName());

        List<UsuarioDTO> usuarios = usuarioService.buscarTodos();
        for (UsuarioDTO usuario : usuarios) {
            usuarioService.excluir(usuario.getId());
        }
    }

    @AfterEach
    private void tearDown() throws Exception {
        List<UsuarioDTO> usuarios = usuarioService.buscarTodos();
        for (UsuarioDTO usuario : usuarios) {
            usuarioService.excluir(usuario.getId());
        }
    }

    @Test
    @DisplayName("Teste de busca de todos os usuários mas não há nenhum")
    @Tag("UsuarioService")
    public void testBuscarTodosMasNaoHaNenhum() {
        List<UsuarioDTO> usuarios = usuarioService.buscarTodos();

        assertFalse(usuarios.size() > 0);
    }

    @Test
    @DisplayName("Teste de busca de todos os usuários mas há um usuário")
    @Tag("UsuarioService")
    public void testBuscarTodosMasHaUm() throws InfoException {
        assumeFalse(usuarioService.buscarTodos().size() > 0);

        Usuario usuario = new Usuario();
        usuario.setNome("Teste 1");
        usuario.setDocumento("12345678901");
        usuario.setEndereco("Rua Teste 1");
        usuario.setCep("12345678");
        usuario.setEmail("teste1@teste.br");

        usuarioService.inserir(usuario);

        List<UsuarioDTO> usuarios = usuarioService.buscarTodos();

        assertTrue(usuarios.size() == 1);
    }

    @Test
    @DisplayName("Teste de busca de todos os usuários mas há dois ou mais usuários")
    @Tag("UsuarioService")
    public void testBuscarTodosMasHaDoisOuMais() throws InfoException {
        assumeTrue(usuarioService.buscarTodos().size() == 0);

        int quantidade = 5;

        for (int i = 0; i < quantidade; i++) {
            Usuario usuario = new Usuario();
            usuario.setNome("Teste " + (i + 2));
            usuario.setDocumento("1234567890" + (i + 2));
            usuario.setEndereco("Rua Teste " + (i + 2));
            usuario.setCep("12345678");
            usuario.setEmail("teste" + (i + 2) + "@teste.br");

            usuarioService.inserir(usuario);
        }

        List<UsuarioDTO> usuarios = usuarioService.buscarTodos();

        assertEquals(quantidade, usuarios.size());
    }

    @Test
    @DisplayName("Teste de inserção de usuário mas o usuário não é válido")
    @Tag("UsuarioService")
    public void testInserirMasUsuarioNaoEValido() {
        Usuario usuario = new Usuario();

        assertThrows(InfoException.class, () -> {
            usuarioService.inserir(usuario);
        });
    }

    @RepeatedTest(5)
    @DisplayName("Teste que insere um usuário")
    @Tag("UsuarioService")
    public void testInserir(RepetitionInfo repetitionInfo) throws Exception {
        System.out.println("Teste repetido: " + repetitionInfo.getCurrentRepetition());

        Usuario usuario = new Usuario();
        usuario.setNome("Teste "+repetitionInfo.getCurrentRepetition());
        usuario.setDocumento("1234567890"+repetitionInfo.getCurrentRepetition());
        usuario.setEndereco("Rua Teste "+repetitionInfo.getCurrentRepetition());
        usuario.setCep("12345678");
        usuario.setEmail("teste"+repetitionInfo.getCurrentRepetition()+"@teste.com");

        usuario = usuarioService.encontrarUsuarioPorId(usuarioService.inserir(usuario).getId());

        assertNotNull(usuario.getId());
        assertEquals("Teste "+repetitionInfo.getCurrentRepetition(), usuario.getNome());
    }
    
    @Nested
    @DisplayName("Testes de alteração de usuário")
    @Tag("UsuarioService")
    class AlterarUsuarioTest {
        @Test
        @DisplayName("Teste que altera um usuário existente")
        public void testAlterarExistente() throws Exception {
            Usuario usuario = new Usuario();
            usuario.setNome("Teste 6");
            usuario.setDocumento("12345678906");
            usuario.setEndereco("Rua Teste 6");
            usuario.setCep("12345678");
            usuario.setEmail("teste6@teste.com");

            long usuarioId = usuarioService.inserir(usuario).getId();

            usuario.setNome("Teste 6 editado");

            usuarioService.alterar(usuarioId, usuario);

            Usuario usuarioSalvo = usuarioService.encontrarUsuarioPorId(usuarioId);

            assertNotNull(usuarioSalvo);

            assertEquals("Teste 6 editado", usuarioSalvo.getNome());
        }

        @Test
        @DisplayName("Teste que tenta alterar um usuário com dados inválidos")
        public void testAlterarComDadosInvalidos() throws Exception {
            Usuario usuario = new Usuario();
            usuario.setNome("Teste 7 novo");
            usuario.setDocumento("12345678907");
            usuario.setEndereco("Rua Teste 7 novo");
            usuario.setCep("12345678");
            usuario.setEmail("teste7novo@teste.com");
            
            long usuarioId = usuarioService.inserir(usuario).getId();

            usuario.setNome("");

            assertThrows(InfoException.class, () -> {
                usuarioService.alterar(usuarioId, usuario);
            });

            Usuario usuarioSalvo = usuarioService.encontrarUsuarioPorId(usuarioId);

            assertNotNull(usuarioSalvo);
            assertEquals(usuarioSalvo.getNome(), "Teste 7 novo");
        }

        @Test
        @DisplayName("Teste que tenta alterar um usuário inexistente")
        public void testAlterarInexistente() throws Exception {
            Usuario usuario = new Usuario();
            usuario.setId(9999L);
            usuario.setNome("Teste 8");
            usuario.setDocumento("12345678908");
            usuario.setEndereco("Rua Teste 8");
            usuario.setCep("12345678");
            usuario.setEmail("teste8@teste.com");

            Exception exception = assertThrows(InfoException.class, () -> {
                usuarioService.alterar(usuario.getId(), usuario);
            });

            assertEquals("Usuário não encontrado", exception.getMessage());
        }
    }

    @Disabled
    @Test
    @DisplayName("Teste que remove um usuário")
    @Tag("UsuarioService")
    public void testExcluir() throws Exception {
        assumeTrue(usuarioService.buscarTodos().size() == 0);

        Usuario usuario = new Usuario();
        usuario.setNome("Teste excluir");
        usuario.setDocumento("12345678901344");
        usuario.setEndereco("Rua Teste excluir");
        usuario.setCep("12345678");
        usuario.setEmail("teste_excluir@teste.com");

        long usuarioId = usuarioService.inserir(usuario).getId();

        usuarioService.excluir(usuarioId);

        assertEquals(0, usuarioService.buscarTodos().size());
    }

    @Test
    @DisplayName("Teste que tenta excluir um usuário inexistente")
    @Tag("UsuarioService")
    public void testExcluirInexistente() throws Exception {
        assumeTrue(usuarioService.buscarTodos().size() == 0);

        Exception exception = assertThrows(InfoException.class, () -> {
            usuarioService.excluir(9999L);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
    }
}
