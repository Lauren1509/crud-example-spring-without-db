package com.jcaa.udec.entrypoint.rest.v1.dto.response;

import java.util.List;

public record UserListResponse(List<UserResponse> users, int total) {

    public static UserListResponse from(List<UserResponse> users) {
        List<UserResponse> immutableUsers = List.copyOf(users);
        return new UserListResponse(immutableUsers, immutableUsers.size());
    }
}
