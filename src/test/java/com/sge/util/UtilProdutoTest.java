package com.sge.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import com.sge.entity.Categoria;
import com.sge.entity.Fabricante;
import com.sge.entity.Produto;
import com.sge.exceptions.InfoException;
import com.sge.service.categoria.CategoriaServiceImpl;
import com.sge.service.fabricante.FabricanteServiceImpl;

@SpringBootTest
@WebAppConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UtilProdutoTest {
    @Autowired
    private CategoriaServiceImpl categoriaService;
    @Autowired
    private FabricanteServiceImpl fabricanteService;

    private Categoria categoria;
    private Fabricante fabricante;
    
    @BeforeAll
    private void setUp() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setNome("Categoria");

        this.categoria = categoriaService.inserir(categoria);

        Fabricante fabricante = new Fabricante();
        fabricante.setNome("Fabricante");

        this.fabricante = fabricanteService.inserir(fabricante);
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
    public void validarProduto() throws InfoException {
        System.out.println("Fabricante: " + this.fabricante);
        System.out.println("Categoria: " + this.categoria);

        Produto produto = new Produto();
        produto.setDescricao("Descrição");
        produto.setValorCusto(10.0);
        produto.setValorVenda(10.0);
        produto.setFabricante(this.fabricante);
        produto.setCategoria(this.categoria);

        assertEquals(true, UtilProduto.validarProduto(produto));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {""})
    public void validarProdutoDescricaoNulaOuVazia(String descricao) {
        Produto produto = new Produto();
        produto.setDescricao(descricao);
        
        assertThrows(InfoException.class, () -> UtilProduto.validarProduto(produto));
    }

    @Test
    public void validarProdutoValorCustoNulo() {
        Produto produto = new Produto();
        produto.setDescricao("Descrição");
        produto.setValorCusto(null);

        assertThrows(InfoException.class, () -> UtilProduto.validarProduto(produto));
    }
    
    @Test
    public void validarProdutoValorVendaNulo() {
        Produto produto = new Produto();
        produto.setDescricao("Descrição");
        produto.setValorCusto(10.0);
        produto.setValorVenda(null);

        assertThrows(InfoException.class, () -> UtilProduto.validarProduto(produto));
    }

    @Test
    public void validarProdutoFabricanteNulo() {
        Produto produto = new Produto();
        produto.setDescricao("Descrição");
        produto.setValorCusto(10.0);
        produto.setValorVenda(10.0);
        produto.setFabricante(null);

        assertThrows(InfoException.class, () -> UtilProduto.validarProduto(produto));
    }

    @Test
    public void validarProdutoCategoriaNulo() {
        Produto produto = new Produto();
        produto.setDescricao("Descrição");
        produto.setValorCusto(10.0);
        produto.setValorVenda(10.0);
        produto.setFabricante(this.fabricante);
        produto.setCategoria(null);

        assertThrows(InfoException.class, () -> UtilProduto.validarProduto(produto));
    }
}
