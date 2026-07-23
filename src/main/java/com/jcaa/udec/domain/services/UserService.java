package com.jcaa.udec.domain.services;

import com.jcaa.udec.domain.exceptions.DuplicateUserException;
import com.jcaa.udec.domain.exceptions.UserNotFoundException;
import com.jcaa.udec.domain.models.User;
import com.jcaa.udec.domain.ports.in.CreateUserUseCase;
import com.jcaa.udec.domain.ports.in.DeleteUserUseCase;
import com.jcaa.udec.domain.ports.in.GetUserUseCase;
import com.jcaa.udec.domain.ports.in.ListUsersUseCase;
import com.jcaa.udec.domain.ports.in.UpdateUserUseCase;
import com.jcaa.udec.domain.ports.out.UserRepositoryPort;
import com.jcaa.udec.domain.services.dto.commands.CreateUserCommand;
import com.jcaa.udec.domain.services.dto.commands.DeleteUserCommand;
import com.jcaa.udec.domain.services.dto.commands.UpdateUserCommand;
import com.jcaa.udec.domain.services.dto.queries.GetUserQuery;
import com.jcaa.udec.domain.services.dto.response.UserResult;
import com.jcaa.udec.domain.services.mappers.UserDomainMapper;
import com.jcaa.udec.domain.valueobjects.EmailAddress;
import com.jcaa.udec.domain.valueobjects.UserId;

import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class UserService implements
        CreateUserUseCase,
        GetUserUseCase,
        ListUsersUseCase,
        UpdateUserUseCase,
        DeleteUserUseCase {

    private final UserRepositoryPort userRepository;
    private final Clock clock;
    private final UserDomainMapper userMapper;

    public UserService(UserRepositoryPort userRepository, Clock clock, UserDomainMapper userMapper) {
        this.userRepository = userRepository;
        this.clock = clock;
        this.userMapper = userMapper;
    }

    @Override
    public UserResult createUser(CreateUserCommand command) {
        ensureEmailIsAvailable(command.email(), Optional.empty());
        Instant creationTime = Instant.now(clock);
        User user = User.create(UserId.random(), command.name(), command.email(), creationTime);
        userRepository.save(user);
        return userMapper.toResult(user);
    }

    @Override
    public UserResult getUser(GetUserQuery query) {
        return userMapper.toResult(findUser(query.userId()));
    }

    @Override
    public List<UserResult> listUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResult)
                .sorted(Comparator.comparing(UserResult::createdAt).thenComparing(UserResult::id))
                .toList();
    }

    @Override
    public UserResult updateUser(UpdateUserCommand command) {
        User currentUser = findUser(command.userId());
        ensureEmailIsAvailable(command.email(), Optional.of(currentUser.getId()));
        User updatedUser = currentUser.update(command.name(), command.email(), Instant.now(clock));
        userRepository.save(updatedUser);
        return userMapper.toResult(updatedUser);
    }

    @Override
    public void deleteUser(DeleteUserCommand command) {
        findUser(command.userId());
        userRepository.deleteById(command.userId());
    }

    private User findUser(UserId userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    private void ensureEmailIsAvailable(EmailAddress email, Optional<UserId> currentUserId) {
        userRepository.findByEmail(email)
                .filter(user -> currentUserId
                        .map(userId -> !Objects.equals(user.getId(), userId))
                        .orElse(true))
                .ifPresent(user -> {
                    throw new DuplicateUserException();
                });
    }
}
