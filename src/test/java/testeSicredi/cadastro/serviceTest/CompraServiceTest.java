package testeSicredi.cadastro.serviceTest;

import com.cadastro.dto.RelatorioCompraDTO;
import com.cadastro.entity.Compra;
import com.cadastro.exception.BusinessException;
import com.cadastro.repository.CompraRepository;
import com.cadastro.service.CompraService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompraServiceTest {

    @Mock
    private CompraRepository compraRepository;

    @InjectMocks
    private CompraService compraService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void salvarCompra_deveSalvarCompraCorretamente() {
        Compra compra = new Compra();
        compra.setProdutoId("132");
        compra.setNomeProduto("Notebook");
        compra.setQuantidade(1);
        compra.setCpfComprador("123.456.789-00");
        compra.setValorUnitario(BigDecimal.valueOf(2500.0));

        when(compraRepository.findByProdutoId(compra.getProdutoId())).thenReturn(Collections.emptyList());
        when(compraRepository.findByCpfCompradorAndNomeProduto(compra.getCpfComprador(), compra.getNomeProduto()))
                .thenReturn(Collections.emptyList());
        when(compraRepository.save(compra)).thenReturn(compra);

        Compra compraSalva = compraService.salvarCompra(compra);

        assertNotNull(compraSalva);
        assertEquals("notebook", compraSalva.getNomeProduto());
        verify(compraRepository, times(1)).save(compra);
    }

    @Test
    void salvarCompra_deveLancarExcecaoQuandoQuantidadeUltrapassarLimite() {
        Compra compra = new Compra();
        compra.setProdutoId("132");
        compra.setNomeProduto("Notebook");
        compra.setQuantidade(2);
        compra.setCpfComprador("123.456.789-00");

        Compra compraExistente1 = new Compra();
        compraExistente1.setQuantidade(2);

        List<Compra> comprasExistentes = Arrays.asList(compraExistente1);

        when(compraRepository.findByCpfCompradorAndNomeProduto(compra.getCpfComprador(), compra.getNomeProduto()))
                .thenReturn(comprasExistentes);

        BusinessException exception = assertThrows(BusinessException.class, () -> compraService.salvarCompra(compra));
        assertEquals("O CPF já comprou mais de 3 unidades deste produto.", exception.getMessage());
    }

    @Test
    void buscarCompras_deveRetornarComprasFiltradasCorretamente() throws Exception {
        Compra compra = new Compra();
        compra.setProdutoId("132");
        compra.setNomeProduto("Notebook");
        compra.setCpfComprador("123.456.789-00");

        List<Compra> compras = Arrays.asList(compra);

        when(compraRepository.findByCpfCompradorAndNomeProdutoContainingIgnoreCaseAndDataCompraBetween(
                eq("123.456.789-00"), anyString(), any(), any())).thenReturn(compras);

        List<Compra> resultado = compraService.buscarCompras("12345678900", "Notebook",
                LocalDateTime.of(2024, 10, 1, 0, 0), LocalDateTime.of(2024, 10, 14, 23, 59));

        assertEquals(1, resultado.size());
        verify(compraRepository, times(1)).findByCpfCompradorAndNomeProdutoContainingIgnoreCaseAndDataCompraBetween(
                eq("123.456.789-00"), anyString(), any(), any());
    }

    @Test
    void gerarRelatorio_deveRetornarRelatorioCorretamente() {
        Compra compra = new Compra();
        compra.setProdutoId("132");
        compra.setNomeProduto("Notebook");
        compra.setQuantidade(1);
        compra.setValorUnitario(BigDecimal.valueOf(2500.0));
        compra.setCpfComprador("123.456.789-00");

        List<Compra> compras = Arrays.asList(compra);

        when(compraRepository.findByDataCompraBetween(any(), any())).thenReturn(compras);

        List<RelatorioCompraDTO> relatorio = compraService.gerarRelatorio(LocalDateTime.now().minusDays(1), LocalDateTime.now());

        assertEquals(1, relatorio.size());
        assertEquals("Notebook", relatorio.get(0).getNomeProduto());
        assertEquals(1, relatorio.get(0).getTotalVendido());
        assertEquals(2500.00, relatorio.get(0).getValorUnitario().doubleValue());
    }

    @Test
    void validarAssociacaoProductId_deveLancarExcecaoSeProductIdAssociadoAOutroProduto() {
        Compra compra = new Compra();
        compra.setProdutoId("132");
        compra.setNomeProduto("Notebook");

        Compra compraExistente = new Compra();
        compraExistente.setProdutoId("132");
        compraExistente.setNomeProduto("Celular");

        when(compraRepository.findByProdutoId(compra.getProdutoId())).thenReturn(Arrays.asList(compraExistente));

        BusinessException exception = assertThrows(BusinessException.class, () -> compraService.salvarCompra(compra));
        assertEquals("O productId já está associado a outro produto.", exception.getMessage());
    }
}
