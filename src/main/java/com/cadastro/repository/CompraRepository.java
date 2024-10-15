package com.cadastro.repository;

import com.cadastro.entity.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CompraRepository extends JpaRepository<Compra, Long> {

    List<Compra> findByProdutoId(String produtoId);

    List<Compra> findByCpfCompradorAndNomeProdutoContainingIgnoreCaseAndDataCompraBetween(
            String cpfComprador, String nomeProduto, LocalDateTime dataInicio, LocalDateTime dataFim);

    List<Compra> findByDataCompraBetween(LocalDateTime dataInicio, LocalDateTime dataFim);

    @Query("SELECT c FROM Compra c WHERE LOWER(c.nomeProduto) = LOWER(:nomeProduto) AND c.cpfComprador = :cpfComprador")
    List<Compra> findByCpfCompradorAndNomeProduto(@Param("cpfComprador") String cpfComprador, @Param("nomeProduto") String nomeProduto);


}
