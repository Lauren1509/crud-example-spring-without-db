package com.jcaa.udec.domain.models;

import com.jcaa.udec.domain.enums.UserStatus;
import com.jcaa.udec.domain.exceptions.InvalidUserDataException;
import com.jcaa.udec.domain.valueobjects.EmailAddress;
import com.jcaa.udec.domain.valueobjects.UserId;
import com.jcaa.udec.domain.valueobjects.UserName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    private static final Instant CREATION_TIME = Instant.parse("2026-01-01T10:00:00Z");

    @Test
    void shouldUpdateUserWhilePreservingIdentityAndCreationData() {
        // Arrange
        User user = createUser();
        Instant updateTime = CREATION_TIME.plusSeconds(60);

        // Act
        User updatedUser = user.update(
                UserName.of("Grace Hopper"),
                EmailAddress.of("grace.hopper@example.com"),
                updateTime);

        // Assert
        assertThat(updatedUser.getId()).isEqualTo(user.getId());
        assertThat(updatedUser.getName().value()).isEqualTo("Grace Hopper");
        assertThat(updatedUser.getEmail().value()).isEqualTo("grace.hopper@example.com");
        assertThat(updatedUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(updatedUser.getCreatedAt()).isEqualTo(CREATION_TIME);
        assertThat(updatedUser.getUpdatedAt()).isEqualTo(updateTime);
    }

    @Test
    void shouldRejectUpdateTimeBeforeCreation() {
        // Arrange
        User user = createUser();
        Instant invalidUpdateTime = CREATION_TIME.minusSeconds(1);

        // Act
        // Assert
        assertThatThrownBy(() -> user.update(
                UserName.of("Grace Hopper"),
                EmailAddress.of("grace.hopper@example.com"),
                invalidUpdateTime))
                .isInstanceOf(InvalidUserDataException.class)
                .hasMessage("User update time cannot precede creation time");
    }

    private static User createUser() {
        return User.create(
                UserId.random(),
                UserName.of("Ada Lovelace"),
                EmailAddress.of("ada.lovelace@example.com"),
                CREATION_TIME);
    }
}
