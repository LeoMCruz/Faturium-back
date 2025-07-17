package com.mrbread.service;

import com.mrbread.domain.repository.PedidoRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PedidoIdService {
    private final PedidoRepository pedidoRepository;

    @Transactional
    public synchronized Long gerarProximoIdPedido() {
        Long ultimoId = pedidoRepository.findMaxIdPedido();
        return (ultimoId != null) ? ultimoId + 1 : 1L;
    }
}
