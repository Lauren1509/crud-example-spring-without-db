package com.jcaa.udec.entrypoint.rest.v1.docs;

import com.jcaa.udec.common.helpers.ApiPaths;
import com.jcaa.udec.entrypoint.rest.v1.dto.request.CreateUserRequest;
import com.jcaa.udec.entrypoint.rest.v1.dto.request.UpdateUserRequest;
import com.jcaa.udec.entrypoint.rest.v1.dto.response.ApiErrorResponse;
import com.jcaa.udec.entrypoint.rest.v1.dto.response.UserListResponse;
import com.jcaa.udec.entrypoint.rest.v1.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Tag(name = "Users", description = "CRUDL de usuarios persistidos en caché local")
@RequestMapping(ApiPaths.USERS_V1)
public interface UserApiDocumentation {

    String USER_ID_PATH = "/{userId}";
    String OK_RESPONSE_CODE = "200";
    String CREATED_RESPONSE_CODE = "201";
    String NO_CONTENT_RESPONSE_CODE = "204";
    String BAD_REQUEST_RESPONSE_CODE = "400";
    String NOT_FOUND_RESPONSE_CODE = "404";
    String CONFLICT_RESPONSE_CODE = "409";

    @Operation(summary = "Crear un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = CREATED_RESPONSE_CODE, description = "Usuario creado",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = BAD_REQUEST_RESPONSE_CODE, description = "Solicitud inválida",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = CONFLICT_RESPONSE_CODE, description = "Correo ya registrado",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request);

    @Operation(summary = "Consultar un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = OK_RESPONSE_CODE, description = "Usuario encontrado",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = NOT_FOUND_RESPONSE_CODE, description = "Usuario no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping(USER_ID_PATH)
    ResponseEntity<UserResponse> getUser(
            @Parameter(description = "Identificador UUID del usuario", required = true)
            @PathVariable UUID userId);

    @Operation(summary = "Listar usuarios")
    @ApiResponse(responseCode = OK_RESPONSE_CODE, description = "Listado de usuarios",
            content = @Content(schema = @Schema(implementation = UserListResponse.class)))
    @GetMapping
    ResponseEntity<UserListResponse> listUsers();

    @Operation(summary = "Actualizar un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = OK_RESPONSE_CODE, description = "Usuario actualizado",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = BAD_REQUEST_RESPONSE_CODE, description = "Solicitud inválida",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = NOT_FOUND_RESPONSE_CODE, description = "Usuario no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = CONFLICT_RESPONSE_CODE, description = "Correo ya registrado",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PutMapping(USER_ID_PATH)
    ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "Identificador UUID del usuario", required = true)
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserRequest request);

    @Operation(summary = "Eliminar un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = NO_CONTENT_RESPONSE_CODE, description = "Usuario eliminado", content = @Content),
            @ApiResponse(responseCode = NOT_FOUND_RESPONSE_CODE, description = "Usuario no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping(USER_ID_PATH)
    ResponseEntity<Void> deleteUser(
            @Parameter(description = "Identificador UUID del usuario", required = true)
            @PathVariable UUID userId);
}
