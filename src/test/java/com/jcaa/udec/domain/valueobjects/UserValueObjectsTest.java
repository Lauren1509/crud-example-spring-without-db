package com.jcaa.udec.domain.valueobjects;

import com.jcaa.udec.domain.exceptions.InvalidUserDataException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserValueObjectsTest {

    @Test
    void shouldNormalizeNameAndEmail() {
        // Arrange
        String name = "  Ada    Lovelace ";
        String email = "  ADA.LOVELACE@EXAMPLE.COM ";

        // Act
        UserName userName = UserName.of(name);
        EmailAddress emailAddress = EmailAddress.of(email);

        // Assert
        assertThat(userName.value()).isEqualTo("Ada Lovelace");
        assertThat(emailAddress.value()).isEqualTo("ada.lovelace@example.com");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "invalid-email", "user@localhost"})
    void shouldRejectInvalidEmail(String email) {
        // Arrange
        // Act
        // Assert
        assertThatThrownBy(() -> EmailAddress.of(email))
                .isInstanceOf(InvalidUserDataException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void shouldRejectInvalidName(String name) {
        // Arrange
        // Act
        // Assert
        assertThatThrownBy(() -> UserName.of(name))
                .isInstanceOf(InvalidUserDataException.class);
    }
}
