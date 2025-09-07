package com.faturium.service;

import com.faturium.config.exception.AppException;
import com.faturium.config.security.SecurityUtils;
import com.faturium.domain.model.*;
import com.faturium.domain.repository.*;
import com.faturium.dto.DetalhesPedidoDTO;
import com.faturium.dto.ItemPedidoDTO;
import com.faturium.dto.PedidoDTO;
import com.faturium.dto.ResumoPedidoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {
        private final PedidoRepository pedidoRepository;
        private final UserRepository userRepository;
        private final OrganizacaoRepository organizacaoRepository;
        private final ClienteRepository clienteRepository;
        private final ProdutoRepository produtoRepository;
        private final ServicoRepository servicoRepository;
        private final PedidoIdService pedidoIdService;
        private final OrganizationSubscriptionService organizationSubscriptionService;

        @Transactional
        public PedidoDTO criarPedido(PedidoDTO pedidoDto) {
                if(!organizationSubscriptionService.canOrganizationCreateOrders(SecurityUtils.obterOrganizacaoId())){
                        throw new AppException(
                                "Limite de Pedidos atingido",
                                "Faça o upgrade do seu plano para adicionar mais pedidos",
                                HttpStatus.CONFLICT
                        );
                }
                if (pedidoDto == null || pedidoDto.getItens() == null || pedidoDto.getItens().isEmpty()) {
                        throw new AppException(
                                "Itens do pedido vazios",
                                "Nenhum item foi enviado",
                                HttpStatus.BAD_REQUEST);
                }

                User usuario = userRepository.findByLogin(SecurityUtils.getEmail())
                                .orElseThrow(() -> new AppException(
                                        "Usuário não encontrado",
                                        "O usuário é inválido",
                                        HttpStatus.NOT_FOUND));

                Organizacao org = organizacaoRepository.findByIdOrg(SecurityUtils.obterOrganizacaoId())
                                .orElseThrow(() -> new AppException(
                                        "Organização não encontrada",
                                        "ID de organização inválido",
                                        HttpStatus.NOT_FOUND));

                Cliente cliente = clienteRepository.findById(pedidoDto.getCliente())
                                .orElseThrow(() -> new AppException(
                                        "Cliente não encontrado",
                                        "ID do cliente é inválido",
                                        HttpStatus.NOT_FOUND));

                Long idPedido = pedidoIdService.gerarProximoIdPedido(SecurityUtils.obterOrganizacaoId());

                Pedido pedido = Pedido.builder()
                        .idPedido(idPedido)
                        .user(usuario)
                        .organizacao(org)
                        .cliente(cliente)
                        .nomeFantasiaCliente(cliente.getNomeFantasia())
                        .status(Status.PENDENTE)
                        .dataCriacao(LocalDateTime.now())
                        .dataAlteracao(LocalDateTime.now())
                        .itens(new ArrayList<>())
                        .obs(pedidoDto.getObs())
                        .build();

                List<ItemPedido> itens = pedidoDto.getItens().stream()
                        .map(item -> {
                                validarItem(
                                        item.getProduto() != null ? item.getProduto() : null,
                                        item.getServico() != null ? item.getServico() : null,
                                        item.getQuantidade(),
                                        item.getPrecoUnitario());

                                Produto produto = Optional.ofNullable(item.getProduto())
                                        .flatMap(id -> produtoRepository.findById(
                                                id, SecurityUtils.obterOrganizacaoId()))
                                        .orElse(null);

                                Servico servico = Optional.ofNullable(item.getServico())
                                        .flatMap(id -> servicoRepository.findById(
                                                id, SecurityUtils.obterOrganizacaoId()))
                                        .orElse(null);

                            return ItemPedido.builder()
                                    .nome(produto != null ? produto.getNomeProduto() : Objects.requireNonNull(servico).getNomeServico())
                                    .descricao(produto != null ? produto.getDescricao() : servico.getDescricao())
                                    .tipo(produto != null? "Produto" : "Serviço")
                                    .pedido(pedido)
                                    .produto(produto)
                                    .servico(servico)
                                    .quantidade(item.getQuantidade())
                                    .precoUnitario(item.getPrecoUnitario())
                                    .precoTotal(item.getQuantidade()
                                            .multiply(item.getPrecoUnitario()))
                                    .status(Status.ATIVO)
                                    .build();
                        }).collect(Collectors.toList());

                pedido.setItens(itens);

                BigDecimal total = itens.stream()
                        .filter(itemPedido -> itemPedido.getStatus() == Status.ATIVO)
                        .map(ItemPedido::getPrecoTotal)
                        .map(value -> value != null ? value : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                pedido.setPrecoTotal(total);

                Pedido salvarPedido = pedidoRepository.save(pedido);

                List<ItemPedidoDTO> itensDtoResponse = salvarPedido.getItens().stream()
                        .map(item -> ItemPedidoDTO.builder()
                                .produto(
                                        Optional.ofNullable(item.getProduto())
                                                .map(Produto::getId)
                                                .orElse(null))
                                .servico(
                                        Optional.ofNullable(item.getServico())
                                                .map(Servico::getId)
                                                .orElse(null))
                                .quantidade(item.getQuantidade())
                                .nome(item.getNome())
                                .descricao(item.getDescricao())
                                .tipo(item.getTipo())
                                .precoUnitario(item.getPrecoUnitario())
                                .precoTotal(item.getPrecoTotal())
                                .build())
                        .collect(Collectors.toList());

                return PedidoDTO.builder()
                        .id(salvarPedido.getId())
                        .idPedido(salvarPedido.getIdPedido())
                        .itens(itensDtoResponse)
                        .precoTotal(salvarPedido.getPrecoTotal())
                        .organizacao(salvarPedido.getOrganizacao().getIdOrg())
                        .user(salvarPedido.getUser().getLogin())
                        .cliente(salvarPedido.getCliente().getId())
                        .nomeFantasiaCliente(cliente.getNomeFantasia())
                        .obs(salvarPedido.getObs())
                        .status(salvarPedido.getStatus())
                        .dataCriacao(salvarPedido.getDataCriacao())
                        .dataAlteracao(salvarPedido.getDataAlteracao())
                        .build();
        }

        private void validarItem(UUID produto, UUID servico, BigDecimal quantidade, BigDecimal preco) {
                if (produto == null && servico == null) {
                        throw new AppException(
                                "Nenhum serviço ou produto informados",
                                "Verifique os dados enviados",
                                HttpStatus.BAD_REQUEST);
                }
                if (produto != null && servico != null) {
                        throw new AppException(
                                "Configuração inválida",
                                "Deve ser definido OU um produto OU um serviço, não ambos",
                                HttpStatus.BAD_REQUEST);
                }
                if (quantidade.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new AppException(
                                "Quantidade inválida",
                                "A quantidade deve ser maior que zero",
                                HttpStatus.BAD_REQUEST);
                }

                if (preco.compareTo(BigDecimal.ZERO) < 0) {
                        throw new AppException(
                                "Preço inválido",
                                "O preço não pode ser negativo",
                                HttpStatus.BAD_REQUEST);
                }
        }

        @Transactional(readOnly = true)
        public List<ResumoPedidoDTO> buscarResumoPedidos(Pageable pageable) {
                if(SecurityUtils.isManager() || SecurityUtils.isAdmin()){
                        return pedidoRepository.findAll(SecurityUtils.obterOrganizacaoId(), pageable).stream()
                                .map(pedido -> ResumoPedidoDTO.builder()
                                        .id(pedido.getId())
                                        .idPedido(pedido.getIdPedido())
                                        .cliente(pedido.getCliente().getNomeFantasia())
                                        .razaoSocial(pedido.getCliente().getRazaoSocial())
                                        .precoTotal(pedido.getPrecoTotal())
                                        .status(pedido.getStatus())
                                        .usuarioCriacao(pedido.getUser().getLogin())
                                        .dataCriacao(pedido.getDataCriacao())
                                        .dataAlteracao(pedido.getDataAlteracao())
                                        .build()).collect(Collectors.toList());
                }
                return pedidoRepository.findAllByUser(SecurityUtils.obterOrganizacaoId(), pageable, SecurityUtils.getEmail()).stream()
                                .map(pedido -> ResumoPedidoDTO.builder()
                                        .id(pedido.getId())
                                        .idPedido(pedido.getIdPedido())
                                        .cliente(pedido.getCliente().getNomeFantasia())
                                        .razaoSocial(pedido.getCliente().getRazaoSocial())
                                        .precoTotal(pedido.getPrecoTotal())
                                        .usuarioCriacao(pedido.getUser().getLogin())
                                        .status(pedido.getStatus())
                                        .dataCriacao(pedido.getDataCriacao())
                                        .dataAlteracao(pedido.getDataAlteracao())
                                        .build()).collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        public DetalhesPedidoDTO buscarPedidoPorId(UUID id) {
                var pedido = pedidoRepository.findById(id, SecurityUtils.obterOrganizacaoId())
                                .orElseThrow(() -> new AppException("Pedido não encontrado",
                                        "ID do pedido inválido",
                                        HttpStatus.NOT_FOUND));

                List<ItemPedidoDTO> itensDtoResponse = pedido.getItens().stream()
                                .filter(item -> item.getStatus() != Status.INATIVO)
                                .map(item -> ItemPedidoDTO.builder()
                                        .id(item.getId())
                                        .produto(
                                                Optional.ofNullable(item.getProduto())
                                                        .map(Produto::getId)
                                                        .orElse(null))
                                        .servico(
                                                Optional.ofNullable(item.getServico())
                                                        .map(Servico::getId)
                                                        .orElse(null))
                                        .quantidade(item.getQuantidade())
                                        .nome(item.getNome())
                                        .descricao(item.getDescricao())
                                        .tipo(item.getTipo())
                                        .precoUnitario(item.getPrecoUnitario())
                                        .precoTotal(item.getPrecoTotal())
                                        .build()).collect(Collectors.toList());

                return DetalhesPedidoDTO.builder()
                        .id(pedido.getId())
                        .idPedido(pedido.getIdPedido())
                        .itens(itensDtoResponse)
                        .precoTotal(pedido.getPrecoTotal())
                        .organizacao(pedido.getOrganizacao().getIdOrg())
                        .user(pedido.getUser().getLogin())
                        .cliente(pedido.getCliente().getId())
                        .cnpj(pedido.getCliente().getCnpj())
                        .cidade(pedido.getCliente().getCidade())
                        .estado(pedido.getCliente().getEstado())
                        .obs(pedido.getObs())
                        .usuarioCriacao(pedido.getUser().getLogin())
                        .nomeFantasiaCliente(pedido.getNomeFantasiaCliente())
                        .status(pedido.getStatus())
                        .dataCriacao(pedido.getDataCriacao())
                        .dataAlteracao(pedido.getDataAlteracao())
                        .build();
        }

        @Transactional
        public void deletePedido(UUID id){
                var pedido = pedidoRepository.findById(id, SecurityUtils.obterOrganizacaoId())
                        .orElseThrow(() -> new AppException("Pedido não encontrado",
                                "ID do pedido inválido",
                                HttpStatus.NOT_FOUND));
                pedido.setDataAlteracao(LocalDateTime.now());
                pedido.setStatus(Status.INATIVO);
                pedidoRepository.save(pedido);
        }

        public PedidoDTO alterarPedido(PedidoDTO pedidoDTO){
                var pedido = pedidoRepository.findById(pedidoDTO.getId(), SecurityUtils.obterOrganizacaoId())
                        .orElseThrow(() -> new AppException("Pedido não encontrado",
                                "ID do pedido inválido",
                                HttpStatus.NOT_FOUND));
                Cliente cliente = clienteRepository.findById(pedidoDTO.getCliente())
                        .orElseThrow(() -> new AppException(
                                "Cliente não encontrado",
                                "ID do cliente é inválido",
                                HttpStatus.NOT_FOUND));
                pedido.setCliente(cliente);

                pedido.setStatus(pedidoDTO.getStatus());

                pedido.getItens().clear();

                List<ItemPedido> itens = pedidoDTO.getItens().stream()
                        .map(item -> {
                                validarItem(
                                        item.getProduto() != null ? item.getProduto() : null,
                                        item.getServico() != null ? item.getServico() : null,
                                        item.getQuantidade(),
                                        item.getPrecoUnitario());

                                Produto produto = Optional.ofNullable(item.getProduto())
                                        .flatMap(id -> produtoRepository.findById(
                                                id, SecurityUtils.obterOrganizacaoId()))
                                        .orElse(null);

                                Servico servico = Optional.ofNullable(item.getServico())
                                        .flatMap(id -> servicoRepository.findById(
                                                id, SecurityUtils.obterOrganizacaoId()))
                                        .orElse(null);

                                return ItemPedido.builder()
                                        .nome(produto != null ? produto.getNomeProduto() : Objects.requireNonNull(servico).getNomeServico())
                                        .descricao(produto != null ? produto.getDescricao() : servico.getDescricao())
                                        .tipo(produto != null? "Produto" : "Serviço")
                                        .pedido(pedido)
                                        .produto(produto)
                                        .servico(servico)
                                        .quantidade(item.getQuantidade())
                                        .precoUnitario(item.getPrecoUnitario())
                                        .precoTotal(item.getQuantidade()
                                                .multiply(item.getPrecoUnitario()))
                                        .status(Status.ATIVO)
                                        .build();
                        }).toList();

                pedido.getItens().addAll(itens);

                BigDecimal total = itens.stream()
                        .filter(itemPedido -> itemPedido.getStatus() == Status.ATIVO)
                        .map(ItemPedido::getPrecoTotal)
                        .map(value -> value != null ? value : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                pedido.setPrecoTotal(total);

                pedido.setObs(pedidoDTO.getObs());

                pedido.setDataAlteracao(LocalDateTime.now());

                var alterarPedido = pedidoRepository.save(pedido);

                List<ItemPedidoDTO> itensDtoResponse = alterarPedido.getItens().stream()
                        .map(item -> ItemPedidoDTO.builder()
                                .produto(
                                        Optional.ofNullable(item.getProduto())
                                                .map(Produto::getId)
                                                .orElse(null))
                                .servico(
                                        Optional.ofNullable(item.getServico())
                                                .map(Servico::getId)
                                                .orElse(null))
                                .quantidade(item.getQuantidade())
                                .nome(item.getNome())
                                .descricao(item.getDescricao())
                                .tipo(item.getTipo())
                                .precoUnitario(item.getPrecoUnitario())
                                .precoTotal(item.getPrecoTotal())
                                .build())
                        .collect(Collectors.toList());

                return PedidoDTO.builder()
                        .id(pedido.getId())
                        .idPedido(pedido.getIdPedido())
                        .itens(itensDtoResponse)
                        .precoTotal(pedido.getPrecoTotal())
                        .organizacao(pedido.getOrganizacao().getIdOrg())
                        .user(pedido.getUser().getLogin())
                        .cliente(pedido.getCliente().getId())
                        .nomeFantasiaCliente(cliente.getNomeFantasia())
                        .status(pedido.getStatus())
                        .obs(pedido.getObs())
                        .dataCriacao(pedido.getDataCriacao())
                        .dataAlteracao(pedido.getDataAlteracao())
                        .build();

        }
}
