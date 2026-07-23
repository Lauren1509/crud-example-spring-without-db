package com.jcaa.udec.domain.ports.in;

import com.jcaa.udec.domain.services.dto.queries.GetUserQuery;
import com.jcaa.udec.domain.services.dto.response.UserResult;

public interface GetUserUseCase {

    UserResult getUser(GetUserQuery query);
}
