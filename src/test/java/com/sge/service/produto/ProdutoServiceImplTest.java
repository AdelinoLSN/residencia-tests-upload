package com.sge.service.produto;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import com.mysql.cj.x.protobuf.MysqlxCrud.Collection;
import com.sge.dto.CategoriaDTO;
import com.sge.dto.ProdutoDTO;
import com.sge.entity.Categoria;
import com.sge.entity.Fabricante;
import com.sge.entity.Produto;
import com.sge.exceptions.InfoException;
import com.sge.service.categoria.CategoriaServiceImpl;
import com.sge.service.fabricante.FabricanteServiceImpl;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@WebAppConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProdutoServiceImplTest {
    @Autowired
    private ProdutoServiceImpl produtoService;
    @Autowired
    private CategoriaServiceImpl categoriaService;
    @Autowired
    private FabricanteServiceImpl fabricanteService;

    @BeforeEach
    private void beforeEach() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setNome("Categoria Teste");
        categoriaService.inserir(categoria);

        Fabricante fabricante = new Fabricante();
        fabricante.setNome("Fabricante Teste");
        fabricanteService.inserir(fabricante);
    }

    @AfterEach
    private void afterEach() throws Exception {
        List<Produto> produtos = produtoService.buscarTodos();
        for (Produto produto : produtos) {
            produtoService.excluir(produto.getId());
        }
    }

    @AfterAll
    private void tearDown() throws Exception {
        List<Categoria> categorias = categoriaService.buscarTodos();
        for (Categoria categoria : categorias) {
            categoriaService.excluir(categoria.getId());
        }

        List<Fabricante> fabricantes = fabricanteService.buscarTodos();
        for (Fabricante fabricante : fabricantes) {
            fabricanteService.excluir(fabricante.getId());
        }
    }

    @Test
    @DisplayName("Teste que busca todos os produtos, mas não encontra nenhum")
    public void testBuscarTodosNenhumProduto() throws Exception {
        List<Produto> produtos = produtoService.buscarTodos();

        assertNotNull(produtos);
        assertEquals(0, produtos.size());
    }

    @Test
    @DisplayName("Teste que busca todos os produtos, mas encontra um")
    public void testBuscarTodosUmProduto() throws Exception {
        Produto produto = new Produto();
        produto.setNome("Produto Test");
        produto.setDescricao("Descrição do produto");
        produto.setValorCusto(10.0);
        produto.setValorVenda(20.0);
        produto.setCategoria(categoriaService.buscarTodos().get(0));
        produto.setFabricante(fabricanteService.buscarTodos().get(0));

        produtoService.inserir(produto);

        List<Produto> produtos = produtoService.buscarTodos();

        assertNotNull(produtos);
        assertEquals(1, produtos.size());
    }

    @Test
    @DisplayName("Teste que busca todos os produtos, mas encontra vários")
    public void testBuscarTodosVariosProdutos() throws Exception {
        for (int i = 0; i < 3; i++) {
            Produto produto = new Produto();
            produto.setNome("Produto Test");
            produto.setDescricao("Descrição do produto");
            produto.setValorCusto(10.0);
            produto.setValorVenda(20.0);
            produto.setCategoria(categoriaService.buscarTodos().get(0));
            produto.setFabricante(fabricanteService.buscarTodos().get(0));

            produtoService.inserir(produto);
        }

        List<Produto> produtos = produtoService.buscarTodos();

        assertNotNull(produtos);
        assertEquals(3, produtos.size());
    }

    @Nested
    @DisplayName("Testes de inserir produto")
    @Tag("ProdutoService")
    class InserirProdutoTest {
        @Test
        @DisplayName("Teste que insere um produto")
        public void testInserirProduto() throws Exception {
            Produto produto = new Produto();
            produto.setNome("Produto Test");
            produto.setDescricao("Descrição do produto");
            produto.setValorCusto(10.0);
            produto.setValorVenda(20.0);
            produto.setCategoria(categoriaService.buscarTodos().get(0));
            produto.setFabricante(fabricanteService.buscarTodos().get(0));

            Produto produtoSalvo = produtoService.inserir(produto);

            assertNotNull(produtoSalvo);
            assertNotNull(produtoSalvo.getId());
            assertEquals(produto.getNome(), produtoSalvo.getNome());
            assertEquals(produto.getDescricao(), produtoSalvo.getDescricao());
            assertEquals(produto.getValorCusto(), produtoSalvo.getValorCusto());
            assertEquals(produto.getValorVenda(), produtoSalvo.getValorVenda());
            assertEquals(produto.getCategoria().getId(), produtoSalvo.getCategoria().getId());
            assertEquals(produto.getFabricante().getId(), produtoSalvo.getFabricante().getId());
        }

        @Test
        @DisplayName("Teste que tenta inserir um produto com descricao nulo")
        public void testInserirProdutoNomeNulo() throws Exception {
            Produto produto = new Produto();
            produto.setDescricao(null);
            produto.setValorCusto(10.0);
            produto.setValorVenda(20.0);
            produto.setCategoria(categoriaService.buscarTodos().get(0));
            produto.setFabricante(fabricanteService.buscarTodos().get(0));

            InfoException exception = assertThrows(InfoException.class, () -> {
                produtoService.inserir(produto);
            });

            assertEquals("MESSAGE.DESCRICAO_REQUIRED", exception.getMessage());
        }

        @TestFactory
        Stream<DynamicTest> testInserirMultiplosProdutos() {
            List<String> descricoes = new ArrayList<>(
                Arrays.asList("Descrição 1", "Descrição 2", "Descrição 3")
            );

            return descricoes.stream().map(descricao -> {
                return DynamicTest.dynamicTest("Teste que insere um produto com a descrição " + descricao, () -> {
                    Produto produto = new Produto();
                    produto.setNome("Produto Test");
                    produto.setDescricao(descricao);
                    produto.setValorCusto(10.0);
                    produto.setValorVenda(20.0);
                    produto.setCategoria(categoriaService.buscarTodos().get(0));
                    produto.setFabricante(fabricanteService.buscarTodos().get(0));

                    Produto produtoSalvo = produtoService.inserir(produto);

                    assertNotNull(produtoSalvo);
                    assertNotNull(produtoSalvo.getId());
                    assertEquals(produto.getNome(), produtoSalvo.getNome());
                    assertEquals(produto.getDescricao(), produtoSalvo.getDescricao());
                    assertEquals(produto.getValorCusto(), produtoSalvo.getValorCusto());
                    assertEquals(produto.getValorVenda(), produtoSalvo.getValorVenda());
                    assertEquals(produto.getCategoria().getId(), produtoSalvo.getCategoria().getId());
                    assertEquals(produto.getFabricante().getId(), produtoSalvo.getFabricante().getId());
                });
            });
        }
    }

    // @Nested
    // @DisplayName("Testes de inserir produto")
    // @Tag("ProdutoService")
    // class inserirProdutoTest {
    //     @ParameterizedTest
    //     @DisplayName("Teste que insere um produto")
    //     public void testInserirProduto() throws Exception {
    //         Produto produto = new Produto();
    //         produto.setNome("Produto Test");
    //         produto.setDescricao("Descrição do produto");
    //         produto.setValorCusto(10.0);
    //         produto.setValorVenda(20.0);
    //         produto.setCategoria(categoriaService.buscarTodos().get(0));
    //         produto.setFabricante(fabricanteService.buscarTodos().get(0));

    //         // ProdutoDTO produtoDTO = produtoService.inserir(produto);

    //         // assertNotNull(produtoDTO);
    //         // assertNotNull(produtoDTO.getId());
    //         // assertEquals(produto.getNome(), produtoDTO.getNome());
    //         // assertEquals(produto.getDescricao(), produtoDTO.getDescricao());
    //         // assertEquals(produto.getValorCusto(), produtoDTO.getValorCusto());
    //         // assertEquals(produto.getValorVenda(), produtoDTO.getValorVenda());
    //         // assertEquals(produto.getCategoria().getId(), produtoDTO.getCategoria().getId());
    //         // assertEquals(produto.getFabricante().getId(), produtoDTO.getFabricante().getId());
    //     }
    // }
        


        // @Test
        // @DisplayName("Teste que insere um produto")
        // public void testInserirProduto() throws Exception {
        //     Produto produto = new Produto();
        //     produto.setNome("Produto Test");
        //     produto.setDescricao("Descrição do produto");
        //     produto.setValorCusto(10.0);
        //     produto.setValorVenda(20.0);
        //     produto.setCategoria(categoriaService.buscarTodos().get(0));
        //     produto.setFabricante(fabricanteService.buscarTodos().get(0));

        //     ProdutoDTO produtoDTO = produtoService.inserir(produto);

        //     assertNotNull(produtoDTO);
        //     assertNotNull(produtoDTO.getId());
        //     assertEquals(produto.getNome(), produtoDTO.getNome());
        //     assertEquals(produto.getDescricao(), produtoDTO.getDescricao());
        //     assertEquals(produto.getValorCusto(), produtoDTO.getValorCusto());
        //     assertEquals(produto.getValorVenda(), produtoDTO.getValorVenda());
        //     assertEquals(produto.getCategoria().getId(), produtoDTO.getCategoria().getId());
        //     assertEquals(produto.getFabricante().getId(), produtoDTO.getFabricante().getId());
        // }

        // @Test
        // @DisplayName("Teste que insere um produto com nome nulo")
        // public void testInserirProdutoNomeNulo() throws Exception {
        //     Produto produto = new Produto();
        //     produto.setNome(null);
        //     produto.setDescricao("Descrição do produto");
        //     produto.setValorCusto(10.0);
        //     produto.setValorVenda(20.0);
        //     produto.setCategoria(categoriaService.buscarTodos().get(0));
        //     produto.setFabricante(fabricanteService.buscarTodos().get(0));

        //     InfoException exception = assertThrows(InfoException.class, () -> {
        //         produtoService.inserir(produto);
        //     });

        //     assertEquals("Nome do produto não pode ser nulo", exception.getMessage());
        // }

        // @Test
        // @DisplayName("Teste que insere um produto com nome vazio")
        // public void testInserirProdutoNomeVazio() throws Exception {
        //     Produto produto = new Produto();
        //     produto.setNome("");
        //     produto.setDescricao("Descrição do produto");
        //     produto.setValorCusto(10.0);
        //     produto.setValorVenda(20.0);
        //     produto.setCategoria(categoriaService.buscarTodos().get(0));
        //     produto.setFabricante(fabricanteService.buscarTodos().get(0));

        //     InfoException exception = assertThrows(InfoException.class, () -> {
        //         produtoService.inserir(produto);
        //     });

        //     assertEquals("Nome do produto não pode ser vazio", exception.getMessage());

        // }

}
    
    // @Nested
    // @DisplayName("Testes de buscar produto")
    // @Tag("ProdutoService")
    // class buscarProdutoTest {
    //     @Test
    //     @DisplayName("Teste que busca todos os produtos, mas com um produto")
    //     public void testBuscarTodosUmProduto() throws Exception {
    //         assumeFalse(produtoService.buscarTodos().isEmpty());

    //         Produto produto = new Produto();
    //         produto.setNome("Produto Test");
    //         produto.setDescricao("Descrição do produto");
    //         produto.setValorCusto(10.0);
    //         produto.setValorVenda(20.0);
    //         produto.setCategoria(categoriaService.buscarTodos().get(0));
    //         produto.setFabricante(fabricanteService.buscarTodos().get(0));

    //         produto = produtoService.inserir(produto);

    //         List<Produto> produtos = produtoService.buscarTodos();

    //         assertFalse(produtos.isEmpty());
    //         assertEquals(1, produtos.size());
    //     }

    //     @Test
    //     @DisplayName("Teste que busca todos os produtos, mas vazio")
    //     public void testBuscarTodosVazio() {
    //         assumeTrue(produtoService.buscarTodos().isEmpty());

    //         List<Produto> produtos = produtoService.buscarTodos();

    //         assertTrue(produtos.isEmpty());
    //     }

    //     @Test
    //     @DisplayName("Teste que busca um produto existente")
    //     public void testBuscarExistente(TestReporter testReporter) throws Exception {
    //         Produto produto = new Produto();
    //         produto.setNome("Produto Test");
    //         produto.setDescricao("Descrição do produto");
    //         produto.setValorCusto(10.0);
    //         produto.setValorVenda(20.0);
    //         produto.setCategoria(categoriaService.buscarTodos().get(0));
    //         produto.setFabricante(fabricanteService.buscarTodos().get(0));
    //         testReporter.publishEntry("Produto", produto.toString());

    //         produto = produtoService.inserir(produto);
    //         testReporter.publishEntry("Produto inserido", produto.toString());

    //         Produto produtoBuscado = produtoService.buscarPorId(produto.getId());
    //         testReporter.publishEntry("Produto buscado", produtoBuscado.toString());

    //         assertNotNull(produtoBuscado);
    //         assertEquals(produto.getId(), produtoBuscado.getId());
    //     }

    //     @Test
    //     @DisplayName("Teste que busca um produto inexistente")
    //     public void testBuscarInexistente() throws Exception {
    //         Produto produto = new Produto();
    //         produto.setNome("Produto Test");
    //         produto.setDescricao("Descrição do produto");
    //         produto.setValorCusto(10.0);
    //         produto.setValorVenda(20.0);
    //         produto.setCategoria(categoriaService.buscarTodos().get(0));
    //         produto.setFabricante(fabricanteService.buscarTodos().get(0));

    //         produto = produtoService.inserir(produto);

    //         Produto produtoBuscado = produtoService.buscarPorId(produto.getId() + 1);

    //         assertNull(produtoBuscado);
    //     }
    // }

    // @Test
    // @RepeatedTest(5)
    // @DisplayName("Teste que insere um produto")
    // @Tag("ProdutoService")
    // public void testInserir() throws Exception {
    //     assumeFalse(categoriaService.buscarTodos().isEmpty());
    //     assumeFalse(fabricanteService.buscarTodos().isEmpty());

    //     Produto produto = new Produto();
    //     produto.setNome("Produto Test");
    //     produto.setDescricao("Descrição do produto");
    //     produto.setValorCusto(10.0);
    //     produto.setValorVenda(20.0);
    //     produto.setCategoria(categoriaService.buscarTodos().get(0));
    //     produto.setFabricante(fabricanteService.buscarTodos().get(0));

    //     produto = produtoService.inserir(produto);

    //     assertNotNull(produto.getId());
    //     assertEquals(1, produtoService.buscarTodos().size());
    // }

    // @Test
    // @DisplayName("Teste que insere um produto sem valor de venda")
    // @Tag("ProdutoService")
    // public void testInserirSemValorDeVenda() throws Exception {
    //     assumeFalse(categoriaService.buscarTodos().isEmpty());
    //     assumeFalse(fabricanteService.buscarTodos().isEmpty());

    //     Produto produto = new Produto();
    //     produto.setNome("Produto Teste");
    //     produto.setDescricao("Descrição do produto");
    //     produto.setValorCusto(10.0);
    //     produto.setCategoria(categoriaService.buscarTodos().get(0));
    //     produto.setFabricante(fabricanteService.buscarTodos().get(0));

    //     assertThrows(InfoException.class, () -> {
    //         produtoService.inserir(produto);
    //     });
    // }

    // @Disabled
    // @Test
    // @DisplayName("Teste que remove um produto")
    // @Tag("ProdutoService")
    // public void testExcluir() throws Exception {
    //     assumeFalse(produtoService.buscarTodos().isEmpty());

    //     Produto produto = new Produto();
    //     produto.setNome("Produto Test");
    //     produto.setDescricao("Descrição do produto");
    //     produto.setValorCusto(10.0);
    //     produto.setValorVenda(20.0);
    //     produto.setCategoria(categoriaService.buscarTodos().get(0));
    //     produto.setFabricante(fabricanteService.buscarTodos().get(0));

    //     produto = produtoService.inserir(produto);

    //     produtoService.excluir(produto.getId());

    //     assertEquals(0, produtoService.buscarTodos().size());
    // }
// }
