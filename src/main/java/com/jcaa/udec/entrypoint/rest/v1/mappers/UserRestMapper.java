package com.jcaa.udec.entrypoint.rest.v1.mappers;

import com.jcaa.udec.domain.enums.UserStatus;
import com.jcaa.udec.domain.services.dto.commands.CreateUserCommand;
import com.jcaa.udec.domain.services.dto.commands.DeleteUserCommand;
import com.jcaa.udec.domain.services.dto.commands.UpdateUserCommand;
import com.jcaa.udec.domain.services.dto.queries.GetUserQuery;
import com.jcaa.udec.domain.services.dto.response.UserResult;
import com.jcaa.udec.domain.valueobjects.EmailAddress;
import com.jcaa.udec.domain.valueobjects.UserId;
import com.jcaa.udec.domain.valueobjects.UserName;
import com.jcaa.udec.entrypoint.rest.v1.dto.request.CreateUserRequest;
import com.jcaa.udec.entrypoint.rest.v1.dto.request.UpdateUserRequest;
import com.jcaa.udec.entrypoint.rest.v1.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserRestMapper {

    CreateUserCommand toCommand(CreateUserRequest request);

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "email", source = "request.email")
    UpdateUserCommand toCommand(UUID userId, UpdateUserRequest request);

    UserResponse toResponse(UserResult result);

    List<UserResponse> toResponses(List<UserResult> results);

    default GetUserQuery toGetQuery(UUID userId) {
        return new GetUserQuery(UserId.from(userId));
    }

    default DeleteUserCommand toDeleteCommand(UUID userId) {
        return new DeleteUserCommand(UserId.from(userId));
    }

    default UserName toUserName(String value) {
        return UserName.of(value);
    }

    default EmailAddress toEmailAddress(String value) {
        return EmailAddress.of(value);
    }

    default UserId toUserId(UUID value) {
        return UserId.from(value);
    }

    default String toStatus(UserStatus status) {
        return status.name();
    }
}
