package com.jcaa.udec.domain.ports.in;

import com.jcaa.udec.domain.services.dto.commands.CreateUserCommand;
import com.jcaa.udec.domain.services.dto.response.UserResult;

public interface CreateUserUseCase {

    UserResult createUser(CreateUserCommand command);
}
