package com.mrbread.service;

import com.mrbread.config.exception.AppException;
import com.mrbread.domain.model.Organizacao;
import com.mrbread.domain.model.Status;
import com.mrbread.domain.repository.OrganizacaoRepository;
import com.mrbread.dto.OrganizacaoDTO;
import com.mrbread.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizacaoService {
    private final OrganizacaoRepository organizacaoRepository;

    @Transactional
    public OrganizacaoDTO atualizarOrganizacao(OrganizacaoDTO organizacaoDTO){
        var organizacao = Organizacao.builder()
                .idOrg(organizacaoDTO.getId())
                .nomeOrganizacao(organizacaoDTO.getNomeOrganizacao())
                .idOrg(organizacaoDTO.getIdOrg())
                .status(organizacaoDTO.getStatus())
                .cnpj(organizacaoDTO.getCnpj())
                .build();
        organizacaoRepository.save(organizacao);
        return organizacaoDTO;
    }
//
//    public Long idOrgRand (){
//        Long randId;
//        do {
//            randId = ThreadLocalRandom.current().nextLong(1, 100001);
//        } while (organizacaoRepository.existsByIdOrg(randId));
//        return randId;
//    }

}
