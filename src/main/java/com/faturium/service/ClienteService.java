package com.faturium.service;

import com.faturium.config.exception.AppException;
import com.faturium.config.security.SecurityUtils;
import com.faturium.domain.model.Cliente;
import com.faturium.domain.model.Status;
import com.faturium.domain.repository.ClienteRepository;
import com.faturium.domain.repository.OrganizacaoRepository;
import com.faturium.domain.repository.UserRepository;
import com.faturium.dto.ClienteDTO;
import com.faturium.dto.metrics.ClientesAtivosDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository clienteRepository;
    private final OrganizacaoRepository organizacaoRepository;
    private final UserRepository userRepository;
//    private final RedisService redisService;

    @Transactional
    public ClienteDTO salvarCliente(ClienteDTO clienteDTO){
        var organizacao = organizacaoRepository.findByIdOrg(SecurityUtils.obterOrganizacaoId())
                .orElseThrow(() -> new AppException("Organização não encontrada",
                        "ID de organização inválido",
                        HttpStatus.NOT_FOUND));

        var user = userRepository.findByLogin(SecurityUtils.getEmail())
                .orElseThrow(()-> new AppException("Usuário não encontrado",
                        "Usuário Inválido",
                        HttpStatus.NOT_FOUND));

        var cliente = Cliente.builder()
                .id(clienteDTO.getId())
                .nomeFantasia(clienteDTO.getNomeFantasia())
                .email(clienteDTO.getEmail())
                .cnpj(clienteDTO.getCnpj())
                .cidade(clienteDTO.getCidade())
                .estado(clienteDTO.getEstado())
                .endereco(clienteDTO.getEndereco())
                .razaoSocial(clienteDTO.getRazaoSocial())
                .telefone(clienteDTO.getTelefone())
                .organizacao(organizacao)
                .usuarioCriacao(user)
                .status(Status.ATIVO)
                .dataCriacao(LocalDateTime.now())
                .dataAlteracao(LocalDateTime.now())
                .build();

        clienteRepository.save(cliente);
//        redisService.clearOrgCache("clientes", SecurityUtils.obterOrganizacaoId());

        return ClienteDTO.builder()
                .id(cliente.getId())
                .nomeFantasia(cliente.getNomeFantasia())
                .email(cliente.getEmail())
                .cnpj(cliente.getCnpj())
                .cidade(cliente.getCidade())
                .estado(cliente.getEstado())
                .endereco(cliente.getEndereco())
                .razaoSocial(cliente.getRazaoSocial())
                .telefone(cliente.getTelefone())
                .organizacao(cliente.getOrganizacao().getIdOrg())
                .usuarioCriacao(cliente.getUsuarioCriacao().getLogin())
                .status(cliente.getStatus())
                .dataCriacao(cliente.getDataCriacao())
                .dataAlteracao(cliente.getDataAlteracao())
                .build();
    }

