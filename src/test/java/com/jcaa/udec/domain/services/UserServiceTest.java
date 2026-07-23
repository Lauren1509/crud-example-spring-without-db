package com.jcaa.udec.domain.services;

import com.jcaa.udec.domain.exceptions.DuplicateUserException;
import com.jcaa.udec.domain.exceptions.UserNotFoundException;
import com.jcaa.udec.domain.models.User;
import com.jcaa.udec.domain.ports.out.UserRepositoryPort;
import com.jcaa.udec.domain.services.dto.commands.CreateUserCommand;
import com.jcaa.udec.domain.services.dto.commands.UpdateUserCommand;
import com.jcaa.udec.domain.services.dto.queries.GetUserQuery;
import com.jcaa.udec.domain.services.dto.response.UserResult;
import com.jcaa.udec.domain.services.mappers.UserDomainMapper;
import com.jcaa.udec.domain.valueobjects.EmailAddress;
import com.jcaa.udec.domain.valueobjects.UserId;
import com.jcaa.udec.domain.valueobjects.UserName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceTest {

    private static final Instant CURRENT_TIME = Instant.parse("2026-01-01T10:00:00Z");
    private static final Clock FIXED_CLOCK = Clock.fixed(CURRENT_TIME, ZoneOffset.UTC);

    @Test
    void shouldCreateUserWhenEmailIsAvailable() {
        // Arrange
        InMemoryUserRepository userRepository = new InMemoryUserRepository();
        UserService userService = createService(userRepository);
        UserName name = UserName.of("Ada Lovelace");
        EmailAddress email = EmailAddress.of("ada.lovelace@example.com");
        CreateUserCommand command = new CreateUserCommand(name, email);

        // Act
        UserResult result = userService.createUser(command);

        // Assert
        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo(name.value());
        assertThat(result.email()).isEqualTo(email.value());
        assertThat(result.createdAt()).isEqualTo(CURRENT_TIME);
        assertThat(result.updatedAt()).isEqualTo(CURRENT_TIME);
        assertThat(userRepository.findById(UserId.from(result.id())))
                .get()
                .extracting(User::getEmail)
                .isEqualTo(email);
    }

    @Test
    void shouldRejectDuplicatedEmail() {
        // Arrange
        InMemoryUserRepository userRepository = new InMemoryUserRepository();
        User existingUser = createUser();
        userRepository.save(existingUser);
        UserService userService = createService(userRepository);
        CreateUserCommand command = new CreateUserCommand(
                UserName.of("Another User"),
                existingUser.getEmail());

        // Act
        // Assert
        assertThatThrownBy(() -> userService.createUser(command))
                .isInstanceOf(DuplicateUserException.class);
        assertThat(userRepository.findAll()).containsExactly(existingUser);
    }

    @Test
    void shouldAllowUpdatingAUserWithItsCurrentEmail() {
        // Arrange
        InMemoryUserRepository userRepository = new InMemoryUserRepository();
        User existingUser = createUser();
        userRepository.save(existingUser);
        UserService userService = createService(userRepository);
        UserName updatedName = UserName.of("Ada Byron");
        UpdateUserCommand command = new UpdateUserCommand(
                existingUser.getId(),
                updatedName,
                existingUser.getEmail());

        // Act
        UserResult result = userService.updateUser(command);

        // Assert
        assertThat(result.name()).isEqualTo(updatedName.value());
        assertThat(result.email()).isEqualTo(existingUser.getEmail().value());
        assertThat(result.updatedAt()).isEqualTo(CURRENT_TIME);
        assertThat(userRepository.findById(existingUser.getId()))
                .get()
                .extracting(User::getName)
                .isEqualTo(updatedName);
    }

    @Test
    void shouldFailWhenUserDoesNotExist() {
        // Arrange
        InMemoryUserRepository userRepository = new InMemoryUserRepository();
        UserService userService = createService(userRepository);
        GetUserQuery query = new GetUserQuery(UserId.random());

        // Act
        // Assert
        assertThatThrownBy(() -> userService.getUser(query))
                .isInstanceOf(UserNotFoundException.class);
    }

    private static UserService createService(UserRepositoryPort userRepository) {
        UserDomainMapper mapper = Mappers.getMapper(UserDomainMapper.class);
        return new UserService(userRepository, FIXED_CLOCK, mapper);
    }

    private static User createUser() {
        return User.create(
                UserId.random(),
                UserName.of("Ada Lovelace"),
                EmailAddress.of("ada.lovelace@example.com"),
                CURRENT_TIME.minusSeconds(60));
    }

    private static final class InMemoryUserRepository implements UserRepositoryPort {

        private final Map<UserId, User> users = new HashMap<>();

        @Override
        public void save(User user) {
            users.put(user.getId(), user);
        }

        @Override
        public Optional<User> findById(UserId userId) {
            return Optional.ofNullable(users.get(userId));
        }

        @Override
        public Optional<User> findByEmail(EmailAddress email) {
            return users.values().stream()
                    .filter(user -> Objects.equals(user.getEmail(), email))
                    .findFirst();
        }

        @Override
        public List<User> findAll() {
            return List.copyOf(users.values());
        }

        @Override
        public void deleteById(UserId userId) {
            users.remove(userId);
        }
    }
}
