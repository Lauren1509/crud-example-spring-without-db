package com.jcaa.udec.domain.ports.in;

import com.jcaa.udec.domain.services.dto.commands.DeleteUserCommand;

public interface DeleteUserUseCase {

    void deleteUser(DeleteUserCommand command);
}
