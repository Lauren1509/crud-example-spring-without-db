package com.jcaa.udec.domain.ports.out;

import com.jcaa.udec.domain.models.User;
import com.jcaa.udec.domain.valueobjects.EmailAddress;
import com.jcaa.udec.domain.valueobjects.UserId;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {

    void save(User user);

    Optional<User> findById(UserId userId);

    Optional<User> findByEmail(EmailAddress email);

    List<User> findAll();

    void deleteById(UserId userId);
}
