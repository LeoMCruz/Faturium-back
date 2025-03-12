package com.mrbread.service;

import com.mrbread.config.exception.AppException;
import com.mrbread.config.security.SecurityUtils;
import com.mrbread.domain.model.Organizacao;
import com.mrbread.domain.model.PerfilAcesso;
import com.mrbread.domain.model.Status;
import com.mrbread.domain.model.User;
import com.mrbread.domain.repository.OrganizacaoRepository;
import com.mrbread.domain.repository.UserRepository;
import com.mrbread.dto.OrgUserDTO;
import com.mrbread.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final OrganizacaoRepository organizacaoRepository;
    private final OrganizacaoService organizacaoService;

    //cria usuario principal (admin), junto com a criação da organização.
    @Transactional
    public UserDTO salvarUser(UserDTO userDTO){
        if(userRepository.existsByLogin(userDTO.getUsername()))
            throw new AppException("Email já cadastrado.", "Tente novamente", HttpStatus.CONFLICT);

        if (organizacaoRepository.existsByCnpj(userDTO.getCnpj())) {
            throw new AppException("CNPJ já cadastrado.", "Verifique o CNPJ informado", HttpStatus.CONFLICT);
        }

        Organizacao organizacao = Organizacao.builder()
                    .nomeOrganizacao(userDTO.getNomeOrganizacao())
                    .cnpj(userDTO.getCnpj())
                    .status(Status.ATIVO)
                    .usuarios(new HashSet<>())
                    .dataCriacao(LocalDateTime.now())
                    .dataAlteracao(LocalDateTime.now())
                    .build();
            organizacaoRepository.save(organizacao);


        var user = User.builder()
                .senha(passwordEncoder.encode(userDTO.getPassword()))
                .login(userDTO.getUsername())
                .nome(userDTO.getNome())
                .status(Status.ATIVO)
                .perfilAcesso(PerfilAcesso.ADMIN)
                .organizacao(organizacao)
                .dataCriacao(LocalDateTime.now())
                .dataAlteracao(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getLogin())
                .nome(user.getNome())
                .nomeOrganizacao(organizacao.getNomeOrganizacao())
                .organizacaoId(organizacao.getIdOrg())
                .dataCriacao(user.getDataCriacao())
                .build();
    }

    //cria novo usuario dentro da organização
    @Transactional
    public UserDTO salvarColaborador(UserDTO userDTO){
        if(userRepository.existsByLogin(userDTO.getUsername()))
            throw new AppException("Email já cadastrado", "Tente novamente", HttpStatus.CONFLICT);

        var organizacao = organizacaoRepository.findByIdOrg(SecurityUtils.obterOrganizacaoId())
                .orElseThrow(() -> new AppException("Organização não encontrada",
                        "ID de organização inválido",
                        HttpStatus.NOT_FOUND));

        var user = User.builder()
                .senha(passwordEncoder.encode(userDTO.getPassword()))
                .login(userDTO.getUsername())
                .nome(userDTO.getNome())
                .status(userDTO.getStatus())
                .perfilAcesso(userDTO.getPerfilAcesso())
                .organizacao(organizacao)
                .dataCriacao(LocalDateTime.now())
                .dataAlteracao(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getLogin())
                .nome(user.getNome())
                .nomeOrganizacao(organizacao.getNomeOrganizacao())
                .organizacaoId(organizacao.getIdOrg())
                .dataCriacao(user.getDataCriacao())
                .build();
    }

    //busca credenciais do usuário autenticado para login.
    @Transactional(readOnly = true)
    public OrgUserDTO getUserInfo(){
        var user = userRepository.findByLogin(SecurityUtils.getEmail())
                .orElseThrow(() -> new AppException("Usuário não encontrado","Login Inválido", HttpStatus.NOT_FOUND));

        var organizacao = organizacaoRepository.findByIdOrg(SecurityUtils.obterOrganizacaoId())
                .orElseThrow(() -> new AppException("Organização não encontrada",
                        "ID de organização inválido",
                        HttpStatus.NOT_FOUND));

        return OrgUserDTO.builder()
                .id(user.getId())
                .username(user.getLogin())
                .nome(user.getNome())
                .perfilAcesso(user.getPerfilAcesso())
                .status(user.getStatus())
                .nomeOrganizacao(organizacao.getNomeOrganizacao())
                .organizacaoId(organizacao.getIdOrg())
                .cnpj(organizacao.getCnpj())
                .dataCriacao(user.getDataCriacao())
                .dataAlteracao(user.getDataAlteracao())
                .build();
    }

    //busca todos os usuarios da organização
    @Transactional(readOnly = true)
    public List<OrgUserDTO> getAllOrganizationUsers(Pageable pageable){
        return userRepository.findAll(SecurityUtils.obterOrganizacaoId(), pageable).stream()
                .map(user -> OrgUserDTO.builder()
                        .id(user.getId())
                        .nome(user.getNome())
                        .username(user.getUsername())
                        .perfilAcesso(user.getPerfilAcesso())
                        .status(user.getStatus())
                        .dataCriacao(user.getDataCriacao())
                        .dataAlteracao(user.getDataAlteracao())
                        .build()).toList();
    }

    // permite alterar senha e nome do usuário
    @Transactional
    public UserDTO updateUser(UserDTO userDTO){
        if(userDTO.getUsername() == null){
            throw new AppException("Usuário inexistente", "Email inválido", HttpStatus.BAD_REQUEST);
        }

        if(!userDTO.getUsername().equals(SecurityUtils.getEmail()) || !SecurityUtils.isAdmin()){
            throw new AppException("Operação não permitida",
                    "O usuário não possui permissão para modificar os dados de usuários",
                    HttpStatus.FORBIDDEN);
        }

        var user = userRepository.findByLoginAndOrganizacaoIdOrg(userDTO.getUsername(), SecurityUtils.obterOrganizacaoId())
                .orElseThrow(() -> new AppException("Usuário não encontrado",
                                "Email inválido",
                                HttpStatus.NOT_FOUND));

        if(userDTO.getNome() != null && !userDTO.getNome().isEmpty()){
            user.setNome(userDTO.getNome());
        }

        if(userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()){
            user.setSenha(passwordEncoder.encode(user.getSenha()));
        }

        if(userDTO.getPerfilAcesso() != null && SecurityUtils.isAdmin()){
            user.setPerfilAcesso(userDTO.getPerfilAcesso());
        }

        user.setDataAlteracao(LocalDateTime.now());

        userRepository.save(user);

        return UserDTO.builder()
                .id(user.getId())
                .nome(user.getNome())
                .username(user.getUsername())
                .perfilAcesso(user.getPerfilAcesso())
                .status(user.getStatus())
                .dataCriacao(user.getDataCriacao())
                .dataAlteracao(user.getDataAlteracao())
                .build();
    }
}
