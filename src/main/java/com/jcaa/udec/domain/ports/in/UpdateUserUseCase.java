package com.jcaa.udec.domain.ports.in;

import com.jcaa.udec.domain.services.dto.commands.UpdateUserCommand;
import com.jcaa.udec.domain.services.dto.response.UserResult;

public interface UpdateUserUseCase {

    UserResult updateUser(UpdateUserCommand command);
}
