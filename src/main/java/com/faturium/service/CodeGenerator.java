package com.faturium.service;

import com.faturium.domain.repository.PedidoRepository;
import com.faturium.domain.repository.ProdutoRepository;
import com.faturium.domain.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CodeGenerator {
    private final ProdutoRepository produtoRepository;
    private final ServicoRepository servicoRepository;
    private final PedidoRepository pedidoRepository;

    @Transactional
    public synchronized Long gerarProximoIdProduto(UUID organizacaoId) {
        Long ultimoId = produtoRepository.findMaxIdProduto(organizacaoId);
        return (ultimoId != null) ? ultimoId + 1 : 1L;
    }

    @Transactional
    public String getCodigoCompleto(String prefixo, Long numeroSequencial) {
        return String.format("%s-%03d", prefixo, numeroSequencial);
    }

    @Transactional
    public synchronized Long gerarProximoIdServico(UUID organizacaoId) {
        Long ultimoId = servicoRepository.findMaxIdServico(organizacaoId);
        return (ultimoId != null) ? ultimoId + 1 : 1L;
    }

    @Transactional
    public synchronized Long gerarProximoIdPedido(UUID organizacaoId) {
        Long ultimoId = pedidoRepository.findMaxIdPedido(organizacaoId);
        return (ultimoId != null) ? ultimoId + 1 : 1L;
    }
}
