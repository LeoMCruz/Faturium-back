package com.faturium.service;

import com.faturium.domain.model.Organizacao;
import com.faturium.domain.repository.OrganizacaoRepository;
import com.faturium.dto.OrganizacaoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
