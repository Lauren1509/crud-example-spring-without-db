package com.jcaa.udec.common.configuration;

import com.jcaa.udec.domain.ports.out.UserRepositoryPort;
import com.jcaa.udec.domain.services.UserService;
import com.jcaa.udec.domain.services.mappers.UserDomainMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration(proxyBeanMethods = false)
public class DomainConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    UserDomainMapper userDomainMapper() {
        return Mappers.getMapper(UserDomainMapper.class);
    }

    @Bean
    UserService userService(UserRepositoryPort userRepository, Clock clock, UserDomainMapper userDomainMapper) {
        return new UserService(userRepository, clock, userDomainMapper);
    }
}
