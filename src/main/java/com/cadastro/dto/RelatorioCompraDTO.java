package com.cadastro.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RelatorioCompraDTO {

    private String nomeProduto;
    private BigDecimal valorUnitario;
    private long quantidadeCompras;
    private int totalVendido;

    public RelatorioCompraDTO(String nomeProduto, BigDecimal valorUnitario, long quantidadeCompras, int totalVendido) {
        this.nomeProduto = nomeProduto;
        this.valorUnitario = valorUnitario;
        this.quantidadeCompras = quantidadeCompras;
        this.totalVendido = totalVendido;
    }
}
