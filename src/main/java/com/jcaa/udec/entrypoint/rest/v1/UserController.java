package com.jcaa.udec.entrypoint.rest.v1;

import com.jcaa.udec.domain.ports.in.CreateUserUseCase;
import com.jcaa.udec.domain.ports.in.DeleteUserUseCase;
import com.jcaa.udec.domain.ports.in.GetUserUseCase;
import com.jcaa.udec.domain.ports.in.ListUsersUseCase;
import com.jcaa.udec.domain.ports.in.UpdateUserUseCase;
import com.jcaa.udec.domain.services.dto.response.UserResult;
import com.jcaa.udec.entrypoint.rest.v1.docs.UserApiDocumentation;
import com.jcaa.udec.entrypoint.rest.v1.dto.request.CreateUserRequest;
import com.jcaa.udec.entrypoint.rest.v1.dto.request.UpdateUserRequest;
import com.jcaa.udec.entrypoint.rest.v1.dto.response.UserListResponse;
import com.jcaa.udec.entrypoint.rest.v1.dto.response.UserResponse;
import com.jcaa.udec.entrypoint.rest.v1.mappers.UserRestMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
public class UserController implements UserApiDocumentation {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final UserRestMapper userMapper;

    public UserController(
            CreateUserUseCase createUserUseCase,
            GetUserUseCase getUserUseCase,
            ListUsersUseCase listUsersUseCase,
            UpdateUserUseCase updateUserUseCase,
            DeleteUserUseCase deleteUserUseCase,
            UserRestMapper userMapper) {
        this.createUserUseCase = createUserUseCase;
        this.getUserUseCase = getUserUseCase;
        this.listUsersUseCase = listUsersUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.userMapper = userMapper;
    }

    @Override
    public ResponseEntity<UserResponse> createUser(CreateUserRequest request) {
        UserResult result = createUserUseCase.createUser(userMapper.toCommand(request));
        UserResponse response = userMapper.toResponse(result);
        URI resourceLocation = ServletUriComponentsBuilder.fromCurrentRequest()
                .path(USER_ID_PATH)
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(resourceLocation).body(response);
    }

    @Override
    public ResponseEntity<UserResponse> getUser(UUID userId) {
        UserResult result = getUserUseCase.getUser(userMapper.toGetQuery(userId));
        return ResponseEntity.ok(userMapper.toResponse(result));
    }

    @Override
    public ResponseEntity<UserListResponse> listUsers() {
        List<UserResponse> users = userMapper.toResponses(listUsersUseCase.listUsers());
        return ResponseEntity.ok(UserListResponse.from(users));
    }

    @Override
    public ResponseEntity<UserResponse> updateUser(UUID userId, UpdateUserRequest request) {
        UserResult result = updateUserUseCase.updateUser(userMapper.toCommand(userId, request));
        return ResponseEntity.ok(userMapper.toResponse(result));
    }

    @Override
    public ResponseEntity<Void> deleteUser(UUID userId) {
        deleteUserUseCase.deleteUser(userMapper.toDeleteCommand(userId));
        return ResponseEntity.noContent().build();
    }
}
