package com.cadastro.service;

import com.cadastro.dto.RelatorioCompraDTO;
import com.cadastro.entity.Compra;
import com.cadastro.exception.BusinessException;
import com.cadastro.repository.CompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompraService {

    @Autowired
    private CompraRepository compraRepository;

    public Compra salvarCompra(Compra compra) {
        validarAssociacaoProductId(compra);
        validarQuantidadeProdutoPorCpf(compra);

        compra.setNomeProduto(compra.getNomeProduto().toLowerCase());
        return compraRepository.save(compra);
    }

    public List<Compra> buscarCompras(String cpf, String nomeProduto, LocalDateTime dataInicio, LocalDateTime dataFim) throws Exception {
        dataInicio = (dataInicio == null) ? LocalDateTime.of(2000, 1, 1, 20, 01, 02) : dataInicio;
        dataFim = (dataFim == null) ? LocalDateTime.now() : dataFim;
        cpf = formatarCpf(cpf);
        validarNomeProduto(nomeProduto);

        List<Compra> compras = compraRepository.findByCpfCompradorAndNomeProdutoContainingIgnoreCaseAndDataCompraBetween(
                cpf, nomeProduto != null ? nomeProduto : "", dataInicio, dataFim);

        if (compras.isEmpty()) {
            throw new Exception("Nenhuma compra encontrada para o CPF: " + cpf);
        }
        return compras.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    public List<RelatorioCompraDTO> gerarRelatorio(LocalDateTime dataInicio, LocalDateTime dataFim) {
        List<Compra> compras = compraRepository.findByDataCompraBetween(dataInicio, dataFim);
        return compras.stream()
                .collect(Collectors.groupingBy(Compra::getNomeProduto))
                .entrySet().stream()
                .map(e -> new RelatorioCompraDTO(
                        e.getKey(),
                        e.getValue().get(0).getValorUnitario(),
                        e.getValue().size(),
                        e.getValue().stream().mapToInt(Compra::getQuantidade).sum()
                )).collect(Collectors.toList());
    }

    private void validarAssociacaoProductId(Compra compra) {
        List<Compra> comprasExistentes = compraRepository.findByProdutoId(compra.getProdutoId());

        if (!comprasExistentes.isEmpty()) {
            for (Compra compraExistente : comprasExistentes) {
                if (!compraExistente.getNomeProduto().equalsIgnoreCase(compra.getNomeProduto())) {
                    throw new BusinessException("O productId já está associado a outro produto.");
                }
            }
        }
    }

    private void validarQuantidadeProdutoPorCpf(Compra compra) {
        List<Compra> comprasDoMesmoProduto = compraRepository.findByCpfCompradorAndNomeProduto(
                compra.getCpfComprador(), compra.getNomeProduto());

        int totalQuantidade = comprasDoMesmoProduto.stream().mapToInt(Compra::getQuantidade).sum();

        if (totalQuantidade + compra.getQuantidade() > 3) {
            throw new BusinessException("O CPF já comprou mais de 3 unidades deste produto.");
        }
    }

    private String formatarCpf(String cpf) {
        if (cpf != null && cpf.matches("\\d{11}")) {
            return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        }
        return cpf;
    }

    private void validarNomeProduto(String nomeProduto) {
        if (nomeProduto != null && nomeProduto.trim().length() < 3) {
            throw new BusinessException("O nome do produto deve ter no mínimo 3 caracteres.");
        }
    }
}
