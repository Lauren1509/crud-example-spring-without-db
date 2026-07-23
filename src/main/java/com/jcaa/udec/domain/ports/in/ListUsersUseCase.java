package com.jcaa.udec.domain.ports.in;

import com.jcaa.udec.domain.services.dto.response.UserResult;

import java.util.List;

public interface ListUsersUseCase {

    List<UserResult> listUsers();
}
