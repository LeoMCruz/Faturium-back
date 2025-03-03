package com.mrbread.service;

import com.mrbread.config.exception.AppException;
import com.mrbread.config.security.SecurityUtils;
import com.mrbread.domain.model.Cliente;
import com.mrbread.domain.model.Status;
import com.mrbread.domain.repository.ClienteRepository;
import com.mrbread.domain.repository.OrganizacaoRepository;
import com.mrbread.domain.repository.UserRepository;
import com.mrbread.dto.ClienteDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository clienteRepository;
    private final OrganizacaoRepository organizacaoRepository;
    private final UserRepository userRepository;

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
                .organizacao(organizacao)
                .usuarioCriacao(user)
                .status(Status.ATIVO)
                .build();

        clienteRepository.save(cliente);

        return ClienteDTO.builder()
                .id(cliente.getId())
                .nomeFantasia(cliente.getNomeFantasia())
                .email(cliente.getEmail())
                .cnpj(cliente.getCnpj())
                .cidade(cliente.getCidade())
                .estado(cliente.getEstado())
                .endereco(cliente.getEndereco())
                .razaoSocial(cliente.getRazaoSocial())
                .organizacao(cliente.getOrganizacao().getIdOrg())
                .usuarioCriacao(cliente.getUsuarioCriacao().getLogin())
                .status(cliente.getStatus())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ClienteDTO> buscarClientesOrganizacao(Pageable pageable){
        return clienteRepository.findByOrganizacaoIdOrgAndStatus(SecurityUtils.obterOrganizacaoId(), Status.ATIVO, pageable).stream()
                .map(cliente -> ClienteDTO.builder()
                        .id(cliente.getId())
                        .nomeFantasia(cliente.getNomeFantasia())
                        .email(cliente.getEmail())
                        .cnpj(cliente.getCnpj())
                        .cidade(cliente.getCidade())
                        .estado(cliente.getEstado())
                        .endereco(cliente.getEndereco())
                        .razaoSocial(cliente.getRazaoSocial())
                        .organizacao(cliente.getOrganizacao().getIdOrg())
                        .usuarioCriacao(cliente.getUsuarioCriacao().getLogin())
                        .status(cliente.getStatus())
                        .build()).collect(Collectors.toList());
    }

    @Transactional
    public void deleteCliente(UUID id){
        var cliente = clienteRepository.findById(id).orElseThrow(() -> new AppException(
                "Cliente não encontrado",
                "O ID informado é inválido",
                HttpStatus.FORBIDDEN
        ));
        if(!cliente.getOrganizacao().getIdOrg().equals(SecurityUtils.obterOrganizacaoId())){
            throw new AppException(
                    "Operação não permitida",
                    "O cliente informado não pertence a carteira de clientes da Organização",
                    HttpStatus.FORBIDDEN
            );
        }
        cliente.setStatus(Status.INATIVO);
        clienteRepository.save(cliente);
    }
}
