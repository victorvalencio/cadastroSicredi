package com.cadastro.controller;

import com.cadastro.dto.RelatorioCompraDTO;
import com.cadastro.entity.Compra;
import com.cadastro.service.CompraService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/compras")
public class CompraController {

    @Autowired
    private CompraService compraService;

    @PostMapping
    public ResponseEntity<Compra> cadastrarCompra(@Valid @RequestBody Compra compra) throws Exception {
        Compra compraSalva = compraService.salvarCompra(compra);
        return ResponseEntity.status(HttpStatus.CREATED).body(compraSalva);
    }

    @GetMapping
    public ResponseEntity<?> buscarCompras(
            @RequestParam(required = true) String cpf,
            @RequestParam(required = true) String nomeProduto,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) throws Exception {

        if (nomeProduto != null && nomeProduto.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Nome do produto não pode ser vazio.");
        }

        List<Compra> compras = compraService.buscarCompras(cpf, nomeProduto, dataInicio, dataFim);
        return ResponseEntity.ok(compras);
    }

    @GetMapping("/relatorio")
    public ResponseEntity<List<RelatorioCompraDTO>> gerarRelatorio(
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim) {
        List<RelatorioCompraDTO> relatorio = compraService.gerarRelatorio(dataInicio, dataFim);
        return ResponseEntity.ok(relatorio);
    }
}