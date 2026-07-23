package com.jcaa.udec.adapter.cache.local;

import com.jcaa.udec.domain.models.User;
import com.jcaa.udec.domain.valueobjects.EmailAddress;
import com.jcaa.udec.domain.valueobjects.UserId;
import com.jcaa.udec.domain.valueobjects.UserName;
import org.junit.jupiter.api.Test;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LocalUserCacheAdapterTest {

    private static final Instant CREATION_TIME = Instant.parse("2026-01-01T10:00:00Z");

    @Test
    void shouldSaveFindListAndDeleteUsers() {
        // Arrange
        LocalUserCacheAdapter adapter = new LocalUserCacheAdapter(
                new ConcurrentMapCacheManager(LocalCacheNames.USERS));
        User firstUser = createUser("Ada Lovelace", "ada@example.com", CREATION_TIME);
        User secondUser = createUser("Grace Hopper", "grace@example.com", CREATION_TIME.plusSeconds(1));

        // Act
        adapter.save(secondUser);
        adapter.save(firstUser);
        List<User> storedUsers = adapter.findAll();

        // Assert
        assertThat(storedUsers).containsExactly(firstUser, secondUser);
        assertThat(adapter.findById(firstUser.getId())).contains(firstUser);
        assertThat(adapter.findByEmail(secondUser.getEmail())).contains(secondUser);

        // Act
        adapter.deleteById(firstUser.getId());

        // Assert
        assertThat(adapter.findById(firstUser.getId())).isEmpty();
        assertThat(adapter.findAll()).containsExactly(secondUser);
    }

    private static User createUser(String name, String email, Instant creationTime) {
        return User.create(
                UserId.random(),
                UserName.of(name),
                EmailAddress.of(email),
                creationTime);
    }
}