//    @Cacheable(value = "clientes", key = "T(com.faturium.config.security.SecurityUtils).obterOrganizacaoId() " +
//            "+ ':' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort",
//            condition = "#search == null || #search.isEmpty()")
    @Transactional(readOnly = true)
    public List<ClienteDTO> buscarClientesOrganizacao(Pageable pageable, String search){
        if(search == null || search.isEmpty()) {
            return clienteRepository.findAll(SecurityUtils.obterOrganizacaoId(), pageable).stream()
                    .map(cliente -> ClienteDTO.builder()
                            .id(cliente.getId())
                            .nomeFantasia(cliente.getNomeFantasia())
                            .email(cliente.getEmail())
                            .cnpj(cliente.getCnpj())
                            .cidade(cliente.getCidade())
                            .estado(cliente.getEstado())
                            .endereco(cliente.getEndereco())
                            .razaoSocial(cliente.getRazaoSocial())
                            .telefone(cliente.getTelefone())
                            .organizacao(cliente.getOrganizacao().getIdOrg())
                            .usuarioCriacao(cliente.getUsuarioCriacao().getLogin())
                            .dataCriacao(cliente.getDataCriacao())
                            .dataAlteracao(cliente.getDataAlteracao())
                            .status(cliente.getStatus())
                            .build()).collect(Collectors.toList());
        }
        return buscarNome(search, pageable);
    }

    private List<ClienteDTO> buscarNome(String search, Pageable pageable){
        var searchForQuery = "%"+search+"%";
        return clienteRepository.findByName(SecurityUtils.obterOrganizacaoId(), searchForQuery, pageable).stream()
                .map(cliente -> ClienteDTO.builder()
                    .id(cliente.getId())
                    .nomeFantasia(cliente.getNomeFantasia())
                    .email(cliente.getEmail())
                    .cnpj(cliente.getCnpj())
                    .cidade(cliente.getCidade())
                    .estado(cliente.getEstado())
                    .endereco(cliente.getEndereco())
                    .razaoSocial(cliente.getRazaoSocial())
                    .telefone(cliente.getTelefone())
                    .organizacao(cliente.getOrganizacao().getIdOrg())
                    .usuarioCriacao(cliente.getUsuarioCriacao().getLogin())
                    .dataCriacao(cliente.getDataCriacao())
                    .dataAlteracao(cliente.getDataAlteracao())
                    .status(cliente.getStatus())
                    .build()).collect(Collectors.toList());
    }

    @Transactional
    public void deleteCliente(UUID id){
        var cliente = clienteRepository.findById(id, SecurityUtils.obterOrganizacaoId()).orElseThrow(() -> new AppException(
                "Cliente não encontrado",
                "O ID informado é inválido",
                HttpStatus.FORBIDDEN
        ));
        cliente.setDataAlteracao(LocalDateTime.now());
        cliente.setStatus(Status.INATIVO);
        clienteRepository.save(cliente);
//        redisService.clearOrgCache("clientes", SecurityUtils.obterOrganizacaoId());
    }

    @Transactional
    public ClienteDTO alteraCliente(UUID id, ClienteDTO clienteDTO){
        var cliente = clienteRepository.findById(id, SecurityUtils.obterOrganizacaoId()).orElseThrow(() -> new AppException(
                "Cliente não encontrado",
                "O ID informado é inválido",
                HttpStatus.FORBIDDEN
        ));

        Optional.ofNullable(clienteDTO.getCidade())
                .filter(cidade -> !cidade.isEmpty())
                .ifPresent(cliente::setCidade);

        Optional.ofNullable(clienteDTO.getEmail())
                .filter(email -> !email.isEmpty())
                .ifPresent(cliente::setEmail);

        Optional.ofNullable(clienteDTO.getEndereco())
                .filter(endereco -> !endereco.isEmpty())
                .ifPresent(cliente::setEndereco);

        Optional.ofNullable(clienteDTO.getEstado())
                .filter(estado -> !estado.isEmpty())
                .ifPresent(cliente::setEstado);

        Optional.ofNullable(clienteDTO.getNomeFantasia())
                .filter(nome -> !nome.isEmpty())
                .ifPresent(cliente::setNomeFantasia);

        Optional.ofNullable(clienteDTO.getRazaoSocial())
                .filter(nome -> !nome.isEmpty())
                .ifPresent(cliente::setRazaoSocial);

        Optional.ofNullable(clienteDTO.getTelefone())
                .filter(nome -> !nome.isEmpty())
                .ifPresent(cliente::setTelefone);

        cliente.setDataAlteracao(LocalDateTime.now());

        clienteRepository.save(cliente);
//        redisService.clearOrgCache("clientes", SecurityUtils.obterOrganizacaoId());

        return ClienteDTO.builder()
                .id(cliente.getId())
                .nomeFantasia(cliente.getNomeFantasia())
                .email(cliente.getEmail())
                .cnpj(cliente.getCnpj())
                .cidade(cliente.getCidade())
                .estado(cliente.getEstado())
                .endereco(cliente.getEndereco())
                .razaoSocial(cliente.getRazaoSocial())
                .telefone(cliente.getTelefone())
                .organizacao(cliente.getOrganizacao().getIdOrg())
                .usuarioCriacao(cliente.getUsuarioCriacao().getLogin())
                .status(cliente.getStatus())
                .dataCriacao(cliente.getDataCriacao())
                .dataAlteracao(cliente.getDataAlteracao())
                .build();
    }

    public ClientesAtivosDTO calcularClientesAtivos(UUID orgId, String userEmail) {
        LocalDateTime inicioMesAtual = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime fimMesAtual = inicioMesAtual.plusMonths(1);
        LocalDateTime inicioMesAnterior = inicioMesAtual.minusMonths(1);

        Long clientesMesAtual = clienteRepository.findClientesCadastradosMesAtual(orgId, inicioMesAtual, fimMesAtual, userEmail);
        Long clientesMesAnterior = clienteRepository.findClientesCadastradosMesAnterior(orgId, inicioMesAnterior, inicioMesAtual, userEmail);

        Long variacao = clientesMesAtual - clientesMesAnterior;

        return ClientesAtivosDTO.builder()
                .quantidade(clientesMesAtual)
                .variacao(variacao)
                .build();
    }
}
