package com.mrbread.service;

import com.mrbread.domain.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PedidoIdService {
    private final PedidoRepository pedidoRepository;

    @Transactional
    public synchronized Long gerarProximoIdPedido(UUID organizacaoId) {
        Long ultimoId = pedidoRepository.findMaxIdPedido(organizacaoId);
        return (ultimoId != null) ? ultimoId + 1 : 1L;
    }
}
