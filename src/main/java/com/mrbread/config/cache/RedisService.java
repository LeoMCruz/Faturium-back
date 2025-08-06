//package com.mrbread.config.cache;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.Set;
//import java.util.UUID;
//
//@Service
//@Slf4j
//public class RedisService {
//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;
//
//    public void clearOrgCache(String cacheName, UUID organizacaoId) {
//        if (cacheName == null || cacheName.trim().isEmpty()) {
//            throw new IllegalArgumentException("O nome do cache não pode ser nulo ou vazio");
//        }
//
//        String pattern = cacheName + "::" + organizacaoId + ":*";
//
//        Set<String> chaves = redisTemplate.keys(pattern);
//        if (chaves != null && !chaves.isEmpty()) {
//            redisTemplate.delete(chaves);
//            log.info("cache deletado");
//        }
//    }
//}
