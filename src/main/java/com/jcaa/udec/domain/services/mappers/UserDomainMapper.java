package com.jcaa.udec.domain.services.mappers;

import com.jcaa.udec.domain.models.User;
import com.jcaa.udec.domain.services.dto.response.UserResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserDomainMapper {

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "name", source = "name.value")
    @Mapping(target = "email", source = "email.value")
    UserResult toResult(User user);
}
