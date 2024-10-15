package testeSicredi.cadastro.controller;

import com.cadastro.controller.CompraController;
import com.cadastro.dto.RelatorioCompraDTO;
import com.cadastro.entity.Compra;
import com.cadastro.service.CompraService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompraControllerTest {

    @Mock
    private CompraService compraService;

    @InjectMocks
    private CompraController compraController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void cadastrarCompra_deveRetornarCompraCriada() throws Exception {
        Compra compra = new Compra();
        compra.setProdutoId("132");
        compra.setNomeProduto("Notebook");
        compra.setQuantidade(1);
        compra.setCpfComprador("123.456.789-00");
        compra.setValorUnitario(BigDecimal.valueOf(2500.0));
        compra.setDataCompra(LocalDateTime.now());

        when(compraService.salvarCompra(any(Compra.class))).thenReturn(compra);

        ResponseEntity<Compra> response = compraController.cadastrarCompra(compra);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Notebook", response.getBody().getNomeProduto());
        verify(compraService, times(1)).salvarCompra(compra);
    }

    @Test
    void buscarCompras_deveRetornarListaDeCompras() throws Exception {
        Compra compra = new Compra();
        compra.setProdutoId("132");
        compra.setNomeProduto("Notebook");
        compra.setQuantidade(1);
        compra.setCpfComprador("123.456.789-00");
        compra.setDataCompra(LocalDateTime.now());

        List<Compra> compras = Arrays.asList(compra);

        when(compraService.buscarCompras(anyString(), anyString(), any(), any())).thenReturn(compras);

        ResponseEntity<?> response = compraController.buscarCompras("12345678900", "Notebook",
                LocalDateTime.of(2024, 10, 1, 0, 0), LocalDateTime.of(2024, 10, 14, 23, 59));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<Compra> result = (List<Compra>) response.getBody();
        assertEquals(1, result.size());
        assertEquals("Notebook", result.get(0).getNomeProduto());
        verify(compraService, times(1)).buscarCompras(anyString(), anyString(), any(), any());
    }

    @Test
    void buscarCompras_deveRetornarErroSeNomeProdutoForVazio() throws Exception {
        ResponseEntity<?> response = compraController.buscarCompras("12345678900", "", null, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Nome do produto não pode ser vazio.", response.getBody());
    }

    @Test
    void gerarRelatorio_deveRetornarRelatorioCorretamente() {
        RelatorioCompraDTO relatorioDTO = new RelatorioCompraDTO("Notebook", BigDecimal.valueOf(2500.0), 3, 3);
        List<RelatorioCompraDTO> relatorio = Arrays.asList(relatorioDTO);

        when(compraService.gerarRelatorio(any(), any())).thenReturn(relatorio);

        ResponseEntity<List<RelatorioCompraDTO>> response = compraController.gerarRelatorio(
                LocalDateTime.of(2024, 10, 1, 0, 0), LocalDateTime.of(2024, 10, 14, 23, 59));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Notebook", response.getBody().get(0).getNomeProduto());
        verify(compraService, times(1)).gerarRelatorio(any(), any());
    }
}
