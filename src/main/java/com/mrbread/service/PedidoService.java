package com.mrbread.service;

import com.mrbread.config.exception.AppException;
import com.mrbread.config.security.SecurityUtils;
import com.mrbread.domain.model.Pedido;
import com.mrbread.domain.model.Produto;
import com.mrbread.domain.model.Servico;
import com.mrbread.domain.model.Status;
import com.mrbread.domain.repository.*;
import com.mrbread.dto.PedidoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final UserRepository userRepository;
    private final OrganizacaoRepository organizacaoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final ServicoRepository servicoRepository;

    @Transactional
    public List<Pedido> criarPedido(List<PedidoDTO> pedidoDTO){
        if (pedidoDTO == null || pedidoDTO.isEmpty()) {
            throw new AppException("Lista de pedidos vazia",
                    "Nenhum pedido foi enviado",
                    HttpStatus.BAD_REQUEST);
        }
        boolean todosClientesIguais = pedidoDTO.stream()
                .allMatch(dto -> dto.getCliente().equals(pedidoDTO.getFirst().getCliente()));

        if (!todosClientesIguais) {
            throw new AppException(
                    "Clientes diferentes",
                    "Todos os pedidos devem ser para o mesmo cliente",
                    HttpStatus.BAD_REQUEST
            );
        }

        var email = userRepository.findByLogin(SecurityUtils.getEmail()).orElseThrow(() -> new AppException(
                "Usuário não encontrato",
                "O usuário é inválido",
                HttpStatus.NOT_FOUND
        ));

        var organizacao = organizacaoRepository.findByIdOrg(SecurityUtils.obterOrganizacaoId())
                .orElseThrow(() -> new AppException("Organização não encontrada",
                        "ID de organizacao inválido",
                        HttpStatus.NOT_FOUND));

        var cliente = clienteRepository.findById(pedidoDTO.getFirst().getCliente())
                .orElseThrow(()-> new AppException("Cliente não encontrado",
                        "ID do cliente é inválido",
                        HttpStatus.NOT_FOUND));

        var idPedido = UUID.randomUUID();

        List<Pedido> pedidos = pedidoDTO.stream().map(item -> {
            validarItem(item.getProduto(), item.getServico(), item.getQuantidade(), item.getPrecoUnitario());
//            if (item.getProduto() == null && item.getServico() == null) {
//                throw new AppException(
//                        "Nenhum serviço ou produto informados",
//                        "Verifique os dados enviados",
//                        HttpStatus.BAD_REQUEST
//                );
//            }
//            if(item.getProduto() != null && item.getServico() != null){
//                throw new AppException(
//                        "Configuração inválida",
//                        "Deve ser definido OU um produto OU um serviço, não ambos",
//                        HttpStatus.BAD_REQUEST
//                );
//            }
//            if (item.getQuantidade().compareTo(BigDecimal.ZERO) <= 0) {
//                throw new AppException(
//                        "Quantidade inválida",
//                        "A quantidade deve ser maior que zero",
//                        HttpStatus.BAD_REQUEST
//                );
//            }
//
//            if (item.getPrecoUnitario().compareTo(BigDecimal.ZERO) < 0) {
//                throw new AppException(
//                        "Preço inválido",
//                        "O preço não pode ser negativo",
//                        HttpStatus.BAD_REQUEST
//                );
//            }

            return Pedido.builder()
                    .id(item.getId())
                    .idPedido(idPedido)
                    .user(email)
                    .dataCriacao(LocalDateTime.now())
                    .dataAlteracao(LocalDateTime.now())
                    .organizacao(organizacao)
                    .cliente(cliente)
                    .status(Status.ATIVO)
                    .quantidade(item.getQuantidade())
                    .precoUnitario(item.getPrecoUnitario())
                    .precoTotal(item.getQuantidade().multiply(item.getPrecoUnitario()))
                    .produto(Optional.ofNullable(item.getProduto())
                                    .flatMap(produtoRepository::findById)
                                    .orElse(null)
                    )
                    .servico(Optional.ofNullable(item.getServico())
                                    .flatMap(servicoRepository::findById)
                                    .orElse(null)
                    ).build();
        }).toList();

        pedidoRepository.saveAll(pedidos);

        return pedidos;
    }

    private void validarItem(UUID produto, UUID servico, BigDecimal quantidade, BigDecimal preco){
        if (produto == null && servico == null) {
            throw new AppException(
                    "Nenhum serviço ou produto informados",
                    "Verifique os dados enviados",
                    HttpStatus.BAD_REQUEST
            );
        }
        if(produto != null && servico != null){
            throw new AppException(
                    "Configuração inválida",
                    "Deve ser definido OU um produto OU um serviço, não ambos",
                    HttpStatus.BAD_REQUEST
            );
        }
        if (quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(
                    "Quantidade inválida",
                    "A quantidade deve ser maior que zero",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (preco.compareTo(BigDecimal.ZERO) < 0) {
            throw new AppException(
                    "Preço inválido",
                    "O preço não pode ser negativo",
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
